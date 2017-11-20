package com.example.bot.spring;

import java.util.LinkedList;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class SearchState {	
	public String substate = "new";
	public LinkedList<String> keywords = new LinkedList<String>();
	public String startDate = "0000-01-01";
	public String endDate 	= "9999-12-31";		//must be YYYY-MM-DD
	public ArrayList<ArrayList<String>> rs;
	public int rsIndex = 0;
	
	
	
    /**
     * Constructor of a searchState.
	 * Sets startDate to date constructed
     */
	public SearchState() {}
    
	/**
	 * Resets dates to default (min start, max end date)
	 */
    public void resetDates() {
    	startDate = "0000-01-01";
		endDate = "9999-12-31";
    }
    
}