package com.example.bot.spring;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
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


public class ProjectInitController {
	private final String [] imgs = ProjectInterface.IMAGE_NAMES;
    public ProjectInitController(){}
	
	public CarouselTemplate createMessage() {
        SQLDatabaseEngine db = new SQLDatabaseEngine();
        CarouselTemplate carouselTemplate = new CarouselTemplate(
    		Arrays.asList(	//uris[1]
        		new CarouselColumn(KitchenSinkController.createUri(imgs[1]), "3111 Travel", "Welcome to 3111 Travel, the leading China travel agency.", 
            		Arrays.asList(
        				new MessageAction("Recommendations", "Recommend me a trip"),
        				new MessageAction("Search for trips", "Search for trips")
    				)
        		),			//uris[2]
                new CarouselColumn(KitchenSinkController.createUri(imgs[2]), "About the chatbot", "Press the buttons, or type cancel at any time to go back.", 
            		Arrays.asList(
	    				new PostbackAction("FAQ Keywords", "Keywords available are: "+db.searchAllKeywords()),
	    				new URIAction("Our website", "https://github.com/khwang0/2017F-COMP3111")
    				)
        		),
                new CarouselColumn("https://images-na.ssl-images-amazon.com/images/I/61G%2BdmtkeeL._SX355_.jpg",
                		"About booking","Press the button, or type enquiry to view your booking record",
                		Arrays.asList(
        	    				new MessageAction("Booking enquiry", "enquiry")
        	    			)
                	)
    		)
		);
        return carouselTemplate;
	}
	
	public String recommendTrip (String userId) {
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		ArrayList<String> rs = new ArrayList<String>();
		String result="";
		
		try {
			rs = db.getRecommendation(userId);
		} catch (URISyntaxException e){
		} catch (SQLException e) {}
		
		if (rs.size()==0)
			return "Sorry, we don't have recommendation for you.";
		else {
			result+="tourId: " + rs.get(0) +"\n";
			result+="tourDate: " + rs.get(2) + "\n";
			result+="hotel: " + rs.get(4) + "\n";
			result+="price: " + rs.get(5); 
		}
		return result;
	}
}
