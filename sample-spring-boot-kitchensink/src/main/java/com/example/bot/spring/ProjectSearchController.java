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

import java.sql.*;
import java.util.LinkedList;



public class ProjectSearchController {
	public ResultSet rs;
	private SQLDatabaseEngine db;
	private PreparedStatement sql;
	
	public LinkedList<String> keywords = new LinkedList<String>();
	public String startDate = "0000-01-01";	
	public String endDate 	= "9999-12-31";		//must be YYYY-MM-DD
	
	public String state = "display";
	public int currentOffset = 0;
	
	public String replyType;
	public String replyText;
	public CarouselTemplate replyCarousel;
	public String[] imgs;
	public String[] uris;
	public String[] baseUris = new String[2];
	
	public ProjectSearchController() {	
		baseUris[0] = KitchenSinkController.createUri(ProjectInterface.IMAGE_NAMES[3]);
		baseUris[1] = KitchenSinkController.createUri(ProjectInterface.IMAGE_NAMES[4]);
	}
	
	public void process(String text) {
		if (keywords.isEmpty() || text.toLowerCase().equals("reset")) {
			//new search
			try {
				replyType = "carousel";
				state = "display";
				keywords.clear();
				startDate = "0000-01-01";	//must be YYYY-MM-DD
				endDate = "9999-12-31";		//must be YYYY-MM-DD
				Connection c = db.getConnection();
				sql = c.prepareStatement("SELECT * from tour");
				rs = sql.executeQuery();
				//update images
				rs.last();
				imgs = new String[rs.getRow()];
				rs.beforeFirst();

				for (String str : imgs) {
					str = ProjectInterface.IMAGE_NAMES[2];
				}
				uris = new String[imgs.length];
				for (int i=0; i<imgs.length; i++) {
					uris[i] = KitchenSinkController.createUri(imgs[i]);
				}
				//do carousel
				createSearchCarousel();
			} catch (Exception e) {
				replyType = "text";
				replyText = "Oh no! An error occurred while trying to display the initial tour search results!";
				state = "error";
			}
		} else if (state.equals("keywordInput")) {
			try {
				keywords.add(text);
				state = "display";
				Connection c = db.getConnection();
				sql = c.prepareStatement("SELECT * from tour");
				rs = sql.executeQuery();
				
				if (rs.first()) {
					//there are rows
					//do carousel
					replyType = "carousel";
					createSearchCarousel();
				} else {
					//there are no rows
					//inform user, return to old dates
					replyType = "text";
					replyText = "Sorry, we don't have any tours with your current combination of filters.";
					keywords.pollLast();
				}
			} catch (Exception e) {
				replyType = "text";
				replyText = "Oh no! An error occurred while trying to display the filtered tour search results!";
				state = "error";
			}
		} else if (text.toLowerCase().contains("date")) {
			//date input instructions
			replyType = "text";
			replyText = "Please enter the date range in the following format: \n YYYY-MM-DD to YYYY-MM-DD";
			//state = "dateInput";
		} else if (text.toLowerCase().contains("filter")) {
			replyType = "text";
			replyText = "Please enter a keyword:";
			state = "keywordInput";
		} else if (text.toLowerCase().matches("(.*)(\\d{4})-(\\d{1,2})-(\\d{1,2}) to (\\d{4})-(\\d{1,2})-(\\d{1,2})(.*)")) {
			try {
				//YYYY-MM-DD to YYYY-MM-DD
				//000000000011111111112222
				//012345678901234567890123
				state = "display";
				String oldStart = startDate;
				String oldEnd = endDate;
				startDate = text.substring(0,9);
				endDate = text.substring(14,23);
				
				Connection c = db.getConnection();
				sql = c.prepareStatement("SELECT * from tour where tourdate between to_date(?,\'yyyy-mm-dd\') and to_date(?,\'yyyy-mm-dd\')");
				sql.setString(1, startDate);
				sql.setString(2, endDate);
				rs = sql.executeQuery();
				
				if (rs.first()) {
					//there are rows
					//do carousel
					replyType = "carousel";
					createSearchCarousel();
				} else {
					//there are no rows
					//inform user, return to old dates
					replyType = "text";
					replyText = "Sorry, we don't have any tours between those dates. Please check if your dates are valid.";
				}
			} catch (Exception e) {
				replyType = "text";
				replyText = "Oh no! An error occurred while trying to display the tour search results with a different date!";
				state = "error";
			}
		} else if (text.toLowerCase().contains("check")) {
			try {
				if (!rs.isLast()) {
					replyType = "carousel";
					createSearchCarousel();
				} else {
					replyType = "text";
					replyText = "There are no more search results! Please scroll up to review the tours, or say \'reset\' to search for results using different filters.";
				}
			} catch (Exception e) {
				replyType = "text";
				replyText = "Oh no! An error occurred while trying to display more search results!";
				state = "error";
			}
		}
		else {
			//unknown
			replyType = "text";
			replyText = "Sorry, I didn't understand that.";
		}
	}
	
	public void createSearchCarousel() throws Exception {
	    List<CarouselColumn> columns = new LinkedList<CarouselColumn>();
	    columns.add(
    		new CarouselColumn(
				baseUris[0], 
				"Tour Search",	//TODO: add "current keywords" and dates into text
				"Swipe left to see your search results or set the date and add filters with the buttons below.", 
	            Arrays.asList(
	                new PostbackAction("Set the dates", "Set a Date"),
	                new MessageAction("Add a filter", "Filter"),
	                new MessageAction("Reset filters", "Reset")
	            )
            )
	    );
	    int extraOffset = 0;
	    while(rs.next() && (extraOffset < 3)) {
	    	columns.add(
    			new CarouselColumn(
					uris[rs.getRow()],
					rs.getString(1),
					rs.getString(2), 
	                Arrays.asList(
	                    new MessageAction("Show the Dates", "Show the dates of " + rs.getString(0)),
	                    new MessageAction("Book a tour", "Book " + rs.getString(0))
                    )
                )
            );
			extraOffset++;
	    }
	        
	    if(!rs.isLast()) {
	        columns.add(
	            new CarouselColumn(
            		baseUris[1],
            		"Check out more",
            		"To see more results, press the button below.", 
	                Arrays.asList(
	                    new MessageAction("Check out more", "Check out more")
	                )
                )
	        );
	    }
	    replyCarousel = new CarouselTemplate(columns);
	}
}
