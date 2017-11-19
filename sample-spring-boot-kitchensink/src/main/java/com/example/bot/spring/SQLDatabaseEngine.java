package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.time.format.DateTimeFormatter;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
        

    /**
     * Return all keywords available in FAQ table
     * @return String of all keywords concatenated and separated by ', ' 
     */
    protected String searchAllKeywords(){
        try (
                Connection c = getConnection();
                PreparedStatement stmt = c.prepareStatement("select keyword from faq;");
                ) // Java try-with-resources
        {
                StringBuilder sb = new StringBuilder();
                ResultSet keywordRs = stmt.executeQuery(); // ResultSet is automatically closed when stmt is closed
                while (keywordRs.next()){
                        sb.append(keywordRs.getString(1));
                        sb.append(", ");
                }
                return sb.toString();
        } catch (URISyntaxException e){
                log.info("URI Syntax problem with URI: " + System.getenv("DATABASE_URL"));
        } catch (SQLException e){
                log.info("Searching for all keywords failed!");
        }
        return null;
    }
    
    //helper function to check whether duplicate keyword exist
    private boolean checkduplicate(int currentString,String s,String[] textlist) {
    	boolean skip=false;
    	for(int i=0;i<currentString-1;i++) {
			if(s.toLowerCase().equals(textlist[i].toLowerCase())) {
				skip=true;
				//for test
				//return "deplicate";
			}
		}
    	return skip;
    }
    
    /**
     * Return keywords available in FAQ table that match one or more keywords in the input 
     * @param input_text A keyword that may exist in FAQ table
     * @return String of all matched answers concatenated and separated by '; ' 
     */
    protected String searchKeywordFromFAQ(String input_text) {
    	
    	StringBuilder sb = new StringBuilder();
		String[] textlist=input_text.split("\\s+");//use"\\s+"instead of " " to split
		int currentString=0;
            boolean skip=false;
		//String result=null;
		String sqlsentence="SELECT Answer FROM FAQ WHERE Keyword=?;"; // use ? with setString() or setInt() etc. to prevent SQL injection
		//String testsql="SELECT * FROM FAQ";
            //this loop is used to check whether deplicate keywords appear,if so, ignore them
            for (String s : textlist) { // more readable
                    skip=false;
                    currentString++;
                    /*for(int i=0;i<currentString-1;i++) {
                            if(s.toLowerCase().equals(textlist[i].toLowerCase())) {
                                    skip=true;
                                    //for test
                                    //return "deplicate";
                            }
                    }*/
                    if(checkduplicate(currentString,s,textlist))
                            continue;
                    try (
                            Connection c = getConnection();
                            PreparedStatement stmt = c.prepareStatement(sqlsentence);
                            ) // Java try-with-resources closes Connection and PreparedStatement automatically (old getStatement() is not feasible since we cannot close the Connection object)
                    {
                            stmt.setString(1, s.toLowerCase()); // setString() ensures safety of DB
                            if(sb.length()!=0)
                                    sb.append("\n\n");
                            //ResultSet keywordRs = getStatement(testsql).executeQuery();
                            ResultSet keywordRs = stmt.executeQuery();
                            if (keywordRs.next()) // while loop would concatenate all ans results if keywords repeat in the db, this my ruin the output String
                                    sb.append(keywordRs.getString(1));
                    } catch (URISyntaxException e) {
                            log.info("URI Syntax problem with URI: " + System.getenv("DATABASE_URL"));
                    } catch (SQLException e) {
                            log.info("Searching for answer from FAQ table failed! Continuing on next word.");
                    }
            }
		return sb.toString();
    }
    
    /**
     * Search for tours with given description, start and end dates
     * etc. with A as a String array
     * @param keywords A linked list of keyword strings
     * @param startDate a date string in format yyyy-mm-dd
     * @param endDate a date string in format yyyy-mm-dd
     * @return String array of formatted search results (see above)
     */
    protected ArrayList<ArrayList<String>> searchTourByDesc(LinkedList<String> keywords, String startDate, String endDate) throws URISyntaxException, SQLException {
        Connection c = getConnection();
        String template = 
		"select tourid, tourname, tourdesc, tourlength from ("
				+ "select t.tourid, tof.tourdate as startdate, tof.tourdate+t.tourlength as enddate, t.tourname, t.tourdesc, t.tourlength "
					+ "from tour as t join touroffering as tof on t.tourid = tof.tourid) as subT "
			+ "where ? <= startdate and ? >= enddate";
        for (int i=0; i<keywords.size(); i++) {
        	template += " and (lower(tourdesc) like lower(?) or lower(tourname) like lower(?))";
        }
        template += " group by tourid, tourname, tourdesc, tourlength order by min(startdate) asc";
        //1st ? is startdate
        //2nd ? is enddate
        //3rd ? onwards is keywords in pairs
        PreparedStatement stmt = c.prepareStatement(template);
        
        stmt.setDate(1, java.sql.Date.valueOf(startDate));
        stmt.setDate(2, java.sql.Date.valueOf(endDate));
        for (int i=0, j=0; i<keywords.size(); i++, j+=2) {
        	stmt.setString(j+3, "%" + keywords.get(i) + "%");
        	stmt.setString(j+1+3, "%" + keywords.get(i) + "%");
        }
        ResultSet tourRs = stmt.executeQuery(); // ResultSet is closed automatically when stmt is closed
        ArrayList<ArrayList<String>> arr = new ArrayList<>();
        while (tourRs.next()){
        	ArrayList<String> temp = new ArrayList<String>();
            temp.add(tourRs.getString(1));	//id
            temp.add(tourRs.getString(2));	//name
            temp.add(tourRs.getString(3));	//desc
            temp.add(tourRs.getString(4));	//length (days)
            arr.add(temp);
        }
        stmt.close();
        c.close();
        return arr;
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
            try (
                    Connection c = getConnection();
                    PreparedStatement stmt = c.prepareStatement("insert into question values (?,?,?,?);");
                    PreparedStatement stmt2 = c.prepareStatement(("select max(qid) from question;"));
                    ) // Java try-with-resources will close the connection for us
            {
                    ResultSet qidRs = stmt2.executeQuery(); // ResultSet will close automatically after the PrepareStatement is closed
                    int currentQid = qidRs.next() ? qidRs.getInt(1) : 0; // need to see if there is any record now
                    stmt.setInt(1, currentQid+1);
                    stmt.setString(2, lineid);
                    stmt.setString(3, fullquestion);
                    stmt.setString(4, lastfivequestions);
                    stmt.executeUpdate();
                    log.info("Inserted record into Question table with qid= " + (currentQid+1));
                    return true;
            } catch (URISyntaxException e){
                    log.info("URI Syntax problem with URI: " + System.getenv("DATABASE_URL"));
            } catch (SQLException e) {
                    log.info("Insertion into Question table failed!");
            }
            return false;
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
    
    /**
     * Search tour table for matching tour ID
     * @param in - tourID
     * @return 2-dimensional ArrayList of search results
     */
    protected ArrayList<ArrayList<String>> searchTourID(String in) throws URISyntaxException, SQLException {
    	Connection c = getConnection();
    	String template = "select * from tour where lower(tourid) = lower(?)";
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, in);
    	ResultSet tourRs = stmt.executeQuery();
    	ArrayList<ArrayList<String>> arr= new ArrayList<>();
    	while (tourRs.next()) {
    		ArrayList<String> temp = new ArrayList<String>();
    		temp.add(tourRs.getString(1));	//tourid
            temp.add(tourRs.getString(2));	//name
            temp.add(tourRs.getString(3));	//desc
            temp.add(tourRs.getString(4));	//length (days)
            arr.add(temp);
    	}
    	stmt.close();
        c.close();
    	return arr;
    }
    
    /**
     * 
     * @param in
     * @return
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected ArrayList<ArrayList<String>> searchTourOfferingID(String in) throws URISyntaxException, SQLException {
    	Connection c = getConnection();
    	String template = "select tourid, concat(tourid,offerid), tourdate, tourguidelineid, hotel, price, maxcapacity, minrequirement, confirmed from touroffering where lower(tourid) = lower(?)";
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, in);
    	ResultSet tourRs = stmt.executeQuery();
    	ArrayList<ArrayList<String>> arr= new ArrayList<>();
    	while (tourRs.next()) {
    		ArrayList<String> temp = new ArrayList<String>();
    		temp.add(tourRs.getString(1));	//tourid
            temp.add(tourRs.getString(2));	//offerid
            temp.add(tourRs.getString(3));	//tourdate
            temp.add(tourRs.getString(4));	//tourguidelineid
            temp.add(tourRs.getString(5));	//hotel
            temp.add(tourRs.getString(6));	//price
            temp.add(tourRs.getString(7));	//maxcapacity
            temp.add(tourRs.getString(8));	//minreq
            temp.add(tourRs.getString(9));	//confirmed
            arr.add(temp);
    	}
    	stmt.close();
        c.close();
    	return arr;
    }
    
    
    /**
     * Search booker table for matching line ID
     * @param id
     * @return 2-dimensional arraylist of strings (there should only be 1 or 0 records)
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected ArrayList<ArrayList<String>> searchBookerForLineID(String id) throws URISyntaxException, SQLException {
    	Connection c = getConnection();
    	String template = "select * from booker where lower(lineid) = lower(?)";
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, id);
    	ResultSet tourRs = stmt.executeQuery();
    	ArrayList<ArrayList<String>> arr= new ArrayList<>();
    	while (tourRs.next()) {
    		ArrayList<String> temp = new ArrayList<String>();
    		temp.add(tourRs.getString(1));	//lineid
            temp.add(tourRs.getString(2));	//name
            temp.add(tourRs.getString(3));	//hkid
            temp.add(tourRs.getString(4));	//phoneno
            temp.add(tourRs.getString(5));	//age
            arr.add(temp);
    	}
    	stmt.close();
        c.close();
    	return arr;
    }
    
    /**
     * Inputs a new user into the booker table
     * @param userId
     * @param name
     * @param hkid
     * @param age
     * @param phoneno
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected void inputUserData(String userId, String name, String hkid, int age, int phoneno) throws URISyntaxException, SQLException {
    	Connection c = getConnection();
    	String template = "insert into booker values (?,?,?,?,?)";
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, userId);
    	stmt.setString(2, name);
    	stmt.setString(3, hkid);
    	stmt.setInt(4, phoneno);
    	stmt.setInt(5, age);
    	stmt.executeUpdate();
    	stmt.close();
        c.close();
    }
    
    /**
     * 
     * @param userId
     * @param offerId
     * @param adults
     * @param children
     * @param toddlers
     * @param tourfee
     * @param requests
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected void inputBooking(String userId, String offerId, int adults, int children, int toddlers, double tourfee, String requests) throws URISyntaxException, SQLException {
    	String template = "insert into booking values (?,?,?,?,?,?,?,?,?)";
    	Connection c = getConnection();
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, userId);
    	stmt.setString(2, offerId);
    	stmt.setInt(3, adults);
    	stmt.setInt(4, children);
    	stmt.setInt(5, toddlers);
    	stmt.setDouble(6, tourfee);
    	stmt.setDouble(7, 0);
    	stmt.setString(8, requests);
    	stmt.setBoolean(9, false);
    	stmt.executeUpdate();
    	stmt.close();
        c.close();
    }
    
    
    /**
     * 
     * @param offerId
     * @return
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected int getCurrentBookingCount(String offerId) throws URISyntaxException, SQLException {
    	Connection c = getConnection();
    	String template = "select sum(adults) + sum(children) + sum(toddlers) from booking where offerid = ? and cancelled = false";
    	//String template = "select sum(adults) + sum(children) + sum(toddlers) from booking where cancelled = false";
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, offerId);
    	ResultSet tourRs = stmt.executeQuery();
    	if (tourRs.next()) {
    		int temp = tourRs.getInt(1);
    		stmt.close();
            c.close();
    		return temp;
    	}
    	stmt.close();
        c.close();
    	return 0;
    }
    
    /**
     * Decrements the remaining discounts of the input tour
     * To be used after a tour has been booked with a discount active
     * @param offerId The combined offerid used in the booking table, as well as the bookState class
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected void decrementDiscount(String offerId) throws URISyntaxException, SQLException {
    	String tourId = offerId.substring(0,5);
    	String offerId_8 = offerId.substring(5);
    	
    	Connection c = getConnection();
    	String template = "update discount set remaining = remaining - 1 where tourid = ? and offerid = ?";
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, tourId);
    	stmt.setString(2, offerId_8);
    	stmt.executeUpdate();
    	stmt.close();
        c.close();
    }
    
    /**
     * 
     * @param offerId
     * @return
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected double getDiscount(String offerId) throws URISyntaxException, SQLException {
    	String tourId = offerId.substring(0,5);
    	String offerId_8 = offerId.substring(5);
    	Connection c = getConnection();
    	String template = "select discount from discount where tourid = ? and offerid = ? and remaining > 0";
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, tourId);
    	stmt.setString(2, offerId_8);
    	ResultSet tourRs = stmt.executeQuery();
    	if (tourRs.next()) {
    		double temp = tourRs.getDouble(1);
    		stmt.close();
            c.close();
    		return temp;
    	}
    	stmt.close();
        c.close();
    	return 1;
    }
    
    /**
     * Checks if the input tour offering has a discount active.
     * @param offerId 15-character offerId used 
     * @return True if there is a discount, false otherwise.
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected boolean checkDiscount(String offerId) throws URISyntaxException, SQLException {
    	String tourId = offerId.substring(0,5);
    	String offerId_8 = offerId.substring(5);
    	Connection c = getConnection();
    	String template = "select discount from discount where tourid = ? and offerid = ? and remaining > 0";
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, tourId);
    	stmt.setString(2, offerId_8);
    	ResultSet tourRs = stmt.executeQuery();
    	if (tourRs.next()) {
    		stmt.close();
            c.close();
    		return true;
    	}
    	stmt.close();
        c.close();
    	return false;
    }
    
    protected ArrayList<ArrayList<String>> getDeals() throws URISyntaxException, SQLException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now().plusDays(3);
    	String template = "select tourname, min(discount) from tour as t join discount as d on t.tourid = d.tourid where t.tourid in (";
    		template += "select distinct d.tourid from ";
    		template += "discount as d join touroffering as t on ";
   			template += "d.offerid = t.offerid and d.tourid = t.tourid ";
			template += "where remaining > 0 and tourdate >= ?) group by tourname, offerid order by offerid asc";
		Connection c = getConnection();
		PreparedStatement stmt = c.prepareStatement(template);
		stmt.setDate(1, java.sql.Date.valueOf(localDate));
		ResultSet rs = stmt.executeQuery();
		ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
		while (rs.next()) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(rs.getString(1));
			temp.add(rs.getString(2));
			out.add(temp);
		}
		stmt.close();
		c.close();
		return out;
    }
    
    protected ArrayList<String> getBookers() throws URISyntaxException, SQLException {
    	String template = "select lineid from booker";
    	Connection c = getConnection();
		PreparedStatement stmt = c.prepareStatement(template);
		ResultSet rs = stmt.executeQuery();
		ArrayList<String> out = new ArrayList<String>();
		while (rs.next()) {
			out.add(rs.getString(1));
		}
		stmt.close();
		c.close();
		return out;
    }
}


