package blooddonation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class Blood_donation extends JFrame {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/Blood_donation";
    static final String USER = "root";
    static final String PASS = "abby";

    JTextField nameField, ageField, bloodTypeField, phoneField, emailField, weightField, hemoglobinField, donationQtyField;
    JComboBox<Integer> donationBankIdCombo;
    JCheckBox healthyCheck, tattooCheck, medicalCheck, alcoholCheck, antibioticCheck, smokingCheck;
    JButton addDonorBtn, addDonationBtn;

    final String[] VALID_BLOOD_TYPES = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    public Blood_donation() {
        setTitle("❤ Blood Donation GUI");
        setSize(700, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(255, 245, 245));

        JLabel headerLabel = new JLabel("❤ Donate Blood, Save Lives!", JLabel.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(220, 20, 60));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(17, 2, 10, 10));
        formPanel.setBackground(new Color(255, 250, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        nameField = new JTextField();
        ageField = new JTextField();
        weightField = new JTextField();
        hemoglobinField = new JTextField();
        bloodTypeField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        donationQtyField = new JTextField();
        donationBankIdCombo = new JComboBox<>(loadBankIds());

        healthyCheck = new JCheckBox("I am in good health (no cold, flu, sore throat)");
        tattooCheck = new JCheckBox("I have not had a tattoo/piercing in the last 6 months");
        medicalCheck = new JCheckBox("I do not have disqualifying medical conditions");
        alcoholCheck = new JCheckBox("I have not consumed alcohol in the past 24 hours");
        antibioticCheck = new JCheckBox("I have not taken injectable antibiotics in the past 10 days or oral antibiotics in past 24 hours");
        smokingCheck = new JCheckBox("I will abstain from smoking 2 hours before and after donation");

        formPanel.add(new JLabel("👤 Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("🎂 Age (18–65):")); formPanel.add(ageField);
        formPanel.add(new JLabel("⚖ Weight (kg ≥ 50):")); formPanel.add(weightField);
        formPanel.add(new JLabel("🩸 Hemoglobin (g/dL):")); formPanel.add(hemoglobinField);
        formPanel.add(new JLabel("Blood Type:")); formPanel.add(bloodTypeField);
        formPanel.add(new JLabel("📞 Phone:")); formPanel.add(phoneField);
        formPanel.add(new JLabel("✉ Email:")); formPanel.add(emailField);
        formPanel.add(new JLabel("💉 Quantity (ml):")); formPanel.add(donationQtyField);
        formPanel.add(new JLabel("🏥 Bank ID:")); formPanel.add(donationBankIdCombo);

        formPanel.add(new JLabel("✅ Health Status:")); formPanel.add(healthyCheck);
        formPanel.add(new JLabel("✅ No Tattoos Recently:")); formPanel.add(tattooCheck);
        formPanel.add(new JLabel("✅ Medical Clearance:")); formPanel.add(medicalCheck);
        formPanel.add(new JLabel("✅ No Alcohol in 24h:")); formPanel.add(alcoholCheck);
        formPanel.add(new JLabel("✅ Antibiotic Clearance:")); formPanel.add(antibioticCheck);
        formPanel.add(new JLabel("✅ Smoking Abstinence:")); formPanel.add(smokingCheck);

        addDonorBtn = new JButton("➕ Add Donor");
        addDonationBtn = new JButton("💾 Add Donation");
        addDonorBtn.setBackground(new Color(100, 200, 100));
        addDonationBtn.setBackground(new Color(100, 149, 237));
        addDonorBtn.setForeground(Color.WHITE);
        addDonationBtn.setForeground(Color.WHITE);
        formPanel.add(addDonorBtn);
        formPanel.add(addDonationBtn);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setIcon(new ImageIcon("blood_drop.png"));

        add(headerLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(imageLabel, BorderLayout.SOUTH);

        addDonorBtn.addActionListener(e -> addDonor());
        addDonationBtn.addActionListener(e -> addDonation());
    }
    private void addDonor() {
    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO Donors (name, age, blood_type, phone, email, last_donation) VALUES (?, ?, ?, ?, ?, CURDATE())")) {

        String ageText = ageField.getText().trim();
        String weightText = weightField.getText().trim();
        String hemoText = hemoglobinField.getText().trim();
        String phoneText = phoneField.getText().trim();
        String bloodType = bloodTypeField.getText().trim().toUpperCase();
        

        if (nameField.getText().isEmpty() || ageText.isEmpty() || weightText.isEmpty() || hemoText.isEmpty()
                || bloodType.isEmpty() || phoneText.isEmpty() || emailField.getText().isEmpty() ) {
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
            JOptionPane.showMessageDialog(this, "You do not meet the weight eligibility criteria.");
            return;
        }

        if (( hemoglobin < 13.0) || (hemoglobin < 12.5)) {
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
        stmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Donor added successfully!");
        clearFields();

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}

// Updated donation method with 56-day interval and 6-times-per-year check
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
    private Integer[] loadBankIds() {
        Vector<Integer> ids = new Vector<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT bank_id FROM BloodBanks")) {
            while (rs.next()) {
                ids.add(rs.getInt("bank_id"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ids.toArray(new Integer[0]);
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
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        SwingUtilities.invokeLater(() -> new Blood_donation().setVisible(true));
}
}