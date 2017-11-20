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
	
	/**
	 * Constructor for Booking Controller
	 */
	public ProjectBookController() {}
	
	/**
	 * Booking handler called by projectInterface.
	 * Uses replyList to pass reply back to projectInterface. 
	 * @param text text to be processed
	 * @param state current state of chatbot 
	 * @param bookState object that stores all necessary information for processing booking state
	 * @param userId id of current user
	 */
	public void process(String text, String state, BookState bookState, String userId) {
		replyList.clear();
		
		//assuming book command is something like:  *book [id]

		if (bookState.substate.equals("init")) {
			bookState.tourId = text.substring(text.toLowerCase().indexOf("book")+5);
			ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
			try {
				getOfferings(bookState);
				temp = db.searchTourID(bookState.tourId);
				if (temp.size() > 0) {
					
					bookState.tourName = temp.get(0).get(1);
					replyList.add(new TextMessage("Now booking:\n" + bookState.tourName));
					temp = db.searchBookerForLineID(userId);
				} else {
					replyList.add(new TextMessage("I'm afraid we don't have the tour you're looking for. Please enter .back (with the full stop) to return to your search results."));
				}
			} catch (Exception e) {
				replyList.add(new TextMessage("Something went wrong!"));
				bookState.substate = "error";
				return;
			}
			
			if (temp.isEmpty()) {
				//prompt for personal details
				replyList.add(new TextMessage("Since this is your first time booking a tour with us, we require some personal information."));
				replyList.add(new TextMessage("If you would like to go back to tour results, enter .back (including the full stop) anytime."));
				replyList.add(new TextMessage("Please enter your name: "));
				bookState.substate = "name";	//EXPECT NAME RESPONSE
			} else {
				//SELECT AN OFFERING
				try {
					showOfferings(bookState);
					bookState.substate = "selectOffering";
				} catch (Exception e) {
					replyList.add(new TextMessage("showOfferings threw an error!"));
					bookState.substate = "error";
					return;
				}
			}
		} 
		
		
		///////NEW USER SECTION
		else if (bookState.substate.equals("name")) {
			bookState.name = text;
			replyList.add(new TextMessage("Please enter your HKID card number (without the brackets or the extra number): "));
			bookState.substate = "hkid";
		} else if (bookState.substate.equals("hkid")) {
			if (text.length() != 7) {
				replyList.add(new TextMessage("You've entered an invalid HKID card number!"));
				replyList.add(new TextMessage("Please enter your HKID card number (without the brackets or the extra number): "));
			} else {
				bookState.hkid = text;
				replyList.add(new TextMessage("Please enter your age: "));
				bookState.substate = "age";
			}
		} else if (bookState.substate.equals("age")) {
			boolean validInput = true;
			try {
				bookState.age = Integer.parseInt(text);
				if (bookState.age > 200 || bookState.age < 0)
					validInput = false;
			} catch (Exception e) {
				validInput = false;
			}
			
			if (validInput) {
				replyList.add(new TextMessage("Please enter your phone number: "));
				bookState.substate = "phoneno";
			} else {
				replyList.add(new TextMessage("You've entered an invalid age!"));
				replyList.add(new TextMessage("Please enter your age (integer years): "));
			}
		} else if (bookState.substate.equals("phoneno")) {
			boolean validInput = true;
			try {
				bookState.phoneno = Integer.parseInt(text);
				if (bookState.phoneno > 99999999 || bookState.phoneno < 10000000)
					validInput = false;
			} catch (Exception e) {
				validInput = false;
			}
			
			if (validInput) {
				String details = "I will now repeat back your inputted details:";
				details += "\nLineId: " + userId;
				details += "\nName: " + bookState.name;
				details += "\nHKID: " + bookState.hkid;
				details += "\nAge: " + bookState.age;
				details += "\nPhone No: " + bookState.phoneno;
				replyList.add(new TextMessage(details));
				replyList.add(
					new TemplateMessage("Confirm user information", 
						new ConfirmTemplate(
		                    "Confirm?",
		                    new MessageAction("Yes", "Yes"),
		                    new MessageAction("No", "No")
						)
					)
				);
				bookState.substate = "newUserConfirm";
			} else {
				replyList.add(new TextMessage("You've entered an invalid phone number!"));
				replyList.add(new TextMessage("Please enter your phone number (8 digits, without country code): "));
			}
			
		} else if (bookState.substate.equals("newUserConfirm")) {
			if (text.toLowerCase().contains("yes")) {
				//add to records
				try {
					db.inputUserData(userId, bookState.name, bookState.hkid, bookState.age, bookState.phoneno);
				} catch (Exception e) {
					replyList.add(new TextMessage("INSERT FAILED!"));
					bookState.substate = "error";
					return;
				}
				replyList.add(new TextMessage("Your details have been successfully stored in our records. Now we will proceed to your original booking."));
				//SELECT AN OFFERING
				try {
					showOfferings(bookState);
					bookState.substate = "selectOffering";
				} catch (Exception e) {
					replyList.add(new TextMessage("DB error!"));
					bookState.substate = "error";
					return;
				}
			} else if (text.toLowerCase().contains("no")) {
				//repeat process
				replyList.add(new TextMessage("Then let's try again."));
				replyList.add(new TextMessage("If you would like to cancel this booking and back to tour results, enter .back (including the full stop)"));
				replyList.add(new TextMessage("Please enter your name: "));
				bookState.substate = "name";
			} else {
				replyList.add(new TextMessage("Are the details correct? (please enter yes or no)"));
			}
		} 
		////END NEW USER SECTION
		
		
		////BOOKING SECTION
		else if (bookState.substate.equals("selectOffering")) {
			boolean validInput = true;
			try {
				bookState.offeringIndex = Integer.parseInt(text);
				if (bookState.offeringIndex < 0 || bookState.offeringIndex >= bookState.offerings.size())
					validInput = false;
			} catch (NumberFormatException e) {
				validInput = false;
			}
			if (validInput) {
				bookState.offerId = bookState.offerings.get(bookState.offeringIndex).get(1);
				replyList.add(new TextMessage("Please enter the number of adults (12 years or older) in your group: "));
				bookState.substate = "adults";
			} else {
				replyList.add(new TextMessage("You've entered an invalid number!"));
				replyList.add(new TextMessage("Please select a date by entering the [number] next to it: "));
			}
			
			
		} else if (bookState.substate.equals("adults")) {
			
			boolean validInput = true;
			try {
				bookState.adults = Integer.parseInt(text);
				if (bookState.adults < 1)
					validInput = false;
			} catch (NumberFormatException e) {
				validInput = false;
			}
			
			if (validInput) {
				replyList.add(new TextMessage("Please enter the number of children (4 to 11 years) in your group: "));
				bookState.substate = "children";
			} else {
				if (bookState.adults == 0) {
					replyList.add(new TextMessage("Be more responsible! You should have at least 1 adult in your group!"));
				} else {
					replyList.add(new TextMessage("You've entered an invalid number!"));
				}
				replyList.add(new TextMessage("Please enter the number of adults (12 years or older) in your group: "));
			}
			
			
		} else if (bookState.substate.equals("children")) {
			boolean validInput = true;
			try {
				bookState.children = Integer.parseInt(text);
				if (bookState.children < 0)
					validInput = false;
			} catch (NumberFormatException e) {
				validInput = false;
			}
			
			if (validInput) {
				replyList.add(new TextMessage("Please enter the number of toddlers (age 3 or younger) in your group: "));
				bookState.substate = "toddlers";
			} else {
				replyList.add(new TextMessage("You've entered an invalid number!"));
				replyList.add(new TextMessage("Please enter the number of children (age 4 to 11 years) in your group: "));
			}
			
			
		} else if (bookState.substate.equals("toddlers")) {
			boolean validInput = true;
			try {
				bookState.toddlers = Integer.parseInt(text);
				if (bookState.toddlers < 0)
					validInput = false;
			} catch (NumberFormatException e) {
				validInput = false;
			}
			
			if (validInput) {
				replyList.add(new TextMessage("Do you have any special requests? If not, enter none."));
				bookState.substate = "requests";
			} else {
				replyList.add(new TextMessage("You've entered an invalid number!"));
				replyList.add(new TextMessage("Please enter the number of toddlers (age 3 or younger) in your group: "));
			}
			
		} else if (bookState.substate.equals("requests")) {
			int groupCount = bookState.adults+bookState.children+bookState.toddlers;
			int remainingSpaces = 0;
			
			try {
				remainingSpaces = (Integer.parseInt(bookState.offerings.get(bookState.offeringIndex).get(6)) - db.getCurrentBookingCount(bookState.offerId));
			} catch (Exception e) {
				replyList.add(new TextMessage("Error getting remaining spaces from db"));
				bookState.substate = "error";
				return;
			}
			if (groupCount > remainingSpaces) {
				replyList.add(new TextMessage("I'm afraid we don't have enough open spaces on that tour for your group."));
				replyList.add(new TextMessage("Please enter .back (with the full stop) to return to your search results."));
				bookState.substate = "error";
			} else {
				bookState.requests = text;
				double cost = Double.parseDouble(bookState.offerings.get(bookState.offeringIndex).get(5));
				double childCost = cost*0.8;
				
				double adultTotalCost = bookState.adults * cost;
				double childTotalCost = (int)(bookState.children * childCost);
				
				bookState.totalCost = adultTotalCost + childTotalCost;
				//round to 1 d.p.
				bookState.totalCost = (double)((int)(bookState.totalCost * 10))/10;
				
				String temp = "You are booking:\n";
				temp += bookState.tourName + "\n";
				temp += bookState.offerings.get(bookState.offeringIndex).get(2) + " offering\n";
				temp += "\n";
				temp += "Special requests: " + bookState.requests + "\n";
				temp += "\n";
				temp += "Pricing:\n";
				temp += bookState.adults + " adult(s) : $" + adultTotalCost + "\n";
				temp += bookState.children + " child(ren) : $" + childTotalCost + "\n";
				temp += bookState.toddlers + " toddler(s) : free!\n";
				
				//DISCOUNT
				double discount = 1;
				try {
					if (db.checkDiscount(bookState.offerId)) {
						discount = db.getDiscount(bookState.offerId);
						temp += "Discount : " + (int)((1-discount)*100) + "% off!\n";
						double savings = (1-discount) * bookState.totalCost;
						temp += "Savings : -$" + savings + "\n";
						bookState.discounted = true;
						bookState.totalCost *= discount;
						bookState.totalCost = (double)((int)(bookState.totalCost * 10))/10;
					}
				} catch (Exception e) {
					replyList.add(new TextMessage("DISCOUNT QUERY FAILED!"));
					bookState.substate = "error";
					return;
				}
				
				temp += "Total cost: $" + bookState.totalCost + "\n";
				temp += "\n";
				temp += "If everything is correct, would you like to confirm your booking?";
				
				replyList.add(new TextMessage(temp));
				
				replyList.add(
					new TemplateMessage("Confirm user information", 
						new ConfirmTemplate(
		                    "Confirm?",
		                    new MessageAction("Yes", "Yes"),
		                    new MessageAction("No", "No")
						)
					)
				);
				bookState.substate = "bookingConfirm";
			}
		} else if (bookState.substate.equals("bookingConfirm")) {
			if (text.toLowerCase().contains("yes")) {
				//add to records
				try {
					db.inputBooking(userId, bookState.offerId, bookState.adults, bookState.children, bookState.toddlers, bookState.totalCost, bookState.requests);
					if (bookState.discounted) {
						db.decrementDiscount(bookState.offerId);
					}
				} catch (Exception e) {
					replyList.add(new TextMessage("INSERT FAILED!"));
					bookState.substate = "error";
					return;
				}
				replyList.add(new TextMessage("Your booking has been successfully stored in our records."));
				replyList.add(new TextMessage("Enter .back (with the full stop) to return to your search results, or cancel to return to the main menu."));
				bookState.substate = "done";
			} else if (text.toLowerCase().contains("no")) {
				//repeat process
				replyList.add(new TextMessage("Then let's try again."));
				try {
					showOfferings(bookState);
					bookState.substate = "selectOffering";
				} catch (Exception e) {
					replyList.add(new TextMessage("Error trying to show offerings!"));
					bookState.substate = "error";
					return;
				}
				bookState.substate = "selectOffering";
			} else {
				replyList.add(new TextMessage("Are the details correct? (please enter yes or no)"));
			}
			
		} else if (bookState.substate.equals("error")) {
			replyList.add(new TextMessage("Please enter .back (with the full stop) to return to your search results."));
		} else if (bookState.substate.equals("done")) {
			replyList.add(new TextMessage("Enter .back (with the full stop) to return to your search results, or cancel to return to the main menu."));
		}
		//for debugging
		//replyList.add(new TextMessage("SUBSTATE: " + bookState.substate));
	}
	
	/**
	 * Helper function that sets tour offering object in bookState 
	 * @param bookState object that stores all necessary information for processing booking state
	 * @throws Exception
	 */
	private void getOfferings(BookState bookState) throws Exception {
		bookState.offerings = db.searchTourOfferingID(bookState.tourId);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now().plusDays(3);
		String start = dtf.format(localDate);
		for (int i = 0; i < bookState.offerings.size(); i++) {
			if (bookState.offerings.get(i).get(2).compareTo(start) < 0) {
				bookState.offerings.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Helper function that sets a reply to show tour offerings
	 * @param bookState object that stores all necessary information for processing booking state
	 * @throws Exception
	 */
	private void showOfferings(BookState bookState) throws Exception {
		if (bookState.offerings.size() == 0) {
			replyList.add(new TextMessage("I'm afraid we don't have any available offerings for this tour!\nPlease enter .back (with the full stop) to return to your search results."));
		} else {
			String text = "We have available tour offerings on the following dates:\n";
			boolean discounted = false;
			for (int i=0; i<bookState.offerings.size(); i++) {
				text += "\n[" + i + "] " + bookState.offerings.get(i).get(2);
				text += " (" + (Integer.parseInt(bookState.offerings.get(i).get(6)) - db.getCurrentBookingCount(bookState.offerings.get(i).get(1))) + " spaces left)";
				if (db.checkDiscount(bookState.offerings.get(i).get(1))) {
					text += "*";
					discounted = true;
				}
			}
			if (discounted) 
				text += "\nDates with a star * next to them are discounted for a limited time!";
			
			text += "\n\nPlease select a date by entering the [number] next to it.\nEnter .back (with the full stop) to return to search results";
			replyList.add(new TextMessage(text));
		}
	}
}
