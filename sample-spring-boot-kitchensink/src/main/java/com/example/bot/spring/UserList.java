package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class UserList {
	
	private CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
        private final KitchenSinkController ksc;
        
        /**
         * Constructor of UserList.
         * @param ksc Reference to the controller to be used.
         */
        public UserList(KitchenSinkController ksc){
                this.ksc = ksc;
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
         * Method to check if the user has been in inactivity or new user.
         * @param userId userId of the user
         * @return boolean indicator of whether the user is in the UserList
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
                        users.add(new User(userId, this, ksc));
                }
                else
                        user.update();
        }
        
        /**
         * Changes the specific User's status.
         * @param userId User to set status to
         * @param status New status for User
         * @throws Exception Thrown if setStatus failed
         */
        public void setStatus(String userId, String status) throws Exception{
                User user = findUser(userId);
                if (user == null) // User not found
                        throw new Exception("Failed to set status: User "+userId+" is not found! (s/he expired?)");
                user.setStatus(status);
        }
        
        /**
         * Get the current status of a specific User.
         * @param userId userId of the User to be changed.
         * @return String containing the current status of the user
         * @throws Exception Thrown if the User does not exist in the UserList
         */
        public String getStatus(String userId) throws Exception{
                User user = findUser(userId);
                if (user == null)
                        throw new Exception("Failed to get status: User "+userId+" is not found! (s/he expired?)");
                return user.getStatus();
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
}
