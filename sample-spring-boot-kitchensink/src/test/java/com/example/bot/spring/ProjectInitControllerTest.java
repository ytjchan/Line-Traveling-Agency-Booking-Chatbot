package com.example.bot.spring;

import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for the class ProjectInitController.
 */
public class ProjectInitControllerTest {
	
	ProjectInitController init;
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
		init = new ProjectInitController();
	}
	
	@After
	public void tearDown() {
		init = null;
	}

	@Test
	/**
	 * Test case to test if createMessage() creates a proper template message (which carousel belongs to).
	 */
	public void testCreateMessage() {
		Message m = init.createMessage();
		assertNotNull(m);
		assertTrue(m instanceof TemplateMessage);
	}
}
