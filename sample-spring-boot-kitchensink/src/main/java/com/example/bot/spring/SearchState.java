package com.example.bot.spring;

import java.util.LinkedList;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class SearchState {	
	public String substate = "new";
	public LinkedList<String> keywords = new LinkedList<String>();
	//public String startDate;
	public String startDate = "0000-01-01";
	public String endDate 	= "9999-12-31";		//must be YYYY-MM-DD
	public ArrayList<ArrayList<String>> rs;
	public int rsIndex = 0;
	
	
	
    /**
     * Constructor of a searchState.
	 * Sets startDate to date constructed
     */
	public SearchState() {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		LocalDate localDate = LocalDate.now().plusDays(3);
//		startDate = dtf.format(localDate);
	}
    
    public void resetDates() {
//    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		LocalDate localDate = LocalDate.now().plusDays(3);
//		startDate = dtf.format(localDate);
    	startDate = "0000-01-01";
		endDate = "9999-12-31";
    }
    
}