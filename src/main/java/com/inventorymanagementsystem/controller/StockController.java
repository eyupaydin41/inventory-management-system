package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.entity.Product;
import com.inventorymanagementsystem.service.ProductService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class StockController implements Initializable {

    @FXML
    private TableColumn<?, ?> product_col_id;

    @FXML
    private TableColumn<?, ?> product_col_item_num;

    @FXML
    private TableColumn<?, ?> product_col_shop_details;

    @FXML
    private TableColumn<?, ?> product_col_quantity;

    @FXML
    private TableColumn<?, ?> product_col_price;

    @FXML
    private TableView<Product> product_table;

    private final ProductService productService = new ProductService();

    public void showProductData(){
        ObservableList<Product> productList = productService.listProductData();

        product_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        product_col_item_num.setCellValueFactory(new PropertyValueFactory<>("itemNumber"));
        product_col_shop_details.setCellValueFactory(new PropertyValueFactory<>("ItemGroup"));
        product_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        product_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        product_table.setItems(productList);
    }


    @FXML
    private void handleEditProduct() {
        AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
        AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
        Product selectedProduct = product_table.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventorymanagementsystem/productEditDialog.fxml"));
                Parent page = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Ürün Düzenle");
                dialogStage.initStyle(StageStyle.UNDECORATED);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                page.setOnMousePressed(event -> {
                    xOffset.set(event.getSceneX());
                    yOffset.set(event.getSceneY());
                });

                page.setOnMouseDragged(event -> {
                    dialogStage.setX(event.getScreenX() - xOffset.get());
                    dialogStage.setY(event.getScreenY() - yOffset.get());
                });
                ProductEditController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setProduct(selectedProduct);

                dialogStage.showAndWait();

                if (controller.isSaveClicked()) {
                    productService.updateProduct(selectedProduct);
                    showProductData();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen düzenlenecek ürünü seçiniz.");
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

//      Stock Pane
        showProductData();
    }
}
