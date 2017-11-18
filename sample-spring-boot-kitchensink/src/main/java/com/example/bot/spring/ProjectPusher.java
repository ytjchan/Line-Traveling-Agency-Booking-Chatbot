package com.example.bot.spring;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
public class ProjectPusher {
	private String userId;
	private TextMessage textMessage;
    private PushMessage pushMessage;
	
	public ProjectPusher(String userId, String textMessage) {
		this.userId = userId;
		
		log.info("Attempting to send timeout message to user " + userId);
		this.textMessage = new TextMessage(textMessage);
		this.pushMessage = new PushMessage(userId, this.textMessage);
		
		pushText();
	}
	
	private void pushText() {
		Response<BotApiResponse> response;
		try {
			response = LineMessagingServiceBuilder
					.create(System.getenv("LINE_BOT_CHANNEL_TOKEN"))
					.build()
					.pushMessage(pushMessage)
					.execute();
		} catch (IOException e) {
            log.info(e.toString());
		}
	}
	
}
