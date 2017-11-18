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

@Slf4j
public class ProjectInterface {
	//TODO define image addresses
	public static final String [] IMAGE_NAMES = {"/static/gather.jpg","/static/gd1.jpg","/static/beach3.jpg","TODO","TODO","TODO","TODO","TODO","TODO","TODO"};
		
	public String replyType;		//i.e. text, image, carousel, confirm, unknown
	public String replyText;		//for replyType: text
	public String replyImageAddress;
	public CarouselTemplate replyCarousel;
	public List<Message> replyList;
    
	public ProjectMasterController controller = new ProjectMasterController();
        private final KitchenSinkController ksc;
        private final UserList userList; 
	
	Message message = null; // placeholder, if all Controllers can return a Message object/a List of Nessage objects after processsing, we can just ask the Controllers return Message to KSC
	
	public ProjectInterface(KitchenSinkController ksc, UserList userList) {
		this.ksc = ksc;
                this.userList = userList;
	}
	
	//this will change the reply type & reply 
        // need to pass userId in order to distinguish every user
	public void process(String text, String userId) {
                log.info(userList.toString());
		userList.updateBuffer(userId, text);
		
		message = null; // since not all Controllers can return Message object right now
		
		if (userList.getState(userId).equals("new") || text.toLowerCase().equals("cancel")) {
                        userList.setState(userId, "init");
                        message = controller.init.createMessage();
			replyText = "Carousel message for init state";
			replyType = "carousel";
		} else if (checkSearchState(text, userId)) {
			userList.setState(userId, "search");
			controller.search.process(text);
			replyType = controller.search.replyType;
			replyText = controller.search.replyText;
			replyCarousel = controller.search.replyCarousel;
		} else if (checkBookState()) {
			//TODO: call booking controller
		} else if (checkEnqState()) {
			//TODO: call enquiry controller
		} else if (checkFAQ(text)) {
            replyText="FAQ result is:\n"+controller.faq.search(text);
            
            
            replyType="text";

		} else {
			//TODO: call unknown controller
			//find some way to send message to staff, and/or store result in database
			
			replyText = "Sorry, I did not understand: " + text + ". We will relay this message to a staff member who will contact you if your question is valid.";
			replyType = "unknown";
			controller.unknown.handleUnknown(userId, text, userList.getBuffer(userId).toArray(new String[0])); // passes buffer as a String array for easier manipulation
		}
			
	}

	public boolean checkSearchState(String text, String userId) {
		//should be accessible from INIT state (search) or BOOK state (back)
		if ((userList.getState(userId).equals("init") && text.toLowerCase().contains("search")) || userList.getState(userId).equals("book") && text.toLowerCase().contains("back")) {
			controller.search.keywords.clear();
			return true;
		} else if (userList.getState(userId).equals("search") && !text.toLowerCase().contains("book")) {
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
	
	public boolean checkEnqState() {
		//TODO: check if state is enq
		//should be accessible from INIT state ONLY
		
		//for test case, remove when you're actually done
		return false;
	}
	
    public boolean checkFAQ(String text) {
        //TODO: check if state is faq
        //lookup faq table in database to see if input message matches any stored questions
        //should be accessible from ANY state        
        StringBuilder newsb=new StringBuilder();
        if(controller.faq.search(text).equals(newsb.toString()))
            return false;
        return true;
    }
    public String getStaffID() {
    	String id=null;
    	id=controller.unknown.getStaffId();
    	return id;
    }


}
