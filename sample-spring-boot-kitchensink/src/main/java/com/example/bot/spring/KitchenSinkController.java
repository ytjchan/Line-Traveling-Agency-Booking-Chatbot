/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import com.linecorp.bot.model.profile.UserProfileResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;

import retrofit2.Response;

@Slf4j
@LineMessageHandler
public class KitchenSinkController {
	


	@Autowired
	private LineMessagingClient lineMessagingClient;
    UserList userList = new UserList(); // default access right (no need to pass ksc anymore)
    DiscountChecker discountChecker = new DiscountChecker();
	public ProjectInterface funInterface = new ProjectInterface(userList);

	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		log.info("This is your entry point:");
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		TextMessageContent message = event.getMessage();
                userList.update(event.getSource().getUserId());
		handleTextContent(event.getReplyToken(), event, message);
	}

	@EventMapping
	public void handleFollowEvent(FollowEvent event) {
		String replyToken = event.getReplyToken();
		MessageFactory mf = new MessageFactory("/static/prof.jpg");
		List<Message> messages = new ArrayList<>();
		messages.add(mf.createTextMessage("Welcome to COMP3111! \nTo start your journey, just type in anything. \nTo go back to front page, type 'cancel' at anytime. \nHave a nice trip!")); 
		messages.add(mf.createImageMessage());
		this.reply(replyToken, messages);
	}

	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
                String replyToken = event.getReplyToken();
                if (userList.isInList(event.getSource().getUserId())){
                        this.replyText(replyToken, event.getPostbackContent().getData());
                        userList.update(event.getSource().getUserId()); // on button press eg carousel
                } else { // User has expired
                        this.replyText(replyToken, User.TIMEOUT_TEXT_MESSAGE);
                }
	}


	@EventMapping
	public void handleOtherEvent(Event event) {
		log.info("Received message(Ignored): {}", event);
	}

	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
        
	// removed since ProjectPusher is ready, but thanks for your investigation, Cloud.
	
        // now has package access right
	void replyText(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "..";
		}
		this.reply(replyToken, new TextMessage(message));
	}

	private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
		String text = content.getText();
		String userId = event.getSource().getUserId();
        
		handleTextContent(replyToken, text, userId);
    }
	
	public void handleTextContent(String replyToken, String text, String userId) {
		log.info("Got text message from {}: {}", replyToken, text);
        
        funInterface.process(text, userId);
        //now the replyType of funInterface will change depending on the text & userID
        
		if (funInterface.message != null)
			reply(replyToken, funInterface.message);
	
		if (text.equals("STOP PROMOTION")) {discountChecker.stopDiscountPromotion(); reply(replyToken, new TextMessage("Stopped")); return;}
		if (text.equals("START PROMOTION")) {discountChecker.startDiscountPromotion(); reply(replyToken, new TextMessage("Started")); return;}
		if (text.equals("FORCE PROMOTION")) {discountChecker.forceRunDiscountPromotion(); reply(replyToken, new TextMessage("Forced")); return;}
        
		replyCategory(funInterface.replyType, replyToken);
	}
	
	public void replyCategory(String replyType, String replyToken) {
		switch (replyType) {
			case "text":{
				//test case
				this.replyText(replyToken, funInterface.replyText);
				break;
			}
			case "carousel":{
				//base on funInterface.replyCarousel
				TemplateMessage templateMessage = new TemplateMessage("Welcome to 3111 Travel", funInterface.replyCarousel);
	            this.reply(replyToken, templateMessage);
				break;
			}
			case "unknown":{
				//the message is always the same, e.g. "sorry i did not understand that"
				this.replyText(replyToken, funInterface.replyText);
				
			break;
			}
			case "mixed": {
				this.reply(replyToken, funInterface.replyList);
				break;
			}
			default:
				break;
	    }
	}

	static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	private void system(String... args) {
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		try {
			Process start = processBuilder.start();
			int i = start.waitFor();
			log.info("result: {} =>  {}", Arrays.toString(args), i);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			log.info("Interrupted", e);
			Thread.currentThread().interrupt();
		}
	}


	public KitchenSinkController() {
		database = new SQLDatabaseEngine();
		itscLOGIN = System.getenv("ITSC_LOGIN");
	}

	private SQLDatabaseEngine database;
	private String itscLOGIN;
	

	//The annontation @Value is from the package lombok.Value
	//Basically what it does is to generate constructor and getter for the class below
	//See https://projectlombok.org/features/Value
	@Value
	public static class DownloadedContent {
		Path path;
		String uri;
	}

}
