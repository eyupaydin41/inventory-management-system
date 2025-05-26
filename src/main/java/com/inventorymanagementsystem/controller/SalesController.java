package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.entity.Invoice;
import com.inventorymanagementsystem.entity.Sales;
import com.inventorymanagementsystem.service.SalesService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
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
import java.util.ResourceBundle;

public class SalesController implements Initializable {
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
    private AnchorPane sales_pane;

    private final SalesService salesService = new SalesService();

    public String getInvoiceNum(){
        String nextInvoice = salesService.generateNextInvoiceNumber();
        Invoice.billingInvoiceNumber = nextInvoice;
        return nextInvoice;
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

    public void printSalesDetails() {
        Connection connection = Database.getInstance().connectDB();
        String sql = "SELECT * FROM sales s INNER JOIN customers c ON s.cust_id=c.id";
        try {
            JasperDesign jasperDesign = JRXmlLoader.load(this.getClass().getClassLoader().getResourceAsStream("jasper-reports/sales_report.jrxml"));
            JRDesignQuery updateQuery = new JRDesignQuery();
            updateQuery.setText(sql);
            jasperDesign.setQuery(updateQuery);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, connection);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showSalesData();
    }

    public AnchorPane getSales_pane() {
        return sales_pane;
    }

    public void setSales_pane(AnchorPane sales_pane) {
        this.sales_pane = sales_pane;
    }
}
