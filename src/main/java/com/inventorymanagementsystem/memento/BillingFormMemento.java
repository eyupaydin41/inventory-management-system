package com.inventorymanagementsystem.memento;

import java.time.LocalDate;

public class BillingFormMemento {
    private final String item;
    private final String quantity;
    private final String price;
    private final String totalAmount;
    private final String customerName;
    private final String customerPhone;
    private final LocalDate date;

    public BillingFormMemento(String item, String quantity, String price,
                              String totalAmount, String customerName,
                              String customerPhone, LocalDate date) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.date = date;
    }

    public String getItem() { return item; }
    public String getQuantity() { return quantity; }
    public String getPrice() { return price; }
    public String getTotalAmount() { return totalAmount; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public LocalDate getDate() { return date; }
}
