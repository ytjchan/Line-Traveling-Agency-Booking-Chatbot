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

//extra imports
import java.util.LinkedList;

public class ProjectInterface {
	//TODO define image addresses
	public static final String [] IMAGE_NAMES = {"/static/gather.jpg","/static/gd1.jpg","/static/beach3.jpg","TODO","TODO","TODO","TODO","TODO","TODO","TODO"};
	
	public String inputText = "";
	public String state = "init";			//define the state i.e. init, search, book, enq
	public Queue<String> buffer = new LinkedList<String>();	//for unknown case
	public Instant lastMessageTime = Instant.MIN;	//for check initial state
	
	public String replyType;		//i.e. text, image, carousel, confirm, unknown
	public String replyText;		//for replyType: text
	public String replyImageAddress;
	public CarouselTemplate replyCarousel;
	public List<Message> replyList;
    public String userID;           //need user's Line ID to support desired functions
    
	public ProjectMasterController controller = new ProjectMasterController();
	
	public ProjectInterface() {
		
	}
	
	//this will change the reply type & reply 
	public void process(String text) {
		buffer.add(text);
		if (buffer.size() > 5)
			buffer.poll();
		
		if (state=="init" && text.toLowerCase().contains("recommend")) {
			replyType = "text";
			replyText = controller.init.recommendTrip(userID);
		} else if (checkInitState(text)) {
			state = "init";
			replyCarousel = controller.init.createMessage();
			replyText = "Carousel message for init state";
			replyType = "carousel";
		} else if (checkSearchState(text)) {
			state = "search";
			controller.search.process(text);
			replyType = controller.search.replyType;
			replyText = controller.search.replyText;
			replyCarousel = controller.search.replyCarousel;
		} else if (checkBookState()) {
			//TODO: call booking controller
		} else if (checkEnqState(text)) {
			replyType = "text";
			replyText = controller.enq.bookingEnq(userID);
		} else if (checkFAQ()) {
            replyText="FAQ result is:\n"+controller.faq.search(text);
            replyType="text";

		} else {
			//TODO: call unknown controller
			//find some way to send message to staff, and/or store result in database
			
			replyText = "Sorry, I did not understand: " + text + ". We will relay this message to a staff member who will contact you if your question is valid.";
			replyType = "unknown";
			controller.unknown.handleUnknown(this.userID, text, buffer.toArray(new String[0])); // passes buffer as a String array for easier manipulation
		}
			
	}
    
    public void setUserID (String userId) {
        this.userID = userId;
    }
	
	public boolean checkInitState(String text) {
		//check if 15 minutes have passed since last message from user
		//should be accessible from ANY state, after 15 minutes or 'cancel' statement
		boolean flag = lastMessageTime.plusSeconds(900).isBefore(Instant.now()) || text.toLowerCase().equals("cancel");
                
		lastMessageTime = Instant.now();
		return flag;
		//for test case, remove when you're actually done
		//return false;
	}
	
	public boolean checkSearchState(String text) {
		//should be accessible from INIT state (search) or BOOK state (back)
		if ((state.equals("init") && text.toLowerCase().contains("search")) || state.equals("book") && text.toLowerCase().contains("back")) {
			controller.search.keywords.clear();
			return true;
		} else if (state.equals("search") && !text.toLowerCase().contains("book")) {
			return true;
		} else {
			return false;
		}	
	}
	
	public boolean checkBookState() {
		//TODO: check if state is book
		//should be accessible from SEARCH (result) state ONLY
		
		//for test case, remove when you're actually done
		return false;
	}
	
	/**
	 * This method take user's text as input and returns a boolean value indicating if this text is a booking enquiry or not.
	 * @param text The text used to decide user's sending a booking enquiry or not
	 * @return bool If true, user is sending a booking enquiry, otherwise is not a booking enquiry.
	 */
	public boolean checkEnqState(String text) {
		//TODO: check if state is enq
		if (state == "init" && text.toLowerCase().contains("enquiry"))
			return true;
		else
			return false;
		
	}
	
    public boolean checkFAQ() {
        //TODO: check if state is faq
        //lookup faq table in database to see if input message matches any stored questions
        //should be accessible from ANY state
        String text=null;
        for(int i=0;i<buffer.size();i++) {
            text=buffer.poll();
            buffer.add(text);
        }
        
        StringBuilder newsb=new StringBuilder();
        if(controller.faq.search(text).equals(newsb.toString()))
            return false;
        return true;
    }


}
