package com.example.bot.spring;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
/** User class to store all users that have not timed out (ie 15mins of inactivity). */
public class User {
	
	private final String userId;
	private String state = "new";
	private Instant lastActivityTime;
	private TimerTask timeoutMessage;
	private final Timer timer = new Timer();
	private final UserList userList; // Users subscribe to a UserList
	private final KitchenSinkController ksc; // needed in order to call reply
	private LinkedList<String> buffer = new LinkedList<>();
        
	private final int TIMEOUT_TIME = 15*60*1000; // in milliseconds
	public final static String TIMEOUT_TEXT_MESSAGE = "*You have been in inactivity for 15mins, please restart by typing anything new*";
	
	//the following variables are for handling the search state
	private SearchState searchState = new SearchState(); 
	
	//the following variables are for handling the booking state
	private BookState bookState = new BookState();
	
	
    /**
     * Constructor of an User.
	 * References to UserList and controller is required in order to remove User from UserList and schedule a timeout message.
     * @param userId userId referring to user's line account
     * @param userList A UserList object that the User registers to
     * @param ksc KitchenSinkController to start a reply on schedule
     */
	public User(String userId, UserList userList, KitchenSinkController ksc) {
		this.userId = userId;
		this.ksc = ksc;
        this.userList = userList;
        timeoutMessage = new TimeoutMessage();
        timer.schedule(timeoutMessage, TIMEOUT_TIME);
        log.info("New user of userId "+userId+" created.");
	}
	
    /**
     * Update the user to renew his/her timeout time.
	 * His/her next timeout message would occur 15mins after this has been run.
	 * S/he would be removed from UserList 15mins after as well.
     */
	public void update() {
        lastActivityTime = Instant.now();
        timeoutMessage.cancel();
        timeoutMessage = new TimeoutMessage();
        timer.schedule(timeoutMessage, TIMEOUT_TIME); // reschedule the timeout message
        log.info("Timeout renewed for user "+userId);
    }
        
	/**
	 * Put a String into buffer, if buffer exceeds maximum size 5, remove the oldest one.
	 * @param text String to be put into buffer
	 */
    public void updateBuffer(String text){
        buffer.add(text);
		if (buffer.size() > 5)
			buffer.poll();
    }
        
	/**
	 * Getter method to get the whole buffer for further usage.
	 * The LinkedList is guaranteed to contain 5 String objects.
	 * @return A LinkedList of String containing the last 5 messages
	 */
    public LinkedList<String> getBuffer(){
        return buffer;
    }
        
    /**
     * User removes itself from UserList.
     */
    public void remove() {
        timer.cancel();
        userList.remove(this);
    }
        
    /**
     * Getter method of userId.
     * @return userId of this User
     */
    public String getUserId() {
            return userId;
    }
    
    /**
     * Mutator method of state.
	 * Set the state of this User object to another one.
     * @param state State to be set
     */
    public void setState(String state) {
        this.state = state;
    }
        
    /**
     * Getter method of current state.
     * @return Current state this user is in.
     */
    public String getState() {
        return state;
    }
	
	/** 
     * Helper class to generate a timeout message.
     * Generates a timeout message and remove the user itself from UserList.
     */
	class TimeoutMessage extends TimerTask {

		@Override
		/**
		 * Overriding TimerTask so that run() would be run on schedule.
		 */
		public void run() {
            log.info("Attempting to send timeout message to user "+userId);
            // The following can be potentially replaced by a class.
            TextMessage textMessage = new TextMessage(TIMEOUT_TEXT_MESSAGE);
            PushMessage pushMessage = new PushMessage(userId, textMessage);
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
            remove();
		}
		
	}
	
	
	//////////////searchstate remake
	
	public SearchState getSearchState() {
		return searchState;
	}
	
	public BookState getBookState() {
		return bookState;
	}
}