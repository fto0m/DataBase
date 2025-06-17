show databases;
create database yesBaby;
use yesBaby;
show tables;
create table employee (
empId varchar(5),
empName varchar(10),
empSalary real ,
phoneNum int,
storeId varchar(5) ,
primary key(empId)
);
select * from employee;
drop table employee;
DROP TABLE IF EXISTS Sale, Inventory, Employee, Customers, Publisher, Items, Store;

CREATE TABLE Store (
    store_id INT PRIMARY KEY AUTO_INCREMENT,
    store_Name VARCHAR(100),
    Store_Phone_Number VARCHAR(20),
    store_address_city VARCHAR(50),
    Street_name VARCHAR(100),
    Street_num VARCHAR(10)
);
drop table publisher;
CREATE TABLE Publisher (
    publisher_id INT PRIMARY KEY AUTO_INCREMENT,
    publisher_phone VARCHAR(20),
    store_id INT,
    FOREIGN KEY (store_id) REFERENCES Store(store_id)
        

);

CREATE TABLE Items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    item_type VARCHAR(50),
    item_name VARCHAR(100),
    item_price DECIMAL(10,2),
    publisher_id INT,
    FOREIGN KEY (publisher_id) REFERENCES Publisher(publisher_id)
);

CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100),
    customer_phone_number VARCHAR(20),
    customer_address_city VARCHAR(50),
    street VARCHAR(100),
    street_number VARCHAR(10),
    street_name VARCHAR(100),
    customer_points INT
);

CREATE TABLE Employee (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_name VARCHAR(100),
    employee_salary DECIMAL(10,2),
    epmloyee_phone_number VARCHAR(20),
    store_id INT,
    FOREIGN KEY (store_id) REFERENCES Store(store_id)
);

CREATE TABLE Inventory (
    inventory_id INT PRIMARY KEY AUTO_INCREMENT,
    restoke_date DATE,
    store_id INT,
    quantity INT,
    item_id INT,
    FOREIGN KEY (store_id) REFERENCES Store(store_id) ,
    FOREIGN KEY (item_id) REFERENCES Items(item_id)
);

CREATE TABLE Sale (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_date DATE,
    sale_price DECIMAL(10,2),
    quantity INT,
    store_id INT,
    employee_id INT,
    customer_id INT,
    item_id INT,
    FOREIGN KEY (store_id) REFERENCES Store(store_id) ,
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id) ,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ,
    FOREIGN KEY (item_id) REFERENCES Items(item_id)
);
show tables;
select * from customers;
INSERT INTO Store (store_id, store_Name, Store_Phone_Number, store_address_city, Street_name, Street_num) VALUES
(1, 'Main Baby Store', '022345678', 'Jerusalem', 'wadi Aljous', '10'),
(2, 'Baby Store Branch', '022987654', 'Jerusalem', 'Sour Bahir', '5');
INSERT INTO Publisher (publisher_id, publisher_phone, store_id) VALUES
(1, '0598765432', 1),
(2, '0591234567', 2);
INSERT INTO Items (item_id, item_type, item_name, item_price, publisher_id) VALUES
(1, 'Baby Stroller', 'Comfy Ride Stroller', 450.00, 1),
(2, 'Lego', 'Lego City Set', 120.00, 1),
(3, 'Toy Cars', 'Remote Control Car', 85.00, 2),
(4, 'Puzzle', 'Wooden Animal Puzzle', 35.00, 2),
(5, 'Baby Carrier', 'Soft Baby Carrier', 150.00, 1),
(6, 'Lego', 'Lego Duplo Set', 90.00, 2),
(7, 'Toy Cars', 'Race Track Set', 110.00, 1),
(8, 'Puzzle', '100-Piece Puzzle', 25.00, 2),
(9, 'Baby Crib', 'Portable Baby Crib', 220.00, 1),
(10, 'Lego', 'Lego Technic Set', 180.00, 2);
INSERT INTO Customers (customer_id, customer_name, customer_phone_number, customer_address_city, street, street_number, street_name, customer_points) VALUES
(1, 'Ahmed Mohammed', '0591111111', 'Ramallah', 'Main Street', '15', 'Main Street', 100),
(2, 'Sarah Khalid', '0592222222', 'Nablus', 'School Street', '8', 'School Street', 50),
(3, 'Mohammed Ali', '0593333333', 'Ramallah', 'Garden Street', '22', 'Garden Street', 75),
(4, 'Layla Hassan', '0594444444', 'Nablus', 'Park Street', '12', 'Park Street', 30),
(5, 'Khaled Ibrahim', '0595555555', 'Ramallah', 'Market Street', '7', 'Market Street', 120),
(6, 'Nora Jameel', '0596666666', 'Nablus', 'Rose Street', '3', 'Rose Street', 60),
(7, 'Ali Saeed', '0597777777', 'Ramallah', 'Olive Street', '9', 'Olive Street', 90),
(8, 'Hana Waleed', '0598888888', 'Nablus', 'Peace Street', '14', 'Peace Street', 40),
(9, 'Yasser Omar', '0599999999', 'Ramallah', 'King Street', '5', 'King Street', 110),
(10, 'Mona Rami', '0590000000', 'Nablus', 'Queen Street', '2', 'Queen Street', 80);
INSERT INTO Employee (employee_id, employee_name, employee_salary, epmloyee_phone_number, store_id) VALUES
(1, 'Omar Ahmed', 2500.00, '0501234567', 1),
(2, 'Sammy Khalil', 2800.00, '0502345678', 1),
(3, 'Nadia Faris', 2300.00, '0503456789', 1),
(4, 'Rami Adel', 2700.00, '0504567890', 2),
(5, 'Heba Wael', 2600.00, '0505678901', 2),
(6, 'Fadi Nasser', 2400.00, '0506789012', 2),
(7, 'Lena Bassam', 2900.00, '0507890123', 1),
(8, 'Basel Raed', 2550.00, '0508901234', 2),
(9, 'Yara Emad', 2650.00, '0509012345', 1),
(10, 'Nasser Kamal', 2750.00, '0500123456', 2);
INSERT INTO Inventory (inventory_id, restoke_date, store_id, quantity, item_id) VALUES
(1, '2023-01-15', 1, 20, 1),
(2, '2023-02-20', 1, 30, 2),
(3, '2023-03-10', 1, 25, 3),
(4, '2023-04-05', 2, 40, 4),
(5, '2023-05-12', 2, 15, 5),
(6, '2023-06-18', 1, 35, 6),
(7, '2023-07-22', 2, 20, 7),
(8, '2023-08-30', 1, 50, 8),
(9, '2023-09-14', 2, 10, 9),
(10, '2023-10-25', 1, 30, 10);
INSERT INTO Sale (sale_id, sale_date, sale_price, quantity, store_id, employee_id, customer_id, item_id) VALUES
(1, '2023-01-20', 450.00, 1, 1, 1, 1, 1),
(2, '2023-02-25', 240.00, 2, 1, 2, 2, 2),
(3, '2023-03-15', 85.00, 1, 1, 3, 3, 3),
(4, '2023-04-10', 70.00, 2, 2, 4, 4, 4),
(5, '2023-05-20', 150.00, 1, 2, 5, 5, 5),
(6, '2023-06-25', 90.00, 1, 1, 6, 6, 6),
(7, '2023-07-30', 110.00, 1, 2, 7, 7, 7),
(8, '2023-08-05', 25.00, 1, 1, 8, 8, 8),
(9, '2023-09-20', 220.00, 1, 2, 9, 9, 9),
(10, '2023-10-30', 180.00, 1, 1, 10, 10, 10);
select * from employee;
select * from customers;
select * from sale;
select * from store;
select * from publisher;
select * from inventory;
select * from items;
DROP TABLE IF EXISTS Cart;

DROP TABLE IF EXISTS Cart;
CREATE TABLE Cart (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    item_id INT,
    quantity INT DEFAULT 1,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ,
    FOREIGN KEY (item_id) REFERENCES Items(item_id)
);
DROP TABLE IF EXISTS User;

CREATE TABLE User (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    password VARCHAR(100),
    role ENUM('employee', 'customer') NOT NULL,
    reference_id INT NOT NULL 
);
select * from User;
INSERT INTO User (username, password, role, reference_id)
VALUES ('Hadeel', 'pass123', 'employee', 1),
('Fatima', 'pass123', 'customer', 2);
select * from customers;
select * from employee;
select * from items;
select * from cart;
select * from sale;
CREATE TABLE Payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT,             
    customer_id INT,    
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL, 
    

    FOREIGN KEY (sale_id) REFERENCES Sale(sale_id),
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);










