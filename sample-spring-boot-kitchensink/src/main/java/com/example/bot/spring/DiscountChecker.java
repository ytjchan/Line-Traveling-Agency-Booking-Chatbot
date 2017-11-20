/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bot.spring;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import retrofit2.Response;

/**
 * Class responsible for repeatedly pushing messages to users when there is a Discount available.
 * On construction, it will push one discount messages to all users present in the Booker table.
 * And it will repeat this for every 15 mins.
 * One can use {@link #forceRunDiscountPromotion() forceRunDiscountPromotion()} to force the discount message running prematurely.
 * Or use {@link #startDiscountPromotion() startDiscountPromotion() } and {@link #stopDiscountPromotion() stopDiscountPromotion()} to start or stop the message spam.
 */
@Slf4j
public class DiscountChecker {
	private final Timer discountTimer = new Timer();
	private TimerTask discountPromotion;
	
	private final int FIRST_MESSAGE_DELAY = 1*1000; // first message is shown 1 second after
	private final int PERIOD = 15*60*1000; // repeat every 15mins
	
	/**
	 * Constructor of DiscountChecker.
	 * On construction, it will automatically push a promotion message to all users.
	 * Then the message will be sent every other 15mins.
	 */
	public DiscountChecker() {
		discountPromotion = new DiscountPromotion();
		discountTimer.schedule(discountPromotion, FIRST_MESSAGE_DELAY, PERIOD); 
	}
	
	/**
	 * Manually stops discount pushmessages.
	 * This makes no more promotion messages sent to any users.
	 * Useful if you want the chatbot to rest or prevent spamming for too long.
	 */
	public void stopDiscountPromotion() {
		discountPromotion.cancel();
	}
	
	/**
	 * Manually start discount pushmessages.
	 * This is used if {@link #stopDiscountPromotion() stopDiscountPromotion()} is run and one wants to start the scheduled messages again.
	 */
	public void startDiscountPromotion() {
		discountPromotion.cancel();
		discountPromotion = new DiscountPromotion();
		discountTimer.schedule(discountPromotion, FIRST_MESSAGE_DELAY, PERIOD);
	}
	
	/**
	 * Forces a discount pushmessage to be sent. 
	 * This is used to notify customers of any newer discounts. 
	 */
	public void forceRunDiscountPromotion() {
		discountPromotion.run();
	}
	
	/**
	 * A helper class to help with scheduling messages. 
	 * @see https://docs.oracle.com/javase/7/docs/api/java/util/TimerTask.html
	 */
	class DiscountPromotion extends TimerTask {
		@Override
		/**
		 * Task to run on timer event.
		 * Sends a pushmessage to every user in the booking table, promoting discounts (if any).
		 */
		public void run() {
			log.info("Running scheduled discount check...");
			ArrayList<ArrayList<String>> deals = new ArrayList<>();
			ArrayList<String> userList = new ArrayList<>(); 
			try {
				log.info("Searching for discounts and bookers");
				SQLDatabaseEngine db = new SQLDatabaseEngine();
				deals = db.getDeals();
				userList = db.getBookers();
				String text = "3111 brings you limited-time discounts to the best tours in China!";
				for (int i=0; i < 3 && i < deals.size(); i++) {
					text += "\n" + Math.round(((1-Double.parseDouble(deals.get(i).get(1)))*100)) + "% off our " + deals.get(i).get(0);
				}
				text += "\nCheck out our offers today!";
				
				TextMessage textMessage = new TextMessage(text);
				
				log.info("Found "+userList.size()+" users!");
				
				for (String userId : userList) {
					log.info("Attempting to send discount message to user "+userId);
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					Response<BotApiResponse> response;
					response = LineMessagingServiceBuilder
						.create(System.getenv("LINE_BOT_CHANNEL_TOKEN"))
						.build()
						.pushMessage(pushMessage)
						.execute();
				}
			
			
			} catch (Exception e) {
				log.info("exception in discount promotion!");
			}
			
		}
	}
}
