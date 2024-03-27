/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ProjectPackage;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
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
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
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
            panel_manageRoomsLbl.setVisible(false);
            panel_managePackLbl.setVisible(false);
            panel_manageStaffLbl.setVisible(false);
            panel_viewFinanceLbl.setVisible(false);
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
    
    public void DisplayStaffInfo(){
        try{
            Connection con = connect();
            String sql = "select s.*, sa.password from staff s INNER JOIN system_admin sa ON s.staff_id = sa.staff_id";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            table_staffAcc.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void DisplayFinance(){
        try{
            Connection con = connect();
            String sql = "SELECT receipts.receipt_id, SUM(finance.Amount) AS total_amount " +
                     "FROM receipts " +
                     "INNER JOIN finance ON receipts.receipt_id = finance.receipt_id " +
                     "LEFT JOIN r_reserved_data ON receipts.r_reserved_id = r_reserved_data.r_reserved_id " +
                     "LEFT JOIN p_reserved_data ON receipts.p_reserved_id = p_reserved_data.p_reserved_id " +
                     "GROUP BY receipts.receipt_id";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            table_finance.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void DisplayFinanceByMonth(String selectedMonth){
        int year = Year.now().getValue();
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("January", 1);
        monthMap.put("February", 2);
        monthMap.put("March", 3);
        monthMap.put("April", 4);
        monthMap.put("May", 5);
        monthMap.put("June", 6);
        monthMap.put("July", 7);
        monthMap.put("August", 8);
        monthMap.put("September", 9);
        monthMap.put("October", 10);
        monthMap.put("November", 11);
        monthMap.put("December", 12);

        // Parse the month name to its numerical value
        String[] parts = selectedMonth.split("\\s+");
        int monthValue = monthMap.get(parts[0]);

        // Construct the date string with numerical month value
        String dateString = String.format("%d-%02d-01", year, monthValue);
        LocalDate selectedDate = LocalDate.parse(dateString);
        try{
            Connection con = connect();
            String sql = "SELECT finance.receipt_id, finance.Amount " +
                        "FROM finance " +
                        "INNER JOIN receipts ON finance.receipt_id = receipts.receipt_id " +
                        "LEFT JOIN r_reserved_data ON receipts.r_reserved_id = r_reserved_data.r_reserved_id " +
                        "LEFT JOIN p_reserved_data ON receipts.p_reserved_id = p_reserved_data.p_reserved_id " +
                        "WHERE " +
                        "(" +
                        "(YEAR(r_reserved_data.check_in_date) * 100 + MONTH(r_reserved_data.check_in_date) = YEAR(?) * 100 + MONTH(?))" +
                        "OR " +
                        "(YEAR(r_reserved_data.check_out_date) * 100 + MONTH(r_reserved_data.check_out_date) = YEAR(?) * 100 + MONTH(?))" +
                        "OR " +
                        "(YEAR(p_reserved_data.reserve_date) * 100 + MONTH(p_reserved_data.reserve_date) = YEAR(?) * 100 + MONTH(?))" +
                        ")";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(selectedDate.withDayOfMonth(1)));
            ps.setDate(2, java.sql.Date.valueOf(selectedDate.withDayOfMonth(1)));
            ps.setDate(3, java.sql.Date.valueOf(selectedDate.withDayOfMonth(1)));
            ps.setDate(4, java.sql.Date.valueOf(selectedDate.withDayOfMonth(1)));
            ps.setDate(5, java.sql.Date.valueOf(selectedDate.withDayOfMonth(1)));
            ps.setDate(6, java.sql.Date.valueOf(selectedDate.withDayOfMonth(1)));
            ResultSet rs = ps.executeQuery();
            table_finance.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void getTotalAmount(){
        int count = table_finance.getRowCount();
        float totalAmount = 0;
        if(count == 0){
            lbl_totalAmount.setText("Total Amount: 0");
        }
        else{
            for(int i = 0; i < count; i++){
                float amount = Float.parseFloat(table_finance.getValueAt(i, 1).toString());
                totalAmount += amount;
            }
            lbl_totalAmount.setText("Total Amount: " + totalAmount + " Ks");
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
    
    public int getCurrentMaxPReservedID(){
        try{
            Connection con = connect();
            String sql = "SELECT MAX(p_reserved_id) AS max_id FROM p_reserved_data";
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
    
    public int getCurrentMaxStaffID(){
        try{
            Connection con = connect();
            String sql = "SELECT MAX(staff_id) AS max_id FROM staff";
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
    
    public String generatePReservedID(){
        int currentMaxId = getCurrentMaxPReservedID();
        int counter = currentMaxId + 1;
        return String.format("PR%06d", counter);
    }
    
    public String generateReceiptID(){
        int currentMaxId = getCurrentMaxReceiptID();
        int counter = currentMaxId + 1;
        return String.format("RE%06d", counter);
    }
    
    public String generateStaffID(){
        int currentMaxId = getCurrentMaxStaffID();
        int counter = currentMaxId + 1;
        return String.format("S%06d", counter);
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
    
    public void clearRBookingData(){
        txt_bookingID.setText("");
        txt_bookedRoomID.setText("");
        txt_bookedCusID.setText("");
        txt_bookingDate.setText("");
        txt_bookingStayPeriod.setText("");
    }
    
    public void clearPBookingData(){
        txt_packageBookingID.setText("");
        txt_pBookedCusID.setText("");
        txt_packageIds.setText("");
        txt_pBookedDate.setText("");
        txt_peopleCount.setText("");
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
    
    public void clearStaffFields(){
        txt_staffID.setText(generateStaffID());
        txt_staffName.setText("");
        txt_staffAge.setText("");
        rbtn_staffMale.setSelected(false);
        rbtn_staffFemale.setSelected(false);
        txt_staffNRC.setText("");
        txt_staffCon.setText("");
        txt_staffAddress.setText("");
        rbtn_manager.setSelected(false);
        rbtn_receptionist.setSelected(false);
        txt_staffPassword.setText("");
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
            String sql = "Select distinct room_id, description, room_no, bed from room where room_type = ? and status = 'Available'";
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
    
    public void changeDesignOfClickedLabel(int index){
        switch(index){
            case 1: 
                panel_manageRoomsLbl.setBackground(Color.BLACK);
                lbl_manageRooms.setBackground(Color.BLACK);
                lbl_manageRooms.setForeground(Color.WHITE);

                panel_manageRBookingLbl.setBackground(Color.white);
                lbl_manageRoomBooking.setBackground(Color.white);
                lbl_manageRoomBooking.setForeground(Color.black);

                panel_managePackLbl.setBackground(Color.white);
                lbl_managePackage.setBackground(Color.white);
                lbl_managePackage.setForeground(Color.black);

                panel_managePBookingLbl.setBackground(Color.white);
                lbl_managePackageBooking.setBackground(Color.white);
                lbl_managePackageBooking.setForeground(Color.black);

                panel_GcheckIn.setBackground(Color.white);
                lbl_guestCheckin.setBackground(Color.white);
                lbl_guestCheckin.setForeground(Color.black);

                panel_GcheckOut.setBackground(Color.white);
                lbl_guestCheckout.setBackground(Color.white);
                lbl_guestCheckout.setForeground(Color.black);

                panel_manageStaffLbl.setBackground(Color.white);
                lbl_manageStaffacc.setBackground(Color.white);
                lbl_manageStaffacc.setForeground(Color.black);

                panel_logOutLbl.setBackground(Color.white);
                lbl_logout.setBackground(Color.white);
                lbl_logout.setForeground(Color.black);
                
                panel_viewFinanceLbl.setBackground(Color.white);
                lbl_viewFinance.setBackground(Color.white);
                lbl_viewFinance.setForeground(Color.black);
                
                break;
            case 2:
                panel_manageRoomsLbl.setBackground(Color.white);
                lbl_manageRooms.setBackground(Color.white);
                lbl_manageRooms.setForeground(Color.black);

                panel_manageRBookingLbl.setBackground(Color.white);
                lbl_manageRoomBooking.setBackground(Color.white);
                lbl_manageRoomBooking.setForeground(Color.black);

                panel_managePackLbl.setBackground(Color.black);
                lbl_managePackage.setBackground(Color.black);
                lbl_managePackage.setForeground(Color.white);

                panel_managePBookingLbl.setBackground(Color.white);
                lbl_managePackageBooking.setBackground(Color.white);
                lbl_managePackageBooking.setForeground(Color.black);

                panel_GcheckIn.setBackground(Color.white);
                lbl_guestCheckin.setBackground(Color.white);
                lbl_guestCheckin.setForeground(Color.black);

                panel_GcheckOut.setBackground(Color.white);
                lbl_guestCheckout.setBackground(Color.white);
                lbl_guestCheckout.setForeground(Color.black);

                panel_manageStaffLbl.setBackground(Color.white);
                lbl_manageStaffacc.setBackground(Color.white);
                lbl_manageStaffacc.setForeground(Color.black);

                panel_logOutLbl.setBackground(Color.white);
                lbl_logout.setBackground(Color.white);
                lbl_logout.setForeground(Color.black);
                
                panel_viewFinanceLbl.setBackground(Color.white);
                lbl_viewFinance.setBackground(Color.white);
                lbl_viewFinance.setForeground(Color.black);
                
                break;
            case 3:
                panel_manageRoomsLbl.setBackground(Color.white);
                lbl_manageRooms.setBackground(Color.white);
                lbl_manageRooms.setForeground(Color.black);

                panel_manageRBookingLbl.setBackground(Color.black);
                lbl_manageRoomBooking.setBackground(Color.black);
                lbl_manageRoomBooking.setForeground(Color.white);

                panel_managePackLbl.setBackground(Color.white);
                lbl_managePackage.setBackground(Color.white);
                lbl_managePackage.setForeground(Color.black);

                panel_managePBookingLbl.setBackground(Color.white);
                lbl_managePackageBooking.setBackground(Color.white);
                lbl_managePackageBooking.setForeground(Color.black);

                panel_GcheckIn.setBackground(Color.white);
                lbl_guestCheckin.setBackground(Color.white);
                lbl_guestCheckin.setForeground(Color.black);

                panel_GcheckOut.setBackground(Color.white);
                lbl_guestCheckout.setBackground(Color.white);
                lbl_guestCheckout.setForeground(Color.black);

                panel_manageStaffLbl.setBackground(Color.white);
                lbl_manageStaffacc.setBackground(Color.white);
                lbl_manageStaffacc.setForeground(Color.black);

                panel_logOutLbl.setBackground(Color.white);
                lbl_logout.setBackground(Color.white);
                lbl_logout.setForeground(Color.black);
                
                panel_viewFinanceLbl.setBackground(Color.white);
                lbl_viewFinance.setBackground(Color.white);
                lbl_viewFinance.setForeground(Color.black);
                
                break;
            case 4:
                panel_manageRoomsLbl.setBackground(Color.white);
                lbl_manageRooms.setBackground(Color.white);
                lbl_manageRooms.setForeground(Color.black);

                panel_manageRBookingLbl.setBackground(Color.white);
                lbl_manageRoomBooking.setBackground(Color.white);
                lbl_manageRoomBooking.setForeground(Color.black);

                panel_managePackLbl.setBackground(Color.white);
                lbl_managePackage.setBackground(Color.white);
                lbl_managePackage.setForeground(Color.black);

                panel_managePBookingLbl.setBackground(Color.white);
                lbl_managePackageBooking.setBackground(Color.white);
                lbl_managePackageBooking.setForeground(Color.black);

                panel_GcheckIn.setBackground(Color.black);
                lbl_guestCheckin.setBackground(Color.black);
                lbl_guestCheckin.setForeground(Color.white);

                panel_GcheckOut.setBackground(Color.white);
                lbl_guestCheckout.setBackground(Color.white);
                lbl_guestCheckout.setForeground(Color.black);

                panel_manageStaffLbl.setBackground(Color.white);
                lbl_manageStaffacc.setBackground(Color.white);
                lbl_manageStaffacc.setForeground(Color.black);

                panel_logOutLbl.setBackground(Color.white);
                lbl_logout.setBackground(Color.white);
                lbl_logout.setForeground(Color.black);
                
                panel_viewFinanceLbl.setBackground(Color.white);
                lbl_viewFinance.setBackground(Color.white);
                lbl_viewFinance.setForeground(Color.black);
                
                break;
            case 5:
                panel_manageRoomsLbl.setBackground(Color.white);
                lbl_manageRooms.setBackground(Color.white);
                lbl_manageRooms.setForeground(Color.black);

                panel_manageRBookingLbl.setBackground(Color.white);
                lbl_manageRoomBooking.setBackground(Color.white);
                lbl_manageRoomBooking.setForeground(Color.black);

                panel_managePackLbl.setBackground(Color.white);
                lbl_managePackage.setBackground(Color.white);
                lbl_managePackage.setForeground(Color.black);

                panel_managePBookingLbl.setBackground(Color.white);
                lbl_managePackageBooking.setBackground(Color.white);
                lbl_managePackageBooking.setForeground(Color.black);

                panel_GcheckIn.setBackground(Color.white);
                lbl_guestCheckin.setBackground(Color.white);
                lbl_guestCheckin.setForeground(Color.black);

                panel_GcheckOut.setBackground(Color.black);
                lbl_guestCheckout.setBackground(Color.black);
                lbl_guestCheckout.setForeground(Color.white);

                panel_manageStaffLbl.setBackground(Color.white);
                lbl_manageStaffacc.setBackground(Color.white);
                lbl_manageStaffacc.setForeground(Color.black);

                panel_logOutLbl.setBackground(Color.white);
                lbl_logout.setBackground(Color.white);
                lbl_logout.setForeground(Color.black);
                
                panel_viewFinanceLbl.setBackground(Color.white);
                lbl_viewFinance.setBackground(Color.white);
                lbl_viewFinance.setForeground(Color.black);
                
                break;
            case 6:
                panel_manageRoomsLbl.setBackground(Color.white);
                lbl_manageRooms.setBackground(Color.white);
                lbl_manageRooms.setForeground(Color.black);

                panel_manageRBookingLbl.setBackground(Color.white);
                lbl_manageRoomBooking.setBackground(Color.white);
                lbl_manageRoomBooking.setForeground(Color.black);

                panel_managePackLbl.setBackground(Color.white);
                lbl_managePackage.setBackground(Color.white);
                lbl_managePackage.setForeground(Color.black);

                panel_managePBookingLbl.setBackground(Color.white);
                lbl_managePackageBooking.setBackground(Color.white);
                lbl_managePackageBooking.setForeground(Color.black);

                panel_GcheckIn.setBackground(Color.white);
                lbl_guestCheckin.setBackground(Color.white);
                lbl_guestCheckin.setForeground(Color.black);

                panel_GcheckOut.setBackground(Color.white);
                lbl_guestCheckout.setBackground(Color.white);
                lbl_guestCheckout.setForeground(Color.black);

                panel_manageStaffLbl.setBackground(Color.black);
                lbl_manageStaffacc.setBackground(Color.black);
                lbl_manageStaffacc.setForeground(Color.white);

                panel_logOutLbl.setBackground(Color.white);
                lbl_logout.setBackground(Color.white);
                lbl_logout.setForeground(Color.black);
                
                panel_viewFinanceLbl.setBackground(Color.white);
                lbl_viewFinance.setBackground(Color.white);
                lbl_viewFinance.setForeground(Color.black);
                
                break;
            case 7:
                panel_manageRoomsLbl.setBackground(Color.white);
                lbl_manageRooms.setBackground(Color.white);
                lbl_manageRooms.setForeground(Color.black);

                panel_manageRBookingLbl.setBackground(Color.white);
                lbl_manageRoomBooking.setBackground(Color.white);
                lbl_manageRoomBooking.setForeground(Color.black);

                panel_managePackLbl.setBackground(Color.white);
                lbl_managePackage.setBackground(Color.white);
                lbl_managePackage.setForeground(Color.black);

                panel_managePBookingLbl.setBackground(Color.black);
                lbl_managePackageBooking.setBackground(Color.black);
                lbl_managePackageBooking.setForeground(Color.white);

                panel_GcheckIn.setBackground(Color.white);
                lbl_guestCheckin.setBackground(Color.white);
                lbl_guestCheckin.setForeground(Color.black);

                panel_GcheckOut.setBackground(Color.white);
                lbl_guestCheckout.setBackground(Color.white);
                lbl_guestCheckout.setForeground(Color.black);

                panel_manageStaffLbl.setBackground(Color.white);
                lbl_manageStaffacc.setBackground(Color.white);
                lbl_manageStaffacc.setForeground(Color.black);

                panel_logOutLbl.setBackground(Color.white);
                lbl_logout.setBackground(Color.white);
                lbl_logout.setForeground(Color.black);
                
                panel_viewFinanceLbl.setBackground(Color.white);
                lbl_viewFinance.setBackground(Color.white);
                lbl_viewFinance.setForeground(Color.black);
                
                break;
            case 8:
                panel_manageRoomsLbl.setBackground(Color.white);
                lbl_manageRooms.setBackground(Color.white);
                lbl_manageRooms.setForeground(Color.black);

                panel_manageRBookingLbl.setBackground(Color.white);
                lbl_manageRoomBooking.setBackground(Color.white);
                lbl_manageRoomBooking.setForeground(Color.black);

                panel_managePackLbl.setBackground(Color.white);
                lbl_managePackage.setBackground(Color.white);
                lbl_managePackage.setForeground(Color.black);

                panel_managePBookingLbl.setBackground(Color.white);
                lbl_managePackageBooking.setBackground(Color.white);
                lbl_managePackageBooking.setForeground(Color.black);

                panel_GcheckIn.setBackground(Color.white);
                lbl_guestCheckin.setBackground(Color.white);
                lbl_guestCheckin.setForeground(Color.black);

                panel_GcheckOut.setBackground(Color.white);
                lbl_guestCheckout.setBackground(Color.white);
                lbl_guestCheckout.setForeground(Color.black);

                panel_manageStaffLbl.setBackground(Color.white);
                lbl_manageStaffacc.setBackground(Color.white);
                lbl_manageStaffacc.setForeground(Color.black);

                panel_logOutLbl.setBackground(Color.white);
                lbl_logout.setBackground(Color.white);
                lbl_logout.setForeground(Color.black);
                
                panel_viewFinanceLbl.setBackground(Color.black);
                lbl_viewFinance.setBackground(Color.black);
                lbl_viewFinance.setForeground(Color.white);
                
                break;
            case 9:
                panel_manageRoomsLbl.setBackground(Color.white);
                lbl_manageRooms.setBackground(Color.white);
                lbl_manageRooms.setForeground(Color.black);

                panel_manageRBookingLbl.setBackground(Color.white);
                lbl_manageRoomBooking.setBackground(Color.white);
                lbl_manageRoomBooking.setForeground(Color.black);

                panel_managePackLbl.setBackground(Color.white);
                lbl_managePackage.setBackground(Color.white);
                lbl_managePackage.setForeground(Color.black);

                panel_managePBookingLbl.setBackground(Color.white);
                lbl_managePackageBooking.setBackground(Color.white);
                lbl_managePackageBooking.setForeground(Color.black);

                panel_GcheckIn.setBackground(Color.white);
                lbl_guestCheckin.setBackground(Color.white);
                lbl_guestCheckin.setForeground(Color.black);

                panel_GcheckOut.setBackground(Color.white);
                lbl_guestCheckout.setBackground(Color.white);
                lbl_guestCheckout.setForeground(Color.black);

                panel_manageStaffLbl.setBackground(Color.white);
                lbl_manageStaffacc.setBackground(Color.white);
                lbl_manageStaffacc.setForeground(Color.black);

                panel_logOutLbl.setBackground(Color.black);
                lbl_logout.setBackground(Color.black);
                lbl_logout.setForeground(Color.white);
                
                panel_viewFinanceLbl.setBackground(Color.white);
                lbl_viewFinance.setBackground(Color.white);
                lbl_viewFinance.setForeground(Color.black);
                
                break;
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
        staffGenderRbtnGroup = new javax.swing.ButtonGroup();
        staffRoleRbtnGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_role = new javax.swing.JLabel();
        txt_staffid = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        panel_manageRoomsLbl = new javax.swing.JPanel();
        lbl_manageRooms = new javax.swing.JLabel();
        panel_manageRBookingLbl = new javax.swing.JPanel();
        lbl_manageRoomBooking = new javax.swing.JLabel();
        panel_GcheckIn = new javax.swing.JPanel();
        lbl_guestCheckin = new javax.swing.JLabel();
        panel_GcheckOut = new javax.swing.JPanel();
        lbl_guestCheckout = new javax.swing.JLabel();
        panel_manageStaffLbl = new javax.swing.JPanel();
        lbl_manageStaffacc = new javax.swing.JLabel();
        panel_logOutLbl = new javax.swing.JPanel();
        lbl_logout = new javax.swing.JLabel();
        panel_managePackLbl = new javax.swing.JPanel();
        lbl_managePackage = new javax.swing.JLabel();
        panel_managePBookingLbl = new javax.swing.JPanel();
        lbl_managePackageBooking = new javax.swing.JLabel();
        panel_viewFinanceLbl = new javax.swing.JPanel();
        lbl_viewFinance = new javax.swing.JLabel();
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
        btn_markAsReserve = new javax.swing.JButton();
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
        panel_manageStaffacc = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        table_staffAcc = new javax.swing.JTable();
        jLabel55 = new javax.swing.JLabel();
        txt_staffID = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        txt_staffName = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        txt_staffAge = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        txt_staffNRC = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        txt_staffCon = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        txt_staffAddress = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        txt_staffPassword = new javax.swing.JTextField();
        btn_addStaff = new javax.swing.JButton();
        btn_updateStaff = new javax.swing.JButton();
        btn_removeStaff = new javax.swing.JButton();
        rbtn_staffMale = new javax.swing.JRadioButton();
        rbtn_staffFemale = new javax.swing.JRadioButton();
        rbtn_manager = new javax.swing.JRadioButton();
        rbtn_receptionist = new javax.swing.JRadioButton();
        btn_generateStaffID = new javax.swing.JButton();
        panel_managePBooking = new javax.swing.JPanel();
        jLabel64 = new javax.swing.JLabel();
        txt_packageBookingID = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        txt_packageIds = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        txt_pBookedCusID = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        txt_pBookedDate = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        txt_peopleCount = new javax.swing.JTextField();
        btn_searchPBooking = new javax.swing.JButton();
        btn_cancelPBooking = new javax.swing.JButton();
        btn_clearPBookingFields = new javax.swing.JButton();
        btn_markAsPReserve = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_finance = new javax.swing.JTable();
        btn_viewAllFinance = new javax.swing.JButton();
        cbox_months = new javax.swing.JComboBox<>();
        jLabel69 = new javax.swing.JLabel();
        lbl_totalAmount = new javax.swing.JLabel();

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setText("Booking ID:");

        txt_bookingID3.setBackground(new java.awt.Color(255, 255, 255));
        txt_bookingID3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_bookingID3.setForeground(new java.awt.Color(0, 0, 0));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 204, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("GOLDEN OASIS HOTEL");

        txt_role.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        txt_role.setForeground(new java.awt.Color(255, 255, 255));
        txt_role.setText("Staff role");

        txt_staffid.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        txt_staffid.setForeground(new java.awt.Color(255, 255, 255));
        txt_staffid.setText("Staff id");

        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));

        panel_manageRoomsLbl.setBackground(new java.awt.Color(255, 255, 255));
        panel_manageRoomsLbl.setForeground(new java.awt.Color(0, 0, 0));

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

        javax.swing.GroupLayout panel_manageRoomsLblLayout = new javax.swing.GroupLayout(panel_manageRoomsLbl);
        panel_manageRoomsLbl.setLayout(panel_manageRoomsLblLayout);
        panel_manageRoomsLblLayout.setHorizontalGroup(
            panel_manageRoomsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manageRoomsLblLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(lbl_manageRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_manageRoomsLblLayout.setVerticalGroup(
            panel_manageRoomsLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manageRoomsLblLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_manageRooms, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_manageRBookingLbl.setBackground(new java.awt.Color(255, 255, 255));
        panel_manageRBookingLbl.setForeground(new java.awt.Color(0, 0, 0));

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

        javax.swing.GroupLayout panel_manageRBookingLblLayout = new javax.swing.GroupLayout(panel_manageRBookingLbl);
        panel_manageRBookingLbl.setLayout(panel_manageRBookingLblLayout);
        panel_manageRBookingLblLayout.setHorizontalGroup(
            panel_manageRBookingLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_manageRBookingLblLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_manageRoomBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        panel_manageRBookingLblLayout.setVerticalGroup(
            panel_manageRBookingLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_manageRBookingLblLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_manageRoomBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panel_GcheckIn.setBackground(new java.awt.Color(255, 255, 255));
        panel_GcheckIn.setForeground(new java.awt.Color(0, 0, 0));

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

        javax.swing.GroupLayout panel_GcheckInLayout = new javax.swing.GroupLayout(panel_GcheckIn);
        panel_GcheckIn.setLayout(panel_GcheckInLayout);
        panel_GcheckInLayout.setHorizontalGroup(
            panel_GcheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_GcheckInLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(lbl_guestCheckin, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );
        panel_GcheckInLayout.setVerticalGroup(
            panel_GcheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_GcheckInLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_guestCheckin, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        panel_GcheckOut.setBackground(new java.awt.Color(255, 255, 255));
        panel_GcheckOut.setForeground(new java.awt.Color(0, 0, 0));

        lbl_guestCheckout.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_guestCheckout.setForeground(new java.awt.Color(0, 0, 0));
        lbl_guestCheckout.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_guestCheckout.setText("Guest Checking Out");
        lbl_guestCheckout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_guestCheckout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_guestCheckoutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panel_GcheckOutLayout = new javax.swing.GroupLayout(panel_GcheckOut);
        panel_GcheckOut.setLayout(panel_GcheckOutLayout);
        panel_GcheckOutLayout.setHorizontalGroup(
            panel_GcheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_GcheckOutLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_guestCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );
        panel_GcheckOutLayout.setVerticalGroup(
            panel_GcheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_GcheckOutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_guestCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_manageStaffLbl.setBackground(new java.awt.Color(255, 255, 255));
        panel_manageStaffLbl.setForeground(new java.awt.Color(0, 0, 0));

        lbl_manageStaffacc.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_manageStaffacc.setForeground(new java.awt.Color(0, 0, 0));
        lbl_manageStaffacc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_manageStaffacc.setText("Manage Staff Accounts");
        lbl_manageStaffacc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_manageStaffacc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_manageStaffaccMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panel_manageStaffLblLayout = new javax.swing.GroupLayout(panel_manageStaffLbl);
        panel_manageStaffLbl.setLayout(panel_manageStaffLblLayout);
        panel_manageStaffLblLayout.setHorizontalGroup(
            panel_manageStaffLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manageStaffLblLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(lbl_manageStaffacc, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_manageStaffLblLayout.setVerticalGroup(
            panel_manageStaffLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manageStaffLblLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_manageStaffacc, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_logOutLbl.setBackground(new java.awt.Color(255, 255, 255));
        panel_logOutLbl.setForeground(new java.awt.Color(0, 0, 0));

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

        javax.swing.GroupLayout panel_logOutLblLayout = new javax.swing.GroupLayout(panel_logOutLbl);
        panel_logOutLbl.setLayout(panel_logOutLblLayout);
        panel_logOutLblLayout.setHorizontalGroup(
            panel_logOutLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_logOutLblLayout.createSequentialGroup()
                .addGap(108, 108, 108)
                .addComponent(lbl_logout, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(117, Short.MAX_VALUE))
        );
        panel_logOutLblLayout.setVerticalGroup(
            panel_logOutLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_logOutLblLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_logout, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_managePackLbl.setBackground(new java.awt.Color(255, 255, 255));
        panel_managePackLbl.setForeground(new java.awt.Color(0, 0, 0));

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

        javax.swing.GroupLayout panel_managePackLblLayout = new javax.swing.GroupLayout(panel_managePackLbl);
        panel_managePackLbl.setLayout(panel_managePackLblLayout);
        panel_managePackLblLayout.setHorizontalGroup(
            panel_managePackLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_managePackLblLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(lbl_managePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_managePackLblLayout.setVerticalGroup(
            panel_managePackLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_managePackLblLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_managePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_managePBookingLbl.setBackground(new java.awt.Color(255, 255, 255));
        panel_managePBookingLbl.setForeground(new java.awt.Color(0, 0, 0));

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

        javax.swing.GroupLayout panel_managePBookingLblLayout = new javax.swing.GroupLayout(panel_managePBookingLbl);
        panel_managePBookingLbl.setLayout(panel_managePBookingLblLayout);
        panel_managePBookingLblLayout.setHorizontalGroup(
            panel_managePBookingLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_managePBookingLblLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(lbl_managePackageBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_managePBookingLblLayout.setVerticalGroup(
            panel_managePBookingLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_managePBookingLblLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_managePackageBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_viewFinanceLbl.setBackground(new java.awt.Color(255, 255, 255));
        panel_viewFinanceLbl.setForeground(new java.awt.Color(0, 0, 0));

        lbl_viewFinance.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_viewFinance.setForeground(new java.awt.Color(0, 0, 0));
        lbl_viewFinance.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_viewFinance.setText("View Finance");
        lbl_viewFinance.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_viewFinance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_viewFinanceMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panel_viewFinanceLblLayout = new javax.swing.GroupLayout(panel_viewFinanceLbl);
        panel_viewFinanceLbl.setLayout(panel_viewFinanceLblLayout);
        panel_viewFinanceLblLayout.setHorizontalGroup(
            panel_viewFinanceLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_viewFinanceLblLayout.createSequentialGroup()
                .addGap(108, 108, 108)
                .addComponent(lbl_viewFinance, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_viewFinanceLblLayout.setVerticalGroup(
            panel_viewFinanceLblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_viewFinanceLblLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_viewFinance, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_manageRoomsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_manageRBookingLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_GcheckIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_GcheckOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_manageStaffLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_logOutLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_managePackLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_managePBookingLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_staffid, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_role, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 60, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(panel_viewFinanceLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(panel_manageRoomsLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_manageRBookingLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_managePackLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(panel_managePBookingLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_GcheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_GcheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_manageStaffLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_viewFinanceLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_logOutLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 880));

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
                .addContainerGap(280, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(190, 190, 190))
        );

        TabbedPane.addTab("tab1", panel_adminHome);

        panel_manageRooms.setBackground(new java.awt.Color(255, 255, 255));
        panel_manageRooms.setForeground(new java.awt.Color(0, 0, 0));

        roomTable.setBackground(new java.awt.Color(255, 255, 255));
        roomTable.setForeground(new java.awt.Color(0, 0, 0));
        roomTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        roomTable.setSelectionBackground(new java.awt.Color(5, 124, 124));
        roomTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
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

        btn_addRoom.setBackground(new java.awt.Color(5, 124, 124));
        btn_addRoom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_addRoom.setForeground(new java.awt.Color(255, 255, 255));
        btn_addRoom.setText("Add Room");
        btn_addRoom.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_addRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addRoomActionPerformed(evt);
            }
        });

        btn_updateRoom.setBackground(new java.awt.Color(5, 124, 124));
        btn_updateRoom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_updateRoom.setForeground(new java.awt.Color(255, 255, 255));
        btn_updateRoom.setText("Update Room");
        btn_updateRoom.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_updateRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateRoomActionPerformed(evt);
            }
        });

        btn_removeRoom.setBackground(new java.awt.Color(5, 124, 124));
        btn_removeRoom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_removeRoom.setForeground(new java.awt.Color(255, 255, 255));
        btn_removeRoom.setText("Remove Room");
        btn_removeRoom.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_removeRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removeRoomActionPerformed(evt);
            }
        });

        btn_chooseImage.setBackground(new java.awt.Color(5, 124, 124));
        btn_chooseImage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_chooseImage.setForeground(new java.awt.Color(255, 255, 255));
        btn_chooseImage.setText("Choose Image");
        btn_chooseImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_chooseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_chooseImageActionPerformed(evt);
            }
        });

        btn_generateID.setBackground(new java.awt.Color(5, 124, 124));
        btn_generateID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_generateID.setForeground(new java.awt.Color(255, 255, 255));
        btn_generateID.setText("Generate ID to add new room");
        btn_generateID.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
                .addContainerGap(62, Short.MAX_VALUE))
        );
        panel_manageRoomsLayout.setVerticalGroup(
            panel_manageRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_manageRoomsLayout.createSequentialGroup()
                .addContainerGap(102, Short.MAX_VALUE)
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

        table_packages.setBackground(new java.awt.Color(255, 255, 255));
        table_packages.setForeground(new java.awt.Color(0, 0, 0));
        table_packages.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table_packages.setSelectionBackground(new java.awt.Color(5, 124, 124));
        table_packages.setSelectionForeground(new java.awt.Color(255, 255, 255));
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

        btn_chooseNewImage.setBackground(new java.awt.Color(5, 124, 124));
        btn_chooseNewImage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_chooseNewImage.setForeground(new java.awt.Color(255, 255, 255));
        btn_chooseNewImage.setText("Choose Image");
        btn_chooseNewImage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_chooseNewImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_chooseNewImageActionPerformed(evt);
            }
        });

        btn_addPackage.setBackground(new java.awt.Color(5, 124, 124));
        btn_addPackage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_addPackage.setForeground(new java.awt.Color(255, 255, 255));
        btn_addPackage.setText("Add Package");
        btn_addPackage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_addPackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addPackageActionPerformed(evt);
            }
        });

        btn_removePackage.setBackground(new java.awt.Color(5, 124, 124));
        btn_removePackage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_removePackage.setForeground(new java.awt.Color(255, 255, 255));
        btn_removePackage.setText("Remove Package");
        btn_removePackage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_removePackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removePackageActionPerformed(evt);
            }
        });

        btn_updatePackage.setBackground(new java.awt.Color(5, 124, 124));
        btn_updatePackage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_updatePackage.setForeground(new java.awt.Color(255, 255, 255));
        btn_updatePackage.setText("Update Package");
        btn_updatePackage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_updatePackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updatePackageActionPerformed(evt);
            }
        });

        btn_generatePackageID.setBackground(new java.awt.Color(5, 124, 124));
        btn_generatePackageID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_generatePackageID.setForeground(new java.awt.Color(255, 255, 255));
        btn_generatePackageID.setText("Generate ID to Add New Package");
        btn_generatePackageID.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_managePackagesLayout.createSequentialGroup()
                .addContainerGap(96, Short.MAX_VALUE)
                .addGroup(panel_managePackagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panel_managePackagesLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
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
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_packageImage, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(panel_managePackagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_addPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_removePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_updatePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_generatePackageID, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
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

        btn_searchBooking.setBackground(new java.awt.Color(5, 124, 124));
        btn_searchBooking.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_searchBooking.setForeground(new java.awt.Color(255, 255, 255));
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

        btn_cancelBooking.setBackground(new java.awt.Color(5, 124, 124));
        btn_cancelBooking.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_cancelBooking.setForeground(new java.awt.Color(255, 255, 255));
        btn_cancelBooking.setText("Cancel Booking");
        btn_cancelBooking.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_cancelBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelBookingActionPerformed(evt);
            }
        });

        btn_clearBookingFields.setBackground(new java.awt.Color(5, 124, 124));
        btn_clearBookingFields.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_clearBookingFields.setForeground(new java.awt.Color(255, 255, 255));
        btn_clearBookingFields.setText("Clear Fields");
        btn_clearBookingFields.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_clearBookingFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearBookingFieldsActionPerformed(evt);
            }
        });

        btn_markAsReserve.setBackground(new java.awt.Color(5, 124, 124));
        btn_markAsReserve.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_markAsReserve.setForeground(new java.awt.Color(255, 255, 255));
        btn_markAsReserve.setText("Mark As Reserved");
        btn_markAsReserve.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_markAsReserve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_markAsReserveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_manageBookingsLayout = new javax.swing.GroupLayout(panel_manageBookings);
        panel_manageBookings.setLayout(panel_manageBookingsLayout);
        panel_manageBookingsLayout.setHorizontalGroup(
            panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_manageBookingsLayout.createSequentialGroup()
                .addContainerGap(166, Short.MAX_VALUE)
                .addGroup(panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bookedCusID, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_manageBookingsLayout.createSequentialGroup()
                        .addComponent(txt_bookingID, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_searchBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bookedRoomID, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bookingStayPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_bookingDate, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_cancelBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_clearBookingFields, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_markAsReserve, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(132, 132, 132))
        );
        panel_manageBookingsLayout.setVerticalGroup(
            panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_manageBookingsLayout.createSequentialGroup()
                .addContainerGap(230, Short.MAX_VALUE)
                .addGroup(panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_manageBookingsLayout.createSequentialGroup()
                        .addComponent(btn_cancelBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_markAsReserve, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_clearBookingFields, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_manageBookingsLayout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel_manageBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_searchBooking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_bookingID, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_bookedRoomID, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
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
                        .addComponent(txt_bookingStayPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(168, 168, 168))
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

        btn_allocateRoom.setBackground(new java.awt.Color(5, 124, 124));
        btn_allocateRoom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_allocateRoom.setForeground(new java.awt.Color(255, 255, 255));
        btn_allocateRoom.setText("Allocate Room");
        btn_allocateRoom.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_allocateRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_allocateRoomActionPerformed(evt);
            }
        });

        btn_clear.setBackground(new java.awt.Color(5, 124, 124));
        btn_clear.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_clear.setForeground(new java.awt.Color(255, 255, 255));
        btn_clear.setText("Clear Fields");
        btn_clear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearActionPerformed(evt);
            }
        });

        btn_addToList.setBackground(new java.awt.Color(5, 124, 124));
        btn_addToList.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_addToList.setForeground(new java.awt.Color(255, 255, 255));
        btn_addToList.setText("Add to List");
        btn_addToList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(btn_addToList, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                        .addComponent(cbox_roomDecs, 0, 253, Short.MAX_VALUE)))
                .addGap(177, 177, 177))
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
                        .addComponent(cbox_roomBeds, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cusNRC, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbox_roomNos, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)))
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
                .addGroup(panel_guestCheckInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(txt_cusContact, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cusCheckinDate, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_guestCheckInLayout.createSequentialGroup()
                        .addComponent(btn_addToList, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(btn_allocateRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(104, 104, 104))
        );

        TabbedPane.addTab("tab5", panel_guestCheckIn);

        panel_guestCheckOut.setBackground(new java.awt.Color(255, 255, 255));
        panel_guestCheckOut.setForeground(new java.awt.Color(0, 0, 0));

        table_rReservedData.setBackground(new java.awt.Color(255, 255, 255));
        table_rReservedData.setForeground(new java.awt.Color(0, 0, 0));
        table_rReservedData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table_rReservedData.setSelectionBackground(new java.awt.Color(5, 124, 124));
        table_rReservedData.setSelectionForeground(new java.awt.Color(255, 255, 255));
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

        btn_checkOut.setBackground(new java.awt.Color(5, 124, 124));
        btn_checkOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_checkOut.setForeground(new java.awt.Color(255, 255, 255));
        btn_checkOut.setText("Check Out");
        btn_checkOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        btn_searchRoomNo.setBackground(new java.awt.Color(5, 124, 124));
        btn_searchRoomNo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_searchRoomNo.setForeground(new java.awt.Color(255, 255, 255));
        btn_searchRoomNo.setText("Search");
        btn_searchRoomNo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
                .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(panel_guestCheckOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addGap(18, 18, 18)
                                .addComponent(txt_roomNoSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_searchRoomNo, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 992, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel_guestCheckOutLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
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
                        .addGap(129, 129, 129)
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
                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(22, Short.MAX_VALUE))
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
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addGap(18, 18, 18)
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
                .addGap(30, 30, 30))
        );

        TabbedPane.addTab("tab6", panel_guestCheckOut);

        panel_manageStaffacc.setBackground(new java.awt.Color(255, 255, 255));
        panel_manageStaffacc.setForeground(new java.awt.Color(0, 0, 0));

        table_staffAcc.setBackground(new java.awt.Color(255, 255, 255));
        table_staffAcc.setForeground(new java.awt.Color(0, 0, 0));
        table_staffAcc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table_staffAcc.setSelectionBackground(new java.awt.Color(5, 124, 124));
        table_staffAcc.setSelectionForeground(new java.awt.Color(255, 255, 255));
        table_staffAcc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_staffAccMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(table_staffAcc);

        jLabel55.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(0, 0, 0));
        jLabel55.setText("Staff ID:");

        txt_staffID.setBackground(new java.awt.Color(255, 255, 255));
        txt_staffID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_staffID.setForeground(new java.awt.Color(0, 0, 0));
        txt_staffID.setEnabled(false);

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(0, 0, 0));
        jLabel56.setText("Staff Name:");

        txt_staffName.setBackground(new java.awt.Color(255, 255, 255));
        txt_staffName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_staffName.setForeground(new java.awt.Color(0, 0, 0));

        jLabel57.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(0, 0, 0));
        jLabel57.setText("Staff Age:");

        txt_staffAge.setBackground(new java.awt.Color(255, 255, 255));
        txt_staffAge.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_staffAge.setForeground(new java.awt.Color(0, 0, 0));

        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(0, 0, 0));
        jLabel58.setText("Staff Gender:");

        jLabel59.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(0, 0, 0));
        jLabel59.setText("Staff NRC:");

        txt_staffNRC.setBackground(new java.awt.Color(255, 255, 255));
        txt_staffNRC.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_staffNRC.setForeground(new java.awt.Color(0, 0, 0));

        jLabel60.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(0, 0, 0));
        jLabel60.setText("Staff Contact:");

        txt_staffCon.setBackground(new java.awt.Color(255, 255, 255));
        txt_staffCon.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_staffCon.setForeground(new java.awt.Color(0, 0, 0));

        jLabel61.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(0, 0, 0));
        jLabel61.setText("Staff Address:");

        txt_staffAddress.setBackground(new java.awt.Color(255, 255, 255));
        txt_staffAddress.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_staffAddress.setForeground(new java.awt.Color(0, 0, 0));

        jLabel62.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(0, 0, 0));
        jLabel62.setText("Staff Role:");

        jLabel63.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(0, 0, 0));
        jLabel63.setText("Password:");

        txt_staffPassword.setBackground(new java.awt.Color(255, 255, 255));
        txt_staffPassword.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_staffPassword.setForeground(new java.awt.Color(0, 0, 0));

        btn_addStaff.setBackground(new java.awt.Color(5, 124, 124));
        btn_addStaff.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_addStaff.setForeground(new java.awt.Color(255, 255, 255));
        btn_addStaff.setText("Add New Staff");
        btn_addStaff.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_addStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addStaffActionPerformed(evt);
            }
        });

        btn_updateStaff.setBackground(new java.awt.Color(5, 124, 124));
        btn_updateStaff.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_updateStaff.setForeground(new java.awt.Color(255, 255, 255));
        btn_updateStaff.setText("Update Staff");
        btn_updateStaff.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btn_removeStaff.setBackground(new java.awt.Color(5, 124, 124));
        btn_removeStaff.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_removeStaff.setForeground(new java.awt.Color(255, 255, 255));
        btn_removeStaff.setText("Remove Staff");
        btn_removeStaff.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        staffGenderRbtnGroup.add(rbtn_staffMale);
        rbtn_staffMale.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rbtn_staffMale.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_staffMale.setText("Male");

        staffGenderRbtnGroup.add(rbtn_staffFemale);
        rbtn_staffFemale.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rbtn_staffFemale.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_staffFemale.setText("Female");

        staffRoleRbtnGroup.add(rbtn_manager);
        rbtn_manager.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rbtn_manager.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_manager.setText("Manager");

        staffRoleRbtnGroup.add(rbtn_receptionist);
        rbtn_receptionist.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rbtn_receptionist.setForeground(new java.awt.Color(0, 0, 0));
        rbtn_receptionist.setText("Receptionist");

        btn_generateStaffID.setBackground(new java.awt.Color(5, 124, 124));
        btn_generateStaffID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_generateStaffID.setForeground(new java.awt.Color(255, 255, 255));
        btn_generateStaffID.setText("Generate Staff ID");
        btn_generateStaffID.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_generateStaffID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_generateStaffIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_manageStaffaccLayout = new javax.swing.GroupLayout(panel_manageStaffacc);
        panel_manageStaffacc.setLayout(panel_manageStaffaccLayout);
        panel_manageStaffaccLayout.setHorizontalGroup(
            panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 951, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_staffID, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_staffName, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_staffAge, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(54, 54, 54)
                        .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_staffNRC, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_staffCon, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                                .addComponent(rbtn_staffMale, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(rbtn_staffFemale, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)))
                        .addGap(61, 61, 61)
                        .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_staffPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_staffAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                                .addComponent(rbtn_manager, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(rbtn_receptionist))))
                    .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                        .addGap(169, 169, 169)
                        .addComponent(btn_generateStaffID)
                        .addGap(39, 39, 39)
                        .addComponent(btn_addStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(btn_updateStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(btn_removeStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        panel_manageStaffaccLayout.setVerticalGroup(
            panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                            .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_staffID, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_manageStaffaccLayout.createSequentialGroup()
                            .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_staffAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                        .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_staffMale)
                            .addComponent(rbtn_staffFemale))
                        .addGap(5, 5, 5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                        .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_staffName, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                        .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rbtn_manager)
                                .addComponent(rbtn_receptionist))
                            .addComponent(txt_staffNRC, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_staffAge, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                        .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_staffCon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_manageStaffaccLayout.createSequentialGroup()
                        .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_staffPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(64, 64, 64)
                .addGroup(panel_manageStaffaccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_addStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_updateStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_removeStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_generateStaffID, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(139, Short.MAX_VALUE))
        );

        TabbedPane.addTab("tab7", panel_manageStaffacc);

        panel_managePBooking.setBackground(new java.awt.Color(255, 255, 255));
        panel_managePBooking.setForeground(new java.awt.Color(0, 0, 0));

        jLabel64.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(0, 0, 0));
        jLabel64.setText("Package Booking ID:");

        txt_packageBookingID.setBackground(new java.awt.Color(255, 255, 255));
        txt_packageBookingID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_packageBookingID.setForeground(new java.awt.Color(0, 0, 0));

        jLabel65.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(0, 0, 0));
        jLabel65.setText("Package IDs:");

        txt_packageIds.setBackground(new java.awt.Color(255, 255, 255));
        txt_packageIds.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_packageIds.setForeground(new java.awt.Color(0, 0, 0));

        jLabel66.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(0, 0, 0));
        jLabel66.setText("Customer ID:");

        txt_pBookedCusID.setBackground(new java.awt.Color(255, 255, 255));
        txt_pBookedCusID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_pBookedCusID.setForeground(new java.awt.Color(0, 0, 0));

        jLabel67.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(0, 0, 0));
        jLabel67.setText("Booking Date:");

        txt_pBookedDate.setBackground(new java.awt.Color(255, 255, 255));
        txt_pBookedDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_pBookedDate.setForeground(new java.awt.Color(0, 0, 0));

        jLabel68.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(0, 0, 0));
        jLabel68.setText("People Count:");

        txt_peopleCount.setBackground(new java.awt.Color(255, 255, 255));
        txt_peopleCount.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_peopleCount.setForeground(new java.awt.Color(0, 0, 0));

        btn_searchPBooking.setBackground(new java.awt.Color(5, 124, 124));
        btn_searchPBooking.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_searchPBooking.setForeground(new java.awt.Color(255, 255, 255));
        btn_searchPBooking.setText("Search");
        btn_searchPBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchPBookingActionPerformed(evt);
            }
        });

        btn_cancelPBooking.setBackground(new java.awt.Color(5, 124, 124));
        btn_cancelPBooking.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_cancelPBooking.setForeground(new java.awt.Color(255, 255, 255));
        btn_cancelPBooking.setText("Cancel Booking");
        btn_cancelPBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelPBookingActionPerformed(evt);
            }
        });

        btn_clearPBookingFields.setBackground(new java.awt.Color(5, 124, 124));
        btn_clearPBookingFields.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_clearPBookingFields.setForeground(new java.awt.Color(255, 255, 255));
        btn_clearPBookingFields.setText("Clear Fields");
        btn_clearPBookingFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearPBookingFieldsActionPerformed(evt);
            }
        });

        btn_markAsPReserve.setBackground(new java.awt.Color(5, 124, 124));
        btn_markAsPReserve.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_markAsPReserve.setForeground(new java.awt.Color(255, 255, 255));
        btn_markAsPReserve.setText("Mark As Reserve");
        btn_markAsPReserve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_markAsPReserveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_managePBookingLayout = new javax.swing.GroupLayout(panel_managePBooking);
        panel_managePBooking.setLayout(panel_managePBookingLayout);
        panel_managePBookingLayout.setHorizontalGroup(
            panel_managePBookingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_managePBookingLayout.createSequentialGroup()
                .addGap(167, 167, 167)
                .addGroup(panel_managePBookingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pBookedCusID, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_packageIds, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_managePBookingLayout.createSequentialGroup()
                        .addComponent(txt_packageBookingID, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_searchPBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel64)
                    .addComponent(txt_peopleCount, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_pBookedDate, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(panel_managePBookingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_clearPBookingFields, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancelPBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_markAsPReserve, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(135, 135, 135))
        );
        panel_managePBookingLayout.setVerticalGroup(
            panel_managePBookingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_managePBookingLayout.createSequentialGroup()
                .addGroup(panel_managePBookingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_managePBookingLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_cancelPBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_markAsPReserve, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_clearPBookingFields, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_managePBookingLayout.createSequentialGroup()
                        .addGap(219, 219, 219)
                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_managePBookingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_searchPBooking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_packageBookingID, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_packageIds, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_pBookedCusID, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_pBookedDate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_peopleCount, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(200, 200, 200))
        );

        TabbedPane.addTab("tab8", panel_managePBooking);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setForeground(new java.awt.Color(0, 0, 0));

        table_finance.setBackground(new java.awt.Color(255, 255, 255));
        table_finance.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        table_finance.setForeground(new java.awt.Color(0, 0, 0));
        table_finance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table_finance.setSelectionBackground(new java.awt.Color(5, 124, 124));
        table_finance.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane6.setViewportView(table_finance);

        btn_viewAllFinance.setBackground(new java.awt.Color(5, 124, 124));
        btn_viewAllFinance.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_viewAllFinance.setForeground(new java.awt.Color(255, 255, 255));
        btn_viewAllFinance.setText("All Data");
        btn_viewAllFinance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_viewAllFinanceActionPerformed(evt);
            }
        });

        cbox_months.setBackground(new java.awt.Color(255, 255, 255));
        cbox_months.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbox_months.setForeground(new java.awt.Color(0, 0, 0));
        cbox_months.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        cbox_months.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbox_monthsItemStateChanged(evt);
            }
        });

        jLabel69.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(0, 0, 0));
        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel69.setText("View Monthly");

        lbl_totalAmount.setBackground(new java.awt.Color(255, 255, 255));
        lbl_totalAmount.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_totalAmount.setForeground(new java.awt.Color(0, 0, 0));
        lbl_totalAmount.setText("Total Amount:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(btn_viewAllFinance, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(90, 90, 90)
                        .addComponent(cbox_months, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(146, 146, 146))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 974, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_totalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(101, Short.MAX_VALUE)
                .addComponent(lbl_totalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_viewAllFinance, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                    .addComponent(cbox_months)
                    .addComponent(jLabel69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(206, 206, 206))
        );

        TabbedPane.addTab("tab9", jPanel3);

        jPanel1.add(TabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(443, -34, 1030, 860));

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
                clearRBookingData();
            }
        }
        catch(Exception e){
            
        }
    }//GEN-LAST:event_btn_searchBookingActionPerformed

    private void btn_clearBookingFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearBookingFieldsActionPerformed
        // TODO add your handling code here:
        clearRBookingData();
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
                    String getRoomIDsSql = "select room_id from room_bookings where booking_id = ?";
                    PreparedStatement griPs = con.prepareStatement(getRoomIDsSql);
                    griPs.setString(1, bookingID);
                    ResultSet rs = griPs.executeQuery();
                    List<String> roomIds = new ArrayList<>();
                    while(rs.next()){
                        roomIds.add(rs.getString("room_id"));
                    }
                    
                    for(String id : roomIds){
                        String updateStatus = "update room set status = 'Available' where room_id = ?";
                        PreparedStatement updatePs = con.prepareStatement(updateStatus);
                        updatePs.setString(1, id);
                        updatePs.executeUpdate();
                    }
                    String sql = "Delete from room_bookings where booking_id = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, bookingID);
                    ps.execute();
                    JOptionPane.showMessageDialog(null, "Booking Canceled!", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                    clearRBookingData();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "The booking can't be canceled since the booked date is no more 1 day apart!", "Cannot Cancel Booking!", JOptionPane.WARNING_MESSAGE);
            clearRBookingData();
        }
    }//GEN-LAST:event_btn_cancelBookingActionPerformed

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
                    if(Pattern.matches("^(0|[1-9][0-9]*)$", cusAge)){
                        if(Pattern.matches("(\\+959|09)\\d*", cusContact)){
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
                        else{
                            JOptionPane.showMessageDialog(null, "Invalid Contact Input", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Invalid Age Input", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Please Enter all Required Fields", "Empty Fields", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btn_allocateRoomActionPerformed

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

    private void btn_checkOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_checkOutActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_rReservedData.getModel();
        int index = table_rReservedData.getSelectedRow();
        String rReservedId = model.getValueAt(index, 0).toString();
        String roomId = model.getValueAt(index, 2).toString();
        String checkOutDate = model.getValueAt(index, 5) != null ? model.getValueAt(index, 5).toString() : null;
        if(checkOutDate == null){
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
                    
                    String sql8 = "select * from receipts where receipt_id = ?";
                    PreparedStatement ps8 = con.prepareStatement(sql8);
                    ps8.setString(1, receiptId);
                    ResultSet rs8 = ps8.executeQuery();
                    float cost = 0;
                    while(rs8.next()){
                        cost += Float.parseFloat(rs8.getString("cost"));
                    }
                    
                    String sql3 = "insert into finance (receipt_id, Amount) values (?, ?)";
                    PreparedStatement ps3 = con.prepareStatement(sql3);
                    ps3.setString(1, receiptId);
                    ps3.setFloat(2, cost);
                    ps3.execute();
                    
                    clearCheckOutFields();
                    txt_roomNoSearch.setText("");
                    DisplayRoomReservedData();

                    int result2 = JOptionPane.showConfirmDialog(null, "Checked Out Successfully.\nDo you want to print the voucher?", "Operation Successful", JOptionPane.YES_NO_OPTION);
                    if(result2 == JOptionPane.YES_OPTION){
                        clearCheckOutFields();
                        txt_roomNoSearch.setText("");
                        String path = "C:\\";
                        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
                        try{
                            String cusID = null, cusName = null, cusNRC = null, cusPass = null, cusCon = null;
                            String sql5 = "select cus_id from receipts where receipt_id = ?";
                            PreparedStatement ps5 = con.prepareStatement(sql5);
                            ps5.setString(1, receiptId);
                            ResultSet rs5 = ps5.executeQuery();
                            while(rs5.next()){
                                cusID = rs5.getString("cus_id");
                            }

                            if(cusID != null){
                                String sql6 = "select cus_name, cus_nrc, cus_passport, cus_contact from customer_info where cus_id  = ?";
                                PreparedStatement ps6 = con.prepareStatement(sql6);
                                ps6.setString(1, cusID);
                                ResultSet rs6 = ps6.executeQuery();
                                while(rs6.next()){
                                    cusName = rs6.getString("cus_name");
                                    cusNRC = rs6.getString("cus_nrc");
                                    cusPass = rs6.getString("cus_passport");
                                    cusCon = rs6.getString("cus_contact");
                                }
                            }
                            
                            String sql7 = "SELECT r.room_no, r.room_type, rr.check_in_date, rr.check_out_date, r.room_price, rp.period " +
                                            "FROM receipts rp " +
                                            "JOIN r_reserved_data rr ON rp.r_reserved_id = rr.r_reserved_id " +
                                            "JOIN room r ON rr.room_id = r.room_id " +
                                            "WHERE rp.receipt_id = ?";
                            PreparedStatement ps7 = con.prepareStatement(sql7);
                            ps7.setString(1, receiptId);
                            ResultSet rs7 = ps7.executeQuery();
                            List<Object[]> dataArray = new ArrayList<>();
                            while (rs7.next()) {
                                String room_no = rs7.getString("room_no");
                                String room_type = rs7.getString("room_type");
                                String check_in_date = rs7.getString("check_in_date");
                                String check_out_date = rs7.getString("check_out_date");
                                float room_price = rs7.getFloat("room_price");
                                int days_of_stay = rs7.getInt("period");

                                Object[] rowData = {room_no, room_type, check_in_date, check_out_date, days_of_stay, room_price, (days_of_stay * room_price)};
                                dataArray.add(rowData);
                            }

                            PdfWriter.getInstance(doc, new FileOutputStream(path + "Receipt_" + receiptId + ".pdf"));
                            doc.open();
                            Paragraph p1 = new Paragraph("                                  The Golden Oasis Hotel Guest Receipt\n");
                            doc.add(p1);
                            Paragraph p2 = new Paragraph("**********************************************************************************************************");
                            doc.add(p2);
                            Paragraph p3 = new Paragraph("Receipt ID: " + receiptId);
                            doc.add(p3);
                            Paragraph p5 = new Paragraph("**********************************************************************************************************");
                            doc.add(p5);
                            Paragraph p6 = new Paragraph("Guest Details\nGuest ID: " + cusID + "\nGuest Name: " + cusName + "\nGuest NRC: " + cusNRC + "\nGuest Passport: " + cusPass + "\nGuest Contact: " + cusCon + "\n\n");
                            doc.add(p6);
                            doc.add(p2);
                            PdfPTable table = new PdfPTable(7);
                            String[] headers = {"Room No", "Room Type", "Check In Date", "Check Out Date", "Days of Stay", "Cost Per Day", "Amount"};
                            for (String header : headers) {
                                PdfPCell cell = new PdfPCell(new Phrase(header));
                                table.addCell(cell);
                            }

                            for (Object[] rowData : dataArray) {
                                for (Object data : rowData) {
                                    PdfPCell cell = new PdfPCell(new Phrase(data.toString()));
                                    table.addCell(cell);
                                }
                            }
                            
                            float totalCost = 0;
                            for(Object[] row : dataArray){
                                totalCost += (float)row[6];
                            }
                            
                            PdfPCell TCcell = new PdfPCell(new Phrase("Total Cost: " + totalCost));
                            TCcell.setColspan(6);
                            table.addCell(TCcell);
                            table.addCell("");
                            doc.add(table);
                            
                            doc.add(p2);
                            Paragraph p7 = new Paragraph("Thanks for visiting. Please Come Again in very near future!");
                            doc.add(p7);
                            doc.close();
                            
                            if(new File("C:\\" + "Receipt_" + receiptId + ".pdf").exists()){
                                Process p = Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler C:\\Receipt_" + receiptId + ".pdf");
                            }
                            else{
                                System.out.println("File does not exist");
                            }    
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        clearCheckOutFields();
                        txt_roomNoSearch.setText("");
                        DisplayRoomReservedData();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }       
        }
        else{
            JOptionPane.showMessageDialog(null, "This room is already Checked Out", "Checked Out Room", JOptionPane.INFORMATION_MESSAGE);
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

    private void table_staffAccMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_staffAccMouseClicked
        // TODO add your handling code here:
        TableModel model = table_staffAcc.getModel();
        int index = table_staffAcc.getSelectedRow();
        txt_staffID.setText(model.getValueAt(index, 0).toString());
        txt_staffName.setText(model.getValueAt(index, 1).toString());
        txt_staffAge.setText(model.getValueAt(index, 2).toString());
        int gender = (int) model.getValueAt(index, 3);
        if(gender == 0){
            rbtn_staffMale.setSelected(true);
        }
        else{
            rbtn_staffFemale.setSelected(true);
        }
        int role = (int) model.getValueAt(index, 7);
        if(role == 0){
            rbtn_manager.setSelected(true);
        }
        else{
            rbtn_receptionist.setSelected(true);
        }
        txt_staffNRC.setText(model.getValueAt(index, 4).toString());
        txt_staffCon.setText(model.getValueAt(index, 5).toString());
        txt_staffAddress.setText(model.getValueAt(index, 6).toString());
        txt_staffPassword.setText(model.getValueAt(index, 8).toString());
    }//GEN-LAST:event_table_staffAccMouseClicked

    private void btn_generateStaffIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_generateStaffIDActionPerformed
        // TODO add your handling code here:
        clearStaffFields();
        txt_staffID.setText(generateStaffID());
    }//GEN-LAST:event_btn_generateStaffIDActionPerformed

    private void btn_addStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addStaffActionPerformed
        // TODO add your handling code here:
        String staffid = txt_staffID.getText();
        String staffname = txt_staffName.getText();
        int staffage = Integer.parseInt(txt_staffAge.getText());
        int gender = rbtn_staffMale.isSelected() ? 0 : (rbtn_staffFemale.isSelected() ? 1 : -1);
        String staffnrc = txt_staffNRC.getText();
        String staffcon = txt_staffCon.getText();
        String staffaddress = txt_staffAddress.getText();
        int role = rbtn_manager.isSelected() ? 0 : (rbtn_receptionist.isSelected() ? 1 : -1);
        String staffpassword = txt_staffPassword.getText();
        if(!txt_staffName.getText().isEmpty() && !txt_staffAge.getText().isEmpty() && !txt_staffNRC.getText().isEmpty() && !txt_staffCon.getText().isEmpty() && !txt_staffPassword.getText().isEmpty()){
            if(gender == -1){
                JOptionPane.showMessageDialog(null, "Staff's Gender needs to be provided", "Empty Field", JOptionPane.WARNING_MESSAGE);
            }
            else{
                if(role == -1){
                    JOptionPane.showMessageDialog(null, "Staff's Role needs to be provided", "Empty Field", JOptionPane.WARNING_MESSAGE);
                }
                else{
                    try{
                        Connection con = connect();
                        String sql = "insert into staff(staff_id, staff_name, staff_age, staff_gender, staff_nrc, staff_contact, staff_address, staff_role) values (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, staffid);
                        ps.setString(2, staffname);
                        ps.setInt(3, staffage);
                        ps.setInt(4, gender);
                        ps.setString(5, staffnrc);
                        ps.setString(6, staffcon);
                        ps.setString(7, staffaddress);
                        ps.setInt(8, role);
                        ps.execute();
                        
                        String sql1 = "insert into system_admin (staff_id, staff_role, password) values (?, ?, ?)";
                        PreparedStatement ps1 = con.prepareStatement(sql1);
                        ps1.setString(1, staffid);
                        ps1.setInt(2, role);
                        ps1.setString(3, staffpassword);
                        ps1.execute();
                        
                        JOptionPane.showMessageDialog(null, "Staff Added Successfully", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }//GEN-LAST:event_btn_addStaffActionPerformed

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearActionPerformed
        // TODO add your handling code here:
        clearCusFields();
    }//GEN-LAST:event_btn_clearActionPerformed

    private void lbl_managePackageBookingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_managePackageBookingMouseClicked
        // TODO add your handling code here:
        TabbedPane.setSelectedIndex(7);
        changeDesignOfClickedLabel(7);
        clearPBookingData();
    }//GEN-LAST:event_lbl_managePackageBookingMouseClicked

    private void lbl_managePackageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_managePackageMouseClicked
        // TODO add your handling code here:
        if(role != 0){
            JOptionPane.showMessageDialog(null, "Only Manager can access to this Operation.", "Not Allowed Access", JOptionPane.WARNING_MESSAGE);
        }
        else{
            TabbedPane.setSelectedIndex(2);
            changeDesignOfClickedLabel(2);
            clearPackageFields();
        }
    }//GEN-LAST:event_lbl_managePackageMouseClicked

    private void lbl_logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_logoutMouseClicked
        // TODO add your handling code here:
        changeDesignOfClickedLabel(9);
        int result = JOptionPane.showConfirmDialog(null, "Confirm Logout?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            txt_staffid.setText("Staff id");
            txt_role.setText("Staff role");
            WelcomeForm wf = new WelcomeForm();
            wf.setVisible(true);
            this.hide();
        }
    }//GEN-LAST:event_lbl_logoutMouseClicked

    private void lbl_manageStaffaccMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_manageStaffaccMouseClicked
        // TODO add your handling code here:
        if(role == 0){
            TabbedPane.setSelectedIndex(6);
            changeDesignOfClickedLabel(6);
            DisplayStaffInfo();
            clearStaffFields();
        }
        else{
            JOptionPane.showMessageDialog(null, "Only Manager can access to this Operation.", "Not Allowed Access", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_lbl_manageStaffaccMouseClicked

    private void lbl_guestCheckoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_guestCheckoutMouseClicked
        // TODO add your handling code here:
        TabbedPane.setSelectedIndex(5);
        changeDesignOfClickedLabel(5);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = LocalDate.now().format(dateFormat);
        txt_checkOutDate.setText(currentDate);
        DisplayRoomReservedData();
    }//GEN-LAST:event_lbl_guestCheckoutMouseClicked

    private void lbl_guestCheckinMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_guestCheckinMouseClicked
        // TODO add your handling code here:
        TabbedPane.setSelectedIndex(4);
        changeDesignOfClickedLabel(4);
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

    private void lbl_manageRoomBookingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_manageRoomBookingMouseClicked
        // TODO add your handling code here:
        TabbedPane.setSelectedIndex(3);
        changeDesignOfClickedLabel(3);
        clearRBookingData();
    }//GEN-LAST:event_lbl_manageRoomBookingMouseClicked

    private void lbl_manageRoomsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_manageRoomsMouseClicked
        // TODO add your handling code here:
        if(role != 0){
            JOptionPane.showMessageDialog(null, "Only Manager can access to this Operation.", "Not Allowed Access", JOptionPane.WARNING_MESSAGE);
        }
        else{
            TabbedPane.setSelectedIndex(1);
//            Color jade = new Color(5, 124, 124);
            changeDesignOfClickedLabel(1);
            clearRoomFields();
        }
    }//GEN-LAST:event_lbl_manageRoomsMouseClicked

    private void btn_searchPBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchPBookingActionPerformed
        // TODO add your handling code here:
        String pBookingId = txt_packageBookingID.getText();
        if(pBookingId.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please Enter Package Booking ID to Search", "Empty Field", JOptionPane.WARNING_MESSAGE);
        }
        else{
            try{
                Connection con = connect();
                String sql = "select pbooking_id, GROUP_CONCAT(package_id) as package_ids, cus_id, booking_date, people_count from package_bookings where pbooking_id = ? GROUP BY pbooking_id, cus_id, booking_date, people_count";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, pBookingId);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    txt_packageBookingID.setText(rs.getString("pbooking_id"));
                    txt_pBookedCusID.setText(rs.getString("cus_id"));
                    txt_packageIds.setText(rs.getString("package_ids"));
                    txt_pBookedDate.setText(rs.getString("booking_date"));
                    txt_peopleCount.setText(rs.getString("people_count"));
                }
                else{
                    JOptionPane.showMessageDialog(null, "Booking does not exist.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                    clearPBookingData();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_searchPBookingActionPerformed

    private void btn_cancelPBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelPBookingActionPerformed
        // TODO add your handling code here:
        String pBookingId = txt_packageBookingID.getText();
        String bookedDate = txt_pBookedDate.getText();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = LocalDate.now().format(dateFormat);
        int difference = getDifferenceInDays(bookedDate, currentDate);
        if(difference >= 1){
            int result = JOptionPane.showConfirmDialog(null, "Confirm Cancel Booking?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION){
                try{
                    Connection con = connect();
                    String getIDsql = "select package_id from package_bookings where pbooking_id = ?";
                    PreparedStatement getIDps = con.prepareStatement(getIDsql);
                    getIDps.setString(1, pBookingId);
                    ResultSet rs = getIDps.executeQuery();
                    List<String> ids = new ArrayList<>();
                    while(rs.next()){
                        ids.add(rs.getString("package_id"));
                    }
                    
                    for(String id : ids){
                        String updateSql = "update packages set status = 'Available' where package_id = ?";
                        PreparedStatement updatePs = con.prepareStatement(updateSql);
                        updatePs.setString(1, id);
                        updatePs.executeUpdate();
                    }
                    
                    String deleteSql = "delete from package_bookings where pbooking_id = ?";
                    PreparedStatement deletePs = con.prepareStatement(deleteSql);
                    deletePs.setString(1, pBookingId);
                    deletePs.execute();
                    
                    JOptionPane.showMessageDialog(null, "Booking has been canceled.", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                    clearPBookingData();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "The booking can't be canceled since the booked date is no more 1 day apart!", "Cannot Cancel Booking!", JOptionPane.WARNING_MESSAGE);
            clearPBookingData();
        }
    }//GEN-LAST:event_btn_cancelPBookingActionPerformed

    private void btn_markAsReserveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_markAsReserveActionPerformed
        // TODO add your handling code here:
        String RbookingId = txt_bookingID.getText();
        String reserveId = generateRReservedID();
        String checkInDate = txt_bookingDate.getText();
        String cusID = txt_bookedCusID.getText();
        int result = JOptionPane.showConfirmDialog(null, "Confirm Mark As Reserved?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            try{
                Connection con = connect();
                String getIDsql = "select room_id from room_bookings where booking_id = ?";
                PreparedStatement getIDps = con.prepareStatement(getIDsql);
                getIDps.setString(1, RbookingId);
                ResultSet getIdrs = getIDps.executeQuery();
                List<String> roomIds = new ArrayList<>();
                while(getIdrs.next()){
                    roomIds.add(getIdrs.getString("room_id"));
                }
                
                for(String roomid : roomIds){
                    String pushSql = "insert into r_reserved_data(r_reserved_id, cus_id, room_id, payment_status, check_in_date, check_out_date) values (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pushPs = con.prepareStatement(pushSql);
                    pushPs.setString(1, reserveId);
                    pushPs.setString(2, cusID);
                    pushPs.setString(3, roomid);
                    pushPs.setString(4, "Not Paid");
                    pushPs.setString(5, checkInDate);
                    pushPs.setString(6, null);
                    pushPs.execute();
                    
                    String updateSql = "update room set status = 'Reserved' where room_id = ?";
                    PreparedStatement updatePs = con.prepareStatement(updateSql);
                    updatePs.setString(1, roomid);
                    updatePs.executeUpdate();
                }
                
                String deleteSql = "delete from room_bookings where booking_id = ?";
                PreparedStatement deletePs = con.prepareStatement(deleteSql);
                deletePs.setString(1, RbookingId);
                deletePs.executeUpdate();
                
                JOptionPane.showMessageDialog(null, "Booking has been changed as reserved.", "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
                clearRBookingData();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_markAsReserveActionPerformed

    private void btn_markAsPReserveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_markAsPReserveActionPerformed
        // TODO add your handling code here:
        String pBookingId = txt_packageBookingID.getText();
        String reserveId = generatePReservedID();
        String cusID = txt_pBookedCusID.getText();
        String reserveDate = txt_pBookedDate.getText();
        int peopleCount = Integer.parseInt(txt_peopleCount.getText());
        int result = JOptionPane.showConfirmDialog(null, "Confirm Mark As Reserve", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            try{
                Connection con = connect();
                String getIDsql = "select package_id from package_bookings where pbooking_id = ?";
                PreparedStatement getIDps = con.prepareStatement(getIDsql);
                getIDps.setString(1, pBookingId);
                List<String> packageIds = new ArrayList<>();
                List<Float> packagePrices = new ArrayList<>();
                ResultSet getIDrs = getIDps.executeQuery();
                while(getIDrs.next()){
                    packageIds.add(getIDrs.getString("package_id"));
                }
                
                for(String packageid : packageIds){
                    String getPPrice = "select package_price from packages where package_id = ?";
                    PreparedStatement getPPricePs = con.prepareStatement(getPPrice);
                    getPPricePs.setString(1, packageid);
                    ResultSet gPPrs = getPPricePs.executeQuery();
                    while(gPPrs.next()){
                        packagePrices.add(Float.valueOf(gPPrs.getString("package_price")));
                    }    
                    String pushSql = "insert into p_reserved_data(p_reserved_id, cus_id, package_id, payment_status, people_count, reserve_date) values (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pushPs = con.prepareStatement(pushSql);
                    pushPs.setString(1, reserveId);
                    pushPs.setString(2, cusID);
                    pushPs.setString(3, packageid);
                    pushPs.setString(4, "Full Paid");
                    pushPs.setInt(5, peopleCount);
                    pushPs.setString(6, reserveDate);
                    pushPs.execute();
                    
                    String updateSql = "update packages set status = 'Reserved' where package_id = ?";
                    PreparedStatement updatePs = con.prepareStatement(updateSql);
                    updatePs.setString(1, packageid);
                    updatePs.executeUpdate();
                }
                
                float totalPrice = 0;
                for(Float price : packagePrices){
                    totalPrice += price;
                }
                
                String receiptId = generateReceiptID();
                String pushReceiptSql = "insert into receipts (receipt_id, cus_id, p_reserved_id, cost, payment_status) values (?, ?, ?, ?, ?)";
                PreparedStatement pushReceiptPs = con.prepareStatement(pushReceiptSql);
                pushReceiptPs.setString(1, receiptId);
                pushReceiptPs.setString(2, cusID);
                pushReceiptPs.setString(3, reserveId);
                pushReceiptPs.setFloat(4, totalPrice);
                pushReceiptPs.setString(5, "Full Paid");
                pushReceiptPs.execute();
                
                String pushFinanceSql = "insert into finance (receipt_id, amount) values (?, ?)";
                PreparedStatement pushFinancePs = con.prepareStatement(pushFinanceSql);
                pushFinancePs.setString(1, receiptId);
                pushFinancePs.setFloat(2, totalPrice);
                pushFinancePs.execute();
                
                String deleteSql = "delete from package_bookings where pbooking_id = ?";
                PreparedStatement deletePs = con.prepareStatement(deleteSql);
                deletePs.setString(1, pBookingId);
                deletePs.executeUpdate();
                clearPBookingData();
                int result1 = JOptionPane.showConfirmDialog(null, "Booking has been changed as reserved.\nDo you want to print the recipt?", "Operation Successful", JOptionPane.YES_NO_OPTION);
                if(result1 == JOptionPane.YES_OPTION){
                    String path = "C:\\";
                    com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
                    try{
                        String CusID = null, cusName = null, cusNRC = null, cusPass = null, cusCon = null;
                        String getcusIDSql = "select cus_id from receipts where receipt_id = ?";
                        PreparedStatement ps5 = con.prepareStatement(getcusIDSql);
                        ps5.setString(1, receiptId);
                        ResultSet rs5 = ps5.executeQuery();
                        while(rs5.next()){
                            CusID = rs5.getString("cus_id");
                        }

                        if(CusID != null){
                            String getCusInfoSql = "select cus_name, cus_nrc, cus_passport, cus_contact from customer_info where cus_id  = ?";
                            PreparedStatement ps6 = con.prepareStatement(getCusInfoSql);
                            ps6.setString(1, CusID);
                            ResultSet rs6 = ps6.executeQuery();
                            while(rs6.next()){
                                cusName = rs6.getString("cus_name");
                                cusNRC = rs6.getString("cus_nrc");
                                cusPass = rs6.getString("cus_passport");
                                cusCon = rs6.getString("cus_contact");
                            }
                        }
                        
                        String getRequiredDataSql = "SELECT p.package_name, p.package_type, p.package_price, p.services " +
                                                    "FROM receipts rp " +
                                                    "JOIN p_reserved_data pr ON rp.p_reserved_id = pr.p_reserved_id " +
                                                    "JOIN packages p ON pr.package_id = p.package_id " +
                                                    "WHERE rp.receipt_id = ?";
                        PreparedStatement gRDPs = con.prepareStatement(getRequiredDataSql);
                        gRDPs.setString(1, receiptId);
                        ResultSet gRDRs = gRDPs.executeQuery();
                        List<Object[]> dataArray = new ArrayList<>();
                        while(gRDRs.next()){
                            String packageName = gRDRs.getString("package_name");
                            String packageType = gRDRs.getString("package_type");
                            float packagePrice = Float.parseFloat(gRDRs.getString("package_price"));
                            String services = gRDRs.getString("services");
                            
                            Object[] rowData = {packageName, packageType, services, packagePrice};
                            dataArray.add(rowData);
                        }
                        
                        PdfWriter.getInstance(doc, new FileOutputStream(path + "Receipt_" + receiptId + ".pdf"));
                        doc.open();
                        Paragraph p1 = new Paragraph("                                  The Golden Oasis Hotel Guest Receipt\n");
                        doc.add(p1);
                        Paragraph p2 = new Paragraph("**********************************************************************************************************");
                        doc.add(p2);
                        Paragraph p3 = new Paragraph("Receipt ID: " + receiptId);
                        doc.add(p3);
                        Paragraph p5 = new Paragraph("**********************************************************************************************************");
                        doc.add(p5);
                        Paragraph p6 = new Paragraph("Guest Details\nGuest ID: " + cusID + "\nGuest Name: " + cusName + "\nGuest NRC: " + cusNRC + "\nGuest Passport: " + cusPass + "\nGuest Contact: " + cusCon + "\n\n");
                        doc.add(p6);
                        doc.add(p2);
                        PdfPTable table = new PdfPTable(4);
                        String[] headers = {"Package Name", "Package Type", "Services", "Package Price"};
                        for (String header : headers) {
                            PdfPCell cell = new PdfPCell(new Phrase(header));
                            table.addCell(cell);
                        }
                        
                        for (Object[] rowData : dataArray) {
                            for (Object data : rowData) {
                                PdfPCell cell = new PdfPCell(new Phrase(data.toString()));
                                table.addCell(cell);
                            }
                        }

                        float totalCost = 0;
                        for(Object[] row : dataArray){
                            totalCost += (float)row[3];
                        }

                        PdfPCell TCcell = new PdfPCell(new Phrase("Total Cost: " + totalCost));
                        TCcell.setColspan(3);
                        table.addCell(TCcell);
                        table.addCell("");
                        doc.add(table);

                        doc.add(p2);
                        Paragraph p7 = new Paragraph("Thanks for visiting. Please Come Again in very near future!");
                        doc.add(p7);
                        doc.close();

                        if(new File("C:\\" + "Receipt_" + receiptId + ".pdf").exists()){
                            Process p = Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler C:\\Receipt_" + receiptId + ".pdf");
                        }
                        else{
                            System.out.println("File does not exist");
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    clearPBookingData();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_markAsPReserveActionPerformed

    private void btn_clearPBookingFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearPBookingFieldsActionPerformed
        // TODO add your handling code here:
        clearPBookingData();
    }//GEN-LAST:event_btn_clearPBookingFieldsActionPerformed

    private void lbl_viewFinanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_viewFinanceMouseClicked
        // TODO add your handling code here:
        TabbedPane.setSelectedIndex(8);
        DisplayFinance();
        getTotalAmount();
        changeDesignOfClickedLabel(8);
    }//GEN-LAST:event_lbl_viewFinanceMouseClicked

    private void btn_viewAllFinanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_viewAllFinanceActionPerformed
        // TODO add your handling code here:
        DisplayFinance();
        getTotalAmount();
    }//GEN-LAST:event_btn_viewAllFinanceActionPerformed

    private void cbox_monthsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbox_monthsItemStateChanged
        // TODO add your handling code here:
        String selectedMonth = cbox_months.getSelectedItem().toString();
        DisplayFinanceByMonth(selectedMonth);
        getTotalAmount();
    }//GEN-LAST:event_cbox_monthsItemStateChanged

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
    private javax.swing.JButton btn_addStaff;
    private javax.swing.JButton btn_addToList;
    private javax.swing.JButton btn_allocateRoom;
    private javax.swing.JButton btn_cancelBooking;
    private javax.swing.JButton btn_cancelPBooking;
    private javax.swing.JButton btn_checkOut;
    private javax.swing.JButton btn_chooseImage;
    private javax.swing.JButton btn_chooseNewImage;
    private javax.swing.JButton btn_clear;
    private javax.swing.JButton btn_clearBookingFields;
    private javax.swing.JButton btn_clearPBookingFields;
    private javax.swing.JButton btn_generateID;
    private javax.swing.JButton btn_generatePackageID;
    private javax.swing.JButton btn_generateStaffID;
    private javax.swing.JButton btn_markAsPReserve;
    private javax.swing.JButton btn_markAsReserve;
    private javax.swing.JButton btn_removePackage;
    private javax.swing.JButton btn_removeRoom;
    private javax.swing.JButton btn_removeStaff;
    private javax.swing.JButton btn_searchBooking;
    private javax.swing.JButton btn_searchPBooking;
    private javax.swing.JButton btn_searchRoomNo;
    private javax.swing.JButton btn_updatePackage;
    private javax.swing.JButton btn_updateRoom;
    private javax.swing.JButton btn_updateStaff;
    private javax.swing.JButton btn_viewAllFinance;
    private javax.swing.JComboBox<String> cbox_months;
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
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lbl_guestCheckin;
    private javax.swing.JLabel lbl_guestCheckout;
    private javax.swing.JLabel lbl_logout;
    private javax.swing.JLabel lbl_managePackage;
    private javax.swing.JLabel lbl_managePackageBooking;
    private javax.swing.JLabel lbl_manageRoomBooking;
    private javax.swing.JLabel lbl_manageRooms;
    private javax.swing.JLabel lbl_manageStaffacc;
    private javax.swing.JLabel lbl_packageImage;
    private javax.swing.JLabel lbl_roomImage;
    private javax.swing.JLabel lbl_totalAmount;
    private javax.swing.JLabel lbl_viewFinance;
    private javax.swing.JPanel panel_GcheckIn;
    private javax.swing.JPanel panel_GcheckOut;
    private javax.swing.JPanel panel_adminHome;
    private javax.swing.JPanel panel_guestCheckIn;
    private javax.swing.JPanel panel_guestCheckOut;
    private javax.swing.JPanel panel_logOutLbl;
    private javax.swing.JPanel panel_manageBookings;
    private javax.swing.JPanel panel_managePBooking;
    private javax.swing.JPanel panel_managePBookingLbl;
    private javax.swing.JPanel panel_managePackLbl;
    private javax.swing.JPanel panel_managePackages;
    private javax.swing.JPanel panel_manageRBookingLbl;
    private javax.swing.JPanel panel_manageRooms;
    private javax.swing.JPanel panel_manageRoomsLbl;
    private javax.swing.JPanel panel_manageStaffLbl;
    private javax.swing.JPanel panel_manageStaffacc;
    private javax.swing.JPanel panel_viewFinanceLbl;
    private javax.swing.JRadioButton rbtn_female;
    private javax.swing.JRadioButton rbtn_male;
    private javax.swing.JRadioButton rbtn_manager;
    private javax.swing.JRadioButton rbtn_receptionist;
    private javax.swing.JRadioButton rbtn_staffFemale;
    private javax.swing.JRadioButton rbtn_staffMale;
    private javax.swing.JTable roomTable;
    private javax.swing.JSpinner spin_bedCount;
    private javax.swing.ButtonGroup staffGenderRbtnGroup;
    private javax.swing.ButtonGroup staffRoleRbtnGroup;
    private javax.swing.JTable table_finance;
    private javax.swing.JTable table_packages;
    private javax.swing.JTable table_rReservedData;
    private javax.swing.JTable table_staffAcc;
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
    private javax.swing.JTextField txt_pBookedCusID;
    private javax.swing.JTextField txt_pBookedDate;
    private javax.swing.JTextField txt_packageBookingID;
    private javax.swing.JTextField txt_packageID;
    private javax.swing.JTextField txt_packageIds;
    private javax.swing.JTextField txt_packageImagePath;
    private javax.swing.JTextField txt_packageName;
    private javax.swing.JTextField txt_packagePrice;
    private javax.swing.JTextField txt_packageStatus;
    private javax.swing.JTextField txt_packageType;
    private javax.swing.JTextField txt_paymentStatus;
    private javax.swing.JTextField txt_peopleCount;
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
    private javax.swing.JTextField txt_staffAddress;
    private javax.swing.JTextField txt_staffAge;
    private javax.swing.JTextField txt_staffCon;
    private javax.swing.JTextField txt_staffID;
    private javax.swing.JTextField txt_staffNRC;
    private javax.swing.JTextField txt_staffName;
    private javax.swing.JTextField txt_staffPassword;
    private javax.swing.JLabel txt_staffid;
    private javax.swing.JTextField txt_totalCost;
    // End of variables declaration//GEN-END:variables
}
