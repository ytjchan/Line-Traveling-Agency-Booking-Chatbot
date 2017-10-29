package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	
	
	ResultSet search(PreparedStatement stmt) throws Exception {
		//Write your code here
		ResultSet rs = stmt.executeQuery();
		return rs;
		/*
		Connection c = this.getConnection();
		PreparedStatement stmt = c.prepareStatement(
			"SELECT response FROM mapping where keyword like ?");
		stmt.setString(1, '%' + text + '%'); //or some other variables
		String reply = null;
		System.out.println("before execute");
		ResultSet rs = stmt.executeQuery();
		System.out.println("after execute");
		while (rs.next()) {
			System.out.println("looping");
			if (reply == null)
				reply = rs.getString(1);
			else
				reply += " " + rs.getString(1);
		}
		
		rs.close();
		stmt.close();
		c.close();
		System.out.println(reply);
		if (reply == null)
			throw new Exception("NOT FOUND");
		else 
			return reply;
		*/
		
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
