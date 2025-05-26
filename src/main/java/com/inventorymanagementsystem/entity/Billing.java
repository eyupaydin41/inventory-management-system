package com.inventorymanagementsystem.entity;

public class Billing {
    private final String item_number;
    private final int quantity;
    private final double price;
    private final double total_amount;

    // Private constructor
    private Billing(Builder builder) {
        this.item_number = builder.item_number;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.total_amount = builder.total_amount;
    }

    public String getItem_number() {
        return item_number;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    // Builder Class
    public static class Builder {
        private String item_number;
        private int quantity;
        private double price;
        private double total_amount;

        public Builder itemNumber(String item_number) {
            this.item_number = item_number;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder totalAmount(double total_amount) {
            this.total_amount = total_amount;
            return this;
        }

        public Billing build() {
            return new Billing(this);
        }
    }
}
