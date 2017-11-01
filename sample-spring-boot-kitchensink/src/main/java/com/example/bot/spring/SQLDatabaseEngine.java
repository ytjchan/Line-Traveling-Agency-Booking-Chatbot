package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.ArrayList;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
        
        private Connection connection;
        
        /**
         * Return all keywords available in FAQ table
         * @return String of all keywords concatenated and separated by ', ' 
         */
        protected String searchAllKeywords(){
                try{
                        StringBuilder sb = new StringBuilder();
                        ResultSet keywordRs = getStatement("select keyword from faq;").executeQuery();
                        while (keywordRs.next()){
                                sb.append(keywordRs.getString(1));
                                sb.append(", ");
                        }
                        return sb.toString();
                } catch (SQLException e){
                        log.info("Searching for all keywords failed!");
                        return null;
                }              
        }
        
        /**
         * Search for tours with given description with format shown below.
         * Suppose we search for tours with 'spring', we will get 
         * A[0]= "1: 2D001|Shimen National Forest Tour"
         * A[1]= "2: 2D002|Yangshan Hot Spring Tour"
         * etc. with A as a String array
         * @param desc Description word to search for
         * @return String array of formatted search results (see above)
         */
        protected String[] searchTourByDesc(String desc){
                try{
                        PreparedStatement stmt = getStatement("select tourid, tourname from tour where lower(tourdesc) like concat('%',?,'%');"); // need to use concat()
                        stmt.setString(1, desc.toLowerCase());
                        ResultSet tourRs = stmt.executeQuery();
                        ArrayList<String> arr = new ArrayList<>();
                        int i=1;
                        while (tourRs.next()){
                                StringBuilder sb = new StringBuilder();
                                sb.append(i).append(": ");
                                sb.append(tourRs.getString(1));
                                sb.append('|');
                                sb.append(tourRs.getString(2));
                                arr.add(sb.toString());
                                i++;
                        }
                        return arr.toArray(new String[0]);
                } catch (SQLException e){
                        log.info("Searching tours by description failed!");
                        return null;
                }
        }
        
        // protected = access within same package and subclasses (=derived class)
        // javadoc below is just for fancy code suggestion in IDE
        /**
         * Insert a new record into Question table, qid is prepared automatically here
         * @param lineid
         * @param fullquestion
         * @param lastfivequestions
         * @return boolean indicator for whether insertion is successful
         */
        protected boolean insertQuestion(String lineid, String fullquestion, String lastfivequestions){
                try{
                        PreparedStatement stmt = getStatement("insert into question values (?,?,?,?,false);");
                        ResultSet qidRs = getStatement("select max(qid) from question;").executeQuery();
                        int currentQid = qidRs.next() ? qidRs.getInt(1) : 0; // need to see if there is any record now
                        stmt.setInt(1, currentQid+1);
                        stmt.setString(2, lineid);
                        stmt.setString(3, fullquestion);
                        stmt.setString(4, lastfivequestions);
                        stmt.executeUpdate();
                        log.info("Inserted record into Question table with qid= " + (currentQid+1));
                        return true;
                } catch (SQLException e) {
                        log.info("Insertion into Question table failed!");
                        return false;
                } 
        }
        
        // made private since only used in SQLDatabaseEngine now 
        // query can be Strings with question mark like "select * from Booker where LineID=?;" and use setString() to set it later
	private PreparedStatement getStatement(String query) throws SQLException {
		try{       
                        if (this.connection != null)
                                this.connection.close();
                        this.connection = this.getConnection();
                        PreparedStatement stmt = this.connection.prepareStatement(query);
                        return stmt;
                } catch (URISyntaxException e) {
                        log.info("URI Syntax problem with URI: " + System.getenv("DATABASE_URL"));
                        throw new SQLException("URI Syntax problem with URI: " + System.getenv("DATABASE_URL"));
                }
	}
        
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}


