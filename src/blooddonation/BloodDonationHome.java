package blooddonation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BloodDonationHome extends JFrame {
    static final String DB_URL = "jdbc:mysql://localhost:3306/BloodDonationDB";
    static final String USER = "root";
    static final String PASS = "abby";

    JTextField nameField, ageField, bloodTypeField, phoneField, emailField, weightField, hemoglobinField, donationQtyField;
    JComboBox<Integer> donationBankIdCombo;
    JComboBox<String> genderCombo;
    JCheckBox healthyCheck, tattooCheck, medicalCheck, alcoholCheck, antibioticCheck, smokingCheck;
    JButton addDonorBtn, addDonationBtn;
    final String[] VALID_BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    public BloodDonationHome() {
        setTitle("🏠 Blood Bank Management System");
        setSize(550, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(250, 240, 245));

        JLabel title = new JLabel("Welcome to Blood Bank System", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(178, 34, 34));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton donateBtn = new JButton("Blood Donation Portal");
        JButton acceptBtn = new JButton("Blood Acceptance Portal");
        JButton searchBtn = new JButton("Search Blood Availability");
        JButton updateBtn = new JButton("Update Donor Information");

        donateBtn.setBackground(new Color(255, 182, 193));
        acceptBtn.setBackground(new Color(255, 160, 122));
        searchBtn.setBackground(new Color(176, 224, 230));
        updateBtn.setBackground(new Color(152, 251, 152));

        donateBtn.setFont(new Font("Arial", Font.BOLD, 16));
        acceptBtn.setFont(new Font("Arial", Font.BOLD, 16));
        searchBtn.setFont(new Font("Arial", Font.BOLD, 16));
        updateBtn.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBackground(new Color(250, 240, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        panel.add(donateBtn);
        panel.add(acceptBtn);
        panel.add(searchBtn);
        panel.add(updateBtn);

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        donateBtn.addActionListener(e -> openDonationPortal());
        acceptBtn.addActionListener(e -> openAcceptancePortal());
        searchBtn.addActionListener(e -> {
            String type = JOptionPane.showInputDialog(this, "Enter blood type:");
            if (type != null && !type.trim().isEmpty()) searchBlood(type.toUpperCase());
        });
        updateBtn.addActionListener(e -> openUpdatePortal());
    }

    private void openDonationPortal() {
        JFrame form = new JFrame("Donor & Donation Form");
        form.setSize(700, 800);
        form.setLayout(new GridLayout(0, 2, 8, 8));
        form.getContentPane().setBackground(new Color(255, 250, 250));

        nameField = new JTextField();
        ageField = new JTextField();
        weightField = new JTextField();
        hemoglobinField = new JTextField();
        bloodTypeField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        donationQtyField = new JTextField();
        genderCombo = new JComboBox<>(new String[] {"Male", "Female"});

        healthyCheck = new JCheckBox("In good health");
        tattooCheck = new JCheckBox("No tattoos in 6 months");
        medicalCheck = new JCheckBox("No serious conditions");
        alcoholCheck = new JCheckBox("No alcohol in 24h");
        antibioticCheck = new JCheckBox("No antibiotics recently");
        smokingCheck = new JCheckBox("Will abstain from smoking");

        addDonorBtn = new JButton("➕ Add Donor");
        addDonationBtn = new JButton("💾 Add Donation");

        donationBankIdCombo = new JComboBox<>(loadBankIds());

        form.add(new JLabel("👤 Name:")); form.add(nameField);
        form.add(new JLabel("🎂 Age:")); form.add(ageField);
        form.add(new JLabel("⚖ Weight (kg):")); form.add(weightField);
        form.add(new JLabel("💉 Hemoglobin:")); form.add(hemoglobinField);
        form.add(new JLabel("👫 Gender:")); form.add(genderCombo);
        form.add(new JLabel(" Blood Type:")); form.add(bloodTypeField);
        form.add(new JLabel("📞 Phone:")); form.add(phoneField);
        form.add(new JLabel("✉ Email:")); form.add(emailField);
        form.add(new JLabel("🏥 Bank ID:")); form.add(donationBankIdCombo);
        form.add(new JLabel("📦 Quantity (ml):")); form.add(donationQtyField);

        form.add(healthyCheck); form.add(tattooCheck);
        form.add(medicalCheck); form.add(alcoholCheck);
        form.add(antibioticCheck); form.add(smokingCheck);

        form.add(addDonorBtn); form.add(addDonationBtn);

        addDonorBtn.addActionListener(e -> addDonor());
        addDonationBtn.addActionListener(e -> addDonation());

        form.setVisible(true);
    }
    private void openAcceptancePortal() {
    JFrame acceptFrame = new JFrame("Blood Acceptance Portal");
    acceptFrame.setSize(450, 350);
    acceptFrame.setLayout(new GridLayout(0, 2, 8, 8));
    acceptFrame.getContentPane().setBackground(new Color(255, 255, 240));

    JTextField patientName = new JTextField();
    JTextField patientBlood = new JTextField();
    JTextField location = new JTextField();
    JTextField quantity = new JTextField();

    JButton submitRequest = new JButton("📨 Request Blood");

    acceptFrame.add(new JLabel("👤 Patient Name:")); acceptFrame.add(patientName);
    acceptFrame.add(new JLabel(" Required Blood Type:")); acceptFrame.add(patientBlood);
    acceptFrame.add(new JLabel("📍 Location:")); acceptFrame.add(location);
    acceptFrame.add(new JLabel("💉 Quantity (ml):")); acceptFrame.add(quantity);
    acceptFrame.add(new JLabel()); acceptFrame.add(submitRequest);

    submitRequest.addActionListener(e -> {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement stmt = conn.prepareStatement(
             "INSERT INTO BloodRequests (patient_name, blood_type, location, quantity_ml) VALUES (?, ?, ?, ?)")) {

        stmt.setString(1, patientName.getText());
        stmt.setString(2, patientBlood.getText());
        stmt.setString(3, location.getText());
        stmt.setInt(4, Integer.parseInt(quantity.getText()));

        stmt.executeUpdate();

        JOptionPane.showMessageDialog(acceptFrame, "Blood request submitted successfully!");

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(acceptFrame, "Error: " + ex.getMessage());
    }
    
    });

    acceptFrame.setVisible(true);
}

    private void openUpdatePortal() {
        JFrame update = new JFrame(" Update Donor Info");
        update.setSize(400, 300);
        update.setLayout(new GridLayout(0, 2, 5, 5));
        update.getContentPane().setBackground(new Color(240, 255, 255));

        JTextField id = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();
        JTextField date = new JTextField();

        JButton confirm = new JButton(" Update");

        update.add(new JLabel("Donor ID:")); update.add(id);
        update.add(new JLabel("New Phone:")); update.add(phone);
        update.add(new JLabel("New Email:")); update.add(email);
        update.add(new JLabel("Last Donation (YYYY-MM-DD):")); update.add(date);
        update.add(new JLabel()); update.add(confirm);

        confirm.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement("UPDATE Donors SET phone=?, email=?, last_donation=? WHERE donor_id=?")) {
                stmt.setString(1, phone.getText());
                stmt.setString(2, email.getText());
                stmt.setString(3, date.getText());
                stmt.setInt(4, Integer.parseInt(id.getText()));
                int updated = stmt.executeUpdate();
                JOptionPane.showMessageDialog(update, updated > 0 ? "Updated successfully!" : "ID not found.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(update, "Error: " + ex.getMessage());
            }
        });

        update.setVisible(true);
    }

    private void searchBlood(String type) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT D.name, D.phone, B.location FROM Donors D JOIN Donations DO ON D.donor_id = DO.donor_id JOIN BloodBanks B ON DO.bank_id = B.bank_id WHERE D.blood_type = ?")) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Name: ").append(rs.getString(1)).append(" | Phone: ").append(rs.getString(2)).append(" | Location: ").append(rs.getString(3)).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.length() == 0 ? "No donors found." : sb.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
        }
    }

    private Integer[] loadBankIds() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT bank_id FROM BloodBanks")) {
            java.util.List<Integer> ids = new java.util.ArrayList<>();
            while (rs.next()) ids.add(rs.getInt(1));
            return ids.toArray(new Integer[0]);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load bank IDs.");
            return new Integer[0];
        }
    }

     private boolean bankIdExists(int bankId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM BloodBanks WHERE bank_id = ?")) {
            stmt.setInt(1, bankId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private int getLastInsertedDonorId() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT donor_id FROM Donors ORDER BY donor_id DESC LIMIT 1")) {
            if (rs.next()) return rs.getInt("donor_id");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        bloodTypeField.setText("");
        phoneField.setText("");
        emailField.setText("");
        donationQtyField.setText("");
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found.");
            return;
        }
        SwingUtilities.invokeLater(() -> new BloodDonationHome().setVisible(true));
    }

    private void addDonor() {
         try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO Donors (name, age, blood_type, phone, email, gender, last_donation) VALUES (?, ?, ?, ?, ?, ?, CURDATE())")) {

        String ageText = ageField.getText().trim();
        String weightText = weightField.getText().trim();
        String hemoText = hemoglobinField.getText().trim();
        String phoneText = phoneField.getText().trim();
        String bloodType = bloodTypeField.getText().trim().toUpperCase();
        String gender = (String) genderCombo.getSelectedItem();

        if (nameField.getText().isEmpty() || ageText.isEmpty() || weightText.isEmpty() || hemoText.isEmpty()
                || bloodType.isEmpty() || phoneText.isEmpty() || emailField.getText().isEmpty() || gender == null) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields.");
            return;
        }

        if (!phoneText.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.");
            return;
        }

        boolean validBlood = false;
        for (String type : VALID_BLOOD_TYPES) {
            if (type.equals(bloodType)) {
                validBlood = true;
                break;
            }
        }
        if (!validBlood) {
            JOptionPane.showMessageDialog(this, "Invalid blood type. Enter one of: A+, A-, B+, B-, AB+, AB-, O+, O-");
            return;
        }

        int age = Integer.parseInt(ageText);
        int weight = Integer.parseInt(weightText);
        double hemoglobin = Double.parseDouble(hemoText);

        if (age < 18 || age > 65 || weight < 50) {
            JOptionPane.showMessageDialog(this, "You do not meet the basic eligibility criteria.");
            return;
        }

        if ((gender.equals("Male") && hemoglobin < 13.0) || (gender.equals("Female") && hemoglobin < 12.5)) {
            JOptionPane.showMessageDialog(this, "Hemoglobin level too low based on gender.");
            return;
        }

        if (!healthyCheck.isSelected() || !tattooCheck.isSelected() || !medicalCheck.isSelected()
                || !alcoholCheck.isSelected() || !antibioticCheck.isSelected() || !smokingCheck.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please confirm all eligibility checkboxes.");
            return;
        }

        stmt.setString(1, nameField.getText());
        stmt.setInt(2, age);
        stmt.setString(3, bloodType);
        stmt.setString(4, phoneText);
        stmt.setString(5, emailField.getText());
        stmt.setString(6, gender);
        stmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Donor added successfully!");
        clearFields();

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }

    }

    private void addDonation() {
        String qtyText = donationQtyField.getText().replaceAll("[^0-9]", "").trim();

    if (qtyText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter donation quantity.");
        return;
    }

    int bankId = (Integer) donationBankIdCombo.getSelectedItem();

    if (!bankIdExists(bankId)) {
        JOptionPane.showMessageDialog(this, "Invalid bank ID. Please select an existing blood bank.");
        return;
    }

    if (!healthyCheck.isSelected() || !tattooCheck.isSelected() || !medicalCheck.isSelected()
            || !alcoholCheck.isSelected() || !antibioticCheck.isSelected() || !smokingCheck.isSelected()) {
        JOptionPane.showMessageDialog(this, "Please confirm all eligibility checkboxes before donating.", "Eligibility Check", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
        int donorId = getLastInsertedDonorId();

        PreparedStatement lastStmt = conn.prepareStatement("SELECT donation_date FROM Donations WHERE donor_id = ? ORDER BY donation_date DESC LIMIT 1");
        lastStmt.setInt(1, donorId);
        ResultSet lastRs = lastStmt.executeQuery();
        if (lastRs.next()) {
            Date lastDate = lastRs.getDate("donation_date");
            long diffDays = (System.currentTimeMillis() - lastDate.getTime()) / (1000 * 60 * 60 * 24);
            if (diffDays < 56) {
                JOptionPane.showMessageDialog(this, "You must wait at least 56 days between donations. Last donation was " + diffDays + " days ago.");
                return;
            }
        }

        PreparedStatement yearStmt = conn.prepareStatement("SELECT COUNT(*) FROM Donations WHERE donor_id = ? AND YEAR(donation_date) = YEAR(CURDATE())");
        yearStmt.setInt(1, donorId);
        ResultSet yearRs = yearStmt.executeQuery();
        if (yearRs.next() && yearRs.getInt(1) >= 6) {
            JOptionPane.showMessageDialog(this, "You have reached the maximum of 6 donations for this year.");
            return;
        }

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Donations (donor_id, bank_id, donation_date, quantity_ml) VALUES (?, ?, CURDATE(), ?)");
        stmt.setInt(1, donorId);
        stmt.setInt(2, bankId);
        stmt.setInt(3, Integer.parseInt(qtyText));
        stmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Donation added successfully!");
        donationQtyField.setText("");

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
}
    }
}