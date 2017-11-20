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
@SpringBootTest(classes = { ProjectSearchControllerTester.class, ProjectSearchController.class, SearchState.class })
public class ProjectSearchControllerTester {
	protected ProjectSearchController search = new ProjectSearchController(); 

	@Test
	public void keywordInputTest() {
		String keyword = "Shimen";
		String state = "search";
		SearchState searchState = new SearchState();
		searchState.substate = "keywordInput";
		search.process(keyword, state, searchState);
		assertThat(searchState.keywords.size() == 1).isEqualTo(true);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		assertThat(searchState.rs.size() > 0).isEqualTo(true);
		
		//check invalid too, check searchState.rs.size()
		keyword = "sung kim approves";
		state = "search";
		searchState.substate = "keywordInput";
		search.process(keyword,state,searchState);
		assertThat(searchState.keywords.size()==2).isEqualTo(true);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		assertThat(searchState.rs.size() == 0).isEqualTo(true);
	}

	
	@Test
	public void showDetailsTest() {
		String state = "search";
		SearchState searchState = new SearchState();
		String text = "show details of 2D001";
		search.process(text, state, searchState);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		
		//invalid test
		text = "show details of Kim Sung";
		search.process(text, state, searchState);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
	}
	
	@Test
	public void showDatesTest() {
		String state = "search";
		SearchState searchState = new SearchState();
		String text = "show dates of 2D001";
		search.process(text, state, searchState);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		
		//invalid test
		text = "show dates of Kim Sung";
		search.process(text, state, searchState);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		
		text = "show dates of 3D991";
		search.process(text, state, searchState);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
	}
	
	@Test
	public void addFilterTest() {
		String state = "search";
		SearchState searchState = new SearchState();
		String text = "add filter";
		search.process(text, state, searchState);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
	}
	
	@Test
	public void enterDateTest() {
		String state = "search";
		SearchState searchState = new SearchState();
		String text = "2000-01-01 to 3000-01-01";
		search.process(text, state, searchState);
		assertThat(searchState.startDate.equals("2000-01-01")).isEqualTo(true);
		assertThat(searchState.endDate.equals("3000-01-01")).isEqualTo(true);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		assertThat(searchState.rs.size() > 0).isEqualTo(true);
		
		//check invalid too
		text = "2500-01-01 to 3000-01-01";
		search.process(text, state, searchState);
		assertThat(searchState.startDate.equals("2500-01-01")).isEqualTo(true);
		assertThat(searchState.endDate.equals("3000-01-01")).isEqualTo(true);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		assertThat(searchState.rs.size() == 0).isEqualTo(true);
	}
	
	@Test
	public void showNextTest() {
		String state = "search";
		String text = "search";
		SearchState searchState = new SearchState();
		
		search.process(text, state, searchState);
		assertThat(searchState.rsIndex == 5).isEqualTo(true);
		
		text = "next 5";
		search.process(text, state, searchState);
		assertThat(searchState.rsIndex == 10).isEqualTo(true);
		
		//check invalid too
		text = "2018-01-01 to 2019-01-01";
		search.process(text, state, searchState);
		assertThat(searchState.startDate.equals("2018-01-01")).isEqualTo(true);
		assertThat(searchState.endDate.equals("2019-01-01")).isEqualTo(true);
		assertThat(searchState.rsIndex < 5).isEqualTo(true);
		
		text = "next 5";
		search.process(text, state, searchState);
		assertThat(searchState.rsIndex < 5).isEqualTo(true);
	}
	
	@Test
	public void bookBackTest() {
		String state = "search";
		SearchState searchState = new SearchState();
		String text = "2018-01-01 to 2019-01-01";
		search.process(text, state, searchState);
		assertThat(searchState.startDate.equals("2018-01-01")).isEqualTo(true);
		assertThat(searchState.endDate.equals("2019-01-01")).isEqualTo(true);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		assertThat(searchState.rs.size() > 0).isEqualTo(true);
		
		int temp = searchState.rs.size();
		int temp2 = searchState.rsIndex;
		
		state = "book";
		text = ".back";
		search.process(text, state, searchState);
		assertThat(temp == searchState.rs.size()).isEqualTo(true);
		assertThat(temp2 == searchState.rsIndex).isEqualTo(true);
	}
	
	@Test
	public void showPrevTest() {
		String state = "search";
		SearchState searchState = new SearchState();
		String text = "search";
		search.process(text, state, searchState);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		
		text = "next";
		search.process(text, state, searchState);
		assertThat(searchState.rsIndex == 10).isEqualTo(true);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		
		text = "back";
		search.process(text, state, searchState);
		assertThat(searchState.rsIndex == 5).isEqualTo(true);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
		
		//check invalid too
		text = "back";
		search.process(text, state, searchState);
		assertThat(searchState.rsIndex == 5).isEqualTo(true);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
	}
	
	@Test
	public void unknownTest() {
		String state = "search";
		SearchState searchState = new SearchState();
		String text = "I love Sung Kim";
		search.process(text, state, searchState);
		assertThat(search.replyList.size() == 1).isEqualTo(true);
	}
	
	
}

