package com.example.bot.spring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.net.URISyntaxException;

import com.linecorp.bot.model.message.Message;


public class ProjectInitController {
	private final String [] imgs = new String[]{"/static/gd1.jpg", "/static/beach3.jpg", "/static/prof.jpg"};

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
			"uri", "Our website", "https://github.com/khwang0/2017F-COMP3111", // button 2
			
			"About booking","Press the button, or type enquiry to view booking record", // column 3 title and descriptions
			"message","Booking enquiry","enquiry", // button 1
			"postback","About me","Hello! This is COMP3111 Group12's chatbot!" // button 2
		};
		Message m = mf.createCarouselMessage(3, 2, carouselContents);
		return m;

	}
	/**
	 * This method returns a message of recommended tour according to the input userId.
	 * @param userId This is used to identify the user and get a recommendation accordingly
	 * @return String This contains the information of the recommeded tour.
	 */
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
