package com.example.bot.spring;

public class ProjectUnknownController {
    
    private dbEngine = new SQLDatabaseEngnie();
    
    public ProjectUnknownController() {}
    
    public void HandleUnknown (Queue<String> buffer, String userID) {
        
        for (String question: buffer) {
        //TODO: insert question into Question table with userID as key
        //need DatabaseEngine, but it is not ready yet
            
        }
    }
    
}