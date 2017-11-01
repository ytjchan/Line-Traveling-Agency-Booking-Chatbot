package com.example.bot.spring;
import java.util.List;
import java.util.Queue;

public class ProjectUnknownController {
    
    private SQLDatabaseEngine dbEngine = new SQLDatabaseEngine();
    
    public ProjectUnknownController() {}
    
    public void HandleUnknown (Queue<String> buffer, String userID) {
        
        String lastFiveQuestions = "";
        String fullQuestion = "empty";
        
        for (String question: buffer) {
            if (question.length() < 200) { // we ignore questions whose length lager than 200
                lastFiveQuestions += question + "*";
                fullQuestion = question;
            }
        }
        
        this.dbEngine.insertQuestion(userID,fullQuestion,lastFiveQuestions);
        
    }
}