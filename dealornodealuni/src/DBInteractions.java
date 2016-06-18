package newdeal;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * DBInteractions Class
 * 
 * This is our class that achieves interactions with our database.
 * First make sure that in NetBeans you configure JavaDB's properties so that the:
 * Java DB Installation points to our db-derby-10.121.1-bin file we have included in our project and
 * that the 
 * Database location points to the TOP level of our project /DealOrNoDealGame/
 * This insures the database will be able to be run in your NetBeans.
 * 
 * This classes methods provide functionality to 
 * establish a connection to the database.
 * find out some particular values from a user's previous dealornodeal game.
 * to save some particular values from a player's dealornodeal game to the database.
 * And to create the table in the database with the particular values if the database isn't present.
 * 
 * @author Andre Cowie 14862344 
 * @author Tony van Swet 0829113 
 *
 * @version 29/05/2016
 */
public class DBInteractions {
    Connection conn = null;
    String url = "jdbc:derby://localhost:1527/games";
    String username ="root";
    String password ="root";
    
    //Working will connect to GAMES.game
    public void establishConnection(){
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e){
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    //Will create the Game table if the table is not already instansiated.
    public void createGamesTable(){
        String createString =
        "create table games" +
        ".GAME " +
        "(MATCHNUM integer NOT NULL, " +
        "PLAYERNAME varchar(40) NOT NULL, " +
        "PRIZE integer NOT NULL, " +
        "CASENUM integer NOT NULL, "+
        "PRIMARY KEY (MATCHNUM))";
        
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet table = dbm.getTables(null, null, "GAME", null);
            if (table.next()){
                //Table Already Exists.
            }else{
                stmt.executeUpdate(createString);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (stmt != null) {
                try { 
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    //Will display a previous players games
    public String newPlayerLoad(String playerName){
        ResultSet rs=null;
        String playersPrevious = "";
        try {
            String sqlQuery = "SELECT * FROM GAMES.game WHERE playername = ?";
            PreparedStatement a = conn.prepareStatement(sqlQuery);
            a.setString(1, playerName);
            rs = a.executeQuery();
            playersPrevious += "Previous games for: "+playerName+"\n";
            while(rs.next()){
                int match=rs.getInt("MATCHNUM");
                int prize=rs.getInt("PRIZE");
                int casenum=rs.getInt("CASENUM");
                playersPrevious += "#"+match+" Won: "+prize+" Case: "+casenum+"\n";
            }
            a.close();
            rs.close();
        } catch (SQLException e){
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE, null, e);
        }
        return playersPrevious;
    }
    
    //Will save a players game to the database
    public void savePlayersGame(String playerName, int caseSelected, int prizeRecieved){
        ResultSet rs=null;
        try{
            String save = "INSERT INTO GAMES.game VALUES(?, ?, ?, ?)";
            PreparedStatement a=conn.prepareStatement(save);
            int matchId;
            Statement b=conn.createStatement();
            rs = b.executeQuery("SELECT MAX(MATCHNUM) FROM GAMES.game");
            rs.next();
            matchId = (rs.getInt(1))+1;
            a.setInt(1, matchId);
            a.setString(2, playerName);
            a.setInt(4, caseSelected);
            a.setInt(3, prizeRecieved);
            a.executeUpdate();
        }   catch (SQLException ex) {
            Logger.getLogger(DBInteractions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}