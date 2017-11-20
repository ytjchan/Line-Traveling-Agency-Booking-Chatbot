package com.example.bot.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectPusherConfig {
	@Bean
	public ProjectPusher pusher() {
		//TODO: put in userID
		ProjectPusher pusher = new ProjectPusher("U4b3770248bd07a25bd8f37c346483ac8");
		return pusher;
	}
}
