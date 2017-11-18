package com.example.bot.spring;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
public class ProjectPusher {
	/**
	 * userId of the User that will receive the push message(s).
	 */
	private String userId;
	
	/**
	 * Constructor of ProjectPusher.
	 * It requires a userId as parameter to specify which user to push to.
	 * Unlike reply() of {@link com.example.bot.spring.KitchenSinkController KitchenSinkController}, this can push at anytime regardless of replyToken. 
	 * @param userId The userId of the user that we want to push the message to.
	 */
	public ProjectPusher(String userId) {
		this.userId = userId;		
	}
	
	/**
	 * Push a text message to the user specified by userId in constructor.
	 * This can push up to 2000 characters in a message.
	 * @see https://developers.line.me/en/docs/messaging-api/reference/#text
	 * @param text A String up to 2000 characters.
	 */
	public void pushText(String text) {
		log.info("Attempting to push to " + userId);
		Response<BotApiResponse> response;
		MessageFactory mf = new MessageFactory();
		PushMessage pushMessage = new PushMessage(userId, mf.createTextMessage(text));
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
	
	/**
	 * Push a Message (of any subclasses of Message) to the user specified by userId in constructor.
	 * This pushes generic kind of Message by exploiting polymorphism.
	 * Can utilize {@link com.example.bot.spring.MessageFactory MessageFactory} to create desired type of Message object.
	 * Thanks to varargs, can pass up to 5 messages or only 1 each time.
	 * For example, can {@code .pushMessage(message1)} and also {@code .pushMessage(message1, message2, message3, message4, message5)}.
	 * @see https://developers.line.me/en/docs/messaging-api/reference/#send-push-message
	 * @see https://docs.oracle.com/javase/1.5.0/docs/guide/language/varargs.html
	 * @param messages A number of Message object, for example TextMessage, ImageMessage, CarouselMessage, etc.
	 */
	public void pushMessage(Message... messages) {
		log.info("Attempting to push "+messages.length+" messages to " + userId);
		Response<BotApiResponse> response;
		ArrayList<Message> messageList = new ArrayList<>();
		for (int i=0; i<5 && i<messages.length; i++)
			messageList.add(messages[i]);
		if (messageList.isEmpty())
			return;
		PushMessage pushMessage = new PushMessage(userId, messageList);
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
	
	/**
	 * Shorthand method to reply a text message on the user specified by userId.
	 * This avoids the need to create a ProjectPush before hand, but only allows one message pushed each time.
	 * @param userId The userId that should recieve the text message.
	 * @see https://developers.line.me/en/docs/messaging-api/reference/#text
	 * @param text A String up to 2000 characters.
	 */
	public static void pushMessage(String userId, String text) {
		log.info("Attempting to push to " + userId);
		ProjectPusher pp = new ProjectPusher(userId);
		pp.pushText(text);
	}
}
