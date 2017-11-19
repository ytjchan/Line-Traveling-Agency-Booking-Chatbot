package com.example.bot.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectPusherConfig {
	@Bean
	public ProjectPusher pusher() {
		ProjectPusher pusher = new ProjectPusher(userId, "JUnit test message");
		return pusher;
	}
}
