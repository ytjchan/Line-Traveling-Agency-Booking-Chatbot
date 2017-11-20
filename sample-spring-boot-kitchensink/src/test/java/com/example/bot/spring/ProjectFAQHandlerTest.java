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
@SpringBootTest(classes = { ProjectFAQHandlerTest.class, ProjectFAQHandler.class })
public class ProjectFAQHandlerTest {

	@Autowired
	ProjectFAQHandler faq=new ProjectFAQHandler();
	@Test
	/**
	 * Test case to test if createMessage() creates a proper template message (which carousel belongs to).
	 */
	public void testSearch1() {
		String result=faq.search("visa");
		assertNotNull(result);
		
		assertTrue(result.length()==0);
	}
	@Test
	/**
	 * Test case to test if createMessage() creates a proper template message (which carousel belongs to).
	 */
	public void testSearch2() {
		String result=faq.search("vegetariansaf");
		assertNotNull(result);
		
		assertTrue(result.length()==0);
	}
}
