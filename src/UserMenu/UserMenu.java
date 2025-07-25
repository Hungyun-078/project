/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UserMenu;



import app.HistoryMenu;
import app.ProfileMenu;
import app.SQL;
import menu.menu;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import org.json.JSONObject;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;

/**
 *
 * @author sam20
 */
public class UserMenu extends javax.swing.JFrame {
    
    /**
     * Creates new form menu
     * @param userid
     */
    private JXMapViewer mapViewer;
    private Point initialClick;
    private GeoPosition initialPosition;
    private double initialZoom;
    private EventWaypoint event;
    private final Set<MyWaypoint> waypoints = new HashSet<>();
    
    
    public UserMenu() {
        initComponents();
        jButton1.setText(SQL.getName());
        initializeMap();
    }

    private void initializeMap() {
        // Create a JXMapViewer
        mapViewer = new JXMapViewer();  // Use the class-level variable

        // Setup a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Set the initial focus
        initialPosition = new GeoPosition(25.0330, 121.5654);
        mapViewer.setZoom(4);
        mapViewer.setAddressLocation(initialPosition);

        // Fetch station data and create waypoints
        /*Set<MyWaypoint> waypoints = fetchStationData();
        WaypointPainter<MyWaypoint> waypointPainter = new WaypointRender();
        addWaypoint(new MyWaypoint(new GeoPosition(11.525000, 104.929033)));*/
        Set<MyWaypoint> waypoints = fetchStationData();
        initWaypoint();
        
        

        // Set the overlay painter
        
        MouseInputListener mm = new PanMouseInputListener(mapViewer);
        // Add mouse listeners for panning and zooming
        mapViewer.addMouseListener(mm);
        mapViewer.addMouseMotionListener(mm);  // Enable panning
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        event = getEvent();
        // Custom listener to track initial click for panning
        

        

        // Add mapViewer to the map panel
        map_panel.setLayout(new BorderLayout());
        map_panel.add(mapViewer, BorderLayout.CENTER);
        map_panel.setPreferredSize(new Dimension(800, 600));
        map_panel.setMinimumSize(new Dimension(800, 600));
        map_panel.setMaximumSize(new Dimension(800, 600));
        map_panel.setSize(new Dimension(800, 600));
        map_panel.revalidate();
        map_panel.repaint();
    }
    private void addWaypoint(MyWaypoint waypoint) {
        for (MyWaypoint d : waypoints) {
            mapViewer.remove(d.getButton());
        }
        waypoints.add(waypoint);
        initWaypoint();
    }
    private EventWaypoint getEvent() {
        return new EventWaypoint() {
            @Override
            public void selected(MyWaypoint waypoint) {
                
            }
        };
    }

    private void initWaypoint() {
        WaypointPainter<MyWaypoint> wp = new WaypointRender();
        wp.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(wp);
        for (MyWaypoint d : waypoints) {
            mapViewer.add(d.getButton());
        }
    }
    

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Ubike";
    private static final String USER = "root";
    private static final String PASS = "0101";
    public static void showStationInfo(String stationUID) {
        try {
            // Step 1: Retrieve station information from the database
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();

            String sql = "SELECT StationName, StationAddress, BikesCapacity FROM ntustations WHERE StationUID = '" + stationUID + "' " +
                         "UNION ALL " +
                         "SELECT StationName, StationAddress, BikesCapacity FROM taipei WHERE StationUID = '" + stationUID + "' " +
                         "UNION ALL " +
                         "SELECT StationName, StationAddress, BikesCapacity FROM newtaipei WHERE StationUID = '" + stationUID + "'";
            ResultSet rs = stmt.executeQuery(sql);

            String stationNameZh = "";
            String stationNameEn = "";
            String stationAddressZh = "";
            String stationAddressEn = "";
            int bikeCapacity = 0;

            if (rs.next()) {
                String stationNameJson = rs.getString("StationName");
                String stationAddressJson = rs.getString("StationAddress");

                JSONObject stationNameObj = new JSONObject(stationNameJson);
                JSONObject stationAddressObj = new JSONObject(stationAddressJson);

                stationNameZh = stationNameObj.getString("Zh_tw").replace("YouBike2.0_", "");
                stationNameEn = stationNameObj.getString("En").replace("YouBike2.0_", "");
                stationAddressZh = stationAddressObj.getString("Zh_tw");
                stationAddressEn = stationAddressObj.getString("En");

                bikeCapacity = rs.getInt("BikesCapacity");
            }

            // Step 2: Retrieve dock information from the database
            sql = "SELECT DockUID, Bike FROM ubike.docks WHERE StationUID = '" + stationUID + "'";
            rs = stmt.executeQuery(sql);

            List<String> dockStatuses = new ArrayList<>();

            while (rs.next()) {
                String bike = rs.getString("Bike");
                if (bike != null && !bike.isEmpty()) {
                    dockStatuses.add("available");
                } else {
                    dockStatuses.add("null");
                }
            }
            

            // If there are fewer docks than the bike capacity, add remaining docks as "null"
            

            rs.close();
            stmt.close();
            conn.close();

            // Step 3: Display the information
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Use a font that supports Chinese characters
            Font chineseFont = new Font("SansSerif", Font.BOLD, 20);

            JLabel nameLabel = new JLabel(stationNameZh);
            nameLabel.setFont(chineseFont);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel enNameLabel = new JLabel(stationNameEn);
            enNameLabel.setFont(chineseFont);
            enNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel addressLabel = new JLabel("Address: " + stationAddressZh + " / " + stationAddressEn);
            addressLabel.setFont(chineseFont);
            addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel availableBikesLabel = new JLabel("Available Bikes: " + dockStatuses.stream().filter(s -> s.equals("available")).count());
            availableBikesLabel.setFont(chineseFont);
            availableBikesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel availableDocksLabel = new JLabel("Available Docks: " + dockStatuses.stream().filter(s -> s.equals("null")).count());
            availableDocksLabel.setFont(chineseFont);
            availableDocksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton detailsButton = new JButton("More Details");
            detailsButton.setFont(chineseFont);
            detailsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add action listener to the details button
            detailsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showDockDetails(stationUID, getDockStatuses(stationUID));
                }
            });

            infoPanel.add(nameLabel);
            infoPanel.add(enNameLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing
            infoPanel.add(addressLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing
            infoPanel.add(availableBikesLabel);
            infoPanel.add(availableDocksLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add some spacing
            infoPanel.add(detailsButton); // Add the button to the panel

            JFrame infoFrame = new JFrame("Station Information");
            infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            infoFrame.add(infoPanel);
            infoFrame.pack();
            infoFrame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getDockStatuses(String stationUID) {
        List<String> dockStatuses = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT DockUID, Bike FROM ubike.docks WHERE StationUID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stationUID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String bike = rs.getString("Bike");
                if (bike != null && !bike.isEmpty()) {
                    dockStatuses.add("available");
                } else {
                    dockStatuses.add("null");
                }
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dockStatuses;
    }

    public static void showDockDetails(String stationUID, List<String> dockStatuses) {
        JPanel dockPanel = new JPanel();
        dockPanel.setLayout(new BoxLayout(dockPanel, BoxLayout.Y_AXIS));
        dockPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font chineseFont = new Font("SansSerif", Font.PLAIN, 16);

        JFrame dockFrame = new JFrame("Dock Details");
        dockFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        boolean isRenting = checkUserRenting(SQL.getUserID());

        for (int i = 0; i < dockStatuses.size(); i++) {
            String status = dockStatuses.get(i);
            JPanel dockInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel dockLabel = new JLabel("Dock " + (i + 1) + ": " + status);
            dockLabel.setFont(chineseFont);
            dockInfoPanel.add(dockLabel);

            if (!isRenting && "available".equals(status)) {
                JButton rentButton = new JButton("rent");
                rentButton.setFont(chineseFont);
                int dockIndex = i; // Capture the current index
                rentButton.addActionListener(e -> {
                    rentBike(stationUID, dockIndex, dockFrame);
                });
                dockInfoPanel.add(rentButton);
            } else if (isRenting && "null".equals(status)) {
                JButton returnButton = new JButton("return");
                returnButton.setFont(chineseFont);
                int dockIndex = i; // Capture the current index
                returnButton.addActionListener(e -> {
                    returnBike(stationUID, dockIndex, dockFrame);
                });
                dockInfoPanel.add(returnButton);
            }

            dockPanel.add(dockInfoPanel);
        }

        dockFrame.add(new JScrollPane(dockPanel));
        dockFrame.pack();
        dockFrame.setVisible(true);
    }

    public static boolean checkUserRenting(String userID) {
        boolean isRenting = false;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM ubike.renting WHERE UserID = ?");
            pstmt.setString(1, userID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                isRenting = true;
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRenting;
    }

    public static void rentBike(String stationUID, int dockIndex, JFrame dockFrame) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement("SELECT balance FROM ubike.user WHERE UserID = ?");
            pstmt.setString(1, SQL.getUserID());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int balance = rs.getInt("balance");
                if (balance > 0) {
                    // Fetch the BikeUID from the docks table
                    pstmt = conn.prepareStatement("SELECT Bike FROM ubike.docks WHERE StationUID = ? LIMIT ?, 1");
                    pstmt.setString(1, stationUID);
                    pstmt.setInt(2, dockIndex);
                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        String bikeUID = rs.getString("Bike");

                        if (bikeUID != null && !bikeUID.isEmpty()) {
                            // Proceed with renting the bike
                            String updateSQL = "INSERT INTO ubike.renting (UserID, StartTime, StartStation, BikeUID) VALUES (?, NOW(), ?, ?)";
                            PreparedStatement updatePstmt = conn.prepareStatement(updateSQL);
                            updatePstmt.setString(1, SQL.getUserID());
                            updatePstmt.setString(2, stationUID);
                            updatePstmt.setString(3, bikeUID);

                            updatePstmt.executeUpdate();
                            
                            // Update docks table to set Bike to null
                            String updateDockSQL = "UPDATE ubike.docks SET Bike = NULL WHERE StationUID = ? AND DockUID = ?";
                            PreparedStatement updateDockPstmt = conn.prepareStatement(updateDockSQL);
                            updateDockPstmt.setString(1, stationUID);
                            updateDockPstmt.setInt(2, dockIndex);
                            updateDockPstmt.executeUpdate();
                            
                            JOptionPane.showMessageDialog(null, "Bike rented successfully!");
                            dockFrame.dispose(); // Close the dock details window
                             // Refresh the dock details
                        } else {
                            JOptionPane.showMessageDialog(null, "Bike not available at this dock.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please top up first.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "User not found.");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void returnBike(String stationUID, int dockIndex, JFrame dockFrame) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // Get the rental details
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ubike.renting WHERE UserID = ?");
            pstmt.setString(1, SQL.getUserID());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String startStation = rs.getString("StartStation");
                String bikeUID = rs.getString("BikeUID");
                String startTime = rs.getString("StartTime");

                // Delete the rental record from the renting table
                pstmt = conn.prepareStatement("DELETE FROM ubike.renting WHERE UserID = ?");
                pstmt.setString(1, SQL.getUserID());
                pstmt.executeUpdate();

                // Insert the rental record into the history table
                String insertHistorySQL = "INSERT INTO ubike.`" + SQL.getUserID() + "_history` (UserID, StartTime, StartStation, BikeUID, EndTime, StopStation, Cost) VALUES (?, ?, ?, ?, NOW(), ?, ?)";
                PreparedStatement insertHistoryPstmt = conn.prepareStatement(insertHistorySQL);
                insertHistoryPstmt.setString(1, SQL.getUserID());
                insertHistoryPstmt.setString(2, startTime);
                insertHistoryPstmt.setString(3, startStation);
                insertHistoryPstmt.setString(4, bikeUID);
                insertHistoryPstmt.setString(5, stationUID);
                // For simplicity, let's assume a fixed cost for each ride
                insertHistoryPstmt.setDouble(6, 80.00); // Adjust the cost as needed

                insertHistoryPstmt.executeUpdate();

                // Update the docks table to set the Bike column with returned bike UID
                String updateDockSQL = "UPDATE ubike.docks SET Bike = ? WHERE StationUID = ? AND DockUID = ?";
                PreparedStatement updateDockPstmt = conn.prepareStatement(updateDockSQL);
                updateDockPstmt.setString(1, bikeUID);
                updateDockPstmt.setString(2, stationUID);
                updateDockPstmt.setInt(3, dockIndex);
                updateDockPstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Bike returned successfully!");
                dockFrame.dispose(); // Close the dock details window
                 // Refresh the dock details

                insertHistoryPstmt.close();
            } else {
                JOptionPane.showMessageDialog(null, "No active rental found for this user.");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<MyWaypoint> fetchStationData() {
        
        Connection conn = null;
        Statement stmt = null;

        try {
            // Database credentials
            String DB_URL = "jdbc:mysql://localhost:3306/Ubike";
            String USER = "root";
            String PASS = "0101";

            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Execute a query
            stmt = conn.createStatement();
            String sql = "SELECT StationUID, StationPosition FROM ntustations " +
                     "UNION ALL " +
                     "SELECT StationUID, StationPosition FROM taipei " +
                     "UNION ALL " +
                     "SELECT StationUID, StationPosition FROM newtaipei";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                String nameStr = rs.getString("StationUID");
                String positionStr = rs.getString("StationPosition");
                JSONObject positionJson = new JSONObject(positionStr);
                double lat = positionJson.getDouble("PositionLat");
                double lon = positionJson.getDouble("PositionLon");
                waypoints.add(new MyWaypoint(nameStr, event, new GeoPosition(lat, lon)));
            }

            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return waypoints;
    }
    


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserMenu userMenuInstance = new UserMenu();
            userMenuInstance.setVisible(true);
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Data = new javax.swing.JPopupMenu();
        Profile = new javax.swing.JMenuItem();
        History = new javax.swing.JMenuItem();
        Card = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        map_panel = new javax.swing.JPanel();
        kButton2 = new keeptoo.KButton();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        kGradientPanel1 = new keeptoo.KGradientPanel();
        jTextField1 = new javax.swing.JTextField();

        Profile.setText("Profile");
        Profile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProfileActionPerformed(evt);
            }
        });

        History.setText("History");
        History.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HistoryActionPerformed(evt);
            }
        });

        Card.setText("Card");
        Card.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CardActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        map_panel.setLayout(new java.awt.BorderLayout());

        kButton2.setText("Search");
        kButton2.setkAllowTab(false);
        kButton2.setkEndColor(new java.awt.Color(81, 127, 181));
        kButton2.setkHoverEndColor(new java.awt.Color(57, 176, 118));
        kButton2.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        kButton2.setkHoverStartColor(new java.awt.Color(75, 124, 100));
        kButton2.setkPressedColor(new java.awt.Color(22, 66, 45));
        kButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kButton2ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(33, 145, 236));

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("name");
        jButton1.setContentAreaFilled(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(32, 123, 198));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Ubike");
        jLabel1.setOpaque(true);

        jButton2.setBackground(new java.awt.Color(220, 86, 86));
        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Close");
        jButton2.setContentAreaFilled(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusPainted(false);
        jButton2.setOpaque(true);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Logout");
        jButton3.setContentAreaFilled(false);
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFocusPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 430, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        kGradientPanel1.setBackground(new java.awt.Color(255, 255, 255));
        kGradientPanel1.setkEndColor(new java.awt.Color(115, 217, 167));
        kGradientPanel1.setkFillBackground(false);
        kGradientPanel1.setkStartColor(new java.awt.Color(75, 124, 100));

        jTextField1.setBorder(null);

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1)
                .addContainerGap())
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(kButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
            .addComponent(map_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(kGradientPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(map_panel, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void kButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kButton2ActionPerformed

        
    }//GEN-LAST:event_kButton2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        menu Menu = new menu();
        Menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void ProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProfileActionPerformed
        ProfileMenu profilemenu = new ProfileMenu();
        profilemenu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_ProfileActionPerformed

    private void HistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HistoryActionPerformed
        HistoryMenu historymenu = new HistoryMenu();
        historymenu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_HistoryActionPerformed

    private void CardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CardActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Data.add(Profile);
        Data.add(History);
        Data.add(Card);
        setVisible(true);
        Data.show(jButton1, jButton1.getWidth()/2, jButton1.getHeight()/2);
    }//GEN-LAST:event_jButton1ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Card;
    private javax.swing.JPopupMenu Data;
    private javax.swing.JMenuItem History;
    private javax.swing.JMenuItem Profile;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private keeptoo.KButton kButton2;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JPanel map_panel;
    // End of variables declaration//GEN-END:variables

    
    private void open_site() {
    	
    }

}
