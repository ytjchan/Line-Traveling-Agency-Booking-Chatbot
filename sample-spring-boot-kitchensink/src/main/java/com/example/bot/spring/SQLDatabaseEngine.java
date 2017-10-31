package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {

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
                        PreparedStatement stmt = getStatement("insert into question values (?,?,?,?);");
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
                        Connection c = this.getConnection();
                        PreparedStatement stmt = c.prepareStatement(query);
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


