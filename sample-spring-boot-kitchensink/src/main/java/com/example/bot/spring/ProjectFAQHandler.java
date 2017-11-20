package com.example.bot.spring;

public class ProjectFAQHandler {
	public ProjectFAQHandler() {
		
	}
	
	 /**
     * search answer for correspoding FAQ question(user input will be seperated into keywords,duplicate keyword will only be search once)
     * @param text text is the FAQ queestion given by the user
     * @return String containing all corresponding FAQ answer which match the key words in the text(FAQ questioin),delimited by semicolon (;)
     */
	public String search(String text) {
		SQLDatabaseEngine db=new SQLDatabaseEngine();
		return db.searchKeywordFromFAQ(text);
	}
	//define methods to handle FAQ
}
