/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=UserTestConfig.class, loader=AnnotationConfigContextLoader.class)
/**
 * JUnit Test of User
 */
//FIXME how to fix this: java.lang.IllegalStateException: Failed to load ApplicationContext
public class UserTest {
	
	@Autowired
	private User user;	
	
	public UserTest() {
	}
	
	@Before
	public void setUp() {
		this.user = new User("COMP3111FUN", new UserList());
	}

	/**
	 * Test of update method, of class User.
	 */
	@Test
	public void testUpdate() {
		System.out.println("update");
		user.update();
	}

	/**
	 * Test of updateBuffer method, of class User.
	 */
	@Test
	public void testUpdateBuffer() {
		System.out.println("updateBuffer");
		// Test if messages added are in 
		user.updateBuffer("MESSAGE1");
		user.updateBuffer("MESSAGE2");
		user.updateBuffer("MESSAGE3");
		user.updateBuffer("MESSAGE4");
		user.updateBuffer("MESSAGE5");
		assertEquals(5, user.getBuffer().size());
		assertEquals("MESSAGE1", user.getBuffer().get(0));
		assertEquals("MESSAGE2", user.getBuffer().get(1));
		assertEquals("MESSAGE3", user.getBuffer().get(2));
		assertEquals("MESSAGE4", user.getBuffer().get(3));
		assertEquals("MESSAGE5", user.getBuffer().get(4));
		// Test if first message is removed when 6th is added
		user.updateBuffer("MESSAGE6");
		assertEquals(5, user.getBuffer().size());
		assertEquals("MESSAGE2", user.getBuffer().get(0));
		assertEquals("MESSAGE6", user.getBuffer().get(4));
	}

	/**
	 * Test of getBuffer method, of class User.
	 */
	@Test
	public void testGetBuffer() {
		System.out.println("getBuffer");
		// Test if buffer is empty initially
		assertTrue(user.getBuffer().isEmpty());
		// Test if getBuffer can return the right buffer.
		user.updateBuffer("MESSAGE1");
		assertFalse(user.getBuffer().isEmpty());
		assertEquals("MESSAGE1", user.getBuffer().get(0));
	}

	/**
	 * Test of remove method, of class User.
	 * User should no longer be in the userList.
	 */
	@Test
	public void testRemove() {
		System.out.println("remove");
		user.remove();
	}

	/**
	 * Test of getUserId method, of class User.
	 */
	@Test
	public void testGetUserId() {
		System.out.println("getUserId");
		assertEquals("COMP3111FUN", user.getUserId());
	}

	/**
	 * Test of setState method, of class User.
	 */
	@Test
	public void testSetState() {
		System.out.println("setState");
		user.setState("DEAD THANKS TO PROJECT");
		assertNotEquals("new", user.getState());
		assertEquals("DEAD THANKS TO PROJECT", user.getState());
	}

	/**
	 * Test of getState method, of class User.
	 */
	@Test
	public void testGetState() {
		System.out.println("getState");
		assertEquals("new", user.getState());
	}
	
	/**
	 * Test of TimeoutMessage, the inner class of User.
	 */
	@Test
	public void testTimeoutMessage() {
		User.TimeoutMessage tm = user.new TimeoutMessage();
		assertNotNull(tm);
		tm.run();
		assertFalse(tm.cancel());
	}
	
}
