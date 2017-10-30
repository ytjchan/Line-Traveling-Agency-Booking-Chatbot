package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	
	
	ResultSet query(String query) throws Exception {
		//
    	Connection c = this.getConnection();
    	PreparedStatement stmt = c.prepareStatement(query); // now other classes need not to create Connection and PreparedStatement obejects
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
	
	ResultSet SelectQuery(String A, String B, String C) {
	// get query result with input A,B,C stand for "selct A from B where C"
		String query="select " + A + " from B" + " where C";
		return this.query(query);
	}
	
	ResultSet InsertQuery(String table_name, String[] values) {
	// use query to insert into tables. String[] values must include all columns in the table e.g: insert into table_name values (values[1],values[2],...)
		String query="insert into " + table_name + "values (";
		for (String s: values) {
			query+= s+",";
		}
		query+=")";
		query.replace(",)", ")");
		return this.query(query);
		
	}
	
	ResultSet UpdateQuery(String table_name, String set_values, String, condition) {
	//e.g UPDATE table_name SET set_values WHERE condition
		String query="update " + table_name + " Set " + set_values + " WHERE " + condition ;
		return this.query(query);
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
