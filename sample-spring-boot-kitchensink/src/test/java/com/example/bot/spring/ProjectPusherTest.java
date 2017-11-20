package com.example.bot.spring;

import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

public class ProjectPusherTest {
	@Autowired
	private ProjectPusher pusher;
	
	public ProjectPusherTest() {}
	
	@Before
	public void setUp() {
		//TODO: put in userID
		this.pusher = new ProjectPusher(userId, "JUnit test message");
	}
	
	@Test
	public void testPushText() {
		System.out.println("push text");
		//TODO: put in userID
		pusher.pushText.ProjectPusher innerPusher = new pusher.pushText.ProjectPusher(userId);
		
		//push a string
		String tempString = "JUnit test message in innerPusher";
		innerPusher.pushText(tempString);
		//TODO: put in userID
		assertThat(log.getRenderedMessage(), is("Attempting to push" + tempString.length+" texts to " + userId));
		
		//push a string array
		String [] tempArray = new String [5];
		tempArray[0] = "JUnit";	tempArray[1] = "test";	tempArray[2] = "message";	tempArray[3] = "in";		tempArray[4] = "innerPusher";
		innerPusher.pushText(tempArray);
		assertThat(log.getRenderedMessage(), is("Attempting to push" + tempArray.length+" texts to " + userId));
	}
	
	@Test
	public void testPushMessage() {
		System.out.println("push message");
		Message messages = new TextMessage("JUnit test push message");
		pusher.pushMessage(messages);
		assertThat(log.getRenderedMessage(), is("Attempting to push " + messages.length+" messages to " + userId));
	}
	
	@Test
	public void testPushTextShorthand() {
		System.out.println("push text shorthand");
		String tempString = "JUnit test push text shorthand"
		assertThat(log.getRenderedMessage(), is("Attempting to push" + tempString.length+" texts to " + userId));
	}
}
