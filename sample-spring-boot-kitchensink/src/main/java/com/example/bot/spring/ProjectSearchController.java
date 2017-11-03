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
	
	public LinkedList<String> keywords = new LinkedList<String>();
	public String startDate;	
	public String endDate 	= "9999-12-31";		//must be YYYY-MM-DD
	
	public String state = "display";
	public int currentOffset = 0;
	
	public String replyType;
	public String replyText;
	public CarouselTemplate replyCarousel;

//	private static String[] imgs;
//	private static String[] uris;
//	private static String[] baseUris = new String[2];
	
	public ArrayList<ArrayList<String>> rs;
	public int rsIndex = 0;
	
	public ProjectSearchController() {
//		baseUris[0] = KitchenSinkController.createUri(ProjectInterface.IMAGE_NAMES[3]);
//		baseUris[1] = KitchenSinkController.createUri(ProjectInterface.IMAGE_NAMES[4]);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		startDate = dtf.format(localDate);
	}
	
	public void process(String text) {
		try {
			//testing
			if (text.toLowerCase().contains("keywords")) {
				replyType = "text";
				replyText = "keywords: ";
				for (String str : keywords) {
					replyText += str + ", ";
				}
				return;
			}
			 
			if (state.equals("keywordInput")) {
				keywords.add(text);
				state = "display";
				rs = db.searchTourByDesc(keywords, startDate, endDate);
				if (rs.size() > 0) {
					replyType = "carousel";
					rsIndex = 0;
					createSearchCarousel();
				} else {
					replyType = "text";
					replyText = "Sorry, it appears we do not have any available tours that match your criteria. Try changing your date range or reset your filters using the Reset filters button or by typing:\n Reset filters";
				}
				return;
			}
			
			//show tour details
			if (text.toLowerCase().contains("show details of ")) {
				state = "display";
				replyType = "text";
				ArrayList<ArrayList<String>> temp = db.searchTourID(text.toLowerCase().replace("show details of ", ""), "tour");
				replyText = temp.get(0).get(1) + "\n";
				replyText += "Duration: " + temp.get(0).get(3) + " days\n";
				replyText += "-" + temp.get(0).get(2).replace(" * ", "\n-");
				return;
			}
			
			if (text.toLowerCase().contains("show dates of ")) {
				state = "display";
				replyType = "text";
				ArrayList<ArrayList<String>> temp = db.searchTourID(text.toLowerCase().replace("show dates of ", ""), "tour");
				replyText = temp.get(0).get(1);
				temp = db.searchTourID(text.toLowerCase().replace("show dates of ", ""), "touroffering");
				replyText += " (" + temp.get(0).get(1) + ")";
				if (temp.size() > 0) {
					for (ArrayList<String> str : temp) {
						replyText += "\n-" + str.get(2);
					}
				} else {
					replyText += " has no available tours!";
				}
				return;
			}
			
			if (text.toLowerCase().contains("add filter")) {
				replyType = "text";
				replyText = "Please enter a keyword:";
				state = "keywordInput";
				return;
			} 
			if (text.toLowerCase().matches("(.*)(\\d{4})-(\\d{1,2})-(\\d{1,2}) to (\\d{4})-(\\d{1,2})-(\\d{1,2})(.*)")) {
				state = "display";
				//replyType = "carousel";
				startDate = text.substring(0,10);
				endDate = text.substring(14,24);
				rs = db.searchTourByDesc(keywords, startDate, endDate);
				if (rs.size() > 0) {
					replyType = "carousel";
					rsIndex = 0;
					createSearchCarousel();
				} else {
					replyType = "text";
					replyText = "Sorry, it appears we do not have any available tours that match your criteria. Try changing your date range or reset your filters using the Reset filters button or by typing:\n Reset filters";
				}
				return;
			}
			if (text.toLowerCase().contains("next")) {
				//show next 5
				state = "display";
				if (rsIndex == rs.size()) {
					replyType = "text";
					replyText = "There are no more records!";
				} else {
					replyType = "carousel";
					createSearchCarousel();
				}
				return;
			}
			if (text.toLowerCase().contains("previous") || text.toLowerCase().contains("back") || text.toLowerCase().contains("last")) {
				//show previous 5
				state = "display";
				if (rsIndex <= 5) {
					replyType = "text";
					replyText = "You are at the start and there are no previous results.";
				} else {
					replyType = "carousel";
					rsIndex = ((rsIndex-1)/5-1)*5;	//get current 5 (or less), move to previous 5, find index
					createSearchCarousel();
				}
				return;
			}
			if ((keywords.isEmpty() && text.toLowerCase().contains("search")) || text.toLowerCase().contains("reset filters")) {
				//new search
				replyType = "carousel";
				state = "display";
				keywords.clear();
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate localDate = LocalDate.now();
				startDate = dtf.format(localDate);
				endDate = "9999-12-31";		//must be YYYY-MM-DD
				rs = db.searchAllTour();
				rsIndex = 0;
				createSearchCarousel();
				return;
			}
			
			
			//unknown
			replyType = "text";
			replyText = "Sorry, I didn't understand that.";
		} catch (URISyntaxException e) {
			replyType = "text";
			replyText = "URI Syntax problem with URI: " + System.getenv("DATABASE_URL");
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
	public void createSearchCarousel() {
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
		
		
		for (int i=rsIndex, max=rsIndex+5; i<max && i<rs.size(); i++) {
			columns.add(
				new CarouselColumn(
					"https://www.scienceabc.com/wp-content/uploads/2017/02/Thailand-beach-sand.jpg",
					rs.get(i).get(1).length() > 40 ? 
						rs.get(i).get(1).substring(0, 35) + "..." : 
						rs.get(i).get(1),
					//temp[1],
					rs.get(i).get(2).replace(" * ",", ").length() > 55 ?
						rs.get(i).get(2).replace(" * ",", ").substring(0,54) + "...": 
						rs.get(i).get(2).replace(" * ",", "),
					Arrays.asList(
						new MessageAction("Details", "Show details of " + rs.get(i).get(0)),
	                    new MessageAction("Show dates", "Show dates of " + rs.get(i).get(0)),
	                    new MessageAction("Book", "Book " + rs.get(i).get(0))
                    )
				)
			);
			rsIndex ++;
		}
		String info = "To search for tours within a period, enter the start and end dates in the following format:\nYYYY-MM-DD to YYYY-MM-DD\n\nCurrently showing:\n"
				+ (((rsIndex-1)/5)*5+1) + "-"  + rsIndex + " of " + rs.size() + " results\n\nKeywords: ";
		for (String str : keywords) {
			info += str + ", ";
		}
		info += "\n\n";
		info += "Start Date: " + startDate + "\nEnd Date: " + endDate;
		
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
		
		
		columns.add(
            new CarouselColumn(
            	"https://thefrailestthing.files.wordpress.com/2010/07/ellipsis.png",
        		"Check out more",
        		"To see more results, press the buttons below.", 
                Arrays.asList(
                    rsIndex == rs.size() ?
                		new PostbackAction("(end of results)","There are no more results!"):
                		new MessageAction("Show next 5", "Next 5"),
                    rsIndex <= 5 ?
                		new PostbackAction("(start of results)","There are no results before these!"):
                		new MessageAction("Show previous 5", "Previous 5"),
                    new MessageAction("Back to welcome", "Cancel")
                )
            )
        );
	
	    replyCarousel = new CarouselTemplate(columns);
	}
}