package com.example.bot.spring;

import java.util.LinkedList;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class BookState {	
	public String substate = "init";
	public String name;
	public String hkid;
	public int age;
	public int phoneno;
	
	public int adults;
	public int children;
	public int toddlers;
	public String requests;
	
	public String userId;
	public String tourName;
	public String tourId;
	public String offerId;
	
	public ArrayList<ArrayList<String>> offerings;
	public int offeringIndex;
	
	public double totalCost;
	public boolean discounted = false;
	
    /**
     * Constructor of a BookState.
	 * Sets startDate to date constructed
     */
	public BookState() {}
    
}