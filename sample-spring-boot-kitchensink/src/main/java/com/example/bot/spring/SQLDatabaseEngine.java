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
        
        //private Connection connection; // removed since we need to either use a finally block to close it or use try-with-resources to close it automatically (latter is eaier to code in)
        
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
            //String temp = "select * from tour where tourdate between to_date(?,\\'yyyy-mm-dd\\') and to_date(?,\\'yyyy-mm-dd\\')";
//            String template = "select * from tour where tourid in "
//    			+ "(select subT.tourid from "
//        			+ "(select tof.tourid, tof.tourdate as startdate, tof.tourdate+t.tourlength as enddate "
//        			+ "from tour as t "
//        				+ "join touroffering as tof on t.tourid = tof.tourid) as subT "
//				+ "where ? <= startdate and ? >= enddate)";
            String template = 
    		"select tourid, tourname, tourdesc, tourlength from ("
    			+ "select subT.tourid, subT.offerid, subT.startdate, subt.tourname, subt.tourdesc, subt.tourlength from ("
    				+ "select tof.tourid, tof.offerid, tof.tourdate as startdate, tof.tourdate+t.tourlength as enddate, t.tourname, t.tourdesc, t.tourlength "
    					+ "from tour as t join touroffering as tof on t.tourid = tof.tourid) as subT "
    			+ "where ? <= startdate and ? >= enddate";
            for (int i=0; i<keywords.size(); i++) {
            	template += " and (lower(tourdesc) like lower(?) or lower(tourname) like lower(?))";
            }
            template += ") as outT "
        		+ "group by tourid, tourname, tourdesc, tourlength order by min(startdate) asc";
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
            c.close();
            stmt.close();
            tourRs.close();
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
        
        // removed since we must either use a finally block to close the connection OR create a connectoin in try-with-resouces block
        
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
     * Search for all tours in tour table.
     * 
     * @return 2-dimensional ArrayList of search results
     */
    protected ArrayList<ArrayList<String>> searchAllTour() throws URISyntaxException, SQLException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		
	    Connection c = getConnection();
	    PreparedStatement stmt = c.prepareStatement(
	//"select * from tour where tourid in (select distinct subT.tourid from (select tof.tourid, tof.tourdate from tour as t join touroffering as tof on t.tourid = tof.tourid) as subT where DATE ? <= subT.tourdate)"
	//"select * from tour where tourid in (select distinct subT.tourid from (select tof.tourid, tof.tourdate from tour as t join touroffering as tof on t.tourid = tof.tourid) as subT where ? <= subT.tourdate)"
		"select tourid, tourname, tourdesc, tourlength from ("
			+ "select subT.tourid, subT.offerid, subT.startdate, subt.tourname, subt.tourdesc, subt.tourlength from ("
				+ "select tof.tourid, tof.offerid, tof.tourdate as startdate, tof.tourdate+t.tourlength as enddate, t.tourname, t.tourdesc, t.tourlength "
					+ "from tour as t join touroffering as tof on t.tourid = tof.tourid) as subT "
			+ "where ? <= startdate) as outT "
		+ "group by tourid, tourname, tourdesc, tourlength order by min(startdate) asc"
		);
        //stmt.setString(1, dtf.format(localDate));
	    //stmt.setString(1, "2017-11-03");
	    stmt.setDate(1, java.sql.Date.valueOf(localDate));
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
        c.close();
        stmt.close();
        tourRs.close();
        return arr;

    }
    
    /*
     * 
     */
    protected ArrayList<ArrayList<String>> searchTourID(String in, String table) throws URISyntaxException, SQLException {
    	Connection c = getConnection();
    	String template = "select * from " + table + " where lower(tourid) = lower(?)";
    	PreparedStatement stmt = c.prepareStatement(template);
    	stmt.setString(1, in);
    	ResultSet tourRs = stmt.executeQuery();
    	ArrayList<ArrayList<String>> arr= new ArrayList<>();
    	while (tourRs.next()) {
    		ArrayList<String> temp = new ArrayList<String>();
    		temp.add(tourRs.getString(1));	//tourid
            temp.add(tourRs.getString(2));	//name	|	offerid
            temp.add(tourRs.getString(3));	//desc	|	tourdate
            temp.add(tourRs.getString(4));	//length (days)	|	tourguidelineid
            arr.add(temp);
    	}
    	c.close();
    	stmt.close();
    	tourRs.close();
    	return arr;
    }

    /**
     * This method takes userId as input and returns a 2D array as this userId's booking record in database.
     * @param userId This is the userId that is used to find bookings in database
     * @return ArrayList<ArrayList<String>> Return an array of records, each record is a String array.
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected ArrayList<ArrayList<String>> searchBookingById(String userId) throws URISyntaxException, SQLException {
    		ArrayList<ArrayList<String>> rs = new ArrayList<>();
    		try (
                Connection c = getConnection();
                PreparedStatement stmt = c.prepareStatement("select * from Booking where LineId = ?");
                )
    		{
    			stmt.setString(1, userId);
    			ResultSet bookRs = stmt.executeQuery();
    			while (bookRs.next()) {
    				ArrayList<String> temp = new ArrayList<String>();
    				for (int i=2;i<10;i++)
    					temp.add(bookRs.getString(i));
    				rs.add(temp);
    			}	
    		}
    		return rs;
    }

    //to get staff id from database
    protected String getStaffId() {
    	
      	StringBuilder sb = new StringBuilder();
    	
    	
    		String sqlsentence="SELECT * FROM Staff;"; 


                        try (
                                Connection c = getConnection();
                                PreparedStatement stmt = c.prepareStatement(sqlsentence);
                                ) // Java try-with-resources closes Connection and PreparedStatement automatically (old getStatement() is not feasible since we cannot close the Connection object)
                        {
                                

                                //ResultSet keywordRs = getStatement(testsql).executeQuery();
                                ResultSet keywordRs = stmt.executeQuery();
                                
                                
                                while (keywordRs.next()){
                                    sb.append(keywordRs.getString(1));
                                    sb.append(";");
                            }
                                
                                
                        } catch (URISyntaxException e) {
                                log.info("URI Syntax problem with URI: " + System.getenv("DATABASE_URL"));
                        } catch (SQLException e) {
                                log.info("Searching for answer from FAQ table failed! Continuing on next word.");
                        }
                
    		return sb.toString();
    	
    	
    	
    }

	/**
	 * Add a user as Staff.
	 * The staff would receive every unanswered question and the customer's userId.
	 * So that the staff can answer it later.
	 * Staffs can also access the database to view all questions apart from registering as a Staff.
	 * @param userId The userId of the user that would be added as staff. 
	 */
	protected void addStaff(String userId) {
                try (
                        Connection c = getConnection();
                        PreparedStatement stmt = c.prepareStatement("insert into staff values(?);");
                        ) // Java try-with-resources
                {
			stmt.setString(1, userId);
			stmt.execute();
                } catch (URISyntaxException e){
                        log.info("URI Syntax problem with URI: " + System.getenv("DATABASE_URL"));
                } catch (SQLException e){
                        log.info("Adding "+userId+" as admin failed!");
                }
	}

    /**
     * This method take userId as input, returns a tour record as String array to recommend to the user.
     * @param userId This is used to find a proper recommendation for the specific user
     * @return ArrayList<String> This contains the information of the tour recommended to that user.
     * @throws URISyntaxException
     * @throws SQLException
     */
    protected ArrayList<String> getRecommendation(String userId) throws URISyntaxException, SQLException {
    	ArrayList<String> result = new ArrayList<String>();
    		try (
                Connection c = getConnection();
                PreparedStatement stmt = c.prepareStatement(
                		"select * from TourOffering where tourId not in ("
                			+"select substr(offerid,1,5) from booking where lineId = ? )"
                			+"and tourdate > ?"
                			+"order by price desc"
                	);
    		)
    		{
    			LocalDate localDate = LocalDate.now();
    			stmt.setString(1, userId);
    			stmt.setDate(2, java.sql.Date.valueOf(localDate));
    			ResultSet rs = stmt.executeQuery();
    			
    			if (rs.next())
    				for (int i=1; i<7; i++)
    					result.add(rs.getString(i));
    			else
    				result = null;
    		}
    		return result;
    }
}


