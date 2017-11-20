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
	 * Test case to test for input text which is keyword, whether it can return corresponding answer
	 */
	public void testSearch1() {
		String result=faq.search("vegetarian");
		assertNotNull(result);
		//System.out.print("\n\nhello"+result+"!!!!!!!!\n\n");
		assertTrue(result.equals("No"));
	}
	@Test
	/**
	 * Test case to test for input text which is not keyword, whether it return empty string
	 */
	public void testSearch2() {
		String result=faq.search("vegetariansaf");
		assertNotNull(result);
		
		assertTrue(result.length()==0);
	}
}
