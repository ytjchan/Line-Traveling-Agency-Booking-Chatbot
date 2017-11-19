package com.example.bot.spring;

public class ProjectFAQHandler {
	public ProjectFAQHandler() {
		
	}
	

	public String search(String text) {
		SQLDatabaseEngine db=new SQLDatabaseEngine();
		return db.searchKeywordFromFAQ(text);
	}
	//define methods to handle FAQ
}
