package com.inventorymanagementsystem.memento;

import java.time.LocalDate;

public class PurchaseFormMemento {
    private final String invoice;
    private final String shopDetails;
    private final String quantity;
    private final String price;
    private final String totalAmount;
    private final LocalDate date;

    public PurchaseFormMemento(String invoice, String shopDetails,
                               String quantity, String price,
                               String totalAmount, LocalDate date) {
        this.invoice = invoice;
        this.shopDetails = shopDetails;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.date = date;
    }
    // getter’lar...
    public String getInvoice()       { return invoice; }
    public String getShopDetails()   { return shopDetails; }
    public String getQuantity()      { return quantity; }
    public String getPrice()         { return price; }
    public String getTotalAmount()   { return totalAmount; }
    public LocalDate getDate()       { return date; }
}
