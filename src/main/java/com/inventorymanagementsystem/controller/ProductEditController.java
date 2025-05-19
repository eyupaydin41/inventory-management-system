package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.entity.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

public class ProductEditController {

    public AnchorPane product_edit_anchor_pane;
    public Button btn_close;
    public Button btn_save_product;
    @FXML private TextField itemNumberField;
    @FXML private TextField itemGroupField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private TextField discountField;

    private Stage dialogStage;
    private Product product;  // Product model sınıfın
    private boolean saveClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setProduct(Product product) {
        this.product = product;
        itemNumberField.setText(product.getItemNumber());
        itemGroupField.setText(product.getItemGroup());
        quantityField.setText(String.valueOf(product.getQuantity()));
        priceField.setText(String.valueOf(product.getPrice()));
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        product.setItemNumber(itemNumberField.getText());
        product.setItemGroup(itemGroupField.getText());
        product.setQuantity(Integer.parseInt(quantityField.getText()));
        product.setPrice(Double.parseDouble(priceField.getText()));

        saveClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }


    @FXML
    private void onExit() {
        Stage stage = (Stage) product_edit_anchor_pane.getScene().getWindow();
        stage.close();
    }
}
