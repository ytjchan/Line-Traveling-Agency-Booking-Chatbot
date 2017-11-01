package com.example.bot.spring;

public class ProjectUnknownController {
    
    private SQLDatabaseEngine dbEngine = new SQLDatabaseEngine();
    
    public ProjectUnknownController() {}
    
    public void HandleUnknown (Queue<String> buffer, String userID) {
        //we store 5 questions each time, otherwise we don't store it.
        if (buffer.size()<5)
            return;
        
        String lastFiveQuestions;
        String fullQuestion;
        
        for (String question: buffer) {
            if (qustion.length() < 200) { // we ignore questions whose length lager than 200
                lastFiveQuestions += question + "*";
                fullQuestion = question;
            }
        }
        
        this.dbEngine.insertQuestion(userID,fullQuestion,lastFiveQuestions);
        
    }
}