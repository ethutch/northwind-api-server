-- file: db/schema.sql
-- Creates tables and sequence needed by Northwind-style orders

CREATE TABLE customers (
                           customer_id VARCHAR(5) PRIMARY KEY,
                           company_name VARCHAR(40) NOT NULL
    -- other customer columns already present in your DB
);

-- Sequence for order IDs (Postgres sequence)
CREATE SEQUENCE orders_order_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE orders (
                        order_id INTEGER NOT NULL DEFAULT nextval('orders_order_id_seq') PRIMARY KEY,
                        customer_id VARCHAR(5) NOT NULL,
                        employee_id SMALLINT,
                        order_date DATE NOT NULL,
                        required_date DATE,
                        shipped_date DATE,
                        freight REAL,
                        ship_name VARCHAR(40),
                        ship_address VARCHAR(60),
                        ship_city VARCHAR(15),
                        ship_region VARCHAR(15),
                        ship_postal_code VARCHAR(10),
                        ship_country VARCHAR(15),
                        CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE products (
                          product_id SMALLINT PRIMARY KEY,
                          product_name VARCHAR(40) NOT NULL
    -- other product fields omitted
);

CREATE TABLE employees (
                           employee_id SMALLINT PRIMARY KEY,
                           last_name VARCHAR(20) NOT NULL,
                           first_name VARCHAR(10) NOT NULL
    -- other employee fields omitted
);

CREATE TABLE order_details (
                               order_id INTEGER NOT NULL,
                               product_id SMALLINT NOT NULL,
                               unit_price REAL NOT NULL,
                               quantity SMALLINT NOT NULL,
                               discount REAL NOT NULL,
                               PRIMARY KEY (order_id, product_id),
                               CONSTRAINT fk_order_details_order FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                               CONSTRAINT fk_order_details_product FOREIGN KEY (product_id) REFERENCES products(product_id)
);
