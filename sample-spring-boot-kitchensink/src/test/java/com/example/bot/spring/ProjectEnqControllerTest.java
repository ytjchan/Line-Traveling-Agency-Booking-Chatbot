package com.example.bot.spring;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Test cases for the class ProjectEnqController.
*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectEnqControllerTest.class, ProjectEnqController.class })

public class ProjectEnqControllerTest {

	@Autowired
	private ProjectEnqController enq;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		enq = new ProjectEnqController();
	}

	@After
	public void tearDown() throws Exception {
		enq = null;
	}
	
	/**
	 * Test if it returns an unexpected result when input userId actually booked no tool.
	 */
	@Test
	public void testBookingEnq() {
		String reply = enq.bookingEnq("nonesense");
		assertNotNull(reply);
		assertEquals("get result for someone not a booker",reply,"You don't have any booking in our database");
	}
	
	/**
	 * test if it returns correct result when input userId has booked some tours.
	 */
	@Test
	public void testBookingEnq2() {
		String reply = enq.bookingEnq("hkjeffer");
		assertNotNull(reply);
		assertFalse(reply.equals("You don't have any booking in our database"));
	}
	

}
