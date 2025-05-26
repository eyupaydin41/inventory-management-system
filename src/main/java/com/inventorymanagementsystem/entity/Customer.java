package com.inventorymanagementsystem.entity;

public class Customer {
    private final int id;
    private final String name;
    private final String phoneNumber;

    private Customer(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public static class Builder {
        private int id;
        private String name;
        private String phoneNumber;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
