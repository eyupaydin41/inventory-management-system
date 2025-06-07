package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.entity.Purchase;
import com.inventorymanagementsystem.memento.PurchaseFormMemento;
import com.inventorymanagementsystem.service.PurchaseService;
import javafx.collections.FXCollections;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Deque;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PurchaseController implements Initializable {

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
    private Button purchase_btn_add;

    @FXML
    private Button purchase_btn_print;

    @FXML
    private Label purchase_total_amount;

    @FXML
    private DatePicker purchase_date;

    @FXML
    private TextField purchase_name;

    @FXML
    private TextField purchase_details;

    @FXML
    private TextField purchase_price;

    @FXML
    private TextField purchase_totalamount;

    @FXML
    private ComboBox<String> purchase_quantity;

    @FXML
    private Button purchase_undo;

    private final PurchaseService purchaseService = new PurchaseService();
    private StockController stockController;
    private BillingController billingController;
    private DashboardController dashboardController;

    private Deque<PurchaseFormMemento> history = new LinkedList<>();

    private void saveState() {
        history.push(new PurchaseFormMemento(
                purchase_name.getText(),
                purchase_details.getText(),
                purchase_quantity.getValue() == null ? "" : purchase_quantity.getValue(),
                purchase_price.getText(),
                purchase_totalamount.getText(),
                purchase_date.getValue()
        ));
    }

    @FXML
    private void undoClear() {
        if (history.isEmpty()) return;
        PurchaseFormMemento m = history.pop();
        purchase_name.setText(m.getInvoice());
        purchase_details.setText(m.getShopDetails());
        purchase_quantity.setValue(m.getQuantity());
        purchase_price.setText(m.getPrice());
        purchase_totalamount.setText(m.getTotalAmount());
        purchase_date.setValue(m.getDate());
    }

    public void comboBoxQuantity() {
        ObservableList<String> comboList = FXCollections.observableArrayList(
                IntStream.rangeClosed(1, 10)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.toList())
        );
        purchase_quantity.setItems(comboList);
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
                stockController.showProductData();
                dashboardController.showDashboardData();
                purchaseClearData();
                billingController.setAutoCompleteItemNumber();
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

    public void printPurchaseDetails(){
        Connection connection= Database.getInstance().connectDB();
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
        saveState();
        purchase_name.clear();
        purchase_details.clear();
        purchase_quantity.setValue(null);
        purchase_price.clear();
        purchase_totalamount.clear();
        LocalDate date=LocalDate.now();
        purchase_date.setValue(date);
    }

    public void checkPurchaseForPriceandQuantity(){
        if(!purchase_price.getText().isBlank()&& !purchase_quantity.getSelectionModel().isEmpty()){
            purchase_totalamount.setText(String.valueOf(Integer.parseInt(purchase_price.getText())*Integer.parseInt(purchase_quantity.getValue().toString())));
        }else{
            purchase_totalamount.setText("0");
        }
    }

    public void onPurchaseInputTextChanged(){
        purchase_price.setOnKeyReleased(event-> checkPurchaseForPriceandQuantity());
        purchase_price.setOnKeyPressed(event-> checkPurchaseForPriceandQuantity());
        purchase_price.setOnKeyTyped(event-> checkPurchaseForPriceandQuantity());
        purchase_quantity.setOnAction(actionEvent -> checkPurchaseForPriceandQuantity());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setStockController(StockController stockController) {
        this.stockController = stockController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showPurchaseData();
        comboBoxQuantity();
        purchase_undo.setOnAction(e -> undoClear());
    }

    public void setBillingController(BillingController billingController) {
        this.billingController = billingController;
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
}
