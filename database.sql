CREATE DATABASE cab_booking;
USE cab_booking;

CREATE TABLE CABS(
id INT PRIMARY KEY auto_increment,
cab_number VARCHAR(20) UNIQUE NOT NULL,
driver_name VARCHAR(50) NOT NULL,
capacity INT NOT NULL,
location VARCHAR(50) NOT NULL
);

CREATE TABLE bookings(
id INT PRIMARY KEY auto_increment,
cab_id INT NOT NULL,
passenger_name VARCHAR(50) NOT NULL,
passenger_contact VARCHAR(15) NOT NULL,
booking_time TIMESTAMP DEFAULT current_timestamp,
FOREIGN KEY (cab_id) REFERENCES cabs(id)
);

INSERT IGNORE INTO cabs (cab_number, driver_name, capacity, location) VALUES
('TN25BB1540', 'KAMESH', 4, 'CHENNAI'),
('TN07AX5689', 'SAI', 8, 'CHENNAI'),
('TN23VS8964', 'VIJAY', 4, 'MARUVATHUR');

SELECT * FROM cabs;

use cab_booking;
ALTER TABLE cabs ADD vehicle_type varchar(10);
SELECT * FROM cabs;


DELETE FROM bookings;


DELETE FROM cabs;


ALTER TABLE cabs ADD COLUMN vehicle_type VARCHAR(10);


INSERT INTO cabs (cab_number, driver_name, capacity, location, vehicle_type) VALUES
('TN10AB1111', 'Ramesh', 4, 'Chennai', 'Sedan'),
('TN06AC2222', 'Suresh', 4, 'Chennai', 'Sedan'),
('TN40VA3344', 'Vignesh', 4, 'Madurai', 'Sedan'),
('TN33LA4514', 'Karthik', 4, 'Coimbatore', 'Sedan'),
('TN90PO5095', 'Prakash', 4, 'Salem', 'Sedan'),
('TN29MA6666', 'Gokul', 4, 'Erode', 'Sedan'),
('TN30KK7017', 'viswa', 4, 'Tiruchirapalli', 'Sedan');

INSERT INTO cabs (cab_number, driver_name, capacity, location, vehicle_type) VALUES
('TN02BB1215', 'Arun', 8, 'Chennai', 'SUV'),
('TN40BB3264', 'Bala', 8, 'Madurai', 'SUV'),
('TN33MK3378', 'Santhosh', 8, 'Coimbatore', 'SUV'),
('TN90P41009', 'Vimal', 8, 'Salem', 'SUV'),
('TN29BB8955', 'Manoj', 8, 'Erode', 'SUV'),
('TN30EW4567', 'Raja', 8, 'Tiruchirapalli', 'SUV'),
('TN61TK9978', 'Tavamani', 8, 'Tenkasi', 'SUV');

select * from cabs;



