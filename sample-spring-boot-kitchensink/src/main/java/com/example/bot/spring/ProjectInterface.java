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
    
	private ProjectMasterController controller = new ProjectMasterController();
    private final KitchenSinkController ksc;
    protected final UserList userList; 
	
	public ProjectInterface(KitchenSinkController ksc, UserList userList) {
		this.ksc = ksc;
        this.userList = userList;
	}
	
	/**
	 * Checks what state the user is in and calls appropriate controllers.
	 * Checking is done by calling state-check helper functions.
	 * Changes replyType, replyText, replyCarousel and replyList appropriately.
	 * @param text - Input text received from user
	 * @param userId - The userID of the user
	 */
	public void process(String text, String userId) {
        log.info(userList.toString());
		userList.updateBuffer(userId, text);
		
		String state = userList.getState(userId);
		if (state.equals("new") || text.toLowerCase().equals("cancel")) {
			
            userList.setState(userId, "init");
            userList.resetSearchState(userId);
            userList.resetBookState(userId);
            replyCarousel = controller.init.createMessage();
			replyType = "carousel";
			
		} 
		
		else if (checkSearchState(text,userId)) {
			
			userList.setState(userId, "search");
			controller.search.process(text, state, userList.getSearchState(userId));
			replyType = controller.search.replyType;
			replyText = controller.search.replyText;
			replyCarousel = controller.search.replyCarousel;
			
		} 
		
		else if (checkBookState(text, userId)) {

			userList.setState(userId, "book");
			controller.book.process(text, state, userList.getBookState(userId), userId);
			replyType = "mixed";
			replyList = controller.book.replyList;
		} 
		
		else if (checkEnqState()) {
			//TODO: call enquiry controller
		} 
		
		else if (checkFAQ(text)) {
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

	/**
	 * Checks if the state is 'search'
	 * Some conditions are checked implicitly by other checks called prior
	 * @param text - Input text received from user
	 * @param state - Previous state
	 * @return true if it is, false if it isn't
	 */
	public boolean checkSearchState(String text, String userId) {
		SearchState searchState = userList.getSearchState(userId);
		String state = userList.getState(userId);
		if ((state.equals("init") && text.toLowerCase().contains("search"))) {
			userList.resetSearchState(userId);
			searchState.substate = "display";
			return true;
		} else if (state.equals("book") && text.toLowerCase().equals(".back")) {
			userList.setState(userId, "search");
			searchState.rsIndex = 0;
			searchState.substate = "display";
			return true;
		} else if (state.equals("search") && !searchState.substate.equals("keywordMessage") && text.toLowerCase().contains("add filter")) {
			searchState.substate = "keywordMessage";
			return true;
		} else if (state.equals("search") && searchState.substate.equals("keywordMessage")) {
			searchState.substate = "keywordInput";
			return true;
		} else if (state.equals("search") && !text.toLowerCase().contains("book")) {
			searchState.substate = "display";
			return true;
		} else {
			return false;
		}	
	}
	
	/**
	 * Checks if the state is 'book'
	 * Some conditions are checked implicitly by other checks called prior
	 * @param text - Input text received from user
	 * @param state - Previous state
	 * @return true if it is, false if it isn't
	 */
	public boolean checkBookState(String text, String userId) {
		//TODO: check if state is book
		//should be accessible from SEARCH (result) state ONLY
		//is really
		BookState bookState = userList.getBookState(userId);
		String state = userList.getState(userId);
		if (state.equals("search") && text.toLowerCase().contains("book")) {
            userList.resetBookState(userId);
			return true;
		} else if (state.contains("book") && !text.toLowerCase().contains(".back")) {
			return true;
		} else {
			return false;
		}
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


}
