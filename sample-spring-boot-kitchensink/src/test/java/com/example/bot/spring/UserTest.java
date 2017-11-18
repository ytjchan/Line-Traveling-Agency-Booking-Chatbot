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
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { User.class, UserTest.class, UserList.class})

/**
 * JUnit Test of User
 */
public class UserTest {
	
	@Autowired
	User user;
	
	@Autowired
	UserList
	
	public UserTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
		
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
		user = new User(, )
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of update method, of class User.
	 */
	@Test
	public void testUpdate() {
		System.out.println("update");
		User instance = null;
		instance.update();
		
	}

	/**
	 * Test of updateBuffer method, of class User.
	 */
	@Test
	public void testUpdateBuffer() {
		System.out.println("updateBuffer");
		String text = "";
		User instance = null;
		instance.updateBuffer(text);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getBuffer method, of class User.
	 */
	@Test
	public void testGetBuffer() {
		System.out.println("getBuffer");
		User instance = null;
		LinkedList<String> expResult = null;
		LinkedList<String> result = instance.getBuffer();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of remove method, of class User.
	 */
	@Test
	public void testRemove() {
		System.out.println("remove");
		User instance = null;
		instance.remove();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getUserId method, of class User.
	 */
	@Test
	public void testGetUserId() {
		System.out.println("getUserId");
		User instance = null;
		String expResult = "";
		String result = instance.getUserId();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setState method, of class User.
	 */
	@Test
	public void testSetState() {
		System.out.println("setState");
		String state = "";
		User instance = null;
		instance.setState(state);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getState method, of class User.
	 */
	@Test
	public void testGetState() {
		System.out.println("getState");
		User instance = null;
		String expResult = "";
		String result = instance.getState();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
	
}
