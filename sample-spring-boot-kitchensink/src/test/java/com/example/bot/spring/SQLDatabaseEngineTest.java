package com.example.bot.spring;

import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SQLDatabaseEngineTest.class, SQLDatabaseEngine.class })
public class SQLDatabaseEngineTest {

	@Autowired
	SQLDatabaseEngine db=new SQLDatabaseEngine();
	@Test
	/**
	 * Test case to test for input text which is keyword, whether it can return corresponding answer
	 */
	public void testSearchKeywordFromFAQ1() {
		String result=db.searchKeywordFromFAQ("vegetarian");
		assertNotNull(result);
		//System.out.print("\n\nhello"+result+"!!!!!!!!\n\n");
		assertTrue(result.equals("No"));
	}
	@Test
	/**
	 * Test case to test for input text which is not keyword, whether it return empty string
	 */
	public void testSearchKeywordFromFAQ2() {
		String result=db.searchKeywordFromFAQ("vegetariansaf");
		assertNotNull(result);
		
		assertTrue(result.length()==0);
	}
	@Test
	/**
	 * Test case to test for duplicate input words which are keyword, whether it can return corresponding answer
	 */
	public void testSearchKeywordFromFAQ3() {
		String result=db.searchKeywordFromFAQ("vegetarian vegetarian");
		assertNotNull(result);
		//System.out.print("\n\nhello"+result+"!!!!!!!!\n\n");
		assertTrue(result.equals("No"));
	}
	@Test
	/**
	 * Test case to test for a input text containing one keyword and one which is not ketword, whether it can return corresponding answer
	 * note that for input text which contain more than two words, after find one keyword,'\n\n' will be added which make reply clear for users 
	 */
	public void testSearchKeywordFromFAQ4() {
		String result=db.searchKeywordFromFAQ("vegetarian vsafsa");
		assertNotNull(result);
		//System.out.print("\n\nhello"+result+"!!!!!!!!\n\n");
		assertTrue(result.equals("No\n\n"));
	}
	
	
	
	
	
	
	//COMP3111FUN is all the staff id in database
	
	
	@Test
	/**
	 * Test case to test for input text which is keyword, whether it can return corresponding answer
	 * note that all staff id will be added a ';' which make it easier to seperate them later
	 */
	public void testGetStaffId() {
		String result=db.getStaffId();
		assertNotNull(result);
		//System.out.print("\n\nhello"+result+"!!!!!!!!\n\n");
		assertTrue(result.equals("COMP3111FUN;"));
	}
	
		/**
	 * test searchBookingById() method under input userId who have booked tours.
	 */
	@Test
	public void testSearchBookingById1() {
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		try {
			result=db.searchBookingById("hkjeffer");
		} catch (URISyntaxException e){
		} catch (SQLException e) {}
		assertNotNull(result);
		assertTrue(result.size()==2);
	}
	
	/**
	 * test searchBookingById() method under input userId who have no booked tours.
	 */
	@Test
	public void testSearchBookingById2() {
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		try {
			result=db.searchBookingById("nobody");
		} catch (URISyntaxException e){
		} catch (SQLException e) {}
		assertNotNull(result);
		assertTrue(result.size()==0);
	}
	
	/**
	 * test getRecommendation() method under input userId who have booked tours.
	 */
	@Test
	public void testGetRecommendation() {
		ArrayList<String> result = new ArrayList<String>();
		try {
			result=db.getRecommendation("hkjeffer");
		} catch (URISyntaxException e){
		} catch (SQLException e) {}
		assertNotNull(result);
		assertTrue(result.size()>0);
	}
	
	
}

