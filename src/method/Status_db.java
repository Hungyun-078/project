package method;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Status_db {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/ubike";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Ubikeproject5";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static String searchBikeID(String bikeID) throws SQLException {
        String query = "SELECT Type FROM bikes WHERE BikeUID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bikeID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Type");
                } else {
                    return null;
                }
            }
        }
    }

    public static boolean updateBikeID(String bikeID, String newStatus) throws SQLException {
        if (!isValidStatus(newStatus)) {
            throw new SQLException("Invalid status: " + newStatus);
        }
        if (!bikeExists(bikeID)) {
            throw new SQLException("Bike ID not found: " + bikeID);
        }
        String query = "UPDATE bikes SET Type = ? WHERE BikeUID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, bikeID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static List<String> getAbnormalBikes() throws SQLException {
        String query = "SELECT BikeUID, Type FROM bikes WHERE Type != 'Normal'";
        List<String> abnormalBikes = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String bikeUID = rs.getString("BikeUID");
                String type = rs.getString("Type");
                abnormalBikes.add(bikeUID + " - " + type);
            }
        }
        return abnormalBikes;
    }

    private static boolean isValidStatus(String status) {
        return "Normal".equalsIgnoreCase(status) || 
               "Broken".equalsIgnoreCase(status) ||
               "Fixing".equalsIgnoreCase(status) ||
               "Missing".equalsIgnoreCase(status) ||
               "In use".equalsIgnoreCase(status);
    }

    private static boolean bikeExists(String bikeID) throws SQLException {
        String query = "SELECT COUNT(*) FROM bikes WHERE BikeUID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bikeID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    public static void changeBikeStatus(String bikeUID, String newStatus) {
        // This method is not used in the current implementation.
    }
}