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

public class ProjectSearchController {
	private SQLDatabaseEngine db = new SQLDatabaseEngine();
	
	public String replyType;
	public String replyText;
	public CarouselTemplate replyCarousel;
	
	public ProjectSearchController() {}
	
	public void process(String text, String state, SearchState searchState) {
//		String startDate = searchState.startDate;
//		String endDate = searchState.endDate;
//		LinkedList<String> keywords = searchState.keywords;
//		ArrayList<ArrayList<String>> rs = searchState.rs;
//		String substate = searchState.substate;
		
		try {
			//enter keyword & update
			if (searchState.substate.equals("keywordInput")) {
				searchState.keywords.add(text);
				searchState.rs = db.searchTourByDesc(searchState.keywords, searchState.startDate, searchState.endDate);
				if (searchState.rs.size() > 0) {
					replyType = "carousel";
					searchState.rsIndex = 0;
					createSearchCarousel(searchState);
				} else {
					replyType = "text";
					replyText = "Sorry, it appears we do not have any available tours that match your criteria. Try changing your date range or reset your filters using the Reset filters button or by typing:\n Reset filters";
				}
				return;
			}
			
			//show tour details
			if (text.toLowerCase().contains("show details of ")) {
				replyType = "text";
				ArrayList<ArrayList<String>> temp = db.searchTourID(text.toLowerCase().replace("show details of ", ""));
				replyText = temp.get(0).get(1) + "\n";
				replyText += "Duration: " + temp.get(0).get(3) + " days\n";
				replyText += "-" + temp.get(0).get(2).replace(" * ", "\n-");
				return;
			}
			
			//show dates of tour
			if (text.toLowerCase().contains("show dates of ")) {
				replyType = "text";
				ArrayList<ArrayList<String>> temp = db.searchTourID(text.toLowerCase().replace("show dates of ", ""));
				replyText = temp.get(0).get(1);
				temp = db.searchTourOfferingID(text.toLowerCase().replace("show dates of ", ""));
				replyText += " (" + temp.get(0).get(0) + ")";
				
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate localDate = LocalDate.now().plusDays(3);
				String start = dtf.format(localDate);
				
				if (temp.size() > 0) {
					boolean hasrecords = false;
					for (ArrayList<String> str : temp) {
						if (str.get(2).compareTo(start) > 0) {
							replyText += "\n-" + str.get(2);
							hasrecords = true;
						}
					}
					if (!hasrecords) {
						replyText += " has no available tours!";
					}
					
				} else {
					replyText += " has no available tours!";
				}
				return;
			}
			
			//move to enter keyword state
			if (text.toLowerCase().contains("add filter")) {
				replyType = "text";
				replyText = "Please enter a keyword:";
				state = "keywordInput";
				return;
			} 
			
			//enter date & update
			if (text.toLowerCase().matches("(.*)(\\d{4})-(\\d{1,2})-(\\d{1,2}) to (\\d{4})-(\\d{1,2})-(\\d{1,2})(.*)")) {
				searchState.startDate = text.substring(0,10);
				searchState.endDate = text.substring(14,24);
				searchState.rs = db.searchTourByDesc(searchState.keywords, searchState.startDate, searchState.endDate);
				if (searchState.rs.size() > 0) {
					replyType = "carousel";
					searchState.rsIndex = 0;
					createSearchCarousel(searchState);
				} else {
					replyType = "text";
					replyText = "Sorry, it appears we do not have any available tours that match your criteria. Try changing your date range or reset your filters using the Reset filters button or by typing:\n Reset filters";
				}
				return;
			}
			
			//show next 5
			if (text.toLowerCase().contains("next")) {
				if (searchState.rsIndex == searchState.rs.size()) {
					replyType = "text";
					replyText = "There are no more records!";
				} else {
					replyType = "carousel";
					createSearchCarousel(searchState);
				}
				return;
			}
			
			if (text.toLowerCase().equals(".back") && state.equals("book")) {
				replyType = "carousel";
				searchState.rsIndex = 0;
				createSearchCarousel(searchState);
				return;
			}
			
			//show prev 5
			if (text.toLowerCase().contains("previous") || text.toLowerCase().contains("back") || text.toLowerCase().contains("last")) {
				if (searchState.rsIndex <= 5) {
					replyType = "text";
					replyText = "You are at the start and there are no previous results.";
				} else {
					replyType = "carousel";
					searchState.rsIndex = ((searchState.rsIndex-1)/5-1)*5;	//get current 5 (or less), move to previous 5, find index
					createSearchCarousel(searchState);
				}
				return;
			}
			
			//new search
			if ((searchState.keywords.isEmpty() && text.toLowerCase().contains("search")) || text.toLowerCase().contains("reset filters")) {
				replyType = "carousel";
				searchState.keywords.clear();
				searchState.resetDates();
				searchState.rs = db.searchAllTour();
				searchState.rsIndex = 0;
				createSearchCarousel(searchState);
//				replyType = "text";
//				replyText = "record count: " + searchState.rs.size();
				return;
			}
			
			
			//unknown
			replyType = "text";
			replyText = "Sorry, I didn't understand that.";
		} catch (URISyntaxException e) {
			replyType = "text";
			replyText = "URI Syntax problem with URI";
			state = "error";
		} catch (SQLException e) {
			replyType = "text";
			replyText = "Searching tours by description failed!";
			state = "error";
		}
		
		
	}
	
	//CREATES A CAROUSEL OBJECT
	//1ST column is a menu
	//2 onwards is up to 5 results from rs
	//Last column is show more menu
	public void createSearchCarousel(SearchState searchState) { //int rsIndex, ArrayList<ArrayList<String>> rs, LinkedList<String> keywords) {
		List<CarouselColumn> columns = new LinkedList<CarouselColumn>();
		
		//dummy
		columns.add(
			new CarouselColumn(
				"https://images-na.ssl-images-amazon.com/images/I/61G%2BdmtkeeL._SX355_.jpg",
				"DUMMY",
				"DUMMY", 
				Arrays.asList(
	                new PostbackAction("DUMMY", "DUMMY"),
	                new PostbackAction("DUMMY", "DUMMY"),
	                new PostbackAction("DUMMY", "DUMMY")
	            )
			)
		);
		
		//content
		for (int i=searchState.rsIndex, max=searchState.rsIndex+5; i<max && i<searchState.rs.size(); i++) {
			columns.add(
				new CarouselColumn(
					"https://www.scienceabc.com/wp-content/uploads/2017/02/Thailand-beach-sand.jpg",
					searchState.rs.get(i).get(1).length() > 40 ? 
						searchState.rs.get(i).get(1).substring(0, 35) + "..." : 
						searchState.rs.get(i).get(1),
					searchState.rs.get(i).get(2).replace(" * ",", ").length() > 55 ?
						searchState.rs.get(i).get(2).replace(" * ",", ").substring(0,54) + "...": 
						searchState.rs.get(i).get(2).replace(" * ",", "),
					Arrays.asList(
						new MessageAction("Details", "Show details of " + searchState.rs.get(i).get(0)),
	                    new MessageAction("Show dates", "Show dates of " + searchState.rs.get(i).get(0)),
	                    new MessageAction("Book", "Book " + searchState.rs.get(i).get(0))
                    )
				)
			);
			searchState.rsIndex ++;
		}
		
		//1st column (info)
		String info = "To search for tours within a period, enter the start and end dates in the following format:\nYYYY-MM-DD to YYYY-MM-DD\n\nCurrently showing:\n"
				+ (((searchState.rsIndex-1)/5)*5+1) + "-"  + searchState.rsIndex + " of " + searchState.rs.size() + " results\n\nKeywords: ";
		for (String str : searchState.keywords) {
			info += str + ", ";
		}
		info += "\n\n";
		info += "Start Date: " + searchState.startDate + "\nEnd Date: " + searchState.endDate;
		columns.set(0,
			new CarouselColumn(
				"https://images-na.ssl-images-amazon.com/images/I/61G%2BdmtkeeL._SX355_.jpg",
				"Tour Search",	//TODO: add "current keywords" and dates into text
				"Swipe left to see search results or press the buttons below.", 
				Arrays.asList(
	                new PostbackAction("Guide", info),
	                new MessageAction("Add a filter", "Add filter"),
	                new MessageAction("Reset filters", "Reset filters")
	            )
			)
		);
		
		//last column (next, prev)
		columns.add(
            new CarouselColumn(
            	"https://thefrailestthing.files.wordpress.com/2010/07/ellipsis.png",
        		"Check out more",
        		"To see more results, press the buttons below.", 
                Arrays.asList(
            		searchState.rsIndex == searchState.rs.size() ?
                		new PostbackAction("(end of results)","There are no more results!"):
                		new MessageAction("Show next 5", "Next 5"),
            		searchState.rsIndex <= 5 ?
                		new PostbackAction("(start of results)","There are no results before these!"):
                		new MessageAction("Show previous 5", "Previous 5"),
                    new MessageAction("Back to welcome", "Cancel")
                )
            )
        );
	
	    replyCarousel = new CarouselTemplate(columns);
	}
}