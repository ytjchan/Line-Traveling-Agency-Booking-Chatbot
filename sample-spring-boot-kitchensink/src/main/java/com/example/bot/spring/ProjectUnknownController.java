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
  //add a new function to get staff id form database StaffId table--cloud
    public String getStaffId() {
    	
    	return dbEngine.getStaffId();
   
    }
}