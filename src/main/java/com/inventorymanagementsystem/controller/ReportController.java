package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.service.ReportService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    @FXML
    private Button bills_btn_print_bill;
    @FXML
    private TextField bills_search_invoice_number;
    @FXML
    private Button bills_btn_close;
    @FXML
    private AnchorPane bills_print_anchor_pane;

    private ReportService reportService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reportService = new ReportService();

        bills_btn_print_bill.setOnAction(event -> searchAndPrintBillDetails());
        bills_btn_close.setOnAction(event -> onExit());
    }

    public void onExit() {
        bills_btn_close.getScene().getWindow().hide();
    }

    public void searchAndPrintBillDetails() {
        String invoiceNumber = bills_search_invoice_number.getText().trim();
        if (!invoiceNumber.isEmpty()) {
            reportService.generateInvoiceReport(invoiceNumber);
        }
    }
}
