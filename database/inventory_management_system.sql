BEGIN;

-- Tablo: billing
CREATE TABLE billing (
                         id SERIAL PRIMARY KEY,
                         item_number VARCHAR(255) NOT NULL,
                         quantity INTEGER NOT NULL,
                         price NUMERIC(50,2) NOT NULL,
                         total_amount NUMERIC(50,2) NOT NULL
);

-- Tablo: customers
CREATE TABLE customers (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           phone_number VARCHAR(10) UNIQUE NOT NULL
);

-- Tablo: inv_seq
CREATE TABLE inv_seq (
                         next_not_cached_value BIGINT NOT NULL,
                         minimum_value BIGINT NOT NULL,
                         maximum_value BIGINT NOT NULL,
                         start_value BIGINT NOT NULL,
                         increment BIGINT NOT NULL,
                         cache_size BIGINT NOT NULL,
                         cycle_option SMALLINT NOT NULL,
                         cycle_count BIGINT NOT NULL
);

-- Tablo: products
CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          item_number VARCHAR(255) NOT NULL,
                          item_group VARCHAR(255) NOT NULL,
                          quantity INTEGER NOT NULL,
                          price NUMERIC(50,2) NOT NULL
);

-- Tablo: purchase
CREATE TABLE purchase (
                          id SERIAL PRIMARY KEY,
                          invoice VARCHAR(255) NOT NULL,
                          shop_and_address VARCHAR(1000) NOT NULL,
                          total_items INTEGER NOT NULL,
                          total_amount NUMERIC(50,2) NOT NULL,
                          date_of_purchase DATE NOT NULL
);

-- Tablo: sales
CREATE TABLE sales (
                       id SERIAL PRIMARY KEY,
                       inv_num VARCHAR(255) NOT NULL,
                       cust_id INTEGER NOT NULL,
                       price NUMERIC(50,2) NOT NULL,
                       quantity INTEGER NOT NULL,
                       total_amount NUMERIC(50,2) NOT NULL,
                       date DATE NOT NULL,
                       item_number VARCHAR(255) NOT NULL,
                       CONSTRAINT fk_customer FOREIGN KEY (cust_id) REFERENCES customers(id)
);

-- Tablo: users
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL
);

INSERT INTO inv_seq (next_not_cached_value, minimum_value, maximum_value, start_value, increment, cache_size, cycle_option, cycle_count) VALUES
    (3001, 1, 99999999999999, 1, 1, 1000, 0, 0);


INSERT INTO users (id, username, password) VALUES
    (1, 'admin', 'admin');

COMMIT;
