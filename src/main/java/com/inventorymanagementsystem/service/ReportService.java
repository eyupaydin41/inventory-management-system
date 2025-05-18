package com.inventorymanagementsystem.service;

import com.inventorymanagementsystem.config.Database;
import javafx.scene.control.Alert;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.Connection;

public class ReportService {

    public void generateInvoiceReport(String invoiceNumber) {
        String sql = "SELECT * FROM sales s INNER JOIN customers c ON s.cust_id = c.id WHERE s.inv_num = ?";

        try (Connection connection = Database.getInstance().connectDB()) {
            if (connection == null) throw new Exception("Veritabanı bağlantısı kurulamadı.");

            JasperDesign jasperDesign = JRXmlLoader.load(getClass().getClassLoader().getResourceAsStream("jasper-reports/Invoice.jrxml"));
            JRDesignQuery query = new JRDesignQuery();
            query.setText("SELECT * FROM sales s INNER JOIN customers c ON s.cust_id = c.id WHERE s.inv_num = '" + invoiceNumber + "'");
            jasperDesign.setQuery(query);

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, connection);
            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText("Fatura raporu oluşturulamadı");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}
