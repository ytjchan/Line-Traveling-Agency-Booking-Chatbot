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
		this.pusher = new ProjectPusher(userId, "JUnit test message");
	}
	
	@Test
	public void testPushText() {
		System.out.println("push text");
	}
}
