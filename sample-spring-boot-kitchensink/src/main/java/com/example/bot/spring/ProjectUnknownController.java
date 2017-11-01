package com.example.bot.spring;

public class ProjectUnknownController {
    
    private SQLDatabaseEngine dbEngine = new SQLDatabaseEngine();
    
    public ProjectUnknownController() {}
    
    public void HandleUnknown (String question, String userID) {
        
        String fullQuestion = question;
        String lastFiveQuestions = " ";
        
        this.dbEngine.insertQuestion(userID,fullQuestion,lastFiveQuestions);
        
    }
}