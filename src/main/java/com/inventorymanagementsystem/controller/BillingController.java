package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.entity.Billing;
import com.inventorymanagementsystem.entity.Product;
import com.inventorymanagementsystem.memento.BillingFormMemento;
import com.inventorymanagementsystem.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BillingController implements Initializable {
    private double x;
    private double y;

    @FXML
    private AnchorPane billing_pane;

    @FXML
    private Label inv_num;

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
    private ComboBox<String> bill_quantity;

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

    @FXML
    private TableColumn<?, ?> col_bill_item_num;

    @FXML
    private TableColumn<?, ?> col_bill_price;

    @FXML
    private TableColumn<?, ?> col_bill_quantity;

    @FXML
    private TableColumn<?, ?> col_bill_total_amt;

    @FXML
    private Button bill_undo;

    private final ProductService productService = new ProductService();
    private final BillingService billingService = new BillingService();
    private final SalesService salesService = new SalesService();
    private final ReportService reportService = new ReportService();
    private final CustomerService customerService = new CustomerService();
    private SalesController salesController;
    private CustomerController customerController;
    private StockController stockController;

    private Deque<BillingFormMemento> history = new LinkedList<>();

    private void saveState() {
        history.push(new BillingFormMemento(
                bill_item.getText(),
                bill_quantity.getValue() == null ? "" : bill_quantity.getValue(),
                bill_price.getText(),
                bill_total_amount.getText(),
                bill_name.getText(),
                bill_phone.getText(),
                bill_date.getValue()
        ));
    }

    @FXML
    private void undoClear() {
        if (history.isEmpty()) return;
        BillingFormMemento m = history.pop();
        bill_item.setText(m.getItem());
        bill_quantity.setValue(m.getQuantity());
        bill_price.setText(m.getPrice());
        bill_total_amount.setText(m.getTotalAmount());
        bill_name.setText(m.getCustomerName());
        bill_phone.setText(m.getCustomerPhone());
        bill_date.setValue(m.getDate());
    }


    public void setInvoiceNum(String nextInvoice){
        inv_num.setText(nextInvoice);
    }

    public void setAutoCompleteItemNumber() {
        List<String> itemNumberList = productService.getAllItemNumbers();
        ContextMenu suggestions = new ContextMenu();

        bill_item.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                suggestions.hide();
            } else {
                List<String> filtered = itemNumberList.stream()
                        .filter(item -> item.toLowerCase().contains(newValue.toLowerCase()))
                        .limit(10)
                        .toList();

                if (!filtered.isEmpty()) {
                    List<CustomMenuItem> menuItems = new ArrayList<>();
                    for (String item : filtered) {
                        Label entryLabel = new Label(item);
                        CustomMenuItem menuItem = new CustomMenuItem(entryLabel, true);
                        menuItem.setOnAction(e -> {
                            bill_item.setText(item);
                            suggestions.hide();
                            getPriceOfTheItem();
                        });
                        menuItems.add(menuItem);
                    }
                    suggestions.getItems().setAll(menuItems);
                    if (!suggestions.isShowing()) {
                        suggestions.show(bill_item, Side.BOTTOM, 0, 0);
                    }
                } else {
                    suggestions.hide();
                }
            }
        });

        bill_item.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                suggestions.hide();
            }
        });
    }

    public void comboBoxQuantity() {
        ObservableList<String> comboList = FXCollections.observableArrayList(
                IntStream.rangeClosed(1, 10)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.toList())
        );
        bill_quantity.setItems(comboList);
    }

    public void checkForPriceandQuantity(){
        if(!bill_price.getText().isBlank() && !bill_quantity.getSelectionModel().isEmpty()){
            bill_total_amount.setText(String.valueOf(Integer.parseInt(bill_price.getText())*Integer.parseInt(bill_quantity.getValue().toString())));
        }else{
            bill_total_amount.setText("0");
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

    public void addBillingData() {
        if (bill_item.getText().isBlank() || bill_quantity.getSelectionModel().isEmpty() ||
                bill_price.getText().isBlank() || bill_total_amount.getText().isBlank()) {
            showAlert(Alert.AlertType.INFORMATION, "Message", "Please fill the mandatory data such as item number, quantity, and price.");
            return;
        }

        String itemNumber = bill_item.getText();
        if (productService.getProductByItemNumber(itemNumber).isEmpty()){
            showAlert(Alert.AlertType.INFORMATION, "Message", "Product not found with Item Number: " + itemNumber);
            return;
        }

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
        saveState();
        bill_item.clear();
        bill_quantity.setValue(null);
        bill_price.clear();
        bill_total_amount.clear();
        bill_name.clear();
        bill_phone.clear();
        bill_date.setValue(LocalDate.now());
    }


    public void printBill() {
        String latestInvoiceNumber = salesService.getLatestInvoiceNumber(); // En son fatura numarasını çek
        if (latestInvoiceNumber != null) {
            reportService.generateInvoiceReport(latestInvoiceNumber);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Bilgi","Fatura Bulunamadı");
        }
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
            customerController.showCustomerData();
            return true;
        }
        return false;
    }

    public void saveInvoiceDetails(){
        ObservableList<Billing> billedItems = billingService.getAllBillingData();
        for (Billing item : billedItems) {
            int currentStock = productService.getStockQuantity(item.getItem_number());
            if (currentStock < item.getQuantity()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Yetersiz stok: " + item.getItem_number());
                return;
            }
        }

        boolean success = salesService.saveInvoiceDetails(
                bill_phone.getText(),
                inv_num.getText(),
                bill_date.getValue()
        );

        if (success) {
            for (Billing item : billedItems) {
                boolean stockUpdated = productService.decreaseStock(item.getItem_number(), item.getQuantity());
                if (!stockUpdated) {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Stok güncellenemedi: " + item.getItem_number());
                }
            }

            billClearCustomerData();
            deleteBillingData();
            salesController.showSalesData();
            stockController.showProductData();

            setInvoiceNum(salesController.getInvoiceNum());
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

    public void searchForBills(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/com/inventorymanagementsystem/bills.fxml"));
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setSalesController(SalesController salesController){
        this.salesController = salesController;
        afterSalesControllerSet();
    }

    public void setCustomerController(CustomerController customerController){
        this.customerController = customerController;
    }

    public void setStockController(StockController stockController){
        this.stockController = stockController;
    }

    private void afterSalesControllerSet() {
        setInvoiceNum(salesController.getInvoiceNum());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAutoCompleteItemNumber();
        comboBoxQuantity();
        showBillingData();
        bill_undo.setOnAction(e -> undoClear());
    }
}
