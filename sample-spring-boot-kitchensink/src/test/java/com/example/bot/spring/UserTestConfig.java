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
 * @author HKJeffer
 */
@Configuration
public class UserTestConfig {
	@Bean
	public User user() {
		User user = new User("COMP3111FUN", new UserList());
		return user;
	}
}
