import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Blood_donation extends JFrame {
    public Blood_donation() {
        setTitle("🏠 Blood Management System");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Blood Bank System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(220, 20, 60));

        JButton donateBtn = new JButton("🩸 Blood Donation Portal");
        JButton acceptBtn = new JButton("❤️ Blood Acceptance Portal");
        JButton searchBtn = new JButton("🔍 Search Blood Availability");

        donateBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        acceptBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        searchBtn.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.add(donateBtn);
        buttonPanel.add(acceptBtn);
        buttonPanel.add(searchBtn);

        add(welcomeLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        donateBtn.addActionListener(e -> {
            Blood_donation donationApp = new Blood_donation();
            donationApp.setVisible(true);
            dispose();
        });

        acceptBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Acceptance portal coming soon with hospital linking, blood request logs, and donor match!", "Work in Progress", JOptionPane.INFORMATION_MESSAGE);
        });

        searchBtn.addActionListener(e -> {
            String type = JOptionPane.showInputDialog(this, "Enter blood type to search:");
            if (type != null && !type.trim().isEmpty()) {
                searchBlood(type.trim().toUpperCase());
            }
        });
    }

    private void searchBlood(String bloodType) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/BloodDonationDB", "root", "yourpassword");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT D.name, D.phone, B.location FROM Donors D JOIN Donations DO ON D.donor_id = DO.donor_id JOIN BloodBanks B ON DO.bank_id = B.bank_id WHERE D.blood_type = ?")) {
            stmt.setString(1, bloodType);
            ResultSet rs = stmt.executeQuery();
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                result.append("Donor: ").append(rs.getString("name"))
                      .append(" | Phone: ").append(rs.getString("phone"))
                      .append(" | Location: ").append(rs.getString("location"))
                      .append("\n");
            }
            if (result.length() == 0) {
                JOptionPane.showMessageDialog(this, "No donors found for blood type: " + bloodType);
            } else {
                JTextArea output = new JTextArea(result.toString());
                output.setEditable(false);
                JScrollPane pane = new JScrollPane(output);
                JOptionPane.showMessageDialog(this, pane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching for blood type: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Blood_donation().setVisible(true));
    }
}
