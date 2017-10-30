package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {

	PreparedStatement statement(String query) throws Exception {
		Connection c = this.getConnection();
		PreparedStatement stmt = c.prepareStatement(query); 
		return stmt;
	}
	
	PreparedStatement selectionStatement(String A, String B, String C) throws Exception {
		String query="select " + A + " from " + B + " where " + C;
		return this.statement(query);
	}
	
	PreparedStatement insertionStatement(String table_name, String[] values) throws Exception {
		String query="insert into " + table_name + "values (";
		for (String s: values) {
			query+= s+",";
		}
		query+=")";
		query.replace(",)", ")");
		return this.statement(query);
	}
	
	PreparedStatement updateStatement(String table_name, String set_values, String condition) throws Exception {
	//e.g UPDATE table_name SET set_values WHERE condition
		String query="update " + table_name + " Set " + set_values + " WHERE " + condition ;
		return this.statement(query);	
	} 
	
// below are examples of how to use above functions, just for refference.

/*
	void InsertTour(int tourID, String TourName, String TourDesc, int TourLength) throws Exception {
		String[] values = new String[] {"?","?","?","?"};
		PreparedStatement stmt = this.insertionStatement("tour", values);
		stmt.setInt(1,tourID);
		stmt.setString(2,TourName);
		stmt.setString(3,TourDesc);
		stmt.setInt(4,TourLength);
		stmt.executeQuery();
	}
	
	ResultSet searchTour(String criteria) throws Exception {
		PreparedStatement stmt = this.selectionStatement("TourName","tour","TourDesc like ?");
		stmt.setString(1, '%'+ criteria +'%');
		return stmt.executeQuery();
	}
	
	void payBooking(String LineID, String OfferID, double amount) throws Exception {
		PreparedStatement stmt = this.updateStatement("Booking","AmountPaid = AmountPaid + ?", 
			"LineID = ?	and OfferID = ?");
		stmt.setDouble(1,amount);
		stmt.setString(2,LineID);
		stmt.setString(3,OfferID);
		stmt.executeQuery();
	}
*/	

	
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


