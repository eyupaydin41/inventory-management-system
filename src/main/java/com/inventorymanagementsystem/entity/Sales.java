package com.inventorymanagementsystem.entity;

public class Sales {
    private final int id;
    private final String inv_num;
    private final int cust_id;
    private final String custName;
    private final double price;
    private final int quantity;
    private final double total_amount;
    private final String date;
    private final String item_num;

    private Sales(Builder builder) {
        this.id = builder.id;
        this.inv_num = builder.inv_num;
        this.cust_id = builder.cust_id;
        this.custName = builder.custName;
        this.price = builder.price;
        this.quantity = builder.quantity;
        this.total_amount = builder.total_amount;
        this.date = builder.date;
        this.item_num = builder.item_num;
    }

    public static class Builder {
        private int id;
        private String inv_num;
        private int cust_id;
        private String custName;
        private double price;
        private int quantity;
        private double total_amount;
        private String date;
        private String item_num;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder inv_num(String inv_num) {
            this.inv_num = inv_num;
            return this;
        }

        public Builder cust_id(int cust_id) {
            this.cust_id = cust_id;
            return this;
        }

        public Builder custName(String custName) {
            this.custName = custName;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder total_amount(double total_amount) {
            this.total_amount = total_amount;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder item_num(String item_num) {
            this.item_num = item_num;
            return this;
        }

        public Sales build() {
            return new Sales(this);
        }
    }

    public int getId() {
        return id;
    }

    public String getInv_num() {
        return inv_num;
    }

    public int getCust_id() {
        return cust_id;
    }

    public String getCustName() {
        return custName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public String getDate() {
        return date;
    }

    public String getItem_num() {
        return item_num;
    }
}
