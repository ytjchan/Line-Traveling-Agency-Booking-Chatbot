package com.example.bot.spring;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
<<<<<<< HEAD
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
=======
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.response.BotApiResponse;
import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

/**
 * A class designed to push messages actively to users.
 * Each time up to 5 messages of any type can be pushed.
 * For example, 
 * <pre> {@code 
 * ProjectPusher pp = new ProjectPusher(userId);
 * pp.pushText("A1","A2","A3","A4","A5","A6");
 * MessageFactory mf = new MessageFactory();
 * pp.pushMessage(mf.createImageMessage(), mf.createImageMessage());
 * ProjectPusher.pushTextShorthand(event.getSource().getUserId(), "B1","B2","B3","B4","B5","B6");
 * } </pre>
 * would push 5 (A1 to A5) text messages, 2 images, and another 5 (B1 to B5) messages to the user immediately.
 * Notice that A6 and B6 are ignored in case more than 5 messages are created.
 */
@Slf4j
public class ProjectPusher {
	/**
	 * userId of the User that will receive the push message(s).
	 */
	private final String userId;
	
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
	 * Push 1 to 5 text messages to the user specified by userId in constructor.
	 * Each message can push up to 2000 characters.
	 * Thanks to varargs, can pass up to 5 Strings in parameter to push multuple messages in a time.
	 * @see https://developers.line.me/en/docs/messaging-api/reference/#text
	 * @see https://docs.oracle.com/javase/1.5.0/docs/guide/language/varargs.html
	 * @param texts 1 to 5 Strings, each up to 2000 characters.
	 */
	public void pushText(String... texts) {
		log.info("Attempting to push"+texts.length+" texts to " + userId);
		Response<BotApiResponse> response;
		ArrayList<Message> messageList = new ArrayList<>();
		MessageFactory mf = new MessageFactory();
		for (int i=0; i<5 && i<texts.length; i++)
			messageList.add(mf.createTextMessage(texts[i]));
		if (messageList.isEmpty())
			return;
		PushMessage pushMessage = new PushMessage(userId, messageList);
>>>>>>> bf05bcae93abb5b7f4825b48aef7c11f7ba2b9eb
		try {
			response = LineMessagingServiceBuilder
					.create(System.getenv("LINE_BOT_CHANNEL_TOKEN"))
					.build()
					.pushMessage(pushMessage)
					.execute();
		} catch (IOException e) {
<<<<<<< HEAD
            log.info(e.toString());
		}
	}
	
=======
			log.info(e.toString());
		}
	}
	
	/**
	 * Push 1 to 5 Messages (of any subclasses of Message) to the user specified by userId in constructor.
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
	 * Shorthand method to reply 1 to 5 text message on the user specified by userId.
	 * This avoids the need to create a ProjectPush before hand, but only allows one message pushed each time.
	 * Thanks to varargs, can pass up to 5 Strings in parameter to push multuple messages in a time.
	 * @see https://developers.line.me/en/docs/messaging-api/reference/#text
	 * @see https://docs.oracle.com/javase/1.5.0/docs/guide/language/varargs.html
	 * @param userId The userId that should recieve the text message.
	 * @param texts 1 to 5 Strings, each up to 2000 characters.
	 */
	public static void pushTextShorthand(String userId, String... texts) {
		log.info("Attempting to push to " + userId);
		ProjectPusher pp = new ProjectPusher(userId);
		pp.pushText(texts);
	}
>>>>>>> bf05bcae93abb5b7f4825b48aef7c11f7ba2b9eb
}
