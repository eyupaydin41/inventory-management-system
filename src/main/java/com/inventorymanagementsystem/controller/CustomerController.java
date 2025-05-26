package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.entity.Customer;
import com.inventorymanagementsystem.service.CustomerService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.net.URL;
import java.sql.Connection;
import java.util.Comparator;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

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

    private SalesController salesController;
    private final CustomerService customerService=new CustomerService();

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
            if (salesController != null) {
                salesController.showSalesData();
            }
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
            if (salesController != null) {
                salesController.showSalesData();
            }
        }

        if (customerService.deleteCustomer(selectedCustomerId, selectedCustomerPhone)) {
            showCustomerData();
            customerClearData();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Message", "No data present in the customer table.");
        }
    }

    public void customerClearData(){
        cust_field_name.setText("");
        cust_field_phone.setText("");
    }

    public void printCustomersDetails(){
        Connection connection= Database.getInstance().connectDB();
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

    public void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setSalesController(SalesController salesController) {
        this.salesController = salesController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showCustomerData();
    }
}
