package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.service.LoginService;
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
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Label c_logo, f_logo;

    @FXML
    private Button login_btn;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private AnchorPane login_form;

    private final LoginService loginService = new LoginService();
    private double x, y;

    public void textfieldDesign() {
        if (username.isFocused()) {
            username.setStyle("-fx-background-color:#fff;" + "-fx-border-width:2px");
            password.setStyle("-fx-background-color:transparent;" + "-fx-border-width:1px");
        } else if (password.isFocused()) {
            username.setStyle("-fx-background-color:transparent;" + "-fx-border-width:1px");
            password.setStyle("-fx-background-color:#fff;" + "-fx-border-width:2px");
        }
    }

    public void onExit() {
        System.exit(0);
    }

    public void login() {
        String user = username.getText();
        String pass = password.getText();

        if (loginService.authenticate(user, pass)) {
            showAlert(Alert.AlertType.INFORMATION, "Success Message", "Login Successful !");
            openDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Wrong Username/Password");
        }
    }

    private void openDashboard() {
        try {
            login_btn.getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("/com/inventorymanagementsystem/dashboard.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });

            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
            });

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
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
    }
}
