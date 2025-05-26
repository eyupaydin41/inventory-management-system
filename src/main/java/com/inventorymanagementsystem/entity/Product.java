package com.inventorymanagementsystem.entity;

public class Product {
    private int id;
    private String itemNumber;
    private String itemGroup;
    private int quantity;
    private double price;

    private Product(Builder builder) {
        this.id = builder.id;
        this.itemNumber = builder.itemNumber;
        this.itemGroup = builder.itemGroup;
        this.quantity = builder.quantity;
        this.price = builder.price;
    }

    public int getId() {
        return id;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static class Builder {
        private int id;
        private String itemNumber;
        private String itemGroup;
        private int quantity;
        private double price;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder itemNumber(String itemNumber) {
            this.itemNumber = itemNumber;
            return this;
        }

        public Builder itemGroup(String itemGroup) {
            this.itemGroup = itemGroup;
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

        public Product build() {
            return new Product(this);
        }
    }
}
