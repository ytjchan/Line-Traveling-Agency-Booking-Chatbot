package com.example.bot.spring;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectBookControllerTester.class, ProjectBookController.class, BookState.class, SQLDatabaseEngine.class })
public class ProjectBookControllerTester {
	protected ProjectBookController book = new ProjectBookController();
	protected SQLDatabaseEngine db = new SQLDatabaseEngine();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Connection c = getConnection();
		String template = "delete from booking where lineid = ?";
		PreparedStatement stmt = c.prepareStatement(template);
		stmt.setString(1, "test");
		stmt.executeUpdate();
		
    	template = "delete from booker where lineid = ?";
    	stmt = c.prepareStatement(template);
    	stmt.setString(1, "test");
    	stmt.executeUpdate();
    	
    	
    	template = "update discount set remaining = remaining + 2 where tourid = ? and offerid = ?";
    	stmt = c.prepareStatement(template);
    	stmt.setString(1, "2D001");
    	stmt.setString(2, "20171115");
    	stmt.executeUpdate();
    	
    	stmt.close();
        c.close();
	}

	
	private static Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}
	
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	
	
	@Test
	public void newUser() {
		//TEST NEW USER
		BookState bookState = new BookState();
		String text = "book Kim Sung";
		String userId = "test";
		
		book.process(text, bookState, userId);
		assertThat(bookState.tourId.equals("Kim Sung")).isEqualTo(true);
		
		bookState = new BookState();
		text = "book 2D005";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("name")).isEqualTo(true);
		
		text = "testName";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("hkid")).isEqualTo(true);
		
		text = "testid";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("hkid")).isEqualTo(true);
		
		text = "HKIDTST";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("age")).isEqualTo(true);
		
		text = "testtextage";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("age")).isEqualTo(true);
		
		text = "9000";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("age")).isEqualTo(true);
		
		text = "20";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("phoneno")).isEqualTo(true);
		
		text = "testtextno";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("phoneno")).isEqualTo(true);
		
		text = "42";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("phoneno")).isEqualTo(true);
		
		text = "123456789";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("phoneno")).isEqualTo(true);
		
		text = "12345678";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("newUserConfirm")).isEqualTo(true);
		
		text = "test";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("newUserConfirm")).isEqualTo(true);
		
		text = "no";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("name")).isEqualTo(true);
		
		bookState.substate = "newUserConfirm";
		text = "yes";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("error")).isEqualTo(true);
		
		//NEW USER DONE
		//TEST BOOKING
		
		text = "book 2D001";
		bookState = new BookState();
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("selectOffering")).isEqualTo(true);
		
		text = "-1";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("selectOffering")).isEqualTo(true);
		
		text = Integer.toString(Integer.MAX_VALUE);
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("selectOffering")).isEqualTo(true);
		
		text = "testtextnumber";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("selectOffering")).isEqualTo(true);
		
		text = "0";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("adults")).isEqualTo(true);
		
		text = "0";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("adults")).isEqualTo(true);
		
		text = "-1";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("adults")).isEqualTo(true);
		
		text = "testtextnumber";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("adults")).isEqualTo(true);
		
		text = "1000";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("children")).isEqualTo(true);
		
		text = "-1";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("children")).isEqualTo(true);
		
		text = "testtextnumber";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("children")).isEqualTo(true);
		
		text = "0";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("toddlers")).isEqualTo(true);
		
		text = "-1";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("toddlers")).isEqualTo(true);
		
		text = "testtextnumber";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("toddlers")).isEqualTo(true);
		
		text = "0";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("requests")).isEqualTo(true);
		
		text = "test";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("error")).isEqualTo(true);
		
		bookState.substate = "requests";
		bookState.adults = 2;
		bookState.children = 2;
		bookState.requests = "test";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("bookingConfirm")).isEqualTo(true);
		
		text = "no";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("selectOffering")).isEqualTo(true);
		
		bookState.substate = "bookingConfirm";
		text = "test";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("bookingConfirm")).isEqualTo(true);
		
		text = "yes";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("done")).isEqualTo(true);
		
		text = "text";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("done")).isEqualTo(true);
		
		bookState.substate = "error";
		text = "text";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("error")).isEqualTo(true);
		
		
		//no discount test
		
		text = "book 2D001";
		bookState = new BookState();
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("selectOffering")).isEqualTo(true);
		
		text = "0";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("adults")).isEqualTo(true);
		
		text = "2";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("children")).isEqualTo(true);
		
		text = "1";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("toddlers")).isEqualTo(true);
		
		text = "1";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("requests")).isEqualTo(true);
		
		text = "test 2";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("bookingConfirm")).isEqualTo(true);
		
		text = "yes";
		book.process(text, bookState, userId);
		assertThat(bookState.substate.equals("done")).isEqualTo(true);
	}
	
	@Test
	public void testGetDiscountFail() throws Exception {
		
		double result = db.getDiscount("2D002");
		assertThat(result >= 0 && result <= 1);
	}
}

