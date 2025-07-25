package method;
import AdminMenu.GUI_Repair;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class repair_db extends GUI_Repair implements ActionListener{
	public static Connection getConnection() throws SQLException{
		//connect to database
        String url = "jdbc:mysql://localhost:3306/ubike";
        String user = "root";
        String password = "Ubikeproject5";
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }
	
	public static String reportBikeID(String bike) throws SQLException {
        String query = "SELECT BikeUID FROM bikes WHERE BikeUID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bike);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("bikeUID");
                } else {
                    return null;
                }
            }
        }
    }
	
	public static String reportDockID(String dock) throws SQLException {
		String query = "SELECT DockUID FROM docks WHERE DockUID = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dock);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("DockUID");
                } else {
                    return null;
                }
            }
        }
    }
	
	public static String reportBoth(String bike, String dock) throws SQLException {
		String query1 = "SELECT BikeUID FROM bikes WHERE BikeUID = ?";
		String query2 = "SELECT DockUID FROM docks WHERE DockUID = ?";
		boolean foundInTable1 = false;
        boolean foundInTable2 = false;
        
		try(Connection conn = getConnection();
			PreparedStatement stmt1 = conn.prepareStatement(query1);
			PreparedStatement stmt2 = conn.prepareStatement(query2)){
			
			stmt1.setString(1, "%" + bike + "%");
            try (ResultSet rs1 = stmt1.executeQuery()) {
                if (rs1.next()) {
                    foundInTable1 = true;
                    System.out.println("Results from table1:");
                    do {
                        System.out.println("Column1: " + rs1.getString("column1"));
                    } while (rs1.next());
                }
            }

            // Search in table2
            stmt2.setString(1, "%" + dock + "%");
            try (ResultSet rs2 = stmt2.executeQuery()) {
                if (rs2.next()) {
                    foundInTable2 = true;
                    System.out.println("Results from table2:");
                    do {
                        System.out.println("Column2: " + rs2.getString("column2"));
                    } while (rs2.next());
                }
            }
        }
		return query2;
      
    }
	
	

}
