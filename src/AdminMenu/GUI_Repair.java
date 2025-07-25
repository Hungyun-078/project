package AdminMenu;

import UserMenu.UserMenu;
import app.SQL;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import method.*;

public class GUI_Repair extends JFrame implements ActionListener {

    private JLabel m1;
    private String descrip;
    private JLabel labelDescrip;
    private JTextField descripText;
    private JLabel success;
    private JLabel sc1;
    private JLabel sc2;
    private JComboBox<String> select;
    public String bike;
    public String dock;

    public GUI_Repair() {
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        setTitle("Repairer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(500, 500);
        add(panel);
        panel.setLayout(null);

        // A message for User
        m1 = new JLabel("What do you want to report?");
        m1.setBounds(30, 20, 400, 20);
        panel.add(m1);

        // Add a drop-down list for the user to choose
        select = new JComboBox<>();
        select.setBounds(30, 50, 400, 20);
        select.addItem("Please choose");
        select.addItem("Report a bike");
        select.addItem("Report a dock");
        select.addItem("Report both");
        select.addActionListener(this);
        panel.add(select);

        success = new JLabel("");
        success.setBounds(10, 210, 300, 25);
        panel.add(success);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String reportItem = (String) select.getSelectedItem();

        if (reportItem.equals("Please choose")) {
            System.out.println("user haven't choose");
            success.setText("Please choose what do you want to do.");
        } else if (reportItem.equals("Report a bike")) {
            reportBike();
        } else if (reportItem.equals("Report a dock")) {
            reportDock();
        } else if (reportItem.equals("Report both")) {
            reportBoth();
        }
    }

    private void reportBike() {
        JFrame bikeFrame = new JFrame();
        JPanel bikePanel = new JPanel();
        bikeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bikeFrame.setResizable(true);
        bikeFrame.setSize(420, 420);
        bikeFrame.add(bikePanel);
        bikePanel.setLayout(null);

        JLabel labelBike = new JLabel("Enter BikeID:");
        labelBike.setBounds(10, 20, 80, 25);
        bikePanel.add(labelBike);

        JTextField bikeIDText = new JTextField();
        bikeIDText.setBounds(100, 20, 165, 25);
        bikePanel.add(bikeIDText);

        labelDescrip = new JLabel("Write down the problems:");
        labelDescrip.setBounds(10, 50, 150, 25);
        bikePanel.add(labelDescrip);

        descripText = new JTextField();
        descripText.setBounds(10, 80, 165, 25);
        bikePanel.add(descripText);

        sc1 = new JLabel("");
        sc1.setBounds(10, 110, 300, 25);
        bikePanel.add(sc1);

        sc2 = new JLabel("");
        sc2.setBounds(10, 140, 300, 25);
        bikePanel.add(sc2);

        JButton button = new JButton("Submit");
        button.setBounds(10, 170, 80, 25);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                bike = bikeIDText.getText();
                descrip = descripText.getText();
                System.out.println("Search for:" + bike);
                try {
                    String result = repair_db.reportBikeID(bike);
                    if (result != null) {
                        sc1.setText("Problem reported!");
                        sc2.setText("Problem: " + descrip);
                        boolean success = Status_db.updateBikeID(bike, "Fixing");
                        if (success) {
                            System.out.println("Report changed status to fixing success!");
                        } else {
                            System.out.println("Report failed to update bike status.");
                        }
                        System.out.println("Reported successfully");
                    } else {
                        sc1.setText("Bike ID not found");
                        System.out.println("not found");
                        System.out.println("failed!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    sc1.setText("Error: " + ex.getMessage());
                    System.out.println("error" + ex.getMessage());
                }
            }
        });
        bikePanel.add(button);

        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 330, 80, 25);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                bikeFrame.dispose();
                main(new String[0]);
            }
        });
        bikePanel.add(backButton);

        bikeFrame.setVisible(true);
        this.dispose();
    }

    private void reportDock() {
        JFrame dockFrame = new JFrame();
        JPanel dockPanel = new JPanel();
        dockFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dockFrame.setResizable(true);
        dockFrame.setSize(420, 420);
        dockFrame.add(dockPanel);
        dockPanel.setLayout(null);

        JLabel labelDock = new JLabel("Enter DockID:");
        labelDock.setBounds(10, 20, 80, 25);
        dockPanel.add(labelDock);

        JTextField dockIDText = new JTextField();
        dockIDText.setBounds(100, 20, 165, 25);
        dockPanel.add(dockIDText);

        labelDescrip = new JLabel("Write down the problems:");
        labelDescrip.setBounds(10, 50, 150, 25);
        dockPanel.add(labelDescrip);

        descripText = new JTextField();
        descripText.setBounds(10, 80, 165, 25);
        dockPanel.add(descripText);

        sc1 = new JLabel("");
        sc1.setBounds(10, 110, 300, 25);
        dockPanel.add(sc1);

        sc2 = new JLabel("");
        sc2.setBounds(10, 140, 300, 25);
        dockPanel.add(sc2);

        JButton button = new JButton("Submit");
        button.setBounds(10, 170, 80, 25);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dock = dockIDText.getText();
                descrip = descripText.getText();
                System.out.println("Search for:" + dock);
                try {
                    String result = repair_db.reportDockID(dock);
                    if (result != null) {
                        sc1.setText("Problem reported!");
                        sc2.setText("Problem: " + descrip);
                        System.out.println("Reported successfully");
                    } else {
                        sc1.setText("Dock ID not found");
                        System.out.println("not found");
                        System.out.println("failed!");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    sc1.setText("Error: " + ex.getMessage());
                    System.out.println("error" + ex.getMessage());
                }
            }
        });
        dockPanel.add(button);

        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 330, 80, 25);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dockFrame.dispose();
                main(new String[0]);
            }
        });
        dockPanel.add(backButton);

        dockFrame.setVisible(true);
        this.dispose();
    }

    private void reportBoth() {
        JFrame bothFrame = new JFrame();
        JPanel bothPanel = new JPanel();
        bothFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bothFrame.setResizable(true);
        bothFrame.setSize(420, 420);
        bothFrame.add(bothPanel);
        bothPanel.setLayout(null);

        JLabel labelBike = new JLabel("Enter BikeID:");
        labelBike.setBounds(10, 20, 80, 25);
        bothPanel.add(labelBike);

        JTextField bikeIDText = new JTextField();
        bikeIDText.setBounds(100, 20, 165, 25);
        bothPanel.add(bikeIDText);

        JLabel labelDock = new JLabel("Enter DockID:");
        labelDock.setBounds(10, 50, 80, 25);
        bothPanel.add(labelDock);

        JTextField dockIDText = new JTextField();
        dockIDText.setBounds(100, 50, 165, 25);
        bothPanel.add(dockIDText);

        labelDescrip = new JLabel("Write down the problems:");
        labelDescrip.setBounds(10, 80, 150, 25);
        bothPanel.add(labelDescrip);

        descripText = new JTextField();
        descripText.setBounds(10, 110, 165, 25);
        bothPanel.add(descripText);

        sc1 = new JLabel("");
        sc1.setBounds(10, 140, 300, 25);
        bothPanel.add(sc1);

        sc2 = new JLabel("");
        sc2.setBounds(10, 170, 300, 25);
        bothPanel.add(sc2);

        JButton button = new JButton("Submit");
        button.setBounds(10, 200, 80, 25);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dock = dockIDText.getText();
                bike = bikeIDText.getText();
                descrip = descripText.getText();
                System.out.println("Search for:" + dock);
                System.out.println("Search for:" + bike);
                try {
                    String result = repair_db.reportBoth(bike, dock);
                    if (result != null) {
                        sc1.setText("Problem reported!");
                        sc2.setText("Problem: " + descrip);
                        boolean success = Status_db.updateBikeID(bike, "Fixing");
                        if (success) {
                            System.out.println("Report changed status to fixing success!");
                        } else {
                            System.out.println("Report failed to update bike status.");
                        }
                        System.out.println("Reported successfully");
                    } else {
                        sc1.setText("Bike or Dock ID not found");
                        System.out.println("not found");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    sc1.setText("Error: " + ex.getMessage());
                    System.out.println("error" + ex.getMessage());
                }
            }
        });
        bothPanel.add(button);

        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 330, 80, 25);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                bothFrame.dispose();
                main(new String[0]);
            }
        });
        bothPanel.add(backButton);

        bothFrame.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI_Repair repair = new GUI_Repair();
            repair.setVisible(true);
        });
    }
}
