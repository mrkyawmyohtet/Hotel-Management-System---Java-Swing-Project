/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ProjectPackage;

import java.awt.Image;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author DELL
 */
public class ViewPackages extends javax.swing.JFrame {

    /**
     * Creates new form ViewPackages
     */
    public ViewPackages() {
        initComponents();
        
        Connection con = connect();
        ResultSet rs = null;
        String[] columns = {"package_type", "package_price"};
        try{
            DatabaseMetaData dbmd = con.getMetaData();
            for(String column : columns){
                rs = dbmd.getColumns(null, "fp_hotel_management_system", "packages", column);
                while(rs.next()){
                    cbox_pColumns.addItem(rs.getString("COLUMN_NAME"));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
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
    
    public List<String> checkBookedRow(String date) throws ParseException{
        Connection con = connect();
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> availablePackages = new ArrayList<>();
        try{
            String bookedPackageSql = "select distinct package_id from package_bookings where booking_date = ?";
            ps = con.prepareStatement(bookedPackageSql);
            ps.setString(1, date);
            rs = ps.executeQuery();
            Set<String> bookedPIds = new HashSet<>();
            while(rs.next()){
                bookedPIds.add(rs.getString("package_id"));
            }
            
            String reservedPackageSql = "select distinct package_id from p_reserved_data where reserve_date = ?";
            ps = con.prepareStatement(reservedPackageSql);
            ps.setString(1, date);
            rs = ps.executeQuery();
            Set<String> reservedPIds = new HashSet<>();
            while(rs.next()){
                reservedPIds.add(rs.getString("package_id"));
            }
            
            String allPackageSql = "select package_id from packages";
            ps = con.prepareStatement(allPackageSql);
            rs = ps.executeQuery();
            Set<String> allPIds = new HashSet<>();
            while(rs.next()){
                allPIds.add(rs.getString("package_id"));
            }
            for(String packageID : allPIds){
                if(!bookedPIds.contains(packageID) && !reservedPIds.contains(packageID)){
                    availablePackages.add(packageID);
                }
            }
            System.out.println("Available Packages: " + availablePackages);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return availablePackages;
    }
    
    public void displayAfterCheck(List<String> availablePackages){
        try{
            Connection con = connect();
            if(!availablePackages.isEmpty()){
                String sql = "SELECT package_id, package_name, package_type, package_price, services from packages where package_id IN (";
                for (int i = 0; i < availablePackages.size(); i++) {
                    sql += "'" + availablePackages.get(i) + "'";
                    if (i < availablePackages.size() - 1) {
                        sql += ",";
                    }
                }
                sql += ")";
                
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                table_packages.setModel(DbUtils.resultSetToTableModel(rs));
            }
            else{
                JOptionPane.showMessageDialog(null, "There is no Package for your preference at this moment!\nThanks for your understanding.", "No Package Available", JOptionPane.INFORMATION_MESSAGE);
                table_packages.setModel(new DefaultTableModel());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void clearFields(){
        lbl_packageImage.setText("Package Image");
        lbl_packageImage.setIcon(null);
        txtArea_details.setText("Details:");
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
        jLabel2 = new javax.swing.JLabel();
        jdc_packageAvaiDate = new com.toedter.calendar.JDateChooser();
        btn_checkAvaiPackage = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cbox_pColumns = new javax.swing.JComboBox<>();
        cbox_pOptions = new javax.swing.JComboBox<>();
        btn_searchPackage = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_packages = new javax.swing.JTable();
        lbl_packageImage = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtArea_details = new javax.swing.JTextArea();
        btn_bookPackage = new javax.swing.JButton();
        btn_viewBPList = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));

        jPanel2.setBackground(new java.awt.Color(255, 204, 0));

        jLabel1.setFont(new java.awt.Font("Trajan Pro", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Packages for your special occasions");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 659, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(81, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Booking Date:");

        btn_checkAvaiPackage.setBackground(new java.awt.Color(255, 204, 0));
        btn_checkAvaiPackage.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        btn_checkAvaiPackage.setForeground(new java.awt.Color(255, 255, 255));
        btn_checkAvaiPackage.setText("Check Available Packages");
        btn_checkAvaiPackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_checkAvaiPackageActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Search for Packages:");

        cbox_pColumns.setBackground(new java.awt.Color(255, 255, 255));
        cbox_pColumns.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        cbox_pColumns.setForeground(new java.awt.Color(0, 0, 0));
        cbox_pColumns.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbox_pColumnsItemStateChanged(evt);
            }
        });

        cbox_pOptions.setBackground(new java.awt.Color(255, 255, 255));
        cbox_pOptions.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        cbox_pOptions.setForeground(new java.awt.Color(0, 0, 0));

        btn_searchPackage.setBackground(new java.awt.Color(0, 0, 0));
        btn_searchPackage.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        btn_searchPackage.setForeground(new java.awt.Color(255, 255, 255));
        btn_searchPackage.setText("Search");
        btn_searchPackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchPackageActionPerformed(evt);
            }
        });

        table_packages.setBackground(new java.awt.Color(255, 255, 255));
        table_packages.setForeground(new java.awt.Color(0, 0, 0));
        table_packages.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table_packages.setSelectionBackground(new java.awt.Color(5, 124, 124));
        table_packages.setSelectionForeground(new java.awt.Color(255, 255, 255));
        table_packages.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_packagesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table_packages);

        lbl_packageImage.setBackground(new java.awt.Color(255, 255, 255));
        lbl_packageImage.setForeground(new java.awt.Color(0, 0, 0));
        lbl_packageImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_packageImage.setText("Package Image");
        lbl_packageImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtArea_details.setBackground(new java.awt.Color(255, 255, 255));
        txtArea_details.setColumns(20);
        txtArea_details.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        txtArea_details.setForeground(new java.awt.Color(0, 0, 0));
        txtArea_details.setRows(5);
        txtArea_details.setText("Details");
        jScrollPane2.setViewportView(txtArea_details);

        btn_bookPackage.setBackground(new java.awt.Color(255, 204, 0));
        btn_bookPackage.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        btn_bookPackage.setForeground(new java.awt.Color(255, 255, 255));
        btn_bookPackage.setText("Book Now");
        btn_bookPackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_bookPackageActionPerformed(evt);
            }
        });

        btn_viewBPList.setBackground(new java.awt.Color(0, 0, 0));
        btn_viewBPList.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        btn_viewBPList.setForeground(new java.awt.Color(255, 255, 255));
        btn_viewBPList.setText("View Your List");
        btn_viewBPList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_viewBPListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbox_pColumns, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jdc_packageAvaiDate, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btn_checkAvaiPackage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cbox_pOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(btn_searchPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_packageImage, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                            .addComponent(btn_bookPackage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_viewBPList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jdc_packageAvaiDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn_checkAvaiPackage, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cbox_pColumns)
                                    .addComponent(cbox_pOptions, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btn_searchPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lbl_packageImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_bookPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_viewBPList, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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

    private void cbox_pColumnsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbox_pColumnsItemStateChanged
        // TODO add your handling code here:
        String columnName = cbox_pColumns.getSelectedItem().toString();
        try{
            cbox_pOptions.removeAllItems();
            Connection con = connect();
            String sql = "select distinct " + columnName + " from packages";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String data = rs.getString(columnName);
                cbox_pOptions.addItem(data);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_cbox_pColumnsItemStateChanged

    private void btn_checkAvaiPackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_checkAvaiPackageActionPerformed
        // TODO add your handling code here:
        clearFields();
        Date getSelectedDate = jdc_packageAvaiDate.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(getSelectedDate);
        if(getSelectedDate == null){
            JOptionPane.showMessageDialog(null, "Please Choose Booking Date for Packages", "Choose A Date", JOptionPane.WARNING_MESSAGE);
        }
        else{
            try{
                displayAfterCheck(checkBookedRow(result));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_checkAvaiPackageActionPerformed

    private void table_packagesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_packagesMouseClicked
        // TODO add your handling code here:
        TableModel model = table_packages.getModel();
        int index = table_packages.getSelectedRow();
        String packageId = model.getValueAt(index, 0).toString();
        String sql = "select * from packages where package_id = ?";
        try{
            Connection con = connect();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, packageId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String imageName = rs.getString("image_name");
                ImageIcon icon = new ImageIcon(getClass().getResource("/ImagesandIcons/" + imageName));
                icon = new ImageIcon(icon.getImage().getScaledInstance(450, 500, Image.SCALE_SMOOTH));
                lbl_packageImage.setText("");
                lbl_packageImage.setIcon(icon);
                
                txtArea_details.setText("Details:" +
                        "\nPackage Name: \n" + rs.getString("package_name") + 
                        "\n\nPackage Type: \n" + rs.getString("package_type") +
                        "\n\nPackage Price: \n" + rs.getString("package_price") + " per Day" +
                        "\n\nServices: \n" + rs.getString("services"));
            }
        }
        catch(Exception e){
            
        }
    }//GEN-LAST:event_table_packagesMouseClicked

    private void btn_searchPackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchPackageActionPerformed
        // TODO add your handling code here:
        clearFields();
        String columnName = cbox_pColumns.getSelectedItem().toString();
        String data = cbox_pOptions.getSelectedItem().toString();
        Date bookingDate = jdc_packageAvaiDate.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = format.format(bookingDate);
        try{
            Connection con = connect();
            String sql = null;
            if(columnName.equals("package_type")){
                sql = "select package_id, package_name, package_type, package_price, services from packages where " + columnName + " = '" + data + "' " +
                "AND package_id NOT IN " +
                "(select distinct package_id from package_bookings where booking_date = ?)" +
                "AND package_id NOT IN " +        
                "(select distinct package_id from p_reserved_id where reserve_date = ?)";
            }
            else{
                sql = "select package_id, package_name, package_type, package_price, services from packages where " + columnName + " = " + data + " " +
                "AND package_id NOT IN " +
                "(select distinct package_id from package_bookings where booking_date = ?)" +
                "AND package_id NOT IN " +        
                "(select distinct package_id from p_reserved_id where reserve_date = ?)";
            }
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, formatted);
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
            
            table_packages.setModel(model);
            
            if (model.getRowCount() == 0){
                JOptionPane.showMessageDialog(null, """
                                                    Sorry, the Package you are looking for is not at our hotel!
                                                    Please contact the reception or try to search again after checking the data you are selecting.
                                                    Thank for your understanding!""", "Sorry", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_btn_searchPackageActionPerformed

    private void btn_bookPackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_bookPackageActionPerformed
        // TODO add your handling code here:
        TableModel model = table_packages.getModel();
        int index = table_packages.getSelectedRow();
        String packageId = model.getValueAt(index, 0).toString();
        Date bookingDate = jdc_packageAvaiDate.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = format.format(bookingDate);
        if(table_packages.getSelectedRowCount() == 0){
            JOptionPane.showMessageDialog(null, "Please Choose a Package to add to the Booking List", "No Package Selected", JOptionPane.WARNING_MESSAGE);
        }
        else{
            String date = "some date";
            JTextField textField = new JTextField();
            Object[] message = {"Enter the people count: ", textField};
            int result = JOptionPane.showConfirmDialog(null, message, "People Count", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(result == JOptionPane.OK_OPTION){
                int peopleCount = Integer.parseInt(textField.getText());
                try{
                    Connection con = connect();
                    String sql = "select booking_date from reserved_temp where package_id = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, packageId);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()){
                        date = rs.getString("booking_date");
                    }

                    if(date.equals(formatted)){
                        JOptionPane.showMessageDialog(null, "This package has already added to your list.\nPlease choose another available package", "Already Added", JOptionPane.WARNING_MESSAGE);
                    }
                    else{
                        try{
                            String sql1 = "insert into reserved_temp(booking_date, package_id, people_count) values (?, ?, ?)";
                            PreparedStatement ps1 = con.prepareStatement(sql1);
                            ps1.setString(1, formatted);
                            ps1.setString(2, packageId);
                            ps1.setInt(3, peopleCount);
                            ps1.execute();
                            JOptionPane.showMessageDialog(null, "Package has been added to the booking list.", "Added to the list", JOptionPane.INFORMATION_MESSAGE);
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
            else{
                JOptionPane.showMessageDialog(null, "People Count must be provided.", "Provide People Count", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_bookPackageActionPerformed

    private void btn_viewBPListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_viewBPListActionPerformed
        // TODO add your handling code here:
        ListedPackageInfo LPI = new ListedPackageInfo();
        LPI.setVisible(true);
        this.hide();
    }//GEN-LAST:event_btn_viewBPListActionPerformed

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
            java.util.logging.Logger.getLogger(ViewPackages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewPackages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewPackages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewPackages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewPackages().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_bookPackage;
    private javax.swing.JButton btn_checkAvaiPackage;
    private javax.swing.JButton btn_searchPackage;
    private javax.swing.JButton btn_viewBPList;
    private javax.swing.JComboBox<String> cbox_pColumns;
    private javax.swing.JComboBox<String> cbox_pOptions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser jdc_packageAvaiDate;
    private javax.swing.JLabel lbl_packageImage;
    private javax.swing.JTable table_packages;
    private javax.swing.JTextArea txtArea_details;
    // End of variables declaration//GEN-END:variables
}
