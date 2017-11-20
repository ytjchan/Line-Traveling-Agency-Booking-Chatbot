package com.example.bot.spring;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProjectEnqController {
	public ProjectEnqController() {}
	
	/**
	 * This method takes a userId as input, returns a String containing informaiton about that user's booking details
	 * @param userID This is the userId of the user whose booking info will be returned
	 * @return String This is a message of user's booking, including offerId, number of tourists, tour fee...
	 */
	public String bookingEnq(String userID) {
//		SQLDatabaseEngine db = new SQLDatabaseEngine();
		SQLDatabaseEngine sql = new SQLDatabaseEngine();
		ArrayList<ArrayList<String>> rs = new ArrayList<>();
		
		try {
//			rs=db.searchBookingById(userID);
			rs = sql.searchBookingById(userID);
		} catch (URISyntaxException e){
		} catch (SQLException e) {}
		
		if (rs.size()==0)
			return "You don't have any booking in our database";
		String reply = "You have booked:\n";
		for (ArrayList<String> row : rs) {
			reply+="offerId:\n" + row.get(0) +"\n";
			reply+="adults: " + row.get(1) +"\n";
			reply+="children: " + row.get(2) + "\n";
			reply+="toddlers: " + row.get(3) + "\n";
			reply+="tour fee: " + row.get(4) + "\n";
			reply+="amount paid: " + row.get(5) + "\n";
		}
		return reply;
	}
}
