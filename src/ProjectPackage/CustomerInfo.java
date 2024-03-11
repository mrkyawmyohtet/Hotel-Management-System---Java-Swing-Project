/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ProjectPackage;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author DELL
 */
public class CustomerInfo extends javax.swing.JFrame {

    /**
     * Creates new form CustomerInfo
     */
    
    public CustomerInfo() {
        initComponents();
        EmptyValidate();
        Validate();
        displayNRC();
        
    }
    
    public Connection connect()
    {
        Connection con = null;
        String driver = "com.mysql.cj.jdbc.Driver";
        try
        {           
            con = DriverManager.getConnection("jdbc:mysql://localhost:3308/fp_hotel_management_system", "root", "root123");
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return con;
    }
    
    //to display NRC and passport panels dynamically
    public void displayNRC(){
        //set as default
        rbtn_local.setSelected(true);
        
        rbtn_local.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    NRC_panel.setVisible(true);                    
                    Passport_panel.setVisible(false);
                    
                    //to set to default again when shown
                    cbox_NRC_code.setSelectedItem("1/");
                    txt_NRC.setText("");
                }
            }
        });
        
        rbtn_foreign.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    Passport_panel.setVisible(true);
                    NRC_panel.setVisible(false);
                    
                    //to set to default again when shown
                    txt_passport.setText("");
                }
            }       
        });
    }
    
    public void Validate(){
        //validation for name
        txt_fullName.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCharacterCountAndValidity(e.getDocument());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCharacterCountAndValidity(e.getDocument());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
            
            public void updateCharacterCountAndValidity(Document document) {
                try {
                    String text = document.getText(0, document.getLength());
                    int characterCount = text.length();
                    if(characterCount == 0){
                        lbl_fn_warn.setText("* required");
                            lbl_fn_warn.setForeground(Color.red);
                    }
                    else{
                        if (isValidName(text)) {
                            lbl_fn_warn.setText("...");
                            lbl_fn_warn.setForeground(Color.black);
                        } else {
                            lbl_fn_warn.setText("* only alphabets are allowed");
                            lbl_fn_warn.setForeground(Color.red);
                        }
                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(CustomerInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private boolean isValidName(String name) {
                return Pattern.matches("^[a-zA-Z]+$", name);
            }
        });
        
        //validation for age
        txt_age.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCharacterCountAndValidity(e.getDocument());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCharacterCountAndValidity(e.getDocument());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
            
            public void updateCharacterCountAndValidity(Document document) {
                try {
                    String text = document.getText(0, document.getLength());
                    int characterCount = text.length();
                    if (characterCount == 0) {
                        lbl_age_warn.setText("* required");
                        lbl_age_warn.setForeground(Color.red);
                    } else if (!isValid(text)) {
                        lbl_age_warn.setText("* Invalid Age Input");
                        lbl_age_warn.setForeground(Color.red);
                    } else {
                        int age = Integer.parseInt(text);
                        if (age <= 0 || age > 110) {
                            lbl_age_warn.setText("* age must be between 0-110");
                            lbl_age_warn.setForeground(Color.red);
                        } else {
                            lbl_age_warn.setText("...");
                            lbl_age_warn.setForeground(Color.black);
                        }
                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(CustomerInfo.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NumberFormatException ex) {
                    lbl_age_warn.setText("* invalid age format");
                    lbl_age_warn.setForeground(Color.red);
                }
            }

            private boolean isValid(String name) {
                return Pattern.matches("^(0|[1-9][0-9]*)$", name);
            }            
        });
        
        //validation for nrc number
        txt_NRC.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCharacterCountAndValidity(e.getDocument());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCharacterCountAndValidity(e.getDocument());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
            
            public void updateCharacterCountAndValidity(Document document) {
                try {
                    String text = document.getText(0, document.getLength());
                    int characterCount = text.length();
                    if(characterCount == 0){
                        lbl_nrc_warn.setText(" * required");
                        lbl_nrc_warn.setForeground(Color.red);
                    }
                    else if (isValid(text)) {
                        lbl_nrc_warn.setText("...");
                        lbl_nrc_warn.setForeground(Color.black);
                    } else {
                        lbl_nrc_warn.setText("* only numbers are allowed");
                        lbl_nrc_warn.setForeground(Color.red);
                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(CustomerInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private boolean isValid(String name) {
                return Pattern.matches("^[0-9]+$", name);
            } 
        });
        
        //validation for passport id
        txt_passport.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkInput(e.getDocument());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkInput(e.getDocument());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        
            public void checkInput(Document document) {
                try {
                    String text = document.getText(0, document.getLength());
                    int characterCount = text.length();
                    if(characterCount == 0){
                        lbl_passport_warn.setText("* required");
                        lbl_passport_warn.setForeground(Color.red);
                    }
                    else if (isValid(text)) {
                        lbl_passport_warn.setText("...");
                        lbl_passport_warn.setForeground(Color.black);
                    } else {
                        lbl_passport_warn.setText("* Invalid Passport Id");
                        lbl_passport_warn.setForeground(Color.red);
                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(CustomerInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private boolean isValid(String name) {
                return Pattern.matches("^[A-Z][1-9]\\d*[1-9]$", name);
            }
        });
        
        txt_conNum.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkInput(e.getDocument());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkInput(e.getDocument());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
            
             public void checkInput(Document document) {
                try {
                    String text = document.getText(0, document.getLength());
                    int characterCount = text.length();
                    if(characterCount == 0){
                        lbl_ph_warn.setText("* required");
                        lbl_ph_warn.setForeground(Color.red);
                    }
                    else if(text.startsWith("+959")){
                        if(characterCount > 13){
                            lbl_ph_warn.setText("* phone number must only have 11 numbers");
                            lbl_ph_warn.setForeground(Color.red);
                        }
                        else{
                            lbl_ph_warn.setText("...");
                            lbl_ph_warn.setForeground(Color.black);
                        }
                    }
                    else if(text.startsWith("09")){
                        if(characterCount > 11){
                            lbl_ph_warn.setText("* phone number must only have 11 numbers");
                            lbl_ph_warn.setForeground(Color.red);
                        }
                        else{
                            lbl_ph_warn.setText("...");
                            lbl_ph_warn.setForeground(Color.black);
                        }
                    }
                    else {
                        if (!text.startsWith("+95")) {
                            text = "+959" + text;
                        }
                        
                        if (isValid(text)) {
                            lbl_ph_warn.setText("...");
                            lbl_ph_warn.setForeground(Color.black);
                        } else {
                            lbl_ph_warn.setText("* Invalid Phone Number");
                            lbl_ph_warn.setForeground(Color.red);
                        }
                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(CustomerInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private boolean isValid(String name) {
                return Pattern.matches("\\+959\\d*", name);
            }
        });
        
        //validation for gender radio buttons
        ItemListener itemListener = new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(rbtn_male.isSelected() || rbtn_female.isSelected()){
                    lbl_gen_warn.setText("...");
                    lbl_gen_warn.setForeground(Color.black);
                }
                else{
                    lbl_gen_warn.setText(" * required");
                    lbl_gen_warn.setForeground(Color.red);
                }
            }           
        };
        
        rbtn_male.addItemListener(itemListener);
        rbtn_female.addItemListener(itemListener);
              
    }
    
    //to show empty field warnings
    public void EmptyValidate(){
        if(txt_fullName.getText().trim().isEmpty()){
            lbl_fn_warn.setText("* required");
            lbl_fn_warn.setForeground(Color.red);
        }
        
        if(txt_age.getText().trim().isEmpty()){
            lbl_age_warn.setText("* required");
            lbl_age_warn.setForeground(Color.red);
        }

        if(txt_NRC.getText().trim().isEmpty()){
            lbl_nrc_warn.setText(" * required");
            lbl_nrc_warn.setForeground(Color.red);
        }
        
        if(txt_passport.getText().trim().isEmpty()){
            lbl_passport_warn.setText(" * required");
            lbl_passport_warn.setForeground(Color.red);
        }
        
        if(!rbtn_male.isSelected() && !rbtn_female.isSelected()){
            lbl_gen_warn.setText("* required");
            lbl_gen_warn.setForeground(Color.red);
        }
        
        if(txt_conNum.getText().trim().isEmpty()){
            lbl_ph_warn.setText(" * required");
            lbl_ph_warn.setForeground(Color.red);
        }
    }
    
    //check if all the required fields are filled correctly or not
    public boolean isCompletedValidation(){
        //have to check for two conditions: local and foreign
        if(lbl_fn_warn.getText().equals("...") && 
            lbl_age_warn.getText().equals("...") && 
            lbl_gen_warn.getText().equals("...") && 
            lbl_ph_warn.getText().equals("...")){
                        
            if (rbtn_local.isSelected()) {
                if (!lbl_nrc_warn.getText().equals("...")) {
                    return false;
                }
            } else if (rbtn_foreign.isSelected()) {
                if (!lbl_passport_warn.getText().equals("...")) {
                    return false;
                }
            }

            // Return true only if none of the conditions failed
            return true;
        }
        else{
            return false;
        }
    }
    
    //for customer_ID
    public int getCurrentMaxID(){
        try{
            Connection con = connect();
            String sql = "SELECT MAX(cus_id) AS max_id FROM customer_info";
            PreparedStatement pstmt = con.prepareStatement(sql);
            try{
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()){
                    String maxID = rs.getString("max_id");
                    return maxID != null ? extractNumericPart(maxID) : 0;
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    
    // Method to extract the numeric part from an ID like 'C000001'
    private static int extractNumericPart(String id) {
        try {
            return Integer.parseInt(id.substring(1)); // Skip the first character 'C'
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0; // Return a default value in case of an error
        }
    }
    public String generateID(){
        int currentMaxId = getCurrentMaxID();
        int counter = currentMaxId + 1;
        return String.format("C%06d", counter);
    }
    
    //for booking id
    public int getCurrentMaxBookingID(){
        try{
            Connection con = connect();
            String sql = "SELECT MAX(booking_id) AS max_id FROM room_bookings";
            PreparedStatement pstmt = con.prepareStatement(sql);
            try{
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()){
                    String maxID = rs.getString("max_id");
                    return maxID != null ? extractNumericPart(maxID) : 0;
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    
    public String generateBookingID(){
        int currentMaxId = getCurrentMaxBookingID();
        int counter = currentMaxId + 1;
        return String.format("B%06d", counter);
    }
    
    public String[] getTempIDs(){
        try{
            Connection con = connect();
            String sql = "Select temp_id from reserved_temp";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            // Create a list to store room IDs dynamically
            List<String> idList = new ArrayList<>();

            // Iterate through the result set to retrieve room IDs
            while (rs.next()) {
                String tempid = rs.getString("temp_id");
                idList.add(tempid);
            }

            // Convert the list to an array
            String[] ids = idList.toArray(new String[0]);
            return ids;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
            return null;
        }     
    }
    
    public List<Object> getRoomBookingDataForTempId(String tempId) {
        List<Object> roomBookingData = new ArrayList<>();
        try{
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement("SELECT room_id, booking_date, stay_period FROM reserved_temp WHERE temp_id = ?");
            ps.setString(1, tempId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String roomId = rs.getString("room_id");
                Date bookingDate = rs.getDate("booking_date");
                int stayPeriod = rs.getInt("stay_period");
                roomBookingData.add(roomId);
                roomBookingData.add(bookingDate);
                roomBookingData.add(stayPeriod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
        return roomBookingData;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        txt_fullName4 = new javax.swing.JTextField();
        GenderbtnGP = new javax.swing.ButtonGroup();
        nationalitybtnGP = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_fullName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_age = new javax.swing.JTextField();
        lbl_age_warn = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_conNum = new javax.swing.JTextField();
        rbtn_female = new javax.swing.JRadioButton();
        rbtn_male = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btn_Cancel = new javax.swing.JButton();
        btn_confirm = new javax.swing.JButton();
        btn_reviewRooms = new javax.swing.JButton();
        txt_fullName2 = new javax.swing.JTextField();
        txt_fullName6 = new javax.swing.JTextField();
        txt_fullName7 = new javax.swing.JTextField();
        lbl_ph_warn = new javax.swing.JLabel();
        lbl_fn_warn = new javax.swing.JLabel();
        lbl_gen_warn = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        rbtn_foreign = new javax.swing.JRadioButton();
        rbtn_local = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        NRC_panel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cbox_NRC_code = new javax.swing.JComboBox<>();
        cbox_NRC_post = new javax.swing.JComboBox<>();
        cbox_NRC_type = new javax.swing.JComboBox<>();
        txt_NRC = new javax.swing.JTextField();
        txt_fullName3 = new javax.swing.JTextField();
        lbl_nrc_warn = new javax.swing.JLabel();
        Passport_panel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txt_passport = new javax.swing.JTextField();
        txt_fullName8 = new javax.swing.JTextField();
        lbl_passport_warn = new javax.swing.JLabel();

        jLabel7.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Gender:");

        txt_fullName4.setBackground(new java.awt.Color(255, 255, 255));
        txt_fullName4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_fullName4.setForeground(new java.awt.Color(0, 0, 0));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 204, 0));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesandIcons/icons8-form-100.png"))); // NOI18N
        jLabel1.setText("Please Fill Your Information...");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(176, 176, 176)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(314, 0, -1, -1));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesandIcons/pngtree-corridor-hotel-room-warm-color-simple-wind-image_990169.jpg"))); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 680, 520));

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Full Name:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 110, 143, 36));

        txt_fullName.setBackground(new java.awt.Color(255, 255, 255));
        txt_fullName.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_fullName.setForeground(new java.awt.Color(0, 0, 0));
        txt_fullName.setBorder(null);
        jPanel1.add(txt_fullName, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 140, 400, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Nationality Type:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 260, 140, 36));

        txt_age.setBackground(new java.awt.Color(255, 255, 255));
        txt_age.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_age.setForeground(new java.awt.Color(0, 0, 0));
        txt_age.setBorder(null);
        jPanel1.add(txt_age, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 210, 410, 30));

        lbl_age_warn.setForeground(new java.awt.Color(0, 0, 0));
        lbl_age_warn.setText("...");
        lbl_age_warn.setFocusable(false);
        jPanel1.add(lbl_age_warn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 210, 180, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Gender:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 440, 70, 36));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("(must start with '+959' or '09')");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 510, 240, 36));

        txt_conNum.setBackground(new java.awt.Color(255, 255, 255));
        txt_conNum.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_conNum.setForeground(new java.awt.Color(0, 0, 0));
        txt_conNum.setBorder(null);
        jPanel1.add(txt_conNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 540, 410, 30));

        rbtn_female.setBackground(new java.awt.Color(255, 255, 255));
        GenderbtnGP.add(rbtn_female);
        rbtn_female.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        rbtn_female.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_female.setText("Female");
        rbtn_female.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(rbtn_female, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 480, 98, -1));

        rbtn_male.setBackground(new java.awt.Color(255, 255, 255));
        GenderbtnGP.add(rbtn_male);
        rbtn_male.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        rbtn_male.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_male.setText("Male");
        rbtn_male.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(rbtn_male, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 480, 98, -1));

        jTextArea1.setBackground(new java.awt.Color(255, 255, 255));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Segoe Print", 1, 14)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(0, 102, 102));
        jTextArea1.setRows(5);
        jTextArea1.setText("* Thank you for your attention to detail in providing\nthe required information. Your careful and precise \ncompletion is greatly appreciated.\nEnjoy a delightful and restful break!");
        jTextArea1.setBorder(null);
        jScrollPane1.setViewportView(jTextArea1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 590, 405, 140));

        btn_Cancel.setBackground(new java.awt.Color(204, 0, 51));
        btn_Cancel.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btn_Cancel.setForeground(new java.awt.Color(255, 255, 255));
        btn_Cancel.setText("Cancel Booking");
        btn_Cancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel1.add(btn_Cancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 760, 194, 46));

        btn_confirm.setBackground(new java.awt.Color(102, 204, 0));
        btn_confirm.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btn_confirm.setForeground(new java.awt.Color(255, 255, 255));
        btn_confirm.setText("Confirm Your Booking");
        btn_confirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_confirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_confirmActionPerformed(evt);
            }
        });
        jPanel1.add(btn_confirm, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 760, 194, 46));

        btn_reviewRooms.setBackground(new java.awt.Color(102, 0, 102));
        btn_reviewRooms.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btn_reviewRooms.setForeground(new java.awt.Color(255, 255, 255));
        btn_reviewRooms.setText("Check Your Reserved Rooms Again");
        btn_reviewRooms.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_reviewRooms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reviewRoomsActionPerformed(evt);
            }
        });
        jPanel1.add(btn_reviewRooms, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 730, -1, 46));

        txt_fullName2.setEditable(false);
        txt_fullName2.setBackground(new java.awt.Color(255, 255, 255));
        txt_fullName2.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_fullName2.setForeground(new java.awt.Color(0, 0, 0));
        txt_fullName2.setText("___________________________________________________________________");
        txt_fullName2.setBorder(null);
        txt_fullName2.setFocusable(false);
        jPanel1.add(txt_fullName2, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 140, 410, 50));

        txt_fullName6.setEditable(false);
        txt_fullName6.setBackground(new java.awt.Color(255, 255, 255));
        txt_fullName6.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_fullName6.setForeground(new java.awt.Color(0, 0, 0));
        txt_fullName6.setText("___________________________________________________________________");
        txt_fullName6.setBorder(null);
        txt_fullName6.setFocusable(false);
        jPanel1.add(txt_fullName6, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 210, 410, 50));

        txt_fullName7.setEditable(false);
        txt_fullName7.setBackground(new java.awt.Color(255, 255, 255));
        txt_fullName7.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_fullName7.setForeground(new java.awt.Color(0, 0, 0));
        txt_fullName7.setText("___________________________________________________________________");
        txt_fullName7.setBorder(null);
        txt_fullName7.setFocusable(false);
        jPanel1.add(txt_fullName7, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 550, 410, 30));

        lbl_ph_warn.setForeground(new java.awt.Color(0, 0, 0));
        lbl_ph_warn.setText("...");
        lbl_ph_warn.setFocusable(false);
        jPanel1.add(lbl_ph_warn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 540, 210, 30));

        lbl_fn_warn.setForeground(new java.awt.Color(0, 0, 0));
        lbl_fn_warn.setText("...");
        lbl_fn_warn.setFocusable(false);
        jPanel1.add(lbl_fn_warn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 140, 180, 30));

        lbl_gen_warn.setForeground(new java.awt.Color(0, 0, 0));
        lbl_gen_warn.setText("...");
        lbl_gen_warn.setFocusable(false);
        jPanel1.add(lbl_gen_warn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 470, 180, 30));

        jLabel11.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Contact Number:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 510, 130, 36));

        jLabel9.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Age:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 180, 40, 36));

        nationalitybtnGP.add(rbtn_foreign);
        rbtn_foreign.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        rbtn_foreign.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_foreign.setText("Foreign");
        jPanel1.add(rbtn_foreign, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 300, -1, -1));

        nationalitybtnGP.add(rbtn_local);
        rbtn_local.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        rbtn_local.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_local.setText("Local");
        jPanel1.add(rbtn_local, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 300, -1, -1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        NRC_panel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("NRC Number:");

        cbox_NRC_code.setBackground(new java.awt.Color(255, 255, 255));
        cbox_NRC_code.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbox_NRC_code.setForeground(new java.awt.Color(0, 0, 0));
        cbox_NRC_code.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1/", "2/", "3/", "4/", "5/", "6/", "7/", "8/", "9/", "10/", "11/", "12/", "13/", "14/" }));
        cbox_NRC_code.setBorder(null);
        cbox_NRC_code.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cbox_NRC_code.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbox_NRC_codeItemStateChanged(evt);
            }
        });

        cbox_NRC_post.setBackground(new java.awt.Color(255, 255, 255));
        cbox_NRC_post.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbox_NRC_post.setForeground(new java.awt.Color(0, 0, 0));
        cbox_NRC_post.setBorder(null);
        cbox_NRC_post.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cbox_NRC_type.setBackground(new java.awt.Color(255, 255, 255));
        cbox_NRC_type.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbox_NRC_type.setForeground(new java.awt.Color(0, 0, 0));
        cbox_NRC_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(N)" }));
        cbox_NRC_type.setBorder(null);
        cbox_NRC_type.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        txt_NRC.setBackground(new java.awt.Color(255, 255, 255));
        txt_NRC.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_NRC.setForeground(new java.awt.Color(0, 0, 0));
        txt_NRC.setBorder(null);

        txt_fullName3.setEditable(false);
        txt_fullName3.setBackground(new java.awt.Color(255, 255, 255));
        txt_fullName3.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_fullName3.setForeground(new java.awt.Color(0, 0, 0));
        txt_fullName3.setText("___________________________________________________________________");
        txt_fullName3.setBorder(null);
        txt_fullName3.setFocusable(false);

        lbl_nrc_warn.setForeground(new java.awt.Color(0, 0, 0));
        lbl_nrc_warn.setText("...");
        lbl_nrc_warn.setFocusable(false);

        javax.swing.GroupLayout NRC_panelLayout = new javax.swing.GroupLayout(NRC_panel);
        NRC_panel.setLayout(NRC_panelLayout);
        NRC_panelLayout.setHorizontalGroup(
            NRC_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NRC_panelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(NRC_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(NRC_panelLayout.createSequentialGroup()
                        .addComponent(cbox_NRC_code, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(cbox_NRC_post, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(cbox_NRC_type, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(NRC_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_NRC, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_fullName3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(lbl_nrc_warn, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14))
        );
        NRC_panelLayout.setVerticalGroup(
            NRC_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NRC_panelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(NRC_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbox_NRC_code, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbox_NRC_post, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbox_NRC_type, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_NRC, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(NRC_panelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(txt_fullName3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_nrc_warn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Passport_panel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Proof ID: (Passport ID)");

        txt_passport.setBackground(new java.awt.Color(255, 255, 255));
        txt_passport.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_passport.setForeground(new java.awt.Color(0, 0, 0));
        txt_passport.setBorder(null);

        txt_fullName8.setEditable(false);
        txt_fullName8.setBackground(new java.awt.Color(255, 255, 255));
        txt_fullName8.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        txt_fullName8.setForeground(new java.awt.Color(0, 0, 0));
        txt_fullName8.setText("___________________________________________________________________");
        txt_fullName8.setBorder(null);
        txt_fullName8.setFocusable(false);

        lbl_passport_warn.setForeground(new java.awt.Color(0, 0, 0));
        lbl_passport_warn.setText("...");
        lbl_passport_warn.setFocusable(false);

        javax.swing.GroupLayout Passport_panelLayout = new javax.swing.GroupLayout(Passport_panel);
        Passport_panel.setLayout(Passport_panelLayout);
        Passport_panelLayout.setHorizontalGroup(
            Passport_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Passport_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Passport_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(Passport_panelLayout.createSequentialGroup()
                        .addGroup(Passport_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_passport, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_fullName8, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_passport_warn, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Passport_panelLayout.setVerticalGroup(
            Passport_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Passport_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Passport_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Passport_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_passport, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_passport_warn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_fullName8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(NRC_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(Passport_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NRC_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Passport_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 330, 630, 100));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1456, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 894, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_reviewRoomsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reviewRoomsActionPerformed
        // TODO add your handling code here:
        ReservedRoomInfo RRI = new ReservedRoomInfo();
        RRI.setVisible(true);
        this.hide();
    }//GEN-LAST:event_btn_reviewRoomsActionPerformed

    private void btn_confirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_confirmActionPerformed
        // TODO add your handling code here:
        // TODO: need to check if the customer is local or foreign
        
        EmptyValidate();
        if(!isCompletedValidation()){
            JOptionPane.showMessageDialog(null, "Please fill all the required fields correctly!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        else{
            String fullname = txt_fullName.getText().toString();
            String age = txt_age.getText().toString(); //need to parse into integer type when inserting into the database       
            int gender = -1; //default value
            if(rbtn_male.isSelected()){
                gender = 1;
            }
            else if(rbtn_female.isSelected()){
                gender = 0;
            }
            String Phnum = txt_conNum.getText().toString();
            String customer_id = generateID();
            String booking_id = generateBookingID();
            if(rbtn_local.isSelected()){
                String passport = null;
                String NRC = cbox_NRC_code.getSelectedItem().toString() + cbox_NRC_post.getSelectedItem().toString() + cbox_NRC_type.getSelectedItem().toString() + txt_NRC.getText().toString();
                try{
                    //first step: insert customer data into customer_info table
                    Connection con = connect();
                    String sql = "insert into customer_info (cus_id, cus_name, cus_age, cus_nrc, cus_gender, cus_contact, cus_passport) values (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, customer_id);
                    ps.setString(2, fullname);
                    ps.setInt(3, Integer.parseInt(age));
                    ps.setString(4, NRC);
                    ps.setInt(5, gender);
                    ps.setString(6, Phnum);
                    ps.setString(7, passport);
                    ps.execute();
                    
                    //second step: insert reserved rooms data along with customer_id into room_bookings table
                    String[] tempids = getTempIDs(); //for every row in temp table, insert the data into the booking table
                    String bookSql = "insert into room_bookings (booking_id, room_id, cus_id, booking_date, stay_period) values (?, ?, ?, ?, ?)";
                    String updateSql = "update room set status = 'Booked' where room_id = ?";
                    for(String tempid : tempids){
                        List<Object> roomBookingData = getRoomBookingDataForTempId(tempid);
                        if(!roomBookingData.isEmpty()){
                            String room_id = (String) roomBookingData.get(0);
                            Date booking_date = (Date) roomBookingData.get(1);
                            int stay_period = (int) roomBookingData.get(2);
                            
                            PreparedStatement bookPs = con.prepareStatement(bookSql);
                            bookPs.setString(1, booking_id);
                            bookPs.setString(2, room_id);
                            bookPs.setString(3, customer_id);
                            bookPs.setDate(4, (java.sql.Date) booking_date);
                            bookPs.setInt(5, stay_period);
                            bookPs.execute();
                            
                            PreparedStatement updatePs = con.prepareStatement(updateSql);
                            updatePs.setString(1, room_id);
                            updatePs.executeUpdate();
                        }
                    }
                    
                    //third step: deleted data in reserved_temp table;
                    String deleteTempSql = "delete from reserved_temp";
                    PreparedStatement deleteTempPs = con.prepareStatement(deleteTempSql);
                    deleteTempPs.execute();
                    
                    JOptionPane.showMessageDialog(null, "Booking Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(rbtn_foreign.isSelected()){
                String passport = txt_passport.getText().toString();
                String NRC = null;
                try{
                    //first step: insert customer data into customer_info table
                    Connection con = connect();
                    String sql = "insert into customer_info (cus_id, cus_name, cus_age, cus_nrc, cus_gender, cus_contact, cus_passport) values (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, customer_id);
                    ps.setString(2, fullname);
                    ps.setInt(3, Integer.parseInt(age));
                    ps.setString(4, NRC);
                    ps.setInt(5, gender);
                    ps.setString(6, Phnum);
                    ps.setString(7, passport);
                    ps.execute();
                    
                    //second step: insert reserved rooms data along with customer_id into room_bookings table
                    String[] tempids = getTempIDs(); //for every row in temp table, insert the data into the booking table
                    String bookSql = "insert into room_bookings (booking_id, room_id, cus_id, booking_date, stay_period) values (?, ?, ?, ?, ?)";
                    String updateSql = "update room set status = 'Booked' where room_id = ?";
                    for(String tempid : tempids){
                        List<Object> roomBookingData = getRoomBookingDataForTempId(tempid);
                        if(!roomBookingData.isEmpty()){
                            String room_id = (String) roomBookingData.get(0);
                            Date booking_date = (Date) roomBookingData.get(1);
                            int stay_period = (int) roomBookingData.get(2);
                            
                            PreparedStatement bookPs = con.prepareStatement(bookSql);
                            bookPs.setString(1, booking_id);
                            bookPs.setString(2, room_id);
                            bookPs.setString(3, customer_id);
                            bookPs.setDate(4, (java.sql.Date) booking_date);
                            bookPs.setInt(5, stay_period);
                            bookPs.execute();
                            
                            PreparedStatement updatePs = con.prepareStatement(updateSql);
                            updatePs.setString(1, room_id);
                            updatePs.executeUpdate();
                        }
                    }
                    
                    //third step: deleted data in reserved_temp table;
                    String deleteTempSql = "delete from reserved_temp";
                    PreparedStatement deleteTempPs = con.prepareStatement(deleteTempSql);
                    deleteTempPs.execute();
                    
                    JOptionPane.showMessageDialog(null, "Booking Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            BookingReceipt br = new BookingReceipt(booking_id);
            br.setVisible(true);
            this.hide();
        }
    }//GEN-LAST:event_btn_confirmActionPerformed

    private void cbox_NRC_codeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbox_NRC_codeItemStateChanged
        // TODO add your handling code here:
        String selectedItem = cbox_NRC_code.getSelectedItem().toString();
        switch(selectedItem){
            case "1/":
                cbox_NRC_post.removeAllItems();
                cbox_NRC_post.addItem("MAKANA");
                cbox_NRC_post.addItem("SABATA");
                cbox_NRC_post.addItem("YABAYA");
                cbox_NRC_post.addItem("BAMANA");
                cbox_NRC_post.setSelectedItem("MAKANA");
                break;
            case "2/":
                cbox_NRC_post.removeAllItems();
                cbox_NRC_post.addItem("BALAKHA");
                cbox_NRC_post.addItem("DAMASA");
                cbox_NRC_post.addItem("LAKANA");
                cbox_NRC_post.addItem("MASANA");
                cbox_NRC_post.addItem("PHASANA");
                cbox_NRC_post.addItem("PHAYASA");
                cbox_NRC_post.addItem("YATANA");
                cbox_NRC_post.addItem("YATANA");
                cbox_NRC_post.setSelectedItem("BALAKHA");
                break;
            case "3/":
                cbox_NRC_post.removeAllItems();
                cbox_NRC_post.addItem("YAYATA");
                cbox_NRC_post.addItem("LATANA");
                cbox_NRC_post.addItem("BAGALA");
                cbox_NRC_post.addItem("KAMAMA");
                cbox_NRC_post.addItem("SAKALA");
                cbox_NRC_post.addItem("WALAMA");
                cbox_NRC_post.addItem("BAANA");
                cbox_NRC_post.addItem("BATASA");
                cbox_NRC_post.addItem("KASAKA");
                cbox_NRC_post.addItem("KADANA");
                cbox_NRC_post.addItem("KAKAYA");
                cbox_NRC_post.addItem("LABANA");
                cbox_NRC_post.addItem("MAWATA");
                cbox_NRC_post.addItem("PAKANA");
                cbox_NRC_post.addItem("PHAPANA");
                cbox_NRC_post.addItem("TATAKA");
                cbox_NRC_post.addItem("TATANA");
                cbox_NRC_post.setSelectedItem("YAYATA");
                break;
            case "4/":
                cbox_NRC_post.removeAllItems();
                cbox_NRC_post.addItem("SAMANA");
                cbox_NRC_post.addItem("HAKHANA");
                cbox_NRC_post.addItem("HTATALA");
                cbox_NRC_post.addItem("KAKHANA");
                cbox_NRC_post.addItem("KAPALA");
                cbox_NRC_post.addItem("MATANA");
                cbox_NRC_post.addItem("MATAPA");
                cbox_NRC_post.addItem("PALAWA");
                cbox_NRC_post.addItem("PHALANA");
                cbox_NRC_post.addItem("TATANA");
                cbox_NRC_post.addItem("TAZANA");
                cbox_NRC_post.addItem("YAKHADA");
                cbox_NRC_post.addItem("YAZANA");
                cbox_NRC_post.setSelectedItem("SAMANA");
                break;
            case "5/":
                cbox_NRC_post.removeAllItems();            
                cbox_NRC_post.addItem("KHAPANA");
                cbox_NRC_post.addItem("DAHANA");
                cbox_NRC_post.addItem("MAPALA");
                cbox_NRC_post.addItem("HTAPAKHA");
                cbox_NRC_post.addItem("SAMAYA");
                cbox_NRC_post.addItem("ATANA");
                cbox_NRC_post.addItem("AYATA");
                cbox_NRC_post.addItem("BAMANA");
                cbox_NRC_post.addItem("BATALA");
                cbox_NRC_post.addItem("DAPAYA");
                cbox_NRC_post.addItem("HAMALA");
                cbox_NRC_post.addItem("HTAKHANA");
                cbox_NRC_post.addItem("KABALA");
                cbox_NRC_post.addItem("KALAHTA");
                cbox_NRC_post.addItem("KALANA");
                cbox_NRC_post.addItem("KALATA");
                cbox_NRC_post.addItem("KALAWA");
                cbox_NRC_post.addItem("KANANA");
                cbox_NRC_post.addItem("KAMANA");
                cbox_NRC_post.addItem("KHATANA");
                cbox_NRC_post.addItem("KATANA");
                cbox_NRC_post.addItem("KHAUNA");
                cbox_NRC_post.addItem("KHAUTA");
                cbox_NRC_post.addItem("LAHANA");
                cbox_NRC_post.addItem("LAYANA");
                cbox_NRC_post.addItem("MAKANA");
                cbox_NRC_post.addItem("MALANA");
                cbox_NRC_post.addItem("MAMATA");
                cbox_NRC_post.addItem("MATANA");
                cbox_NRC_post.addItem("NAYANA");
                cbox_NRC_post.addItem("NGAZANA");
                cbox_NRC_post.addItem("PALANA");
                cbox_NRC_post.addItem("PASANA");
                cbox_NRC_post.addItem("SAKANA");
                cbox_NRC_post.addItem("SALAKA");
                cbox_NRC_post.addItem("TASANA");
                cbox_NRC_post.addItem("WALANA");
                cbox_NRC_post.addItem("YABANA");
                cbox_NRC_post.addItem("YAMAPA");
                cbox_NRC_post.addItem("MAMANA");
                cbox_NRC_post.addItem("MAYANA");
                cbox_NRC_post.addItem("PALABA");
                cbox_NRC_post.addItem("PHAPANA");
                cbox_NRC_post.addItem("TAMANA");
                cbox_NRC_post.addItem("WATANA");
                cbox_NRC_post.addItem("YAUNA");
                cbox_NRC_post.setSelectedItem("KHAPANA");
                break;
            case "6/":
                cbox_NRC_post.removeAllItems();
                cbox_NRC_post.addItem("KALAA");
                cbox_NRC_post.addItem("PALATA");
                cbox_NRC_post.addItem("PAKAMA");
                cbox_NRC_post.addItem("BAPANA");
                cbox_NRC_post.addItem("HTAWANA");
                cbox_NRC_post.addItem("KATANA");
                cbox_NRC_post.addItem("KAYAYA");
                cbox_NRC_post.addItem("LALANA");
                cbox_NRC_post.addItem("MAANA");
                cbox_NRC_post.addItem("MAAYA");
                cbox_NRC_post.addItem("PALANA");
                cbox_NRC_post.addItem("TATAYA");
                cbox_NRC_post.addItem("YAPHANA");
                cbox_NRC_post.addItem("MAMANA");
                cbox_NRC_post.addItem("KASANA");
                cbox_NRC_post.addItem("KHAMAKA");
                cbox_NRC_post.addItem("MATANA");
                cbox_NRC_post.addItem("TAYAKHA");
                cbox_NRC_post.setSelectedItem("KALAA");
                break;
            case "7/":
            case "8/":
            case "9/":
            case "10/":
            case "11/":
            case "12/":
            case "13/":
            case "14/":
            default:
                break;
        }
    }//GEN-LAST:event_cbox_NRC_codeItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CustomerInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomerInfo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup GenderbtnGP;
    private javax.swing.JPanel NRC_panel;
    private javax.swing.JPanel Passport_panel;
    private javax.swing.JButton btn_Cancel;
    private javax.swing.JButton btn_confirm;
    private javax.swing.JButton btn_reviewRooms;
    private javax.swing.JComboBox<String> cbox_NRC_code;
    private javax.swing.JComboBox<String> cbox_NRC_post;
    private javax.swing.JComboBox<String> cbox_NRC_type;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lbl_age_warn;
    private javax.swing.JLabel lbl_fn_warn;
    private javax.swing.JLabel lbl_gen_warn;
    private javax.swing.JLabel lbl_nrc_warn;
    private javax.swing.JLabel lbl_passport_warn;
    private javax.swing.JLabel lbl_ph_warn;
    private javax.swing.ButtonGroup nationalitybtnGP;
    private javax.swing.JRadioButton rbtn_female;
    private javax.swing.JRadioButton rbtn_foreign;
    private javax.swing.JRadioButton rbtn_local;
    private javax.swing.JRadioButton rbtn_male;
    private javax.swing.JTextField txt_NRC;
    private javax.swing.JTextField txt_age;
    private javax.swing.JTextField txt_conNum;
    private javax.swing.JTextField txt_fullName;
    private javax.swing.JTextField txt_fullName2;
    private javax.swing.JTextField txt_fullName3;
    private javax.swing.JTextField txt_fullName4;
    private javax.swing.JTextField txt_fullName6;
    private javax.swing.JTextField txt_fullName7;
    private javax.swing.JTextField txt_fullName8;
    private javax.swing.JTextField txt_passport;
    // End of variables declaration//GEN-END:variables
}
