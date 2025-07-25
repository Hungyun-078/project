/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

/**
 *
 * @author sam20
 */
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQL {
    
    static String url = "jdbc:mysql://localhost:3306/Ubike";
    static String user = "root";
    static String password = "0101";
    static String UserID;
    
    public static void setUserID(String userid){
        UserID = new String(userid);
    }
    public static void storeData(history h) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "CREATE TABLE IF NOT EXISTS " + h.getUserId() + " ("
                    + "FromDate VARCHAR(255) NOT NULL,"
                    + "ToDate VARCHAR(255) NOT NULL,"
                    + "Time INT,"
                    + "cost INT,"
                    + "start VARCHAR(255) NOT NULL,"
                    + "stop VARCHAR(255) NOT NULL,"
                    + "BikeUID VARCHAR(255) NOT NULL)";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
                System.out.println("Table created successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO " + h.getUserId() + " (FromDate, ToDate, Time, cost, start, stop, BikeUID) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setTimestamp(1, h.getfdate());
                statement.setTimestamp(2, h.gettdate());
                statement.setInt(3, h.getiv());
                statement.setInt(4, h.getc());
                statement.setString(5, h.getst());
                statement.setString(6, h.getsp());
                statement.setString(7, h.getbid());
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new user was inserted successfully!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    


    public static String getName() {
        String name = "";
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT Name FROM user WHERE UserID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, UserID);  // Set the UserID parameter
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        name = resultSet.getString("Name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        
        return name;
    }
    public static String getID() {
        String id = "";
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT ID FROM user WHERE UserID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, UserID);  // Set the UserID parameter
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        id = resultSet.getString("ID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        
        return id;
    }
    public static String getEmail() {
        String email = "";
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT email FROM user WHERE UserID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, UserID);  // Set the UserID parameter
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        email = resultSet.getString("email");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        
        return email;
    }
    public static String getUserID() {
        
        
        return UserID;
    }
    
    
    

    public static List<String> getHistory() {
        List<String> historyList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM " + UserID+"_history";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String fromDate = resultSet.getString("StartTime");
                        String toDate = resultSet.getString("EndTime");
                        int cost = resultSet.getInt("cost");
                        String start = resultSet.getString("StartStation");
                        String stop = resultSet.getString("StopStation");
                        String bikeUID = resultSet.getString("BikeUID");

                        String historyEntry = "StartTime: " + fromDate + ", EndTime: " + toDate
                                + ", Cost: " + cost
                                + ", StartStation: " + start + ", StopStation: " + stop
                                + ", BikeUID: " + bikeUID;
                        historyList.add(historyEntry);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyList;
    }

}
