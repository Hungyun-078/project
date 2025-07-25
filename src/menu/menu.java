/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package menu;


import org.jxmapviewer.JXMapViewer;
import javax.swing.*;
import java.awt.*;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;
import app.Log;
import org.json.JSONObject;
import javax.swing.event.MouseInputListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author sam20
 */
public class menu extends javax.swing.JFrame {
    
    private JXMapViewer mapViewer;
    private Point initialClick;
    private GeoPosition initialPosition;
    private double initialZoom;
    private EventWaypoint event;
    private final Set<MyWaypoint> waypoints = new HashSet<>();
    
    
    public menu() {
        initComponents();
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
    

    public static void showStationInfo(String stationUID) {
        try {
            // Step 1: Retrieve station information from the database
            String DB_URL = "jdbc:mysql://localhost:3306/Ubike";
            String USER = "root";
            String PASS = "0101";
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();

            String sql = "SELECT StationName, StationAddress, BikesCapacity FROM ntustations WHERE StationUID = '" + stationUID + "' " +
                         "UNION ALL " +
                         "SELECT StationName, StationAddress, BikesCapacity FROM taipei WHERE StationUID = '" + stationUID + "' " +
                         "UNION ALL " +
                         "SELECT StationName, StationAddress, BikesCapacity FROM newtaipei WHERE StationUID = '" + stationUID + "'";
            ResultSet rs = stmt.executeQuery(sql);
            
            String stationName = "";
            String stationAddress = "";
            int bikeCapacity = 0;

            if (rs.next()) {
                String stationNameJson = rs.getString("StationName");
                String stationAddressJson = rs.getString("StationAddress");
                
                JSONObject stationNameObj = new JSONObject(stationNameJson);
                JSONObject stationAddressObj = new JSONObject(stationAddressJson);
                
                stationName = stationNameObj.getString("Zh_tw") + " / " + stationNameObj.getString("En");
                stationAddress = stationAddressObj.getString("Zh_tw") + " / " + stationAddressObj.getString("En");
                
                bikeCapacity = rs.getInt("BikesCapacity");
            }

            // Step 2: Retrieve dock information from the database
            sql = "SELECT COUNT(*) AS totalDocks, SUM(CASE WHEN Bike IS NOT NULL AND Bike <> '' THEN 1 ELSE 0 END) AS availableBikes " +
                  "FROM ubike.docks WHERE StationUID = '" + stationUID + "'";
            rs = stmt.executeQuery(sql);

            int availableBikes = 0;
            

            if (rs.next()) {
                
                availableBikes = rs.getInt("availableBikes");
            }

            int availableDocks = bikeCapacity - availableBikes;

            rs.close();
            stmt.close();
            conn.close();

            // Step 3: Display the information
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new GridLayout(0, 1));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Use a font that supports Chinese characters
            Font chineseFont = new Font("SansSerif", Font.BOLD, 20);

            JLabel nameLabel = new JLabel(stationName);
            nameLabel.setFont(chineseFont);
            JLabel addressLabel = new JLabel("Address: " + stationAddress);
            addressLabel.setFont(chineseFont);
            JLabel availableBikesLabel = new JLabel("Available Bikes: " + availableBikes);
            availableBikesLabel.setFont(chineseFont);
            JLabel availableDocksLabel = new JLabel("Available Docks: " + availableDocks);
            availableDocksLabel.setFont(chineseFont);

            infoPanel.add(nameLabel);
            infoPanel.add(addressLabel);
            infoPanel.add(availableBikesLabel);
            infoPanel.add(availableDocksLabel);

            JFrame infoFrame = new JFrame("Station Information");
            infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            infoFrame.add(infoPanel);
            infoFrame.pack();
            infoFrame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String fetchStationInfo(GeoPosition pos) {
        // Replace this with the actual code to fetch detailed station information
        return "Station at: " + pos.getLatitude() + ", " + pos.getLongitude() + "\nAvailable bikes: 10\nEmpty docks: 5";
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
            menu menuInstance = new menu();
            menuInstance.setVisible(true);
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

        jPanel1 = new javax.swing.JPanel();
        map_panel = new javax.swing.JPanel();
        kButton2 = new keeptoo.KButton();
        jPanel2 = new javax.swing.JPanel();
        log = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        kGradientPanel1 = new keeptoo.KGradientPanel();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        map_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        log.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        log.setForeground(new java.awt.Color(255, 255, 255));
        log.setText("Login");
        log.setContentAreaFilled(false);
        log.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        log.setFocusPainted(false);
        log.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(log, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 516, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(log, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void logActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logActionPerformed
        Log LoginFrame = new Log();
        LoginFrame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized

    }//GEN-LAST:event_formComponentResized

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private keeptoo.KButton kButton2;
    private keeptoo.KGradientPanel kGradientPanel1;
    private javax.swing.JButton log;
    private javax.swing.JPanel map_panel;
    // End of variables declaration//GEN-END:variables


    

}
