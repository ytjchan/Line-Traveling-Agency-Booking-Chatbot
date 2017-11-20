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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MessageFactoryTest.class, MessageFactory.class })
/**
 * Test cases for MessageFactory.
 */
public class MessageFactoryTest {
	
	@Autowired
	private MessageFactory mf;
	
	@Before
	public void setUp() {
		mf = new MessageFactory();
	}
	
	@After
	public void tearDown() {
		mf = null;
	}
	/**
	 * Test of createTextMessage method, of class MessageFactory.
	 * Test if it creates a TextMessage
	 */
	@Test
	public void testCreateTextMessage() {
		System.out.println("createTextMessage");
		Message m = mf.createTextMessage("1");
		assertTrue(m instanceof TextMessage);
		assertTrue(m instanceof Message);
	}

	/**
	 * Test of createCarouselMessage method, of class MessageFactory.
	 * Test if carousel messages work properly.
	 * This tests for 3 different kinds of action buttons and multiple columns shown.
	 */
	@Test
	public void testCreateCarouselMessage() {
		System.out.println("createCarouselMessage");
		Message m = mf.createCarouselMessage(10, 3, new String[]{
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM"
		});
		assertTrue(m instanceof TemplateMessage);
		assertTrue(m instanceof Message);
		// This tset for unexpected amount of buttons
		m = mf.createCarouselMessage(11, 3, new String[]{
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM"
		});
		assertTrue(m instanceof TemplateMessage);
		assertTrue(m instanceof Message);
		
	}

	/**
	 * Test of createImageMessage method, of class MessageFactory.
	 * Test if a ImageMessage is created.
	 */
	@Test
	public void testCreateImageMessage() {
		System.out.println("createImageMessage");
		Message m = mf.createImageMessage();
		assertTrue(m instanceof ImageMessage);
		assertTrue(m instanceof Message);
	}

	/**
	 * Test of createMessage method, of class MessageFactory.
	 * Test if one Message variable is enough all types of Polymorphism.
	 */
	@Test
	public void testCreateMessage() {
		System.out.println("createMessage");
		Message m = mf.createMessage("text", "1");
		assertTrue(m instanceof TextMessage);
		assertTrue(m instanceof Message);
		m = mf.createMessage("carousel", 2, 3,
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM",
			
			"TITLE", "DESCRIPTION",
			"message", "BUTTON NAME", "PARAM",
			"postback", "BUTTON NAME", "PARAM",
			"uri", "BUTTON NAME", "PARAM"
		);
		assertTrue(m instanceof TemplateMessage);
		assertTrue(m instanceof Message);
		m = mf.createMessage("image");
		assertTrue(m instanceof ImageMessage);
		assertTrue(m instanceof Message);
	}
	
}
