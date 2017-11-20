/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bot.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Albert
 */
@Configuration
public class UserListTestConfig {
	@Bean
	public UserList userList() {
		UserList userList = new UserList();
		//User user1 = new User("COMP", userList);
		//User user2 = new User("COMP3111", userList);
		//User user3 = new User("COMP3111FUN", userList);
		//User user4 = new User("COMP3111VERYFUN", userList);
		//User user5 = new User("COMP3111VERYSUPERFUN", userList);
		return userList;
	}
	/*
	@Bean
	public User user() {
		return new User("COMP", new UserList());
	}*/
}