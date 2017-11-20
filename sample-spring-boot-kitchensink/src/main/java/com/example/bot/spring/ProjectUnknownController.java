package com.example.bot.spring;

/**
 * This is the controller used to handle unknown user text messages.
 * The message will be recorded into the database for later review.
 * Additional info including userId and the last 5 text messages s/he sent will be recorded for tracing.
 * And all registered staff (by saying a passcode or injefting userId into database) will be notified immediately.
 */
public class ProjectUnknownController {
          
    /**
     * Method to handle unknown questions.
     * This will record the question(String)+lsat 5 questions (String array) into the database Question table.
     * A message will also be sent to all Staffs in Staff table.
     * @param userID The sender if this text message.
     * @param fullQuestion The question that the chatbot couldn't handle.
     * @param questionArray Array of up to 5 text messages sent by the user.
     */
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

    /**
     * Getter method of all Staff's userId for identifying who is a staff.
     * @return String containing all userIds of staffs, delimited by semicolon (;)
     */
    public String getStaffId() {
    	SQLDatabaseEngine dbEngine = new SQLDatabaseEngine();
    	return dbEngine.getStaffId();
   
    }
}