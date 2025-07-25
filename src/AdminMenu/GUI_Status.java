package AdminMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import method.Status_db;
import javax.swing.*;

public class GUI_Status extends JFrame implements ActionListener {

    private static JFrame frame;
    private static JLabel q;
    private static JLabel sc;
    private static JButton searchButton;
    private static JButton changeButton;
    private static JButton showAbnormalButton;
    public static String bikeID;
    public GUI_Status() {
        initComponents();
    }
    private void initComponents(){
        frame = new JFrame();
        JPanel panel = new JPanel();
        frame.setTitle("Repairer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setSize(420, 420);
        frame.add(panel);
        panel.setLayout(null);
        
        // A hint for User to choose whether they want to search or change bikeID
        q = new JLabel("What do you want to do?");
        q.setBounds(30, 20, 200, 20);
        panel.add(q);
        
        // Click on search Button will take you to the "search" page
        searchButton = new JButton("Search bike status");
        searchButton.setBounds(20, 80, 150, 25);
        searchButton.addActionListener(new GUI_Status());
        panel.add(searchButton);
        
        // Click on change Button will take you to the "change" page
        changeButton = new JButton("Change bike status");
        changeButton.setBounds(200, 80, 150, 25);
        changeButton.addActionListener(new GUI_Status());
        panel.add(changeButton);
        
        // New button to show all abnormal bikes
        showAbnormalButton = new JButton("Show abnormal bikes");
        showAbnormalButton.setBounds(20, 120, 200, 25);
        showAbnormalButton.addActionListener(new GUI_Status());
        panel.add(showAbnormalButton);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            // create the "search" page
            JFrame searchFrame = new JFrame();
            JPanel searchPanel = new JPanel();
            searchFrame.setTitle("Search bike status");
            searchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            searchFrame.setResizable(true);
            searchFrame.setSize(420, 420);
            searchFrame.add(searchPanel);
            searchPanel.setLayout(null);

            JLabel labelBike = new JLabel("Enter BikeID:");
            labelBike.setBounds(10, 20, 80, 25);
            searchPanel.add(labelBike);

            JTextField bikeIDText = new JTextField();
            bikeIDText.setBounds(100, 20, 165, 25);
            searchPanel.add(bikeIDText);

            sc = new JLabel("");
            sc.setBounds(10, 200, 300, 25);
            searchPanel.add(sc);

            JButton button = new JButton("Submit");
            button.setBounds(10, 80, 80, 25);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    bikeID = bikeIDText.getText();
                    System.out.println("Search for: " + bikeID);
                    try {
                        String result = Status_db.searchBikeID(bikeID);
                        if (result != null) {
                            sc.setText("Bike status: " + result);
                            System.out.println("found");
                        } else {
                            sc.setText("Bike ID not found");
                            System.out.println("not found");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        sc.setText("Error: " + ex.getMessage());
                        System.out.println("error" + ex.getMessage());
                    }
                }
            });
            searchPanel.add(button);

            // Add Back button
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, 120, 80, 25);
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    searchFrame.dispose();
                    main(new String[0]);
                }
            });
            searchPanel.add(backButton);

            searchFrame.setVisible(true);
            frame.dispose();
        } else if (e.getSource() == changeButton) {
            // create the "change" page
            JFrame changeFrame = new JFrame();
            JPanel changePanel = new JPanel();
            changeFrame.setTitle("Change bike status");
            changeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            changeFrame.setResizable(true);
            changeFrame.setSize(420, 420);
            changeFrame.add(changePanel);
            changePanel.setLayout(null);

            JLabel labelBikeID = new JLabel("Enter bikeID :");
            labelBikeID.setBounds(10, 20, 150, 25);
            changePanel.add(labelBikeID);

            JTextField bikeIDText = new JTextField();
            bikeIDText.setBounds(200, 20, 165, 25);
            changePanel.add(bikeIDText);
            
            String[] statuses = {"Normal", "Broken", "Fixing", "Missing", "In use"};
            int yPosition = 50;
            for (String status : statuses) {
                JButton statusButton = new JButton(status);
                statusButton.setBounds(10, yPosition, 150, 25);
                statusButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        String bikeID = bikeIDText.getText();
                        try {
                            boolean success = Status_db.updateBikeID(bikeID, status);
                            if (success) {
                                sc.setText("Bike status updated to " + status + ".");
                                System.out.println("Success!");
                            } else {
                                sc.setText("Failed to update bike status.");
                                System.out.println("failed!");
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            sc.setText("Error: " + ex.getMessage());
                            System.out.println("Error: " + ex.getMessage());
                        }
                    }
                });
                changePanel.add(statusButton);
                yPosition += 30;
            }

            sc = new JLabel("");
            sc.setBounds(10, yPosition + 10, 300, 25);  // Adjusted position to be under all buttons
            changePanel.add(sc);

            // Add Back button
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, yPosition + 50, 80, 25);  // Position below status label
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    changeFrame.dispose();
                    main(new String[0]);
                }
            });
            changePanel.add(backButton);

            changeFrame.setVisible(true);
            frame.dispose();
        } else if (e.getSource() == showAbnormalButton) {
            // create the "show abnormal bikes" page
            JFrame showAbnormalFrame = new JFrame();
            JPanel showAbnormalPanel = new JPanel();
            showAbnormalFrame.setTitle("Show abnormal bikes");
            showAbnormalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            showAbnormalFrame.setResizable(true);
            showAbnormalFrame.setSize(420, 420);
            showAbnormalFrame.add(showAbnormalPanel);
            showAbnormalPanel.setLayout(null);

            JTextArea abnormalBikesArea = new JTextArea();
            abnormalBikesArea.setBounds(10, 20, 380, 300);
            showAbnormalPanel.add(abnormalBikesArea);

            try {
                List<String> abnormalBikes = Status_db.getAbnormalBikes();
                if (abnormalBikes.isEmpty()) {
                    abnormalBikesArea.setText("No abnormal bikes found.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (String bike : abnormalBikes) {
                        sb.append(bike).append("\n");
                    }
                    abnormalBikesArea.setText(sb.toString());
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                abnormalBikesArea.setText("Error: " + ex.getMessage());
                System.out.println("Error: " + ex.getMessage());
            }

            // Add Back button
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, 330, 80, 25);
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    showAbnormalFrame.dispose();
                    main(new String[0]);
                }
            });
            showAbnormalPanel.add(backButton);

            showAbnormalFrame.setVisible(true);
            frame.dispose();
        }
    }
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> {
            GUI_Status Status = new GUI_Status();
            Status.setVisible(true);
        });
        
    }
}