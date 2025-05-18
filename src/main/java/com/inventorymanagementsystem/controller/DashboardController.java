package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.entity.*;
import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.service.*;
import com.inventorymanagementsystem.utils.MonthUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.burningwave.core.assembler.StaticComponentContainer.Modules;

public class DashboardController implements Initializable {
    private double x;
    private double y;

    @FXML
    private Button billing_btn;

    @FXML
    private AnchorPane billing_pane;

    @FXML
    private Button customer_btn;

    @FXML
    private Button dashboard_btn;

    @FXML
    private AnchorPane customer_pane;

    @FXML
    private AnchorPane dasboard_pane;


    @FXML
    private Button purchase_btn;

    @FXML
    private AnchorPane purchase_pane;

    @FXML
    private Button sales_btn;

    @FXML
    private AnchorPane sales_pane;

    @FXML
    private Label user;

    @FXML
    private Label inv_num;

    private Connection connection;

    private Statement statement;

    private PreparedStatement preparedStatement;

    private ResultSet resultSet;

    @FXML
    private Button bill_add;

    @FXML
    private Button bill_clear;

    @FXML
    private DatePicker bill_date;

    @FXML
    private TextField bill_item;

    @FXML
    private TextField bill_name;

    @FXML
    private TextField bill_phone;

    @FXML
    private TextField bill_price;

    @FXML
    private Button bill_print;

    @FXML
    private ComboBox<?> bill_quantity;

    @FXML
    private Button bill_save;

    @FXML
    private TextField bill_total_amount;

    @FXML
    private TableView<Billing> billing_table;

    @FXML
    private TextField billing_table_search;

    @FXML
    private Label final_amount;

    private String quantityList[]={"1","2","3","4","5","6","7","8","9","10"};

    @FXML
    private TableColumn<?, ?> col_bill_item_num;

    @FXML
    private TableColumn<?, ?> col_bill_price;

    @FXML
    private TableColumn<?, ?> col_bill_quantity;

    @FXML
    private TableColumn<?, ?> col_bill_total_amt;

    @FXML
    private Button cust_btn_add;

    @FXML
    private Button cust_btn_delete;

    @FXML
    private Button cust_btn_edit;

    @FXML
    private TableColumn<?, ?> cust_col_id;

    @FXML
    private TableColumn<?, ?> cust_col_name;

    @FXML
    private TableColumn<?, ?> cust_col_phone;

    @FXML
    private TextField cust_field_name;

    @FXML
    private TextField cust_field_phone;

    @FXML
    private TextField customer_search;

    @FXML
    private TableView<Customer> customer_table;

    @FXML
    private TableColumn<?, ?> sales_col_cust_name;

    @FXML
    private TableColumn<?, ?> sales_col_date_of_sales;

    @FXML
    private TableColumn<?, ?> sales_col_id;

    @FXML
    private TableColumn<?, ?> sales_col_inv_num;

    @FXML
    private TableColumn<?, ?> sales_col_quantity;

    @FXML
    private TableColumn<?, ?> sales_col_total_amount;

    @FXML
    private TableColumn<?, ?> sales_col_price;

    @FXML
    private TableColumn<?, ?> sales_col_item_num;

    @FXML
    private TableView<Sales> sales_table;

    @FXML
    private Label sales_total_amount;

    @FXML
    private Button purchase_btn_add;

    @FXML
    private Button purchase_btn_print;

    @FXML
    private Label purchase_total_amount;

    @FXML
    private TableColumn<?, ?> purchase_col_date_of_purchase;

    @FXML
    private TableColumn<?, ?> purchase_col_id;

    @FXML
    private TableColumn<?, ?> purchase_col_invoice;

    @FXML
    private TableColumn<?, ?> purchase_col_shop_details;

    @FXML
    private TableColumn<?, ?> purchase_col_total_amount;

    @FXML
    private TableColumn<?, ?> purchase_col_total_items;

    @FXML
    private TableView<Purchase> purchase_table;

    @FXML
    private Label dash_total_items_sold_this_month;

    @FXML
    private Label dash_total_purchase;

    @FXML
    private Label dash_total_sales_items_this_month_name;

    @FXML
    private Label dash_total_sales_this_month;

    @FXML
    private Label dash_total_sales_this_month_name;

    @FXML
    private Label dash_total_sold;

    @FXML
    private Label dash_total_stocks;

    @FXML
    private Button signout_btn;

    @FXML
    private TextField purchase_name;

    @FXML
    private TextField purchase_details;

    @FXML
    private TextField purchase_price;

    @FXML
    private TextField purchase_totalamount;

    @FXML
    private ComboBox<?> purchase_quantity;

    @FXML
    private DatePicker purchase_date;

    private final ProductService productService = new ProductService();
    private final SalesService salesService = new SalesService();
    private final BillingService billingService = new BillingService();
    private final CustomerService customerService = new CustomerService();
    private final ReportService reportService = new ReportService();
    private final PurchaseService purchaseService = new PurchaseService();
    private final String monthInTurkish = MonthUtils.translate(LocalDate.now().getMonth());

    public void onExit(){
        System.exit(0);
    }

    public void activateAnchorPane() {
        Button[] btnList = {dashboard_btn, billing_btn, customer_btn, sales_btn, purchase_btn};
        AnchorPane[] panes = {dasboard_pane, billing_pane, customer_pane, sales_pane, purchase_pane};

        for (int i = 0; i < btnList.length; i++) {
            final int index = i;
            btnList[i].setOnMouseClicked(mouseEvent -> updateUI(index, btnList, panes));
        }
    }

    private void updateUI(int index, Button[] btnList, AnchorPane[] panes) {
        for (int i = 0; i < panes.length; i++) {
            panes[i].setVisible(i == index);
        }

        for (int i = 0; i < btnList.length; i++) {
            String style = (i == index)
                    ? "-fx-background-color:linear-gradient(to bottom right , #21965a 0%, #00b2c2 100%)"
                    : "-fx-background-color:transparent";
            btnList[i].setStyle(style);
        }
    }

    public void setUsername(){
        user.setText(User.name.toUpperCase());
    }

    public void activateDashboard(){
        dasboard_pane.setVisible(true);
        billing_pane.setVisible(false);
        customer_pane.setVisible(false);
        sales_pane.setVisible(false);
        purchase_pane.setVisible(false);
    }

    public void setInvoiceNum(){
        String nextInvoice = salesService.generateNextInvoiceNumber();
        Invoice.billingInvoiceNumber = nextInvoice;
        inv_num.setText(nextInvoice);
    }

    public void setAutoCompleteItemNumber() {
        List<String> itemNumberList = productService.getAllItemNumbers();
        ObservableList<String> observableItemList = FXCollections.observableArrayList(itemNumberList);
        TextFields.bindAutoCompletion(bill_item, observableItemList);
    }

    public void comboBoxQuantity(){
        List<String> list=new ArrayList<>();
        for(String quantity:quantityList){
            list.add(quantity);
        }
        ObservableList comboList= FXCollections.observableArrayList(list);
        bill_quantity.setItems(comboList);
        purchase_quantity.setItems(comboList);
    }

    public void checkForPriceandQuantity(){
        if(!bill_price.getText().isBlank() && !bill_quantity.getSelectionModel().isEmpty()){
            bill_total_amount.setText(String.valueOf(Integer.parseInt(bill_price.getText())*Integer.parseInt(bill_quantity.getValue().toString())));
        }else{
            bill_total_amount.setText("0");
        }
    }

    public void checkPurchaseForPriceandQuantity(){
        if(!purchase_price.getText().isBlank()&& !purchase_quantity.getSelectionModel().isEmpty()){
            purchase_totalamount.setText(String.valueOf(Integer.parseInt(purchase_price.getText())*Integer.parseInt(purchase_quantity.getValue().toString())));
        }else{
            purchase_totalamount.setText("0");
        }
    }

    public void getPriceOfTheItem() {
        String itemNumber = bill_item.getText();
        Optional<Product> productOpt = productService.getProductByItemNumber(itemNumber);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            bill_price.setText(String.valueOf((int) product.getPrice()));
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Message", "Product not found with Item Number: " + itemNumber);
        }
    }

    public void onInputTextChanged(){
        bill_price.setOnKeyReleased(event-> checkForPriceandQuantity());
        bill_price.setOnKeyPressed(event-> checkForPriceandQuantity());
        bill_price.setOnKeyTyped(event-> checkForPriceandQuantity());
        bill_quantity.setOnAction(actionEvent -> checkForPriceandQuantity());
        bill_item.setOnKeyPressed(actionEvent ->{
            if(actionEvent.getCode().equals(KeyCode.ENTER)) {
                getPriceOfTheItem();
            }
        });
    }

    public void onPurchaseInputTextChanged(){
        purchase_price.setOnKeyReleased(event-> checkPurchaseForPriceandQuantity());
        purchase_price.setOnKeyPressed(event-> checkPurchaseForPriceandQuantity());
        purchase_price.setOnKeyTyped(event-> checkPurchaseForPriceandQuantity());
        purchase_quantity.setOnAction(actionEvent -> checkPurchaseForPriceandQuantity());
    }

    public void addBillingData() {
        if (bill_item.getText().isBlank() || bill_quantity.getSelectionModel().isEmpty() ||
                bill_price.getText().isBlank() || bill_total_amount.getText().isBlank()) {
            showAlert(Alert.AlertType.INFORMATION, "Message", "Please fill the mandatory data such as item number, quantity, and price.");
            return;
        }

        String itemNumber = bill_item.getText();
        int quantity = Integer.parseInt(bill_quantity.getValue().toString());
        int price = Integer.parseInt(bill_price.getText());
        int totalAmount = Integer.parseInt(bill_total_amount.getText());

        boolean success = billingService.addBillingData(itemNumber, quantity, price, totalAmount);

        if (success) {
            showBillingData();
            billClearData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Failed to save billing data.");
        }
    }

    public void showBillingData(){
        ObservableList<Billing> billingList=billingService.getAllBillingData();
        col_bill_item_num.setCellValueFactory(new PropertyValueFactory<>("item_number"));
        col_bill_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        col_bill_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_bill_total_amt.setCellValueFactory(new PropertyValueFactory<>("total_amount"));

        billing_table.setItems(billingList);
        LocalDate date=LocalDate.now();
        bill_date.setValue(date);
        if(!billingList.isEmpty()){
            double totalAmount = billingService.calculateTotalBillingAmount();
            final_amount.setText(String.format("%.2f", totalAmount));
        }else{
            final_amount.setText("0.00");
        }
    }

    public void billClearCustomerData(){
        bill_name.setText("");
        bill_phone.setText("");
    }

    public void billClearData(){
        bill_item.clear();
        bill_quantity.setValue(null);
        bill_price.setText("");
        bill_total_amount.setText("");
    }

    public void selectBillingTableData(){
        int num=billing_table.getSelectionModel().getSelectedIndex();
        Billing billingData=billing_table.getSelectionModel().getSelectedItem();
        if(num-1 < -1){
            return;
        }
        bill_item.setText(billingData.getItem_number());
        bill_price.setText(String.valueOf((int)billingData.getPrice()));
        bill_total_amount.setText(String.valueOf((int)billingData.getTotal_amount()));
    }

    public void updateSelectedBillingData() {
        if (bill_quantity.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Please select a quantity.");
            return;
        }

        try {
            int quantity = Integer.parseInt(bill_quantity.getValue().toString());
            double price = Double.parseDouble(bill_price.getText());
            double totalAmount = Double.parseDouble(bill_total_amount.getText());
            String itemNumber = bill_item.getText();

            boolean success = billingService.updateBilling(itemNumber, quantity, price, totalAmount);

            if (success) {
                showBillingData();
                billClearData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error Message", "Update failed. Please check the data.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Please enter valid numeric values for quantity, price, and total amount.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void deleteBillingData() {
        String itemNumber = null;

        if (!billing_table.getSelectionModel().isEmpty()) {
            itemNumber = billing_table.getSelectionModel().getSelectedItem().getItem_number();
        }

        boolean success = billingService.deleteBillingData(itemNumber);

        if (success) {
            showBillingData();
            billClearData();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Message", "No data present in the billing table.");
        }
    }

    public boolean saveCustomerDetails() {
        String name = bill_name.getText();
        String phoneNumber = bill_phone.getText();

        boolean success = customerService.saveCustomerDetails(name, phoneNumber);

        if (success) {
            showCustomerData(); // Show the updated customer data if saved
            return true;
        }
        return false;
    }

    public void saveInvoiceDetails(){
        boolean success = salesService.saveInvoiceDetails(
                bill_phone.getText(),
                inv_num.getText(),
                bill_date.getValue()
        );

        if (success) {
            billClearCustomerData();
            deleteBillingData();
            showSalesData();
            setInvoiceNum();
            showDashboardData();
            showAlert(Alert.AlertType.INFORMATION, "Message", "Data is successfully saved.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save invoice.");
        }
    }

    public void billSave(){
        if(!saveCustomerDetails()) {
            return;
        }
        saveInvoiceDetails();
    }

    public void printBill() {
        String latestInvoiceNumber = salesService.getLatestInvoiceNumber(); // En son fatura numarasını çek
        if (latestInvoiceNumber != null) {
            reportService.generateInvoiceReport(latestInvoiceNumber);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Bilgi","Fatura Bulunamadı");
        }
    }

    public void searchForBills(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("bills.fxml"));
            Scene scene = new Scene(root);
            Stage stage=new Stage();
            root.setOnMousePressed((event)->{
                x=event.getSceneX();
                y=event.getSceneY();
            });
            root.setOnMouseDragged((event)->{
                stage.setX(event.getScreenX()-x);
                stage.setY(event.getScreenY()-y);
            });
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        }catch (Exception err){
            showAlert(Alert.AlertType.ERROR, "Error Message", err.getMessage());
        }
    }

    public void customerClearData(){
        cust_field_name.setText("");
        cust_field_phone.setText("");
    }

    public void showCustomerData(){
        ObservableList<Customer> customerList=customerService.listCustomerData().sorted(new Comparator<Customer>() {
            @Override
            public int compare(Customer o1, Customer o2) {
                return Integer.compare(o1.getId(),o2.getId());
            }
        });

        cust_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        cust_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        cust_col_phone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customer_table.setItems(customerList);
    }

    public void addCustomerData() {
        String name = cust_field_name.getText();
        String phone = cust_field_phone.getText();

        if (!customerService.isCustomerPhoneAvailable(phone)) {
            showAlert(Alert.AlertType.INFORMATION, "Message", "Customer already present in the customer table.");
            return;
        }

        if (customerService.addCustomer(name, phone)) {
            showCustomerData();
            customerClearData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Failed to save customer data.");
        }
    }

    public void selectCustomerTableData(){
        int num=customer_table.getSelectionModel().getSelectedIndex();
        Customer customerData=customer_table.getSelectionModel().getSelectedItem();
        if(num-1 < -1){
            return;
        }

        cust_field_name.setText(customerData.getName());
        cust_field_phone.setText(customerData.getPhoneNumber());
    }

    public void updateCustomerData() {
        String name = cust_field_name.getText();
        String phone = cust_field_phone.getText();

        if (name.isBlank() || phone.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Please fill the mandatory data such as name, phone number.");
            return;
        }

        if (customerService.updateCustomer(name, phone)) {
            showCustomerData();
            customerClearData();
            showSalesData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Failed to update customer data.");
        }
    }

    public void deleteCustomerData() {
        if (customer_table.getSelectionModel().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Message", "Please select customer for deletion.");
            return;
        }

        int selectedCustomerId = customer_table.getSelectionModel().getSelectedItem().getId();
        String selectedCustomerPhone = customer_table.getSelectionModel().getSelectedItem().getPhoneNumber();

        if (customerService.deleteCustomerFromSales(selectedCustomerId)) {
            showSalesData();
        }

        if (customerService.deleteCustomer(selectedCustomerId, selectedCustomerPhone)) {
            showCustomerData();
            customerClearData();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Message", "No data present in the customer table.");
        }
    }

    public void printCustomersDetails(){
        connection=Database.getInstance().connectDB();
        String sql="SELECT * FROM customers";
        try{
            JasperDesign jasperDesign= JRXmlLoader.load(this.getClass().getClassLoader().getResourceAsStream("jasper-reports/customers.jrxml"));
            JRDesignQuery updateQuery=new JRDesignQuery();
            updateQuery.setText(sql);
            jasperDesign.setQuery(updateQuery);
            JasperReport jasperReport= JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,null,connection);
            JasperViewer.viewReport(jasperPrint ,false);
        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public void getTotalSalesAmount() {
        try {
            String totalSalesAmount = salesService.getTotalSalesAmount();
            sales_total_amount.setText(totalSalesAmount);
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Error Message", e.getMessage());
        }
    }

    public void showSalesData(){
        ObservableList<Sales> salesList= salesService.listSalesData();
        sales_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        sales_col_inv_num.setCellValueFactory(new PropertyValueFactory<>("inv_num"));
        sales_col_cust_name.setCellValueFactory(new PropertyValueFactory<>("custName"));
        sales_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        sales_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sales_col_total_amount.setCellValueFactory(new PropertyValueFactory<>("total_amount"));
        sales_col_date_of_sales.setCellValueFactory(new PropertyValueFactory<>("date"));
        sales_col_item_num.setCellValueFactory(new PropertyValueFactory<>("item_num"));
        sales_table.setItems(salesList);

        getTotalSalesAmount();
    }

    public void printSalesDetails(){
        connection=Database.getInstance().connectDB();
        String sql="SELECT * FROM sales s INNER JOIN customers c ON s.cust_id=c.id";
        try{
            JasperDesign jasperDesign= JRXmlLoader.load(this.getClass().getClassLoader().getResourceAsStream("jasper-reports/sales_report.jrxml"));
            JRDesignQuery updateQuery=new JRDesignQuery();
            updateQuery.setText(sql);
            jasperDesign.setQuery(updateQuery);
            JasperReport jasperReport= JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,null,connection);
            JasperViewer.viewReport(jasperPrint ,false);
        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public void printPurchaseDetails(){
        connection=Database.getInstance().connectDB();
        String sql="SELECT * FROM purchase";
        try{
            JasperDesign jasperDesign= JRXmlLoader.load(this.getClass().getClassLoader().getResourceAsStream("jasper-reports/purchase_report.jrxml"));
            JRDesignQuery updateQuery=new JRDesignQuery();
            updateQuery.setText(sql);
            jasperDesign.setQuery(updateQuery);
            JasperReport jasperReport= JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,null,connection);
            JasperViewer.viewReport(jasperPrint ,false);
        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public void showPurchaseData(){
        String totalAmount = purchaseService.getTotalPurchaseAmount();
        ObservableList<Purchase> purchaseList= purchaseService.listPurchaseData();
        purchase_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        purchase_col_invoice.setCellValueFactory(new PropertyValueFactory<>("invoice"));
        purchase_col_shop_details.setCellValueFactory(new PropertyValueFactory<>("shopDetails"));
        purchase_col_total_items.setCellValueFactory(new PropertyValueFactory<>("totalItems"));
        purchase_col_total_amount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        purchase_col_date_of_purchase.setCellValueFactory(new PropertyValueFactory<>("dateOfPurchase"));
        purchase_table.setItems(purchaseList);
        purchase_total_amount.setText(totalAmount);
        LocalDate date=LocalDate.now();
        purchase_date.setValue(date);
    }

    public void getTotalStocks(){
        int totalPurchase=Integer.parseInt(dash_total_purchase.getText());
        int total_sold= Integer.parseInt(dash_total_sold.getText());
        int totalStockLeft=totalPurchase-total_sold;
        dash_total_stocks.setText(String.valueOf(totalStockLeft));
    }

    public void addPurchaseData() {
        if (purchase_name.getText().isBlank()
                || purchase_quantity.getSelectionModel().isEmpty()
                || purchase_price.getText().isBlank()
                || purchase_details.getText().isBlank()) {

            showAlert(Alert.AlertType.INFORMATION, "Message", "Please fill the mandatory data such as item number, quantity and price.");
            return;
        }

        try {
            String invoice = purchase_name.getText();
            String shopAndAddress = purchase_details.getText();
            int quantity = Integer.parseInt(purchase_quantity.getValue().toString());
            double price = Double.parseDouble(purchase_price.getText());
            LocalDate date = purchase_date.getValue();

            boolean success = purchaseService.addPurchase(invoice, shopAndAddress, quantity, price, date);
            if (success) {
                purchase_total_amount.setText(String.valueOf(quantity * price));
                showPurchaseData();
                purchaseClearData();
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Message", "Failed to add purchase record.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numbers for quantity and price.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    public void deletePurchaseData() {
        if (purchase_table.getSelectionModel().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Message", "No purchase selected to delete.");
            return;
        }

        int selectedId = purchase_table.getSelectionModel().getSelectedItem().getId();
        PurchaseService purchaseService = new PurchaseService();

        try {
            boolean deleted = purchaseService.deletePurchaseById(selectedId);
            if (deleted) {
                showPurchaseData();
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Message", "No data present in the billing table.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    public void purchaseClearData(){
        purchase_name.clear();
        purchase_details.clear();
        purchase_quantity.setValue(null);
        purchase_price.clear();
        purchase_totalamount.clear();
        LocalDate date=LocalDate.now();
        purchase_date.setValue(date);
    }

    public void showDashboardData(){
        dash_total_purchase.setText(purchaseService.getTotalPurchasedItemCount());
        dash_total_sold.setText(salesService.getTotalSoldQuantity());
        dash_total_sales_this_month.setText(String.format("%.2f", salesService.getTotalSalesThisMonth()));
        dash_total_sales_this_month_name.setText(monthInTurkish);
        dash_total_items_sold_this_month.setText(String.valueOf(salesService.getTotalItemsSoldThisMonth()));
        dash_total_sales_items_this_month_name.setText(monthInTurkish);
        getTotalStocks();
    }
    public void signOut(){
        signout_btn.getScene().getWindow().hide();
        try{
        Parent root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(root);
        Stage stage=new Stage();
            root.setOnMousePressed((event)->{
                x=event.getSceneX();
                y=event.getSceneY();
            });
            root.setOnMouseDragged((event)->{
                stage.setX(event.getScreenX()-x);
                stage.setY(event.getScreenY()-y);
            });

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        }catch (Exception err){
            showAlert(Alert.AlertType.ERROR, "Error Message", err.getMessage());
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Exports all modules to other modules
        Modules.exportAllToAll();

        setUsername();
        activateDashboard();

//      DASHBOARD PANE
        showDashboardData();

//      BILLING PANE
        setAutoCompleteItemNumber();
        comboBoxQuantity();
        setInvoiceNum();
        showBillingData();

//      CUSTOMER PANE
        showCustomerData();

//      SALES PANE
        showSalesData();

//      Purchase Pane
        showPurchaseData();
    }
}
