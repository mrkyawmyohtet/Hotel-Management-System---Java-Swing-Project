/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ProjectPackage;

import com.mysql.cj.jdbc.Blob;
import java.awt.Color;
import java.awt.Image;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DELL
 */
public class ViewRooms extends javax.swing.JFrame {

    /**
     * Creates new form ViewRooms
     */
    public ViewRooms() {
        initComponents();
        
        //to get the columns' names into the combobox
        Connection con = connect();
        ResultSet rs = null;
        String[] columnNames = {"room_type", "room_price", "description", "bed"};
        try
        {
            DatabaseMetaData dbmd = con.getMetaData();
            for(String columnName : columnNames){               
                rs = dbmd.getColumns(null, "fp_hotel_management_system", "room", columnName);
                while(rs.next())
                {
                    cbox_options.addItem(rs.getString("COLUMN_NAME"));
                }
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
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
    
    public void displayAll(){
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = connect();
            String sql = "Select room_id, room_no, room_type, room_price, description, bed from room";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            table_rooms.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public void displayAfterCheck(List<String> availableRooms) {
    try {
        Connection con = connect();
        if (!availableRooms.isEmpty()) {
            String sql = "SELECT room_id, room_no, room_type, room_price, description, bed FROM room WHERE room_id IN (";
            for (int i = 0; i < availableRooms.size(); i++) {
                sql += "'" + availableRooms.get(i) + "'";
                if (i < availableRooms.size() - 1) {
                    sql += ",";
                }
            }
            sql += ")";
            
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            table_rooms.setModel(DbUtils.resultSetToTableModel(rs));
            } else {
                // No available rooms, display an empty table
                JOptionPane.showMessageDialog(null, "There is no room for your preference at the moment!\nThanks for your understanding.", "No room Available", JOptionPane.INFORMATION_MESSAGE);
                table_rooms.setModel(new DefaultTableModel());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    //get the date as argument and check which rooms are free on that day
    public List<String> checkReservedAndBookedRow(String date) throws ParseException{
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> availableRooms = new ArrayList<>();
        try{
            con = connect();

            // Retrieve booked room IDs for the specific date
            String bookedSql = "SELECT DISTINCT room_id FROM room_bookings WHERE booking_date = ?";
            ps = con.prepareStatement(bookedSql);
            ps.setString(1, date);
            rs = ps.executeQuery();
            Set<String> bookedRoomIds = new HashSet<>();
            while (rs.next()) {
                bookedRoomIds.add(rs.getString("room_id"));
            }

            // Retrieve reserved room IDs for the specific date
            String reservedSql = "SELECT DISTINCT room_id FROM r_reserved_data WHERE check_out_date >= ? OR check_out_date is NULL";
            ps = con.prepareStatement(reservedSql);
            ps.setString(1, date);
            rs = ps.executeQuery();
            Set<String> reservedRoomIds = new HashSet<>();
            while (rs.next()) {
                reservedRoomIds.add(rs.getString("room_id"));
            }

            // Retrieve all room IDs from the room table
            String allRoomsSql = "SELECT room_id FROM room";
            ps = con.prepareStatement(allRoomsSql);
            rs = ps.executeQuery();
            Set<String> allRoomIds = new HashSet<>();
            while (rs.next()) {
                allRoomIds.add(rs.getString("room_id"));
            }

            // Filter out the booked and reserved room IDs from the available room IDs
            for (String roomId : allRoomIds) {
                if (!bookedRoomIds.contains(roomId) && !reservedRoomIds.contains(roomId)) {
                    availableRooms.add(roomId);
                }
            }

            System.out.println("Available Rooms: " + availableRooms);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            try{
                rs.close();
                ps.close();
                con.close();  
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        return availableRooms;
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_rooms = new javax.swing.JTable();
        lbl_image = new javax.swing.JLabel();
        btn_bookNow = new javax.swing.JButton();
        lbl_roomNum = new javax.swing.JLabel();
        lbl_cost = new javax.swing.JLabel();
        lbl_roomType = new javax.swing.JLabel();
        lbl_bedNum = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbox_options = new javax.swing.JComboBox<>();
        btn_searchRoom = new javax.swing.JButton();
        btn_viewReserved = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tarea_desc = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        btn_packages = new javax.swing.JButton();
        cbox_searchRoom = new javax.swing.JComboBox<>();
        lbl_available_date = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        booking_datechooser = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        stay_period = new javax.swing.JSpinner();
        btn_checkAvailability = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));

        jPanel2.setBackground(new java.awt.Color(255, 204, 0));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Trajan Pro", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesandIcons/icons8-search-50.png"))); // NOI18N
        jLabel1.setText("Please, take a look at our rooms depending on your need....");
        jLabel1.setIconTextGap(10);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1074, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        table_rooms.setBackground(new java.awt.Color(255, 255, 255));
        table_rooms.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        table_rooms.setForeground(new java.awt.Color(0, 0, 0));
        table_rooms.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        table_rooms.setSelectionBackground(new java.awt.Color(5, 124, 124));
        table_rooms.setSelectionForeground(new java.awt.Color(255, 255, 255));
        table_rooms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_roomsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table_rooms);

        lbl_image.setText("Room Photo");
        lbl_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btn_bookNow.setBackground(new java.awt.Color(255, 204, 0));
        btn_bookNow.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        btn_bookNow.setForeground(new java.awt.Color(255, 255, 255));
        btn_bookNow.setText("Book Now");
        btn_bookNow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_bookNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_bookNowActionPerformed(evt);
            }
        });

        lbl_roomNum.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lbl_roomNum.setForeground(new java.awt.Color(0, 0, 0));
        lbl_roomNum.setText("Room Number");

        lbl_cost.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lbl_cost.setForeground(new java.awt.Color(0, 0, 0));
        lbl_cost.setText("Cost");

        lbl_roomType.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        lbl_roomType.setForeground(new java.awt.Color(0, 0, 0));
        lbl_roomType.setText("Room Type");

        lbl_bedNum.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lbl_bedNum.setForeground(new java.awt.Color(0, 0, 0));
        lbl_bedNum.setText("Bed");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Search for Rooms:");

        cbox_options.setBackground(new java.awt.Color(255, 255, 255));
        cbox_options.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        cbox_options.setForeground(new java.awt.Color(0, 0, 0));
        cbox_options.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbox_optionsItemStateChanged(evt);
            }
        });

        btn_searchRoom.setBackground(new java.awt.Color(0, 0, 0));
        btn_searchRoom.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        btn_searchRoom.setForeground(new java.awt.Color(255, 255, 255));
        btn_searchRoom.setText("Search");
        btn_searchRoom.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_searchRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchRoomActionPerformed(evt);
            }
        });

        btn_viewReserved.setBackground(new java.awt.Color(0, 0, 0));
        btn_viewReserved.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        btn_viewReserved.setForeground(new java.awt.Color(255, 255, 255));
        btn_viewReserved.setText("View Your Reserved Rooms");
        btn_viewReserved.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_viewReserved.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_viewReservedActionPerformed(evt);
            }
        });

        tarea_desc.setBackground(new java.awt.Color(255, 255, 255));
        tarea_desc.setColumns(20);
        tarea_desc.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        tarea_desc.setForeground(new java.awt.Color(0, 0, 0));
        tarea_desc.setRows(5);
        tarea_desc.setText("Description");
        tarea_desc.setFocusable(false);
        jScrollPane2.setViewportView(tarea_desc);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(5, 124, 124));
        jLabel2.setText("Or Planing for an Occasion?");

        btn_packages.setBackground(new java.awt.Color(5, 124, 124));
        btn_packages.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        btn_packages.setForeground(new java.awt.Color(255, 255, 255));
        btn_packages.setText("Check Event Packages");
        btn_packages.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_packages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_packagesActionPerformed(evt);
            }
        });

        cbox_searchRoom.setBackground(new java.awt.Color(255, 255, 255));
        cbox_searchRoom.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        cbox_searchRoom.setForeground(new java.awt.Color(0, 0, 0));

        lbl_available_date.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lbl_available_date.setForeground(new java.awt.Color(0, 0, 0));
        lbl_available_date.setText("Available Date");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Booking Date:");

        booking_datechooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                booking_datechooserPropertyChange(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Stay Period:");

        stay_period.setBorder(null);

        btn_checkAvailability.setBackground(new java.awt.Color(255, 204, 0));
        btn_checkAvailability.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_checkAvailability.setForeground(new java.awt.Color(255, 255, 255));
        btn_checkAvailability.setText("Check Available Room");
        btn_checkAvailability.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_checkAvailability.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_checkAvailabilityActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 714, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(booking_datechooser, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel5)
                                        .addGap(18, 18, 18)
                                        .addComponent(stay_period, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbox_options, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cbox_searchRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btn_searchRoom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn_checkAvailability, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_image, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_bookNow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_viewReserved, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(lbl_roomType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_roomNum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_bedNum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_cost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                            .addComponent(btn_packages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lbl_available_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_packages, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(lbl_roomType, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_roomNum, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_bedNum, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_cost, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_available_date, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_bookNow, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_viewReserved, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(booking_datechooser, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(stay_period, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btn_checkAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_image, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbox_options, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbox_searchRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btn_searchRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(26, 26, 26))
        );

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

    private void btn_bookNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_bookNowActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_rooms.getModel();
        Date selectedDate  = booking_datechooser.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(selectedDate);
        //first check if the user has selected a room or not
        if(table_rooms.getSelectedRowCount() == 0){
            //this mean the user has not selected a room yet
            JOptionPane.showMessageDialog(null, "Please choose a room to book", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        else{
            int index = table_rooms.getSelectedRow();
            String room_id = model.getValueAt(index, 0).toString();
            String status = model.getValueAt(index, 4).toString();
            String date = "Don't have same date";
            try{
                Connection con = connect();
                String sql = "select booking_date from reserved_temp where room_id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, room_id);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    date = rs.getString("booking_date");
                }
                
                if(date.equals(formattedDate)){
                    //this mean the room is already occupied
                    JOptionPane.showMessageDialog(null, "This room is already chosen for the same booking date!\nPlease check another room.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
                else{
                    try{
                        //first add the room to the temp table
                        Connection conn = connect();
                        String sql1 = "insert into reserved_temp (room_id, booking_date, stay_period) values ('" + room_id + "', '"+ formattedDate +"', '"+ (int) stay_period.getValue()+"')";
                        PreparedStatement ps1 = conn.prepareStatement(sql1);
                        ps1.execute();

    //                    //change the status of the room
    //                    String changeSql = "update room set status = 'Occupied' where room_id = ?";
    //                    PreparedStatement changePs = con.prepareStatement(changeSql);
    //                    changePs.setString(1, room_id);
    //                    changePs.executeUpdate();

                        //show a message to the customer
                        JOptionPane.showMessageDialog(null, "Room has been added to the booking list!");
    //                    lbl_status.setText("Occupied");
    //                    lbl_status.setForeground(Color.red);
    //                    //recall the display function to show updated data in the table
//                        displayAll();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }            
        }              
    }//GEN-LAST:event_btn_bookNowActionPerformed

    private void btn_viewReservedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_viewReservedActionPerformed
        // TODO add your handling code here:
        ListedRoomInfo RRI = new ListedRoomInfo();
        RRI.setVisible(true);
        this.hide();
    }//GEN-LAST:event_btn_viewReservedActionPerformed

    private void table_roomsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_roomsMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_rooms.getModel();
        int index = table_rooms.getSelectedRow();
        String room_id = model.getValueAt(index, 0).toString();
        String sql = "Select * from room where room_id = ?";
        try {
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, room_id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                //setting the icon of the lbl_image
                String image_name = rs.getString("image_name");
                ImageIcon icon = new ImageIcon(getClass().getResource("/ImagesandIcons/" + image_name));
                icon = new ImageIcon(icon.getImage().getScaledInstance(450, 500, Image.SCALE_SMOOTH));
                lbl_image.setText("");
                lbl_image.setIcon(icon);

                //setting the fields to show the information about the room to the user
                lbl_roomType.setText("Room Type: " + rs.getString("room_type"));
                lbl_roomNum.setText("Room Number: " + rs.getString("room_no"));
                lbl_bedNum.setText("Bed:" + rs.getInt("bed"));
                lbl_cost.setText("Cost: " + rs.getString("room_price"));
                tarea_desc.setText(rs.getString("description"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }       
    }//GEN-LAST:event_table_roomsMouseClicked

    private void cbox_optionsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbox_optionsItemStateChanged
        // TODO add your handling code here:
        String columnName = cbox_options.getSelectedItem().toString();
        try{
            cbox_searchRoom.removeAllItems();
            Connection con = connect();
            String sql = "SELECT DISTINCT " + columnName + " FROM room";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String data = rs.getString(columnName);
                cbox_searchRoom.addItem(data);
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_cbox_optionsItemStateChanged

    private void btn_searchRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchRoomActionPerformed
        // TODO add your handling code here:
        String columnName = cbox_options.getSelectedItem().toString();
        String data = cbox_searchRoom.getSelectedItem().toString();
        Date selectedDate  = booking_datechooser.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(selectedDate);
        
        try{
            Connection con = connect();
            String sql = null;
            if(columnName.equals("room_price") || columnName.equals("bed")){
                sql = "SELECT room_id, room_no, room_type, room_price, description, bed FROM room " +
                  "WHERE " + columnName + " = " + data + " " +
                  "AND room_id NOT IN " +
                  "(SELECT DISTINCT room_id FROM room_bookings WHERE booking_date = ?) " +
                  "AND room_id NOT IN " +
                  "(SELECT DISTINCT room_id FROM r_reserved_data WHERE check_out_date >= ? OR check_out_date is NULL)";
            }
            else{
                sql = "SELECT room_id, room_no, room_type, room_price, description, bed FROM room " +
                  "WHERE " + columnName + " = '" + data + "' " +
                  "AND room_id NOT IN " +
                  "(SELECT DISTINCT room_id FROM room_bookings WHERE booking_date = ?) " +
                  "AND room_id NOT IN " +
                  "(SELECT DISTINCT room_id FROM r_reserved_data WHERE check_out_date >= ? OR check_out_date is NULL)";
            }
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, formattedDate);
            ps.setString(2, formattedDate);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = new DefaultTableModel();
            //get the column name and columncount
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add column names to the model
            for (int column = 1; column <= columnCount; column++) {
                model.addColumn(metaData.getColumnName(column));
            }

            // Add rows to the model
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
            }

            // Set the model to the table
            table_rooms.setModel(model);

            if (model.getRowCount() == 0){
                JOptionPane.showMessageDialog(null, """
                                                    Sorry, the room you are looking for is not at our hotel!
                                                    Please contact the reception or try to search again after checking the data you are selecting.
                                                    Thank for your understanding!""", "Sorry", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_searchRoomActionPerformed

    private void btn_checkAvailabilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_checkAvailabilityActionPerformed
        // TODO add your handling code here:
        // Get the selected date from jDateChooser
        Date selectedDate = booking_datechooser.getDate();

        // Get the value from jSpinner
        int daysToAdd = (int) stay_period.getValue();

        // Create a Calendar instance and set it to the selected date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);

        // Add the selected number of days to the date
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);

        // Get the result date
        Date resultDate = calendar.getTime();

        // Display the result
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String result = dateFormat.format(selectedDate);
        if(selectedDate == null || daysToAdd <= 0){
            JOptionPane.showMessageDialog(null, "Please choose booking date and stay period!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        else{           
            try {
                displayAfterCheck(checkReservedAndBookedRow(result));
            } catch (ParseException ex) {
                Logger.getLogger(ViewRooms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btn_checkAvailabilityActionPerformed

    private void booking_datechooserPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_booking_datechooserPropertyChange
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_rooms.getModel();
        model.setRowCount(0); // Clear all rows from the table
    }//GEN-LAST:event_booking_datechooserPropertyChange

    private void btn_packagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_packagesActionPerformed
        // TODO add your handling code here:
        ViewPackages vp = new ViewPackages();
        vp.setVisible(true);
        this.hide();
    }//GEN-LAST:event_btn_packagesActionPerformed

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
            java.util.logging.Logger.getLogger(ViewRooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewRooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewRooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewRooms.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewRooms().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser booking_datechooser;
    private javax.swing.JButton btn_bookNow;
    private javax.swing.JButton btn_checkAvailability;
    private javax.swing.JButton btn_packages;
    private javax.swing.JButton btn_searchRoom;
    private javax.swing.JButton btn_viewReserved;
    private javax.swing.JComboBox<String> cbox_options;
    private javax.swing.JComboBox<String> cbox_searchRoom;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_available_date;
    private javax.swing.JLabel lbl_bedNum;
    private javax.swing.JLabel lbl_cost;
    private javax.swing.JLabel lbl_image;
    private javax.swing.JLabel lbl_roomNum;
    private javax.swing.JLabel lbl_roomType;
    private javax.swing.JSpinner stay_period;
    private javax.swing.JTable table_rooms;
    private javax.swing.JTextArea tarea_desc;
    // End of variables declaration//GEN-END:variables
}
