package com.example.bot.spring;

import java.util.LinkedList;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class BookState {	
	public String substate = "new";
	public String name;
	public String hkid;
	public int age;
	public int phoneno;
	
	public int adults;
	public int children;
	public int toddlers;
	public String requests;
	
	public String userId;
	
	
	
    /**
     * Constructor of a BookState.
	 * Sets startDate to date constructed
     */
	public BookState() {}
    
}