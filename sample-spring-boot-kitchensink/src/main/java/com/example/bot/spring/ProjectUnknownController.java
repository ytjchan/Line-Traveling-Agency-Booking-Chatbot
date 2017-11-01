package com.example.bot.spring;

public class ProjectUnknownController {
    
    private final SQLDatabaseEngine dbEngine = new SQLDatabaseEngine();
    
    public ProjectUnknownController() {}
    
    public void handleUnknown (String userID, String fullQuestion, String[] questionArray) {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (String question: questionArray){
                    sb.append(i++).append(". ");
                    sb.append(question);
                    sb.append("||");
            }
            dbEngine.insertQuestion(userID,fullQuestion,sb.toString());
    }
}