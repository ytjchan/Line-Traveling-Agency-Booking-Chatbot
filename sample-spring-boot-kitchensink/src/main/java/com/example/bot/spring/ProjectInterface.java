package com.example.bot.spring;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Response;
import java.util.Calendar;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
import retrofit2.Response;

@Slf4j
public class ProjectInterface {
	//TODO define image addresses
	public static final String [] IMAGE_NAMES = {"/static/gather.jpg","/static/gd1.jpg","/static/beach3.jpg","TODO","TODO","TODO","TODO","TODO","TODO","TODO"};
	private final String STAFF_PASSCODE = "LOVE SUNG KIM ADD STAFF";
	public String replyType;		//i.e. text, image, carousel, confirm, unknown
	public String replyText;		//for replyType: text
	public String replyImageAddress;
	public CarouselTemplate replyCarousel;
	public List<Message> replyList;
	
	private ProjectMasterController controller = new ProjectMasterController();
        private final UserList userList;    
	
	private final Timer discountTimer = new Timer();
	private TimerTask discountPromotion;
		
	Message message = null; // placeholder, if all Controllers can return a Message object/a List of Nessage objects after processsing, we can just ask the Controllers return Message to KSC
	
    /**
     * Constructor
     * @param userList List of user objects
     */
	public ProjectInterface(UserList userList) {
        this.userList = userList;
	}
	
	
	// TODO make it return controllers instead of calling them?
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
		
		message = null; // since not all Controllers can return Message object right now
		
		if (text.equals(STAFF_PASSCODE)){
			new SQLDatabaseEngine().addStaff(userId);
		}
		
		String state = userList.getState(userId);
		if (userList.getState(userId).equals("new") || text.toLowerCase().equals("cancel")) {
                        userList.setState(userId, "init");
                        message = controller.init.createMessage();
			replyText = "Carousel message for init state";
			replyType = "carousel";
		} else if (userList.getState(userId).equals("init") && text.toLowerCase().contains("recommend")) {
			replyType = "text";
			replyText = controller.init.recommendTrip(userId);
		
		} else if (checkSearchState(text, userId)) {
			controller.search.process(text, state, userList.getSearchState(userId));
			userList.setState(userId, "search");
			replyType = "mixed";
			replyList = controller.search.replyList;
		} else if (checkBookState(text, userId)) {
			userList.setState(userId, "book");
			controller.book.process(text, userList.getBookState(userId), userId);
			replyType = "mixed";
			replyList = controller.book.replyList;
		} else if (checkEnqState(text,userId)) {
			replyType = "text";
			replyText = controller.enq.bookingEnq(userId);
		} else if (checkFAQ(text)) {
			replyText="FAQ result is:\n"+controller.faq.search(text);
			replyType="text";
		} else {
			replyText = "Sorry, I did not understand: " + text + ". We will relay this message to a staff member who will contact you if your question is valid.";
			replyType = "unknown";
			controller.unknown.handleUnknown(userId, text, userList.getBuffer(userId).toArray(new String[0])); // passes buffer as a String array for easier manipulation
		}
			
	}
	
	/**
	 * Checks if the state is 'search'
	 * Some conditions are checked implicitly by other checks called prior
	 * @param text - Input text received from user
	 * @param userId - ID of user
	 * @return true if it is, false if it isn't
	 */
	public boolean checkSearchState(String text, String userId) {
		SearchState searchState = userList.getSearchState(userId);
		String state = userList.getState(userId);
		if ((state.equals("init") && text.toLowerCase().contains("search"))) {
			userList.resetSearchState(userId);
			searchState.substate = "display";
			return true;
		} else if (state.equals("book") && text.toLowerCase().equals(".back")) { // TODO are you sure you want a dot here?
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
	 * @param userId - ID of user
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
	
	/**
	 * This method take user's text as input and returns a boolean value indicating if this text is a booking enquiry or not.
	 * @param text The text used to decide user's sending a booking enquiry or not
	 * @param userId ID of user
	 * @return bool If true, user is sending a booking enquiry, otherwise is not a booking enquiry.
	 */
	public boolean checkEnqState(String text,String userId) {
		//TODO: check if state is enq
		if (userList.getState(userId).equals("init") && text.toLowerCase().contains("enquiry"))
			return true;
		else
			return false;
		
	}
	
	/**
     * check whether input text contain the keywolds in the FAQ table in database
     * @param text the user input message
     * @return if text contain keywolds whose answer is stored in FAQ table,return true, else return false
     */
    public boolean checkFAQ(String text) {
        //TODO: check if state is faq
        //lookup faq table in database to see if input message matches any stored questions
        //should be accessible from ANY state        
        StringBuilder newsb=new StringBuilder();
        if(controller.faq.search(text).equals(newsb.toString()))
            return false;
        return true;
    }
    
    /**
     * Getter method of all Staff's userId for identifying who is a staff.
     * @return String containing all userIds of staffs, delimited by semicolon (;)
     */
    public String getStaffID() {
    	String id=null;
    	id=controller.unknown.getStaffId();
    	return id;
    }


}
