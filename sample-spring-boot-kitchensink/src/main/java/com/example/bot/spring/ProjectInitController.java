package com.example.bot.spring;

import com.linecorp.bot.model.message.Message;

public class ProjectInitController {
	private final String [] imgs = new String[]{"/static/gd1.jpg", "/static/beach3.jpg"};

	public ProjectInitController(){}
	
	public Message createMessage() {
		SQLDatabaseEngine db = new SQLDatabaseEngine();
		MessageFactory mf = new MessageFactory(imgs);
		String[] carouselContents = new String[]{
			"3111 Travel", "Welcome to 3111 Travel, the leading China travel agency.", // column 1 title and descriptions
			"message", "Recommendations", "Recommend me a trip", // button 1 
			"message", "Search for trips", "Search for trips", // button 2
			
			"About the chatbot", "Press the buttons, or type cancel at any time to go back", // column 2 title and descriptions
			"postback", "FAQ keywords", "Keywords available are: "+db.searchAllKeywords(), // button 1
			"uri", "Our website", "https://github.com/khwang0/2017F-COMP3111" // button 2
		};
		Message m = mf.createCarouselMessage(2, 2, carouselContents);
		return m;
	}
}
