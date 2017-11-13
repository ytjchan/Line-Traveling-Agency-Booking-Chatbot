package com.example.bot.spring;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
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
import java.net.URISyntaxException;
import java.sql.*;
import java.util.LinkedList;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProjectBookController {
	private SQLDatabaseEngine db = new SQLDatabaseEngine();
	public List<Message> replyList = new LinkedList<Message>();
	
	public ProjectBookController() {
		
	}
	//TODO: define methods to handle bookings
	public String process(String text, String state, String userID) {
		//1. Check if user is in booker table
		//		select * from booker where lineid = ?
		//	A: it is, do nothing
		//	B: it is not, ask for name, hkid, age, phone number
		//
		//2. Ask for no. of adults, no. of children (4 - 11), no. of toddlers (3 or less), special requests]
		//3. throw a confirmation message
		replyList.clear();
		
		//assuming book command is something like:  *book [id]
		String tourID = text.substring(text.toLowerCase().indexOf("book")+5);
		
		
		
		if (state.equals("book.init")) {
			try {
				ArrayList<ArrayList<String>> temp = db.searchBookerForLineID(userID);
			} catch (URISyntaxException e) {
				replyList.add(new TextMessage("URI Syntax problem with URI: " + System.getenv("DATABASE_URL")));
				return "book.error";
			} catch (SQLException e) {
				replyList.add(new TextMessage("Searching tours by description failed!"));
				return "book.error";
			}
			if (temp.isEmpty()) {
				//prompt for personal details
				replyList.add(new TextMessage("Since this is your first time booking a tour with us, we require some personal information."));
				replyList.add(new TextMessage("If you would like to go back to tour results, enter .back (including the full stop)"));
				replyList.add(new TextMessage("Please enter your name: "));
				return "book.name";
			} else {
				//prompt for booking details
				replyList.add(new TextMessage("Since this is your first time booking a tour with us, we require some personal information."));
				replyList.add(new TextMessage("If you would like to go back to tour results, enter .back (including the full stop)"));
				replyList.add(new TextMessage("Please enter your name: "));
			}
		} else if (state.equals("book.name")) {
			
		} else if (state.equals("book.hkid")) {
			
		} else if (state.equals("book.age")) {
			
		} else if (state.equals("book.phoneno")) {
			
		} else if (state.equals("book.adults")) {
			
		} else if (state.equals("book.children")) {
			
		} else if (state.equals("book.toddlers")) {
			
		} else if (state.equals("book.requests")) {
			
		} else if (state.equals("book.confirm")) {
			
		}
		return "book.init";
	}
}
