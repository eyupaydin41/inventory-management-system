package com.inventorymanagementsystem.service;

import com.inventorymanagementsystem.config.Database;
import javafx.scene.control.Alert;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportService {

    public void generateInvoiceReport(String invoiceNumber) {
        String sql = "SELECT * FROM sales s INNER JOIN customers c ON s.cust_id = c.id WHERE s.inv_num = ?";

        try (Connection connection = Database.getInstance().connectDB()) {
            if (connection == null) throw new Exception("Veritabanı bağlantısı kurulamadı.");

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, invoiceNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        showAlert(Alert.AlertType.ERROR, "Hata", "Girilen fatura numarasına ait bir fatura bulunamadı.");
                        return;
                    }
                }
            }

            JasperDesign jasperDesign = JRXmlLoader.load(getClass().getClassLoader().getResourceAsStream("jasper-reports/Invoice.jrxml"));
            JRDesignQuery query = new JRDesignQuery();
            query.setText("SELECT * FROM sales s INNER JOIN customers c ON s.cust_id = c.id WHERE s.inv_num = '" + invoiceNumber + "'");
            jasperDesign.setQuery(query);

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, connection);
            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,"Hata","Fatura raporu oluşturulamadı");
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
}
