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
@ContextConfiguration(classes=UserListTestConfig.class, loader=AnnotationConfigContextLoader.class)
/**
 * JUnit Test of User
 */
//FIXME how to fix this: java.lang.IllegalStateException: Failed to load ApplicationContext
public class UserListTest {
	
	@Autowired
	private UserList userList;
	private User user1;
	private User user2;
	private User user3;
	private User user4;
	private User user5;
	
	public UserListTest() {
	}
	
	@Before
	public void setUp() {
		this.userList = new UserList();
		this.user1 = new User("COMP", userList);
		this.user2 = new User("COMP3111", userList);
		this.user3 = new User("COMP3111FUN", userList);
		this.user4 = new User("COMP3111VERYFUN", userList);
		this.user5 = new User("COMP3111VERYSUPERFUN", userList);
		userList.update("COMP");
		userList.update("COMP3111");
		userList.update("COMP3111FUN");
		userList.update("COMP3111VERYFUN");
		userList.update("COMP3111VERYSUPERFUN");
	}

	/**
	 * Test of findUser & isInLList.
	 */
	@Test
	public void testIsInList() {
		System.out.println("find user & is in list");
		//user found
		boolean tempUser1 = userList.isInList("COMP");
		assertTrue(tempUser1);
		
		//user not found
		boolean tempUser2 = userList.isInList("COMP3111NOTFUN");
		assertFalse(tempUser2);
	}
	
	/**
	 * Test of update.
	 */
	@Test
	public void testUpdate() {
		System.out.println("update");
		//update user which already exist
		userList.update("COMP");
		userList.update("COMP3111");
		userList.update("COMP3111FUN");
		userList.update("COMP3111VERYFUN");
		userList.update("COMP3111VERYSUPERFUN");
		
	}
	
	/**
	 * Test of updateBuffer.
	 */
	@Test
	public void testUpdateBuffer() {
		System.out.println("update buffer");
		//user found
		userList.updateBuffer("COMP", "This is a garbage message");
		
		//user not found
		userList.updateBuffer("COMP3111NOTFUN", "This is a garbage message");
	}
	
	/**
	 * Test of getBuffer.
	 */
	@Test
	public void testGetBuffer() {
		System.out.println("get buffer");
		LinkedList<String> bufferString;
		
		//user found
		userList.updateBuffer("COMP", "This is a garbage message");
		bufferString = userList.getBuffer("COMP");
		assertEquals(bufferString.get(0), "This is a garbage message");
		
		//more buffer
		userList.updateBuffer("COMP3111", "This is a garbage message1");
		userList.updateBuffer("COMP3111", "This is a garbage message2");
		userList.updateBuffer("COMP3111", "This is a garbage message3");
		userList.updateBuffer("COMP3111", "This is a garbage message4");
		bufferString = userList.getBuffer("COMP3111");
		assertEquals(bufferString.get(0), "This is a garbage message1");
		assertEquals(bufferString.get(1), "This is a garbage message2");
		assertEquals(bufferString.get(2), "This is a garbage message3");
		assertEquals(bufferString.get(3), "This is a garbage message4");
		
		//user not found
		bufferString = userList.getBuffer("COMP3111NOTFUN");
		assertEquals(bufferString, null);
	}
	
	/**
	 * Test of setState.
	 */
	@Test
	public void testSetState() {
		System.out.println("set state");
		//user found
		userList.setState("COMP", "testing state");
		
		//user not found
		userList.setState("COMP3111NOTFUN", "testing state");
	}
	
	/**
	 * Test of getState.
	 */
	@Test
	public void testGetState() {
		System.out.println("get state");
		String state;
		//user found
		userList.setState("COMP", "testing state");
		state = userList.getState("COMP");
		assertEquals(state, "testing state");
		
		//user not found
		state = userList.getState("COMP3111NOTFUN");
		assertEquals(state, null);
	}
	
	/**
	 * Test of remove with user.
	 */
	@Test
	public void testRemoveWithUser() {
		System.out.println("remove with user");
		
		//user found
		userList.remove(user5);
		userList.remove(user4);
	}
	
	/**
	 * Test of remove with string.
	 */
	@Test
	public void testRemoveWithString() {
		System.out.println("remove with string");
		
		//user found
		userList.remove("COMP");
		userList.remove("COMP3111");
		userList.remove("COMP3111FUN");
		
		//user not found
		userList.remove("COMP3111NOTFUN");
	}
	
	@Test
	public void searchBookGetters() {
		userList.getSearchState("COMP3111");
		userList.getBookState("COMP3111");
		userList.resetSearchState("COMP3111");
		userList.resetBookState("COMP3111");
		
		userList.getSearchState("");
		userList.getBookState("");
		userList.resetSearchState("");
		userList.resetBookState("");
	}
}