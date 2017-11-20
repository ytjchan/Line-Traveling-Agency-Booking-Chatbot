package com.example.bot.spring;

import java.util.LinkedList;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class UserList {
	
	private CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
        
        /**
         * Constructor of UserList.
	 * A controller is required as parameter in order for each User object to schedule their timeout messages.
         */
        public UserList(){
        }
        
        /**
         * Helper method to find the User object for an userId.
         * @param userId userId to search for
         * @return An User object if User is found, null if User is not found
         */
        private User findUser(String userId){
                for (User user: users)
                        if (user.getUserId().equals(userId)){
                                log.info("User of userId "+userId+" found.");
                                return user;
                        }
                log.info("User of userId "+userId+" NOT found.");
                return null; // User not found
        }
        
        /**
         * Method to check if the user has been in inactivity or s/he is a new user.
         * @param userId userId of the user
         * @return boolean indicator of whether the user is in the UserList (ie inactive for 15mins or new user)
         */
        public boolean isInList(String userId){
                return findUser(userId)!=null;
        }
        
        /**
         * Update the status of an existing user, or if user does not exist, add him/her to the UserList.
         * @param userId userId of the User to be updated
         */
        public void update(String userId) {
                User user = findUser(userId);
                if (user == null) {// User not found
                        log.info("Adding a new user of userId "+userId);
                        users.add(new User(userId, this));
                }
                else
                        user.update();
        }
        
        /**
         * Update the buffer of a certain userId.
	 * The String text would be put into the buffer of the user, the buffer would contain up to 5 latest messages.
         * @param userId User that we want to update its buffer
         * @param text A String preparing to be added to buffer
         */
        public void updateBuffer(String userId, String text){
                User user = findUser(userId);
                if (user == null){
                        log.info("Failed to update buffer! User "+userId+" not in UserList yet!");
                        return;
                }
                user.updateBuffer(text);
        }
        
        /**
         * Getter method for the buffer of a certain userId.
         * @param userId userId of the user
         * @return LinkedList of String object as buffer of that user, or null if User is not present in this UserList.
         */
        public LinkedList<String> getBuffer(String userId){
                User user = findUser(userId);
                if (user != null)
                        return user.getBuffer();
                return null;
        }
        
        /**
         * Changes the specific User's status.
         * @param userId User to set status to
         * @param state New state for User
         */
        public void setState(String userId, String state) {
                User user = findUser(userId);
                if (user == null) // User not found
                        return;
                user.setState(state);
        }
        
        /**
         * Get the current status of a specific User.
         * @param userId userId of the User to be changed.
         * @return String containing the current status of the user
         */
        public String getState(String userId) {
                User user = findUser(userId);
                if (user == null)
                        return null;
                return user.getState();
        }
        
        /** 
         * Remove a user given the User object.
         * Used by User class when updating.
         * @param user An User object.
         */
	public void remove(User user) {
                log.info("Removing user "+user.getUserId());
		users.remove(user);
	}
        
        /**
         * Remove a user given the userId.
         * Typically would just let User expire.
         * The timeout message would not be shown if used.
         * @param userId 
         */
        public void remove(String userId) {
                User user = findUser(userId);
                if (user == null)
                        log.info("Failed to remove user with userId: "+userId);
                else
                        remove(user);
        }    
    
////////////////////searchstate remake
    
    /**
     * Getter method for input user's searchState dataobject
     * @param userId
     * @return
     */
    public SearchState getSearchState(String userId) {
    	User user = findUser(userId);
    	if (user == null)
            return null;
		return user.getSearchState();
    }
    
    /**
     * Getter method for input user's bookState data object
     * @param userId
     * @return
     */
    public BookState getBookState(String userId) {
    	User user = findUser(userId);
    	if (user == null)
            return null;
		return user.getBookState();
    }
    
    /**
     * Resets input user's bookstate data object
     * @param userId
     */
    public void resetBookState(String userId) {
    	User user = findUser(userId);
    	if (user != null)
    		user.resetBookState();
	}
	
    /**
     * Resets input user's searchstate data object
     * @param userId
     */
	public void resetSearchState(String userId) {
		User user = findUser(userId);
    	if (user != null)
    		user.resetSearchState();
	}
}