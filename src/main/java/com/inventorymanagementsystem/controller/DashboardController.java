package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.entity.*;
import com.inventorymanagementsystem.service.*;
import com.inventorymanagementsystem.utils.MonthUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static org.burningwave.core.assembler.StaticComponentContainer.Modules;

public class DashboardController implements Initializable {
    private double x;
    private double y;

    @FXML
    private AnchorPane billing_pane;

    @FXML
    private AnchorPane sales_pane;

    @FXML
    private Button billing_btn;

    @FXML
    private Button customer_btn;

    @FXML
    private Button dashboard_btn;

    @FXML
    private Button stock_btn;

    @FXML
    private AnchorPane stock_pane;

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
    private Label user;

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
    private BillingController billing_paneController; // fx:id billing_pane için controller

    @FXML
    private SalesController sales_paneController; // fx:id sales_pane için controller

    @FXML
    private CustomerController customer_paneController;

    @FXML
    private PurchaseController purchase_paneController;

    @FXML
    private StockController stock_paneController;

    private final SalesService salesService = new SalesService();
    private final PurchaseService purchaseService = new PurchaseService();
    private final String monthInTurkish = MonthUtils.translate(LocalDate.now().getMonth());

    public void onExit(){
        System.exit(0);
    }

    public void activateAnchorPane() {
        Button[] btnList = {dashboard_btn, billing_btn, customer_btn, sales_btn, purchase_btn, stock_btn};
        AnchorPane[] panes = {dasboard_pane, billing_pane, customer_pane, sales_pane, purchase_pane, stock_pane};

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
        stock_pane.setVisible(false);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void getTotalStocks(){
        int totalPurchase=Integer.parseInt(dash_total_purchase.getText());
        int total_sold= Integer.parseInt(dash_total_sold.getText());
        int totalStockLeft=totalPurchase-total_sold;
        dash_total_stocks.setText(String.valueOf(totalStockLeft));
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
        billing_paneController.setSalesController(sales_paneController);
        billing_paneController.setCustomerController(customer_paneController);
        billing_paneController.setStockController(stock_paneController);
        customer_paneController.setSalesController(sales_paneController);
        purchase_paneController.setStockController(stock_paneController);
        purchase_paneController.setBillingController(billing_paneController);
        Modules.exportAllToAll();

        setUsername();
        activateDashboard();
        showDashboardData();
    }

}
