/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ProjectPackage;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author DELL
 */
public class DashBoard extends javax.swing.JFrame {

    /**
     * Creates new form DashBoard
     */
    private String staffid;
    private int role;
    private boolean isNewGuestButtonClicked = false;
    private String sameRReservedID;
    private List<String> selectedRoomIDs = new ArrayList<>();
    public DashBoard() {
        initComponents();
    }
    
    public DashBoard(String staffid, int role){
        initComponents();
        this.staffid = staffid;
        this.role = role;
        txt_staffid.setText("Staff ID: " + staffid);
        if(role == 0){
            txt_role.setText("Staff Role: Manager");
        }
        else if(role == 1){
            txt_role.setText("Staff Role: Receptionist");
        }
        TabbedPane.setSelectedIndex(0);
        DisplayRoom();
        DisplayPackages();
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
    
    public void DisplayRoom(){
        try{
            Connection con = connect();
            String sql = "Select * from room";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            roomTable.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void DisplayPackages(){
        try{
            Connection con = connect();
            String sql = "Select * from packages";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            table_packages.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void DisplayRoomReservedData(){
        try{
            Connection con = connect();
            String sql = "select * from r_reserved_data";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            table_rReservedData.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getExtension(String imagePath){
        var array = imagePath.split("\\.");
        if(array.length > 1){
            return array[array.length-1];
        }
        return "";
    }
    
    public String changeRoomImageName(String imagePath){
        var extension = getExtension(imagePath);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String timeStamp = LocalDateTime.now().format(df);
        String roomNo = txt_roomNum.getText();
        return roomNo + "_" + timeStamp + "." + extension;
    }
    
    public String changePackageImageName(String imagePath){
        var extension = getExtension(imagePath);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String timeStamp = LocalDateTime.now().format(df);
        String packageID = txt_packageID.getText();
        return packageID + "_" + timeStamp + "." + extension;
    }
    
    public void copyImage(String imagePath, String imageName){
        Path sourcePath = Paths.get(imagePath);
        Path destination = Paths.get("src/ImagesandIcons/");
        try{
            Path destinationFile = destination.resolve(imageName);
            Files.createDirectories(destination.getParent());
            Files.copy(sourcePath, destinationFile, StandardCopyOption.REPLACE_EXISTING);
//            JOptionPane.showMessageDialog(null, "Copied!");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void deleteImage(String imageName){
        Path imagePath = Paths.get("src/ImagesandIcons/", imageName);
        try{
            Files.deleteIfExists(imagePath);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //for room ID
    public int getCurrentMaxRoomID(){
        try{
            Connection con = connect();
            String sql = "SELECT MAX(room_id) AS max_id FROM room";
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
    
    //for package ID
    public int getCurrentMaxPackageID(){
        try{
            Connection con = connect();
            String sql = "SELECT MAX(package_id) AS max_id FROM packages";
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
    
    //for Customer ID
    public int getCurrentMaxCustomerID(){
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
    
    //for Reserved Room ID
    public int getCurrentMaxRReservedID(){
        try{
            Connection con = connect();
            String sql = "SELECT MAX(r_reserved_id) AS max_id FROM r_reserved_data";
            PreparedStatement pstmt = con.prepareStatement(sql);
            try{
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()){
                    String maxID = rs.getString("max_id");
                    return maxID != null ? extractNumericPartForTwoCharacter(maxID) : 0;
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
    
    //for Receipt ID
    public int getCurrentMaxReceiptID(){
        try{
            Connection con = connect();
            String sql = "SELECT MAX(receipt_id) AS max_id FROM receipts";
            PreparedStatement pstmt = con.prepareStatement(sql);
            try{
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()){
                    String maxID = rs.getString("max_id");
                    return maxID != null ? extractNumericPartForTwoCharacter(maxID) : 0;
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
    
    private static int extractNumericPart(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0; // Return a default value in case of an error
        }
    }
    
    private static int extractNumericPartForTwoCharacter(String id) {
        try {
            return Integer.parseInt(id.substring(2));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0; // Return a default value in case of an error
        }
    }
    
    public String generateRoomID(){
        int currentMaxId = getCurrentMaxRoomID();
        int counter = currentMaxId + 1;
        return String.format("R%05d", counter);
    }
    
    public String generatePackageID(){
        int currentMaxId = getCurrentMaxPackageID();
        int counter = currentMaxId + 1;
        return String.format("P%05d", counter);
    }
    
    public String generateCustomerID(){
        int currentMaxId = getCurrentMaxCustomerID();
        int counter = currentMaxId + 1;
        return String.format("C%06d", counter);
    }
    
    public String generateRReservedID(){
        int currentMaxId = getCurrentMaxRReservedID();
        int counter = currentMaxId + 1;
        return String.format("RR%06d", counter);
    }
    
    public String generateReceiptID(){
        int currentMaxId = getCurrentMaxReceiptID();
        int counter = currentMaxId + 1;
        return String.format("RE%06d", counter);
    }
    
    public boolean emptyValidateRoom(){
        boolean empty = true;
        if(txt_roomID.getText().isEmpty() || 
           txt_roomNum.getText().isEmpty() || 
           txt_roomType.getText().isEmpty() ||
           txt_roomStatus.getText().isEmpty() ||
           txt_roomDec.getText().isEmpty() ||
           spin_bedCount.getValue().equals(0) ||
           txt_roomPrice.getText().isEmpty() ||
           txt_imagePath.getText().isEmpty())
        {
            return empty;
        }
        else{
            return !empty;
        }
    }
    
    public boolean emptyValidatePackage(){
        boolean empty = true;
        if(txt_packageID.getText().isEmpty() || 
           txt_packageName.getText().isEmpty() || 
           txt_packageType.getText().isEmpty() ||
           txt_packagePrice.getText().isEmpty() ||
           txt_services.getText().isEmpty() ||
           txt_packageStatus.getText().isEmpty() ||
           txt_packageImagePath.getText().isEmpty())
        {
            return empty;
        }
        else{
            return !empty;
        }
    }
    
    public void clearRoomFields(){
        txt_roomID.setText("");
        txt_roomNum.setText("");
        txt_roomType.setText("");
        txt_roomPrice.setText("");
        txt_roomStatus.setText("");
        txt_imagePath.setText("");
        txt_roomDec.setText("");
        spin_bedCount.setValue(0);
        lbl_roomImage.setIcon(null);
        lbl_roomImage.setText("Room Image");
    }
    
    public void clearPackageFields(){
        txt_packageID.setText("");
        txt_packageName.setText("");
        txt_packageType.setText("");
        txt_packagePrice.setText("");
        txt_services.setText("");
        txt_packageStatus.setText("");
        txt_packageImagePath.setText("");
        lbl_packageImage.setIcon(null);
        lbl_packageImage.setText("Package Image");
    }
    
    public void clearBookingData(){
        txt_bookingID.setText("");
        txt_bookedRoomID.setText("");
        txt_bookedCusID.setText("");
        txt_bookingDate.setText("");
        txt_bookingStayPeriod.setText("");
    }
    
    public void clearCusFields(){
        txt_cusID.setText(generateCustomerID());
        txt_cusName.setText("");
        txt_cusAge.setText("");
        txt_cusNRC.setText("");
        txt_cusPassport.setText("");
        rbtn_male.setSelected(false);
        rbtn_female.setSelected(false);
        txt_cusContact.setText("");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = LocalDate.now().format(dateFormat);
        txt_cusCheckinDate.setText(currentDate);
    }
    
    public void clearCheckOutFields(){
        txt_cusIDcheckOut.setText("");
        txt_cusNamecheckOut.setText("");
        txt_cusAgecheckOut.setText("");
        txt_cusNRCcheckOut.setText("");
        txt_cusPasscheckOut.setText("");
        txt_cusGendercheckOut.setText("");
        txt_cusConcheckOut.setText("");
        txt_checkInDate.setText("");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = LocalDate.now().format(dateFormat);
        txt_checkOutDate.setText(currentDate);
        txt_roomIDcheckOut.setText("");
        txt_roomNocheckOut.setText("");
        txt_roomTypecheckOut.setText("");
        txt_roomPricecheckOut.setText("");
        txt_totalCost.setText("");
        txt_paymentStatus.setText("");
    }
    
    public static int getDifferenceInDays(String date1, String date2) {
        LocalDate localDate1 = LocalDate.parse(date1);
        LocalDate localDate2 = LocalDate.parse(date2);

        long difference = Math.abs(ChronoUnit.DAYS.between(localDate1, localDate2));

        return (int) difference;
    }
    
    public boolean isRoomBooked(Connection con, String roomID, String currentDate) throws SQLException{
        String sql = "select * from room_bookings where room_id = ? and booking_date = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, roomID);
        ps.setString(2, currentDate);
        ResultSet rs = ps.executeQuery();
        boolean isBooked = rs.next();
        rs.close();
        return isBooked;
    }
    
    public void getRoomDetails(){
        String roomType = cbox_roomTypes.getSelectedItem().toString();
        String currentDate = txt_cusCheckinDate.getText();
        cbox_roomDecs.removeAllItems();
        cbox_roomNos.removeAllItems();
        cbox_roomBeds.removeAllItems();
        txt_roomIdCheckIn.setText("");
        txt_roomPriceCheckIn.setText("");
        try{
            Connection con = connect();
            String sql = "Select room_id, description, room_no, bed from room where room_type = ? and status = 'Available'";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, roomType);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String roomID = rs.getString("room_id");
                boolean isBooked = isRoomBooked(con, roomID, currentDate);
                if(!isBooked){
                    cbox_roomDecs.addItem(rs.getString("description"));
                    cbox_roomNos.addItem(rs.getString("room_no"));
                    cbox_roomBeds.addItem(rs.getString("bed"));
                }                              
            }
            rs.close();
            
            if(cbox_roomNos.getItemCount() == 0){
                //
            }
            else{
               String sql2 = "Select room_id, room_price from room where room_no = ?";
                PreparedStatement ps2 = con.prepareStatement(sql2);
                ps2.setString(1, cbox_roomNos.getSelectedItem().toString());
                ResultSet rs2 = ps2.executeQuery();
                while(rs2.next()){
                    txt_roomIdCheckIn.setText(rs2.getString("room_id"));
                    txt_roomPriceCheckIn.setText(rs2.getString("room_price"));
                } 
            }           
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel22 = new javax.swing.JLabel();
        txt_bookingID3 = new javax.swing.JTextField();
        cusGenderRbtnGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_role = new javax.swing.JLabel();
        txt_staffid = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        lbl_manageRooms = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lbl_manageRoomBooking = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lbl_guestCheckin = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lbl_guestCheckiout = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lbl_manageStaffacc = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lbl_logout = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        lbl_managePackage = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lbl_managePackageBooking = new javax.swing.JLabel();
        TabbedPane = new javax.swing.JTabbedPane();
        panel_adminHome = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        panel_manageRooms = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        roomTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txt_roomID = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_roomNum = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_roomType = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txt_roomStatus = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_roomDec = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_roomPrice = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_imagePath = new javax.swing.JTextField();
        spin_bedCount = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        lbl_roomImage = new javax.swing.JLabel();
        btn_addRoom = new javax.swing.JButton();
        btn_updateRoom = new javax.swing.JButton();
        btn_removeRoom = new javax.swing.JButton();
        btn_chooseImage = new javax.swing.JButton();
        btn_generateID = new javax.swing.JButton();
        panel_managePackages = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_packages = new javax.swing.JTable();
        lbl_packageImage = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_packageID = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txt_packageName = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txt_packageType = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txt_packagePrice = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_services = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txt_packageStatus = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_packageImagePath = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        btn_chooseNewImage = new javax.swing.JButton();
        btn_addPackage = new javax.swing.JButton();
        btn_removePackage = new javax.swing.JButton();
        btn_updatePackage = new javax.swing.JButton();
        btn_generatePackageID = new javax.swing.JButton();
        panel_manageBookings = new javax.swing.JPanel();
        txt_bookingID = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        btn_searchBooking = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txt_bookedRoomID = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_bookedCusID = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txt_bookingDate = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txt_bookingStayPeriod = new javax.swing.JTextField();
        btn_cancelBooking = new javax.swing.JButton();
        btn_clearBookingFields = new javax.swing.JButton();
        panel_guestCheckIn = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        txt_cusID = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txt_cusName = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txt_cusAge = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txt_cusNRC = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txt_cusPassport = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txt_cusContact = new javax.swing.JTextField();
        rbtn_male = new javax.swing.JRadioButton();
        rbtn_female = new javax.swing.JRadioButton();
        jLabel32 = new javax.swing.JLabel();
        cbox_roomTypes = new javax.swing.JComboBox<>();
        jLabel33 = new javax.swing.JLabel();
        cbox_roomDecs = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        txt_cusCheckinDate = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        cbox_roomBeds = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        cbox_roomNos = new javax.swing.JComboBox<>();
        jLabel37 = new javax.swing.JLabel();
        txt_roomIdCheckIn = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        txt_roomPriceCheckIn = new javax.swing.JTextField();
        btn_allocateRoom = new javax.swing.JButton();
        btn_clear = new javax.swing.JButton();
        btn_addToList = new javax.swing.JButton();
        panel_guestCheckOut = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_rReservedData = new javax.swing.JTable();
        jLabel39 = new javax.swing.JLabel();
        txt_cusIDcheckOut = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        txt_cusNamecheckOut = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        txt_cusAgecheckOut = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        txt_cusNRCcheckOut = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        txt_cusPasscheckOut = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        txt_cusConcheckOut = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        txt_checkInDate = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        txt_cusGendercheckOut = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        txt_roomIDcheckOut = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        txt_roomNocheckOut = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        txt_roomTypecheckOut = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        txt_roomPricecheckOut = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        txt_totalCost = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        txt_paymentStatus = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        txt_checkOutDate = new javax.swing.JTextField();
        btn_checkOut = new javax.swing.JButton();
        jLabel54 = new javax.swing.JLabel();
        txt_roomNoSearch = new javax.swing.JTextField();
        btn_searchRoomNo = new javax.swing.JButton();

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setText("Booking ID:");

        txt_bookingID3.setBackground(new java.awt.Color(255, 255, 255));
        txt_bookingID3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_bookingID3.setForeground(new java.awt.Color(0, 0, 0));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("GOLDEN OASIS HOTEL");

        txt_role.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        txt_role.setForeground(new java.awt.Color(255, 255, 255));
        txt_role.setText("Staff role");

        txt_staffid.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        txt_staffid.setForeground(new java.awt.Color(255, 255, 255));
        txt_staffid.setText("Staff id");

        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setForeground(new java.awt.Color(0, 0, 0));

        lbl_manageRooms.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_manageRooms.setForeground(new java.awt.Color(0, 0, 0));
        lbl_manageRooms.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_manageRooms.setText("Manage Rooms");
        lbl_manageRooms.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_manageRooms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_manageRoomsMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(lbl_manageRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_manageRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setForeground(new java.awt.Color(0, 0, 0));

        lbl_manageRoomBooking.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_manageRoomBooking.setForeground(new java.awt.Color(0, 0, 0));
        lbl_manageRoomBooking.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_manageRoomBooking.setText("Manage Room Bookings");
        lbl_manageRoomBooking.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_manageRoomBooking.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_manageRoomBookingMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_manageRoomBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_manageRoomBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setForeground(new java.awt.Color(0, 0, 0));

        lbl_guestCheckin.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_guestCheckin.setForeground(new java.awt.Color(0, 0, 0));
        lbl_guestCheckin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_guestCheckin.setText("Guest Checking In");
        lbl_guestCheckin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_guestCheckin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_guestCheckinMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(lbl_guestCheckin, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_guestCheckin, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setForeground(new java.awt.Color(0, 0, 0));

        lbl_guestCheckiout.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_guestCheckiout.setForeground(new java.awt.Color(0, 0, 0));
        lbl_guestCheckiout.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_guestCheckiout.setText("Guest Checking Out");
        lbl_guestCheckiout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_guestCheckiout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_guestCheckioutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_guestCheckiout, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_guestCheckiout, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setForeground(new java.awt.Color(0, 0, 0));

        lbl_manageStaffacc.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_manageStaffacc.setForeground(new java.awt.Color(0, 0, 0));
        lbl_manageStaffacc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_manageStaffacc.setText("Manage Staff Accounts");
        lbl_manageStaffacc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(lbl_manageStaffacc, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_manageStaffacc, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setForeground(new java.awt.Color(0, 0, 0));

        lbl_logout.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_logout.setForeground(new java.awt.Color(0, 0, 0));
        lbl_logout.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_logout.setText("Log Out");
        lbl_logout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_logoutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(108, 108, 108)
                .addComponent(lbl_logout, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_logout, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setForeground(new java.awt.Color(0, 0, 0));

        lbl_managePackage.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_managePackage.setForeground(new java.awt.Color(0, 0, 0));
        lbl_managePackage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_managePackage.setText("Manage Packages");
        lbl_managePackage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_managePackage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_managePackageMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(lbl_managePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_managePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setForeground(new java.awt.Color(0, 0, 0));

        lbl_managePackageBooking.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_managePackageBooking.setForeground(new java.awt.Color(0, 0, 0));
        lbl_managePackageBooking.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_managePackageBooking.setText("Manage Package Bookings");
        lbl_managePackageBooking.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_managePackageBooking.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_managePackageBookingMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(lbl_managePackageBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_managePackageBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txt_staffid, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_role, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(66, Short.MAX_VALUE))
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_staffid, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_role, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 810));

        panel_adminHome.setBackground(new java.awt.Color(255, 255, 255));
        panel_adminHome.setForeground(new java.awt.Color(0, 0, 0));

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(255, 255, 255));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Segoe UI Black", 1, 48)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(255, 204, 0));
        jTextArea1.setRows(5);
        jTextArea1.setText("         THE GOLDEN OASIS HOTEL\n\n                         WELCOME\n                               TO\n                     ADMIN PANEL");
        jTextArea1.setBorder(null);
        jTextArea1.setFocusable(false);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout panel_adminHomeLayout = new javax.swing.GroupLayout(panel_adminHome);
        panel_adminHome.setLayout(panel_adminHomeLayout);
        panel_adminHomeLayout.setHorizontalGroup(
            panel_adminHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_adminHomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1014, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_adminHomeLayout.setVerticalGroup(
            panel_adminHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_adminHomeLayout.createSequentialGroup()
                .addContainerGap(221, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(202, 202, 202))
        );

        TabbedPane.addTab("tab1", panel_adminHome);

        panel_manageRooms.setBackground(new java.awt.Color(255, 255, 255));
        panel_manageRooms.setForeground(new java.awt.Color(0, 0, 0));

        roomTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        roomTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                roomTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(roomTable);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Room ID:");

        txt_roomID.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_roomID.setForeground(new java.awt.Color(0, 0, 0));
        txt_roomID.setEnabled(false);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Room No:");

        txt_roomNum.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomNum.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_roomNum.setForeground(new java.awt.Color(0, 0, 0));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Room Type:");

        txt_roomType.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomType.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_roomType.setForeground(new java.awt.Color(0, 0, 0));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Room Status:");

        txt_roomStatus.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomStatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_roomStatus.setForeground(new java.awt.Color(0, 0, 0));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Room Description:");

        txt_roomDec.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomDec.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_roomDec.setForeground(new java.awt.Color(0, 0, 0));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Bed Count:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Room Price:");

        txt_roomPrice.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_roomPrice.setForeground(new java.awt.Color(0, 0, 0));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Room Image Path:");

        txt_imagePath.setBackground(new java.awt.Color(255, 255, 255));
        txt_imagePath.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_imagePath.setForeground(new java.awt.Color(0, 0, 0));
        txt_imagePath.setEnabled(false);

        spin_bedCount.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Choose New Image:");

        lbl_roomImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_roomImage.setText("Room Image");
        lbl_roomImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbl_roomImage.setFocusable(false);

        btn_addRoom.setBackground(new java.awt.Color(255, 255, 255));
        btn_addRoom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_addRoom.setForeground(new java.awt.Color(0, 0, 0));
        btn_addRoom.setText("Add Room");
        btn_addRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addRoomActionPerformed(evt);
            }
        });

        btn_updateRoom.setBackground(new java.awt.Color(255, 255, 255));
        btn_updateRoom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_updateRoom.setForeground(new java.awt.Color(0, 0, 0));
        btn_updateRoom.setText("Update Room");
        btn_updateRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateRoomActionPerformed(evt);
            }
        });

        btn_removeRoom.setBackground(new java.awt.Color(255, 255, 255));
        btn_removeRoom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_removeRoom.setForeground(new java.awt.Color(0, 0, 0));
        btn_removeRoom.setText("Remove Room");
        btn_removeRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removeRoomActionPerformed(evt);
            }
        });

        btn_chooseImage.setBackground(new java.awt.Color(255, 255, 255));
        btn_chooseImage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_chooseImage.setForeground(new java.awt.Color(0, 0, 0));
        btn_chooseImage.setText("Choose Image");
        btn_chooseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_chooseImageActionPerformed(evt);
            }
        });

        btn_generateID.setBackground(new java.awt.Color(255, 255, 255));
        btn_generateID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_generateID.setForeground(new java.awt.Color(0, 0, 0));
        btn_generateID.setText("Generate ID to add new room");
        btn_generateID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_generateIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_manageRoomsLayout = new javax.swing.GroupLayout(panel_manageRooms);
        panel_manageRooms.setLayout(panel_manageRoomsLayout);
        panel_manageRoomsLayout.setHorizontalGroup(
            panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manageRoomsLayout.createSequentialGroup()
                .addGroup(panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manageRoomsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_roomImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)))
                    .addGroup(panel_manageRoomsLayout.createSequentialGroup()
                        .addGap(155, 155, 155)
                        .addComponent(btn_addRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_updateRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_removeRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manageRoomsLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel_manageRoomsLayout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_chooseImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txt_roomPrice)
                            .addComponent(spin_bedCount)
                            .addComponent(txt_roomStatus)
                            .addComponent(txt_roomType)
                            .addComponent(txt_roomNum)
                            .addComponent(txt_roomID)
                            .addComponent(txt_imagePath, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_roomDec, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel_manageRoomsLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(btn_generateID)))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        panel_manageRoomsLayout.setVerticalGroup(
            panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_manageRoomsLayout.createSequentialGroup()
                .addContainerGap(55, Short.MAX_VALUE)
                .addGroup(panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_manageRoomsLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomID, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomNum, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomType, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomDec, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(spin_bedCount, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_imagePath, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_chooseImage, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel_manageRoomsLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_roomImage, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_addRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_updateRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_removeRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_generateID, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40))
        );

        TabbedPane.addTab("tab2", panel_manageRooms);

        panel_managePackages.setBackground(new java.awt.Color(255, 255, 255));
        panel_managePackages.setForeground(new java.awt.Color(0, 0, 0));

        table_packages.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table_packages.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_packagesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(table_packages);

        lbl_packageImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_packageImage.setText("Package Image");
        lbl_packageImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Package ID:");

        txt_packageID.setBackground(new java.awt.Color(255, 255, 255));
        txt_packageID.setForeground(new java.awt.Color(0, 0, 0));
        txt_packageID.setEnabled(false);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Package Name:");

        txt_packageName.setBackground(new java.awt.Color(255, 255, 255));
        txt_packageName.setForeground(new java.awt.Color(0, 0, 0));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("Package Type:");

        txt_packageType.setBackground(new java.awt.Color(255, 255, 255));
        txt_packageType.setForeground(new java.awt.Color(0, 0, 0));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("Package Price:");

        txt_packagePrice.setBackground(new java.awt.Color(255, 255, 255));
        txt_packagePrice.setForeground(new java.awt.Color(0, 0, 0));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Services:");

        txt_services.setBackground(new java.awt.Color(255, 255, 255));
        txt_services.setForeground(new java.awt.Color(0, 0, 0));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("Status:");

        txt_packageStatus.setBackground(new java.awt.Color(255, 255, 255));
        txt_packageStatus.setForeground(new java.awt.Color(0, 0, 0));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Package Image Path:");

        txt_packageImagePath.setBackground(new java.awt.Color(255, 255, 255));
        txt_packageImagePath.setForeground(new java.awt.Color(0, 0, 0));
        txt_packageImagePath.setEnabled(false);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setText("Choose Image:");

        btn_chooseNewImage.setBackground(new java.awt.Color(255, 255, 255));
        btn_chooseNewImage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_chooseNewImage.setForeground(new java.awt.Color(0, 0, 0));
        btn_chooseNewImage.setText("Choose Image");
        btn_chooseNewImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_chooseNewImageActionPerformed(evt);
            }
        });

        btn_addPackage.setBackground(new java.awt.Color(255, 255, 255));
        btn_addPackage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_addPackage.setForeground(new java.awt.Color(0, 0, 0));
        btn_addPackage.setText("Add Package");
        btn_addPackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addPackageActionPerformed(evt);
            }
        });

        btn_removePackage.setBackground(new java.awt.Color(255, 255, 255));
        btn_removePackage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_removePackage.setForeground(new java.awt.Color(0, 0, 0));
        btn_removePackage.setText("Remove Package");
        btn_removePackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removePackageActionPerformed(evt);
            }
        });

        btn_updatePackage.setBackground(new java.awt.Color(255, 255, 255));
        btn_updatePackage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_updatePackage.setForeground(new java.awt.Color(0, 0, 0));
        btn_updatePackage.setText("Update Package");
        btn_updatePackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updatePackageActionPerformed(evt);
            }
        });

        btn_generatePackageID.setBackground(new java.awt.Color(255, 255, 255));
        btn_generatePackageID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_generatePackageID.setForeground(new java.awt.Color(0, 0, 0));
        btn_generatePackageID.setText("Generate ID to Add New Package");
        btn_generatePackageID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_generatePackageIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_managePackagesLayout = new javax.swing.GroupLayout(panel_managePackages);
        panel_managePackages.setLayout(panel_managePackagesLayout);
        panel_managePackagesLayout.setHorizontalGroup(
            panel_managePackagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_managePackagesLayout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(btn_addPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_updatePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_removePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                .addComponent(btn_generatePackageID)
                .addGap(52, 52, 52))
            .addGroup(panel_managePackagesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_managePackagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_packageImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addGroup(panel_managePackagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_packageID, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_packageName, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_packageType, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_packagePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_services, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_packageStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_packageImagePath, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_managePackagesLayout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_chooseNewImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_managePackagesLayout.setVerticalGroup(
            panel_managePackagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_managePackagesLayout.createSequentialGroup()
                .addGroup(panel_managePackagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panel_managePackagesLayout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_packageID, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_packageName, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_packageType, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_packagePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_services, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_packageStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_packageImagePath, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel_managePackagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_chooseNewImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panel_managePackagesLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_packageImage, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(panel_managePackagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_addPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_removePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_updatePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_generatePackageID, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        TabbedPane.addTab("tab3", panel_managePackages);

        panel_manageBookings.setBackground(new java.awt.Color(255, 255, 255));
        panel_manageBookings.setForeground(new java.awt.Color(0, 0, 0));

        txt_bookingID.setBackground(new java.awt.Color(255, 255, 255));
        txt_bookingID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_bookingID.setForeground(new java.awt.Color(0, 0, 0));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("Booking ID:");

        btn_searchBooking.setBackground(new java.awt.Color(255, 255, 255));
        btn_searchBooking.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_searchBooking.setForeground(new java.awt.Color(0, 0, 0));
        btn_searchBooking.setText("Search");
        btn_searchBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchBookingActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("Room ID:");

        txt_bookedRoomID.setBackground(new java.awt.Color(255, 255, 255));
        txt_bookedRoomID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_bookedRoomID.setForeground(new java.awt.Color(0, 0, 0));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setText("Customer ID:");

        txt_bookedCusID.setBackground(new java.awt.Color(255, 255, 255));
        txt_bookedCusID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_bookedCusID.setForeground(new java.awt.Color(0, 0, 0));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 0, 0));
        jLabel23.setText("Booking Date:");

        txt_bookingDate.setBackground(new java.awt.Color(255, 255, 255));
        txt_bookingDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_bookingDate.setForeground(new java.awt.Color(0, 0, 0));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 0));
        jLabel24.setText("Stay Period:");

        txt_bookingStayPeriod.setBackground(new java.awt.Color(255, 255, 255));
        txt_bookingStayPeriod.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_bookingStayPeriod.setForeground(new java.awt.Color(0, 0, 0));

        btn_cancelBooking.setBackground(new java.awt.Color(255, 255, 255));
        btn_cancelBooking.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_cancelBooking.setForeground(new java.awt.Color(0, 0, 0));
        btn_cancelBooking.setText("Cancel Booking");
        btn_cancelBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelBookingActionPerformed(evt);
            }
        });

        btn_clearBookingFields.setBackground(new java.awt.Color(255, 255, 255));
        btn_clearBookingFields.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_clearBookingFields.setForeground(new java.awt.Color(0, 0, 0));
        btn_clearBookingFields.setText("Clear Fields");
        btn_clearBookingFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearBookingFieldsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_manageBookingsLayout = new javax.swing.GroupLayout(panel_manageBookings);
        panel_manageBookings.setLayout(panel_manageBookingsLayout);
        panel_manageBookingsLayout.setHorizontalGroup(
            panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_manageBookingsLayout.createSequentialGroup()
                .addContainerGap(195, Short.MAX_VALUE)
                .addGroup(panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_bookingStayPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bookingDate, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bookedCusID, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_manageBookingsLayout.createSequentialGroup()
                        .addGroup(panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_manageBookingsLayout.createSequentialGroup()
                                .addComponent(txt_bookingID, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_searchBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_bookedRoomID, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(106, 106, 106)
                        .addGroup(panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_clearBookingFields, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_cancelBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(38, 38, 38))
        );
        panel_manageBookingsLayout.setVerticalGroup(
            panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manageBookingsLayout.createSequentialGroup()
                .addGap(165, 165, 165)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_cancelBooking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_searchBooking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_bookingID, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manageBookingsLayout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_bookedRoomID, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_manageBookingsLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(btn_clearBookingFields, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_bookedCusID, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_bookingDate, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_bookingStayPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(188, 188, 188))
        );

        TabbedPane.addTab("tab4", panel_manageBookings);

        panel_guestCheckIn.setBackground(new java.awt.Color(255, 255, 255));
        panel_guestCheckIn.setForeground(new java.awt.Color(0, 0, 0));

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setText("Customer ID:");

        txt_cusID.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_cusID.setForeground(new java.awt.Color(0, 0, 0));
        txt_cusID.setEnabled(false);

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("Customer Name:");

        txt_cusName.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_cusName.setForeground(new java.awt.Color(0, 0, 0));

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setText("Customer Age:");

        txt_cusAge.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusAge.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_cusAge.setForeground(new java.awt.Color(0, 0, 0));

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(0, 0, 0));
        jLabel28.setText("Customer NRC:");

        txt_cusNRC.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusNRC.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_cusNRC.setForeground(new java.awt.Color(0, 0, 0));

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 0, 0));
        jLabel29.setText("Customer Passport ID:");

        txt_cusPassport.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusPassport.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_cusPassport.setForeground(new java.awt.Color(0, 0, 0));

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 0, 0));
        jLabel30.setText("Customer Gender:");

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(0, 0, 0));
        jLabel31.setText("Customer Contact:");

        txt_cusContact.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusContact.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_cusContact.setForeground(new java.awt.Color(0, 0, 0));

        rbtn_male.setBackground(new java.awt.Color(255, 255, 255));
        cusGenderRbtnGroup.add(rbtn_male);
        rbtn_male.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rbtn_male.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_male.setText("Male");

        rbtn_female.setBackground(new java.awt.Color(255, 255, 255));
        cusGenderRbtnGroup.add(rbtn_female);
        rbtn_female.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rbtn_female.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_female.setText("Female");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(0, 0, 0));
        jLabel32.setText("Room Type:");

        cbox_roomTypes.setBackground(new java.awt.Color(255, 255, 255));
        cbox_roomTypes.setForeground(new java.awt.Color(0, 0, 0));
        cbox_roomTypes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbox_roomTypesItemStateChanged(evt);
            }
        });

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(0, 0, 0));
        jLabel33.setText("Room Description:");

        cbox_roomDecs.setBackground(new java.awt.Color(255, 255, 255));
        cbox_roomDecs.setForeground(new java.awt.Color(0, 0, 0));

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(0, 0, 0));
        jLabel34.setText("Check In Date:");

        txt_cusCheckinDate.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusCheckinDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_cusCheckinDate.setForeground(new java.awt.Color(0, 0, 0));
        txt_cusCheckinDate.setEnabled(false);

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(0, 0, 0));
        jLabel35.setText("Bed Count:");

        cbox_roomBeds.setBackground(new java.awt.Color(255, 255, 255));
        cbox_roomBeds.setForeground(new java.awt.Color(0, 0, 0));

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(0, 0, 0));
        jLabel36.setText("Room No:");

        cbox_roomNos.setBackground(new java.awt.Color(255, 255, 255));
        cbox_roomNos.setForeground(new java.awt.Color(0, 0, 0));
        cbox_roomNos.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbox_roomNosItemStateChanged(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(0, 0, 0));
        jLabel37.setText("Room ID:");

        txt_roomIdCheckIn.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomIdCheckIn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_roomIdCheckIn.setForeground(new java.awt.Color(0, 0, 0));
        txt_roomIdCheckIn.setEnabled(false);

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(0, 0, 0));
        jLabel38.setText("Room Price:");

        txt_roomPriceCheckIn.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomPriceCheckIn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_roomPriceCheckIn.setForeground(new java.awt.Color(0, 0, 0));
        txt_roomPriceCheckIn.setEnabled(false);

        btn_allocateRoom.setBackground(new java.awt.Color(0, 153, 153));
        btn_allocateRoom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_allocateRoom.setForeground(new java.awt.Color(255, 255, 255));
        btn_allocateRoom.setText("Allocate Room");
        btn_allocateRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_allocateRoomActionPerformed(evt);
            }
        });

        btn_clear.setBackground(new java.awt.Color(204, 204, 204));
        btn_clear.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_clear.setForeground(new java.awt.Color(0, 0, 0));
        btn_clear.setText("Clear Fields");

        btn_addToList.setBackground(new java.awt.Color(0, 153, 153));
        btn_addToList.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_addToList.setForeground(new java.awt.Color(255, 255, 255));
        btn_addToList.setText("Add to List");
        btn_addToList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addToListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_guestCheckInLayout = new javax.swing.GroupLayout(panel_guestCheckIn);
        panel_guestCheckIn.setLayout(panel_guestCheckInLayout);
        panel_guestCheckInLayout.setHorizontalGroup(
            panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(rbtn_male, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(rbtn_female, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cusID)
                    .addComponent(txt_cusName)
                    .addComponent(txt_cusAge)
                    .addComponent(txt_cusNRC)
                    .addComponent(txt_cusPassport)
                    .addComponent(txt_cusContact)
                    .addComponent(txt_cusCheckinDate, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(241, 241, 241)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbox_roomTypes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbox_roomBeds, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbox_roomNos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_roomIdCheckIn)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_roomPriceCheckIn)
                    .addComponent(btn_allocateRoom, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(btn_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbox_roomDecs, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_addToList, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                .addContainerGap(182, Short.MAX_VALUE))
        );
        panel_guestCheckInLayout.setVerticalGroup(
            panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbox_roomTypes)
                    .addComponent(txt_cusID, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cusName, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbox_roomDecs, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cusAge, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbox_roomBeds, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cusNRC, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbox_roomNos, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cusPassport, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomIdCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_male)
                            .addComponent(rbtn_female)))
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomPriceCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_cusContact, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_addToList, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_cusCheckinDate, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_allocateRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );

        TabbedPane.addTab("tab5", panel_guestCheckIn);

        panel_guestCheckOut.setBackground(new java.awt.Color(255, 255, 255));
        panel_guestCheckOut.setForeground(new java.awt.Color(0, 0, 0));

        table_rReservedData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table_rReservedData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_rReservedDataMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(table_rReservedData);

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(0, 0, 0));
        jLabel39.setText("Customer ID:");

        txt_cusIDcheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusIDcheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_cusIDcheckOut.setForeground(new java.awt.Color(0, 0, 0));
        txt_cusIDcheckOut.setEnabled(false);

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(0, 0, 0));
        jLabel40.setText("Customer Name:");

        txt_cusNamecheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusNamecheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_cusNamecheckOut.setForeground(new java.awt.Color(0, 0, 0));

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(0, 0, 0));
        jLabel41.setText("Customer Age:");

        txt_cusAgecheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusAgecheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_cusAgecheckOut.setForeground(new java.awt.Color(0, 0, 0));

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(0, 0, 0));
        jLabel42.setText("Customer NRC:");

        txt_cusNRCcheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusNRCcheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_cusNRCcheckOut.setForeground(new java.awt.Color(0, 0, 0));

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(0, 0, 0));
        jLabel43.setText("Customer Passport:");

        txt_cusPasscheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusPasscheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_cusPasscheckOut.setForeground(new java.awt.Color(0, 0, 0));

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(0, 0, 0));
        jLabel44.setText("Customer Contact:");

        txt_cusConcheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusConcheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_cusConcheckOut.setForeground(new java.awt.Color(0, 0, 0));

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(0, 0, 0));
        jLabel45.setText("Check In Date:");

        txt_checkInDate.setBackground(new java.awt.Color(255, 255, 255));
        txt_checkInDate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_checkInDate.setForeground(new java.awt.Color(0, 0, 0));
        txt_checkInDate.setEnabled(false);

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 0, 0));
        jLabel46.setText("Customer Gender:");

        txt_cusGendercheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_cusGendercheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_cusGendercheckOut.setForeground(new java.awt.Color(0, 0, 0));

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(0, 0, 0));
        jLabel47.setText("Room ID:");

        txt_roomIDcheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomIDcheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_roomIDcheckOut.setForeground(new java.awt.Color(0, 0, 0));
        txt_roomIDcheckOut.setEnabled(false);

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(0, 0, 0));
        jLabel48.setText("Room No:");

        txt_roomNocheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomNocheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_roomNocheckOut.setForeground(new java.awt.Color(0, 0, 0));

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(0, 0, 0));
        jLabel49.setText("Room Type:");

        txt_roomTypecheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomTypecheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_roomTypecheckOut.setForeground(new java.awt.Color(0, 0, 0));

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(0, 0, 0));
        jLabel50.setText("Room Price:");

        txt_roomPricecheckOut.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomPricecheckOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_roomPricecheckOut.setForeground(new java.awt.Color(0, 0, 0));

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(0, 0, 0));
        jLabel51.setText("Total Cost:");

        txt_totalCost.setBackground(new java.awt.Color(255, 255, 255));
        txt_totalCost.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_totalCost.setForeground(new java.awt.Color(0, 0, 0));

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(0, 0, 0));
        jLabel52.setText("Payment Status:");

        txt_paymentStatus.setBackground(new java.awt.Color(255, 255, 255));
        txt_paymentStatus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_paymentStatus.setForeground(new java.awt.Color(0, 0, 0));

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(0, 0, 0));
        jLabel53.setText("Check Out Date:");

        txt_checkOutDate.setBackground(new java.awt.Color(255, 255, 255));
        txt_checkOutDate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_checkOutDate.setForeground(new java.awt.Color(0, 0, 0));
        txt_checkOutDate.setEnabled(false);

        btn_checkOut.setBackground(new java.awt.Color(0, 153, 153));
        btn_checkOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_checkOut.setForeground(new java.awt.Color(255, 255, 255));
        btn_checkOut.setText("Check Out");
        btn_checkOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_checkOutActionPerformed(evt);
            }
        });

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(0, 0, 0));
        jLabel54.setText("Room No:");

        txt_roomNoSearch.setBackground(new java.awt.Color(255, 255, 255));
        txt_roomNoSearch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_roomNoSearch.setForeground(new java.awt.Color(0, 0, 0));

        btn_searchRoomNo.setBackground(new java.awt.Color(255, 255, 255));
        btn_searchRoomNo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_searchRoomNo.setForeground(new java.awt.Color(0, 0, 0));
        btn_searchRoomNo.setText("Search");
        btn_searchRoomNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchRoomNoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_guestCheckOutLayout = new javax.swing.GroupLayout(panel_guestCheckOut);
        panel_guestCheckOut.setLayout(panel_guestCheckOutLayout);
        panel_guestCheckOutLayout.setHorizontalGroup(
            panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_cusNRCcheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cusPasscheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cusGendercheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cusIDcheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cusNamecheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_cusAgecheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_guestCheckOutLayout.createSequentialGroup()
                        .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_checkOutDate, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_cusConcheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_checkInDate, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(120, 120, 120))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_guestCheckOutLayout.createSequentialGroup()
                        .addComponent(btn_checkOut, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(139, 139, 139)))
                .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_paymentStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_totalCost, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_roomPricecheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_roomTypecheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_roomIDcheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_roomNocheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(73, 73, 73))
            .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                        .addComponent(jLabel54)
                        .addGap(18, 18, 18)
                        .addComponent(txt_roomNoSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_searchRoomNo, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 992, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        panel_guestCheckOutLayout.setVerticalGroup(
            panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_roomNoSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_searchRoomNo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                        .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_cusIDcheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_cusNamecheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_cusAgecheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_cusConcheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_checkInDate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_checkOutDate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cusNRCcheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cusPasscheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_cusGendercheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_checkOut, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomIDcheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomNocheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomTypecheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_roomPricecheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_totalCost, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_paymentStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(62, 62, 62))
        );

        TabbedPane.addTab("tab6", panel_guestCheckOut);

        jPanel1.add(TabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(438, -34, -1, 810));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lbl_manageRoomsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_manageRoomsMouseClicked
        // TODO add your handling code here:
        if(role != 0){
            JOptionPane.showMessageDialog(null, "Only Manager can access to this Operation.", "Not Allowed Access", JOptionPane.WARNING_MESSAGE);
        }
        else{
            TabbedPane.setSelectedIndex(1);
            clearRoomFields();
        }       
    }//GEN-LAST:event_lbl_manageRoomsMouseClicked

    private void roomTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roomTableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) roomTable.getModel();
        int index = roomTable.getSelectedRow();
        txt_roomID.setText(model.getValueAt(index, 0).toString());
        txt_roomNum.setText(model.getValueAt(index, 1).toString());
        txt_roomType.setText(model.getValueAt(index, 2).toString());
        txt_roomPrice.setText(model.getValueAt(index, 3).toString());
        txt_roomStatus.setText(model.getValueAt(index, 4).toString());
        txt_imagePath.setText(model.getValueAt(index, 5).toString());
        txt_roomDec.setText(model.getValueAt(index, 6).toString());
        spin_bedCount.setValue(model.getValueAt(index, 7));
        
        ImageIcon icon = new ImageIcon(getClass().getResource("/ImagesandIcons/" + txt_imagePath.getText()));
        icon = new ImageIcon(icon.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH));
        lbl_roomImage.setText("");
        lbl_roomImage.setIcon(icon);
    }//GEN-LAST:event_roomTableMouseClicked

    private void btn_chooseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_chooseImageActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File file = chooser.getSelectedFile();
        String path = file.getAbsolutePath();
        txt_imagePath.setText(path);
        ImageIcon icon = new ImageIcon(path);
        icon = new ImageIcon(icon.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH));
        lbl_roomImage.setText("");
        lbl_roomImage.setIcon(icon);
    }//GEN-LAST:event_btn_chooseImageActionPerformed

    private void btn_generateIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_generateIDActionPerformed
        // TODO add your handling code here:
        txt_roomID.setText(generateRoomID());
        txt_roomNum.setText("");
        txt_roomType.setText("");
        txt_roomPrice.setText("");
        txt_roomStatus.setText("");
        txt_imagePath.setText("");
        txt_roomDec.setText("");
        spin_bedCount.setValue(0);
        lbl_roomImage.setIcon(null);
        lbl_roomImage.setText("Room Image");
    }//GEN-LAST:event_btn_generateIDActionPerformed

    private void btn_removeRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removeRoomActionPerformed
        // TODO add your handling code here:
        String roomid = txt_roomID.getText();
        int result = JOptionPane.showConfirmDialog(null, "Confirm Room Removal?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            try{
                Connection con = connect();
                String sql = "delete from room where room_id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, roomid);
                ps.executeUpdate();
                deleteImage(txt_imagePath.getText());
                clearRoomFields();
                DisplayRoom();
                JOptionPane.showMessageDialog(null, "Room is removed successfully!", "Removed", JOptionPane.INFORMATION_MESSAGE);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }       
    }//GEN-LAST:event_btn_removeRoomActionPerformed

    private void btn_addRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addRoomActionPerformed
        // TODO add your handling code here:
        String imageName = changeRoomImageName(txt_imagePath.getText());
        if(!emptyValidateRoom()){
            try{
                Connection con = connect();
                String sql = "Insert into room (room_id, room_no, room_type, room_price, status, image_name, description, bed) values (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, txt_roomID.getText());
                ps.setString(2, txt_roomNum.getText());
                ps.setString(3, txt_roomType.getText());
                ps.setFloat(4, Float.parseFloat(txt_roomPrice.getText()));
                ps.setString(5, txt_roomStatus.getText());
                ps.setString(6, imageName);
                ps.setString(7, txt_roomDec.getText());
                ps.setInt(8, (int) spin_bedCount.getValue());
                int rowAffected = ps.executeUpdate();
                if(rowAffected > 0){
                    JOptionPane.showMessageDialog(null, "New Room is added successfully!", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                    copyImage(txt_imagePath.getText(), imageName);
                    clearRoomFields();
                    DisplayRoom();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Room addition failed!", "Operation Failed!", JOptionPane.ERROR_MESSAGE);
                    clearRoomFields();
                    DisplayRoom();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Please Fill all fields", "Empty Fields", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btn_addRoomActionPerformed

    private void btn_updateRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateRoomActionPerformed
        // TODO add your handling code here:
        String roomID = txt_roomID.getText();
        String oldImageName = txt_imagePath.getText();
        String updateImageName = changeRoomImageName(txt_imagePath.getText());
        int result = JOptionPane.showConfirmDialog(null, "Confirm Update?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            try{
                Connection con = connect();
                String getOldImageNameSql = "select image_name from room where room_id = ?";
                PreparedStatement pstmt = con.prepareStatement(getOldImageNameSql);
                pstmt.setString(1, roomID);
                ResultSet rs = pstmt.executeQuery();
                String OIN = null;
                while(rs.next()){
                    OIN = rs.getString("image_name");
                }
                
                String sql = "update room set room_no = ?, room_type = ?, room_price = ?, status = ?, image_name = ?, description = ?, bed = ? where room_id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, txt_roomNum.getText());
                ps.setString(2, txt_roomType.getText());
                ps.setFloat(3, Float.parseFloat(txt_roomPrice.getText()));
                ps.setString(4, txt_roomStatus.getText());
                ps.setString(5, updateImageName);
                ps.setString(6, txt_roomDec.getText());
                ps.setInt(7, (int) spin_bedCount.getValue());
                ps.setString(8, roomID);
                int rowAffected = ps.executeUpdate();
                if(rowAffected > 0){
                    if(txt_imagePath.getText().startsWith("C:\\") || txt_imagePath.getText().startsWith("D:\\")){
                        copyImage(txt_imagePath.getText(), updateImageName);
                    }
                    else{
                        copyImage("src/ImagesandIcons/" + txt_imagePath.getText(), updateImageName);
                    }
                    
                    if(OIN != null){
                       deleteImage(OIN); 
                    }
                    else{
                       deleteImage(oldImageName);
                    }                   
                    JOptionPane.showMessageDialog(null, "Updated Successfully!", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                    clearRoomFields();
                    DisplayRoom();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Update Failed", "Operation Failed", JOptionPane.ERROR_MESSAGE);
                    clearRoomFields();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_updateRoomActionPerformed

    private void table_packagesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_packagesMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_packages.getModel();
        int index = table_packages.getSelectedRow();
        txt_packageID.setText(model.getValueAt(index, 0).toString());
        txt_packageName.setText(model.getValueAt(index, 1).toString());
        txt_packageType.setText(model.getValueAt(index, 2).toString());
        txt_packagePrice.setText(model.getValueAt(index, 3).toString());
        txt_services.setText(model.getValueAt(index, 4).toString());
        txt_packageStatus.setText(model.getValueAt(index, 5).toString());
        txt_packageImagePath.setText(model.getValueAt(index, 6).toString());
        
        ImageIcon icon = new ImageIcon(getClass().getResource("/ImagesandIcons/" + txt_packageImagePath.getText()));
        icon = new ImageIcon(icon.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH));
        lbl_packageImage.setText("");
        lbl_packageImage.setIcon(icon);
    }//GEN-LAST:event_table_packagesMouseClicked

    private void btn_chooseNewImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_chooseNewImageActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File file = chooser.getSelectedFile();
        String path = file.getAbsolutePath();
        txt_packageImagePath.setText(path);
        ImageIcon icon = new ImageIcon(path);
        icon = new ImageIcon(icon.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH));
        lbl_packageImage.setText("");
        lbl_packageImage.setIcon(icon);
    }//GEN-LAST:event_btn_chooseNewImageActionPerformed

    private void lbl_managePackageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_managePackageMouseClicked
        // TODO add your handling code here:
        if(role != 0){
            JOptionPane.showMessageDialog(null, "Only Manager can access to this Operation.", "Not Allowed Access", JOptionPane.WARNING_MESSAGE);
        }
        else{
            TabbedPane.setSelectedIndex(2);
            clearPackageFields();
        }
    }//GEN-LAST:event_lbl_managePackageMouseClicked

    private void btn_generatePackageIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_generatePackageIDActionPerformed
        // TODO add your handling code here:
        txt_packageID.setText(generatePackageID());
        txt_packageName.setText("");
        txt_packageType.setText("");
        txt_packagePrice.setText("");
        txt_services.setText("");
        txt_packageStatus.setText("");
        txt_packageImagePath.setText("");
        lbl_packageImage.setIcon(null);
        lbl_packageImage.setText("Package Image");
    }//GEN-LAST:event_btn_generatePackageIDActionPerformed

    private void btn_addPackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addPackageActionPerformed
        // TODO add your handling code here:
        String imageName = changePackageImageName(txt_packageImagePath.getText());
        if(!emptyValidatePackage()){
            try{
                Connection con = connect();
                String sql = "insert into packages (package_id, package_name, package_type, package_price, services, status, image_name) values (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, txt_packageID.getText());
                ps.setString(2, txt_packageName.getText());
                ps.setString(3, txt_packageType.getText());
                ps.setFloat(4, Float.parseFloat(txt_packagePrice.getText()));
                ps.setString(5, txt_services.getText());
                ps.setString(6, txt_packageStatus.getText());
                ps.setString(7, imageName);
                int rowAffected = ps.executeUpdate();
                if(rowAffected > 0){
                    JOptionPane.showMessageDialog(null, "New Package is added Successfully!", "Operation Success", JOptionPane.INFORMATION_MESSAGE);
                    copyImage(txt_packageImagePath.getText(), imageName);
                    clearPackageFields();
                    DisplayPackages();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Please Fill all fields", "Empty Fields", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btn_addPackageActionPerformed

    private void btn_updatePackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updatePackageActionPerformed
        // TODO add your handling code here:
        String packageID = txt_packageID.getText();
        String oldImageName = txt_packageImagePath.getText();
        String updateImageName = changePackageImageName(txt_packageImagePath.getText());
        int result = JOptionPane.showConfirmDialog(null, "Confirm Update?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            try{
                Connection con = connect();
                String getOldImageNameSql = "select image_name from packages where package_id = ?";
                PreparedStatement pstmt = con.prepareStatement(getOldImageNameSql);
                pstmt.setString(1, packageID);
                ResultSet rs = pstmt.executeQuery();
                String OIN = null;
                while(rs.next()){
                    OIN = rs.getString("image_name");
                }
                
                String updateSql = "update packages set package_name = ?, package_type = ?, package_price = ?, services = ?, status = ?, image_name = ? where package_id = ?";
                PreparedStatement ps = con.prepareStatement(updateSql);
                ps.setString(1, txt_packageName.getText());
                ps.setString(2, txt_packageType.getText());
                ps.setFloat(3, Float.parseFloat(txt_packagePrice.getText()));
                ps.setString(4, txt_services.getText());
                ps.setString(5, txt_packageStatus.getText());
                ps.setString(6, updateImageName);
                ps.setString(7, packageID);
                int rowAffected = ps.executeUpdate();
                if(rowAffected > 0){
                    if(txt_packageImagePath.getText().startsWith("C:\\") || txt_packageImagePath.getText().startsWith("D:\\")){
                        copyImage(txt_packageImagePath.getText(), updateImageName);
                    }
                    else{
                        copyImage("src/ImagesandIcons/" + txt_packageImagePath.getText(), updateImageName);
                    }
                    
                    if(OIN != null){
                       deleteImage(OIN); 
                    }
                    else{
                       deleteImage(oldImageName);
                    }                   
                    JOptionPane.showMessageDialog(null, "Updated Successfully!", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                    clearPackageFields();
                    DisplayPackages();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Update Failed", "Operation Failed", JOptionPane.ERROR_MESSAGE);
                    clearRoomFields();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_updatePackageActionPerformed

    private void btn_removePackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removePackageActionPerformed
        // TODO add your handling code here:
        String packageID = txt_packageID.getText();
        int result = JOptionPane.showConfirmDialog(null, "Confirm Package Removal?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            try{
                Connection con = connect();
                String sql = "delete from packages where package_id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, packageID);
                ps.executeUpdate();
                deleteImage(txt_packageImagePath.getText());
                clearPackageFields();
                DisplayPackages();
                JOptionPane.showMessageDialog(null, "Package is removed successfully!", "Removed", JOptionPane.INFORMATION_MESSAGE);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_removePackageActionPerformed

    private void btn_searchBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchBookingActionPerformed
        // TODO add your handling code here:
        String bookingID = txt_bookingID.getText();
        try{
            Connection con = connect();
            String sql = "SELECT booking_id, GROUP_CONCAT(room_id) AS room_ids, cus_id, booking_date, stay_period FROM room_bookings WHERE booking_id = ? GROUP BY booking_id, cus_id, booking_date, stay_period";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, bookingID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                txt_bookedRoomID.setText(rs.getString("room_ids"));
                txt_bookedCusID.setText(rs.getString("cus_id"));
                txt_bookingDate.setText(rs.getString("booking_date"));
                txt_bookingStayPeriod.setText(rs.getString("stay_period"));
            }
            else{
                JOptionPane.showMessageDialog(null, "Booking does not exist", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch(Exception e){
            
        }
    }//GEN-LAST:event_btn_searchBookingActionPerformed

    private void lbl_manageRoomBookingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_manageRoomBookingMouseClicked
        // TODO add your handling code here:
        TabbedPane.setSelectedIndex(3);
        clearBookingData();
    }//GEN-LAST:event_lbl_manageRoomBookingMouseClicked

    private void btn_clearBookingFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearBookingFieldsActionPerformed
        // TODO add your handling code here:
        clearBookingData();
    }//GEN-LAST:event_btn_clearBookingFieldsActionPerformed

    private void btn_cancelBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelBookingActionPerformed
        // TODO add your handling code here:
        String bookingID = txt_bookingID.getText();
        String bookedDate = txt_bookingDate.getText();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = LocalDate.now().format(dateFormat);
        int difference = getDifferenceInDays(bookedDate, currentDate);
        if(difference >= 1){
            int result = JOptionPane.showConfirmDialog(null, "Confirm Cancel Booking?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION){
                try{
                    Connection con = connect();
                    String sql = "Delete from room_bookings where booking_id = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, bookingID);
                    ps.execute();
                    JOptionPane.showMessageDialog(null, "Booking Canceled!", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                    clearBookingData();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "The booking can't be canceled since the booked date is no more 1 day apart!", "Cannot Cancel Booking!", JOptionPane.WARNING_MESSAGE);
            clearBookingData();
        }
    }//GEN-LAST:event_btn_cancelBookingActionPerformed

    private void lbl_guestCheckinMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_guestCheckinMouseClicked
        // TODO add your handling code here:
        TabbedPane.setSelectedIndex(4);
        txt_cusID.setText(generateCustomerID());
        sameRReservedID = generateRReservedID();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = LocalDate.now().format(dateFormat);
        txt_cusCheckinDate.setText(currentDate);
        cbox_roomTypes.removeAllItems();
        cbox_roomDecs.removeAllItems();
        cbox_roomBeds.removeAllItems();
        cbox_roomNos.removeAllItems();
        
        try{
            Connection con = connect();
            String sql = "Select distinct room_type from room";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                cbox_roomTypes.addItem(rs.getString("room_type"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_lbl_guestCheckinMouseClicked

    private void cbox_roomTypesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbox_roomTypesItemStateChanged
        // TODO add your handling code here:
        getRoomDetails();
    }//GEN-LAST:event_cbox_roomTypesItemStateChanged

    private void cbox_roomNosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbox_roomNosItemStateChanged
        // TODO add your handling code here:
        if(cbox_roomNos.getItemCount() == 0){
            // combo boxes are being refreshed
        }
        else{
            String roomNo = cbox_roomNos.getSelectedItem().toString();
            txt_roomIdCheckIn.setText("");
            txt_roomPriceCheckIn.setText("");
            try{
                Connection con = connect();
                String sql = "Select room_id, room_price from room where room_no = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, roomNo);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    txt_roomIdCheckIn.setText(rs.getString("room_id"));
                    txt_roomPriceCheckIn.setText(rs.getString("room_price"));
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }        
    }//GEN-LAST:event_cbox_roomNosItemStateChanged

    private void btn_allocateRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_allocateRoomActionPerformed
        // TODO add your handling code here:
        if(!txt_cusName.getText().isEmpty() && !txt_cusAge.getText().isEmpty() && !txt_cusContact.getText().isEmpty()){
            if(txt_cusNRC.getText().isEmpty() && txt_cusPassport.getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "At least one of the two fields NRC and Passport ID needs to be filled!", "Empty Field", JOptionPane.WARNING_MESSAGE);
            }
            else{
                if(!rbtn_male.isSelected() && !rbtn_female.isSelected()){
                    JOptionPane.showMessageDialog(null, "Guest's gender must be provided", "Empty Field", JOptionPane.WARNING_MESSAGE);
                }
                else{
                    String cusID = txt_cusID.getText();
                    String cusName = txt_cusName.getText();
                    String cusAge = txt_cusAge.getText();
                    String cusNRC = txt_cusNRC.getText();
                    String cusPass = txt_cusPassport.getText();
                    int gender = -1;
                    if(rbtn_male.isSelected()){
                        gender = 1;
                    }
                    if(rbtn_female.isSelected()){
                        gender = 0;
                    }
                    String cusContact = txt_cusContact.getText();
                    String checkInDate = txt_cusCheckinDate.getText();
                    if(selectedRoomIDs.isEmpty()){
                        try{
                            Connection con = connect();
                            String sql = "insert into customer_info (cus_id, cus_name, cus_age, cus_nrc, cus_gender, cus_contact, cus_passport) values (?, ?, ?, ?, ?, ?, ?)";
                            PreparedStatement ps = con.prepareStatement(sql);
                            ps.setString(1, cusID);
                            ps.setString(2, cusName);
                            ps.setInt(3, Integer.parseInt(cusAge));
                            ps.setString(4, cusNRC);
                            ps.setInt(5, gender);
                            ps.setString(6, cusContact);
                            ps.setString(7, cusPass);
                            ps.execute();

                            String sql2 = "insert into r_reserved_data (r_reserved_id, cus_id, room_id, payment_status, check_in_date, check_out_date) values (?, ?, ?, ?, ?, ?)";
                            PreparedStatement ps2 = con.prepareStatement(sql2);
                            ps2.setString(1, generateRReservedID());
                            ps2.setString(2, cusID);
                            ps2.setString(3, txt_roomIdCheckIn.getText());
                            ps2.setString(4, "Not Paid");
                            ps2.setString(5, checkInDate);
                            ps2.setString(6, null);
                            ps2.execute();

                            String sql3 = "update room set status = 'Reserved' where room_id = ?";
                            PreparedStatement ps3 = con.prepareStatement(sql3);
                            ps3.setString(1, txt_roomIdCheckIn.getText());
                            ps3.executeUpdate();

                            JOptionPane.showMessageDialog(null, "Room Allocated Successfully", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                            getRoomDetails();
                            clearCusFields();
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        try{
                            Connection con = connect();
                            String sql = "insert into customer_info (cus_id, cus_name, cus_age, cus_nrc, cus_gender, cus_contact, cus_passport) values (?, ?, ?, ?, ?, ?, ?)";
                            PreparedStatement ps = con.prepareStatement(sql);
                            ps.setString(1, cusID);
                            ps.setString(2, cusName);
                            ps.setInt(3, Integer.parseInt(cusAge));
                            ps.setString(4, cusNRC);
                            ps.setInt(5, gender);
                            ps.setString(6, cusContact);
                            ps.setString(7, cusPass);
                            ps.execute();
                            
                            for(String roomId : selectedRoomIDs){
                                String sql2 = "insert into r_reserved_data (r_reserved_id, cus_id, room_id, payment_status, check_in_date, check_out_date) values (?, ?, ?, ?, ?, ?)";
                                PreparedStatement ps2 = con.prepareStatement(sql2);
                                ps2.setString(1, sameRReservedID);
                                ps2.setString(2, cusID);
                                ps2.setString(3, roomId);
                                ps2.setString(4, "Not Paid");
                                ps2.setString(5, checkInDate);
                                ps2.setString(6, null);
                                ps2.execute();

                                String sql3 = "update room set status = 'Reserved' where room_id = ?";
                                PreparedStatement ps3 = con.prepareStatement(sql3);
                                ps3.setString(1, roomId);
                                ps3.executeUpdate();
                            }
                            
                            JOptionPane.showMessageDialog(null, "Rooms Allocated Successfully", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                            getRoomDetails();
                            clearCusFields();
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        
                    }
                }
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Please Enter all Required Fields", "Empty Fields", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btn_allocateRoomActionPerformed

    private void lbl_guestCheckioutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_guestCheckioutMouseClicked
        // TODO add your handling code here:
        TabbedPane.setSelectedIndex(5);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = LocalDate.now().format(dateFormat);
        txt_checkOutDate.setText(currentDate);
        DisplayRoomReservedData();
    }//GEN-LAST:event_lbl_guestCheckioutMouseClicked

    private void table_rReservedDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_rReservedDataMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_rReservedData.getModel();
        int index = table_rReservedData.getSelectedRow();
        txt_checkInDate.setText(model.getValueAt(index, 4).toString());
        txt_paymentStatus.setText(model.getValueAt(index, 3).toString());
        String cusID = model.getValueAt(index, 1).toString();
        String roomID = model.getValueAt(index, 2).toString();
        try{
            Connection con = connect();
            String cusSql = "select cus_name, cus_age, cus_nrc, cus_passport, cus_gender, cus_contact from customer_info where cus_id = ?";
            PreparedStatement cusPs = con.prepareStatement(cusSql);
            cusPs.setString(1, cusID);
            ResultSet cusRs = cusPs.executeQuery();
            while(cusRs.next()){
                txt_cusIDcheckOut.setText(cusID);
                txt_cusNamecheckOut.setText(cusRs.getString("cus_name"));
                txt_cusAgecheckOut.setText(cusRs.getString("cus_age"));
                txt_cusNRCcheckOut.setText(cusRs.getString("cus_nrc"));
                txt_cusPasscheckOut.setText(cusRs.getString("cus_passport"));
                int gender = cusRs.getInt("cus_gender");
                if(gender == 1){
                    txt_cusGendercheckOut.setText("Male");
                }
                else{
                    txt_cusGendercheckOut.setText("Female");
                }
                txt_cusConcheckOut.setText(cusRs.getString("cus_contact"));
            }
            
            String roomSql = "select room_no, room_type, room_price from room where room_id = ?";
            PreparedStatement roomPs = con.prepareStatement(roomSql);
            roomPs.setString(1, roomID);
            ResultSet roomRs = roomPs.executeQuery();
            while(roomRs.next()){
                txt_roomIDcheckOut.setText(roomID);
                txt_roomNocheckOut.setText(roomRs.getString("room_no"));
                txt_roomTypecheckOut.setText(roomRs.getString("room_type"));
                txt_roomPricecheckOut.setText(roomRs.getString("room_price"));
            }
            
            int stayDays = getDifferenceInDays(txt_checkInDate.getText(), txt_checkOutDate.getText());
            float totalCost = Float.parseFloat((txt_roomPricecheckOut.getText())) * stayDays;
            txt_totalCost.setText(String.valueOf(totalCost));
        }
        catch(Exception e){
            e.printStackTrace();
        }   
    }//GEN-LAST:event_table_rReservedDataMouseClicked

    private void lbl_managePackageBookingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_managePackageBookingMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_managePackageBookingMouseClicked

    private void lbl_logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_logoutMouseClicked
        // TODO add your handling code here:
        int result = JOptionPane.showConfirmDialog(null, "Confirm Logout?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            txt_staffid.setText("Staff id");
            txt_role.setText("Staff role");
            WelcomeForm wf = new WelcomeForm();
            wf.setVisible(true);
            this.hide();
        }
    }//GEN-LAST:event_lbl_logoutMouseClicked

    private void btn_checkOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_checkOutActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_rReservedData.getModel();
        int index = table_rReservedData.getSelectedRow();
        String rReservedId = model.getValueAt(index, 0).toString();
        String roomId = model.getValueAt(index, 2).toString();
        int result = JOptionPane.showConfirmDialog(null, "Confirm Check Out?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            try{
                Connection con = connect();
                String sql = "update r_reserved_data set check_out_date = ?, payment_status = ? where r_reserved_id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, txt_checkOutDate.getText());
                ps.setString(2, "Full Paid");
                ps.setString(3, rReservedId);
                ps.executeUpdate();
                
                String sql4 = "update room set status = 'Available' where room_id = ?";
                PreparedStatement ps4 = con.prepareStatement(sql4);
                ps4.setString(1, roomId);
                ps4.executeUpdate();
                
                String receiptId = generateReceiptID();
                String sql2 = "insert into receipts (receipt_id, cus_id, r_reserved_id, p_reserved_id, period, cost, payment_status) values (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps2 = con.prepareStatement(sql2);
                ps2.setString(1, receiptId);
                ps2.setString(2, txt_cusIDcheckOut.getText());
                ps2.setString(3, rReservedId);
                ps2.setString(4, null);
                ps2.setInt(5, getDifferenceInDays(txt_checkOutDate.getText(), txt_checkInDate.getText()));
                ps2.setFloat(6, Float.parseFloat(txt_totalCost.getText()));
                ps2.setString(7, "Full Paid");
                ps2.execute();
                
                String sql3 = "insert into finance (receipt_id, Amount) values (?, ?)";
                PreparedStatement ps3 = con.prepareStatement(sql3);
                ps3.setString(1, receiptId);
                ps3.setFloat(2, Float.parseFloat(txt_totalCost.getText()));
                ps3.execute();
                
                int result2 = JOptionPane.showConfirmDialog(null, "Checked Out Successfully.\nDo you want to print the voucher?", "Operation Successful", JOptionPane.YES_NO_OPTION);
                if(result2 == JOptionPane.YES_OPTION){
                    String path = "C:\\";
                    com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
                    try{
                        PdfWriter.getInstance(doc, new FileOutputStream(path + "Receipt_" + receiptId + ".pdf"));
                        doc.open();
                        Paragraph p1 = new Paragraph("                        The Golden Oasis Hotel Guest Receipt\n");
                        doc.add(p1);
                        Paragraph p2 = new Paragraph("*****************************************************************************************");
                        doc.add(p2);
                        Paragraph p3 = new Paragraph("Receipt ID: " + receiptId);
                        doc.add(p3);
                        Paragraph p4 = new Paragraph("\n");
                        doc.add(p4);
                        Paragraph p5 = new Paragraph("");
                        doc.add(p5);
                        PdfPTable table = new PdfPTable(7);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    clearCheckOutFields();
                    txt_roomNoSearch.setText("");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_checkOutActionPerformed

    private void btn_searchRoomNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchRoomNoActionPerformed
        // TODO add your handling code here:
        String roomNo = txt_roomNoSearch.getText();
        if(roomNo.isEmpty()){
            JOptionPane.showMessageDialog(null, "Enter Room No to Search", "Empty Field", JOptionPane.WARNING_MESSAGE);
        }
        else{
            try{
                Connection con = connect();
                String sql = "select room_id from room where room_no = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, roomNo);
                ResultSet rs = ps.executeQuery();
                String roomID = null;
                while(rs.next()){
                    roomID = rs.getString("room_id");
                }

                if(roomID == null){
                    JOptionPane.showMessageDialog(null, "There is no room with this room number!", "No Such Room", JOptionPane.WARNING_MESSAGE);
                    clearCheckOutFields();
                }
                else{
                    String sql2 = "select * from r_reserved_data where room_id = ? and check_out_date is null";
                    PreparedStatement ps2 = con.prepareStatement(sql2);
                    ps2.setString(1, roomID);
                    ResultSet rs2 = ps2.executeQuery();
                    table_rReservedData.setModel(DbUtils.resultSetToTableModel(rs2));
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }//GEN-LAST:event_btn_searchRoomNoActionPerformed

    private void btn_addToListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addToListActionPerformed
        // TODO add your handling code here:
        selectedRoomIDs.add(txt_roomIdCheckIn.getText());
    }//GEN-LAST:event_btn_addToListActionPerformed

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
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DashBoard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TabbedPane;
    private javax.swing.JButton btn_addPackage;
    private javax.swing.JButton btn_addRoom;
    private javax.swing.JButton btn_addToList;
    private javax.swing.JButton btn_allocateRoom;
    private javax.swing.JButton btn_cancelBooking;
    private javax.swing.JButton btn_checkOut;
    private javax.swing.JButton btn_chooseImage;
    private javax.swing.JButton btn_chooseNewImage;
    private javax.swing.JButton btn_clear;
    private javax.swing.JButton btn_clearBookingFields;
    private javax.swing.JButton btn_generateID;
    private javax.swing.JButton btn_generatePackageID;
    private javax.swing.JButton btn_removePackage;
    private javax.swing.JButton btn_removeRoom;
    private javax.swing.JButton btn_searchBooking;
    private javax.swing.JButton btn_searchRoomNo;
    private javax.swing.JButton btn_updatePackage;
    private javax.swing.JButton btn_updateRoom;
    private javax.swing.JComboBox<String> cbox_roomBeds;
    private javax.swing.JComboBox<String> cbox_roomDecs;
    private javax.swing.JComboBox<String> cbox_roomNos;
    private javax.swing.JComboBox<String> cbox_roomTypes;
    private javax.swing.ButtonGroup cusGenderRbtnGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lbl_guestCheckin;
    private javax.swing.JLabel lbl_guestCheckiout;
    private javax.swing.JLabel lbl_logout;
    private javax.swing.JLabel lbl_managePackage;
    private javax.swing.JLabel lbl_managePackageBooking;
    private javax.swing.JLabel lbl_manageRoomBooking;
    private javax.swing.JLabel lbl_manageRooms;
    private javax.swing.JLabel lbl_manageStaffacc;
    private javax.swing.JLabel lbl_packageImage;
    private javax.swing.JLabel lbl_roomImage;
    private javax.swing.JPanel panel_adminHome;
    private javax.swing.JPanel panel_guestCheckIn;
    private javax.swing.JPanel panel_guestCheckOut;
    private javax.swing.JPanel panel_manageBookings;
    private javax.swing.JPanel panel_managePackages;
    private javax.swing.JPanel panel_manageRooms;
    private javax.swing.JRadioButton rbtn_female;
    private javax.swing.JRadioButton rbtn_male;
    private javax.swing.JTable roomTable;
    private javax.swing.JSpinner spin_bedCount;
    private javax.swing.JTable table_packages;
    private javax.swing.JTable table_rReservedData;
    private javax.swing.JTextField txt_bookedCusID;
    private javax.swing.JTextField txt_bookedRoomID;
    private javax.swing.JTextField txt_bookingDate;
    private javax.swing.JTextField txt_bookingID;
    private javax.swing.JTextField txt_bookingID3;
    private javax.swing.JTextField txt_bookingStayPeriod;
    private javax.swing.JTextField txt_checkInDate;
    private javax.swing.JTextField txt_checkOutDate;
    private javax.swing.JTextField txt_cusAge;
    private javax.swing.JTextField txt_cusAgecheckOut;
    private javax.swing.JTextField txt_cusCheckinDate;
    private javax.swing.JTextField txt_cusConcheckOut;
    private javax.swing.JTextField txt_cusContact;
    private javax.swing.JTextField txt_cusGendercheckOut;
    private javax.swing.JTextField txt_cusID;
    private javax.swing.JTextField txt_cusIDcheckOut;
    private javax.swing.JTextField txt_cusNRC;
    private javax.swing.JTextField txt_cusNRCcheckOut;
    private javax.swing.JTextField txt_cusName;
    private javax.swing.JTextField txt_cusNamecheckOut;
    private javax.swing.JTextField txt_cusPasscheckOut;
    private javax.swing.JTextField txt_cusPassport;
    private javax.swing.JTextField txt_imagePath;
    private javax.swing.JTextField txt_packageID;
    private javax.swing.JTextField txt_packageImagePath;
    private javax.swing.JTextField txt_packageName;
    private javax.swing.JTextField txt_packagePrice;
    private javax.swing.JTextField txt_packageStatus;
    private javax.swing.JTextField txt_packageType;
    private javax.swing.JTextField txt_paymentStatus;
    private javax.swing.JLabel txt_role;
    private javax.swing.JTextField txt_roomDec;
    private javax.swing.JTextField txt_roomID;
    private javax.swing.JTextField txt_roomIDcheckOut;
    private javax.swing.JTextField txt_roomIdCheckIn;
    private javax.swing.JTextField txt_roomNoSearch;
    private javax.swing.JTextField txt_roomNocheckOut;
    private javax.swing.JTextField txt_roomNum;
    private javax.swing.JTextField txt_roomPrice;
    private javax.swing.JTextField txt_roomPriceCheckIn;
    private javax.swing.JTextField txt_roomPricecheckOut;
    private javax.swing.JTextField txt_roomStatus;
    private javax.swing.JTextField txt_roomType;
    private javax.swing.JTextField txt_roomTypecheckOut;
    private javax.swing.JTextField txt_services;
    private javax.swing.JLabel txt_staffid;
    private javax.swing.JTextField txt_totalCost;
    // End of variables declaration//GEN-END:variables
}
