package com.example.bot.spring;

public class ProjectUnknownController {
      
    public ProjectUnknownController() {}
    
    public void handleUnknown (String userID, String fullQuestion, String[] questionArray) {
            SQLDatabaseEngine dbEngine = new SQLDatabaseEngine();
	    StringBuilder sb = new StringBuilder();
            int i = 1;
            for (String question: questionArray){
                    sb.append(i++).append(". ");
                    sb.append(question);
                    sb.append("||");
            }
            dbEngine.insertQuestion(userID,fullQuestion,sb.toString());
	    
	    String[] staffs = this.getStaffId().split(";");
	    if (staffs[0].equals(""))
		    return;
            for (String staff: staffs)
		    ProjectPusher.pushTextShorthand(staff, "Here is a unknown messages from user_+"+userID+" : "+fullQuestion);
	    // above won't be executed if no staff is found
    }
  //add a new function to get staff id form database StaffId table--cloud
    public String getStaffId() {
    	SQLDatabaseEngine dbEngine = new SQLDatabaseEngine();
    	return dbEngine.getStaffId();
   
    }
}