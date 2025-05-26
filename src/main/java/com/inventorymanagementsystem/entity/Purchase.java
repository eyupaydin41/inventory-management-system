package com.inventorymanagementsystem.entity;

public class Purchase {
    private int id;
    private final String invoice;
    private final String shopDetails;
    private final int totalItems;
    private final Double totalAmount;
    private final String dateOfPurchase;

    // Private constructor to force usage of Builder
    private Purchase(Builder builder) {
        this.id = builder.id;
        this.invoice = builder.invoice;
        this.shopDetails = builder.shopDetails;
        this.totalItems = builder.totalItems;
        this.totalAmount = builder.totalAmount;
        this.dateOfPurchase = builder.dateOfPurchase;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getShopDetails() {
        return shopDetails;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    // Builder class
    public static class Builder {
        private int id;
        private String invoice;
        private String shopDetails;
        private int totalItems;
        private Double totalAmount;
        private String dateOfPurchase;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder invoice(String invoice) {
            this.invoice = invoice;
            return this;
        }

        public Builder shopDetails(String shopDetails) {
            this.shopDetails = shopDetails;
            return this;
        }

        public Builder totalItems(int totalItems) {
            this.totalItems = totalItems;
            return this;
        }

        public Builder totalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder dateOfPurchase(String dateOfPurchase) {
            this.dateOfPurchase = dateOfPurchase;
            return this;
        }

        public Purchase build() {
            return new Purchase(this);
        }
    }
}
