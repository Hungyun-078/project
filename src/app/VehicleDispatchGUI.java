package app;

import method.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class VehicleDispatchGUI {

    private static JFrame frame;
    private static JTextField dockUIDField;
    private static JTextField bikeUIDField;
    private static JTextArea outputArea;

    public static void main(String[] args) {
        frame = new JFrame("Vehicle Dispatch");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel dockUIDLabel = new JLabel("Dock UID:");
        dockUIDLabel.setBounds(10, 20, 80, 25);
        panel.add(dockUIDLabel);

        dockUIDField = new JTextField(20);
        dockUIDField.setBounds(100, 20, 165, 25);
        panel.add(dockUIDField);

        JLabel bikeUIDLabel = new JLabel("Bike UID:");
        bikeUIDLabel.setBounds(10, 50, 80, 25);
        panel.add(bikeUIDLabel);

        bikeUIDField = new JTextField(20);
        bikeUIDField.setBounds(100, 50, 165, 25);
        panel.add(bikeUIDField);

        JButton assignButton = new JButton("Assign Bike to Dock");
        assignButton.setBounds(10, 80, 200, 25);
        panel.add(assignButton);
        assignButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String dockUID = dockUIDField.getText();
                String bikeUID = bikeUIDField.getText();
                try {
                    VehicleDispatch.assignBikeToDock(bikeUID, dockUID);
                    outputArea.setText("Assigned bike " + bikeUID + " to dock " + dockUID);
                } catch (SQLException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        JButton removeButton = new JButton("Remove Bike from Dock");
        removeButton.setBounds(10, 110, 200, 25);
        panel.add(removeButton);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String dockUID = dockUIDField.getText();
                try {
                    VehicleDispatch.removeBikeFromDock(dockUID);
                    outputArea.setText("Removed bike from dock " + dockUID);
                } catch (SQLException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        JButton dispatchAllButton = new JButton("Dispatch All Bikes");
        dispatchAllButton.setBounds(10, 140, 200, 25);
        panel.add(dispatchAllButton);
        dispatchAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    VehicleDispatch.dispatchAllBikesEvenly();
                    outputArea.setText("Dispatched all bikes to available docks.");
                } catch (SQLException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        JButton removeAllButton = new JButton("Remove All Bikes");
        removeAllButton.setBounds(10, 170, 200, 25);
        panel.add(removeAllButton);
        removeAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    VehicleDispatch.removeAllBikes();
                    outputArea.setText("Removed all bikes from docks.");
                } catch (SQLException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        outputArea = new JTextArea();
        outputArea.setBounds(10, 200, 360, 50);
        panel.add(outputArea);
    }
}
