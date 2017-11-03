package com.example.bot.spring;

public class ProjectMasterController {
	//this class doesn't really do anything
	//use this class to call subclass methods
        public ProjectInitController init = new ProjectInitController();
	public ProjectBookController book = new ProjectBookController();
	public ProjectSearchController search = new ProjectSearchController();
	public ProjectEnqController enq = new ProjectEnqController();
	public ProjectFAQHandler faq = new ProjectFAQHandler();
	public ProjectUnknownController unknown = new ProjectUnknownController();
	
	public ProjectMasterController() {} 
	
	
}
