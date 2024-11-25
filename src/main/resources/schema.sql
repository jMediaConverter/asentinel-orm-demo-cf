DROP TABLE IF EXISTS CarManufacturers;
CREATE TABLE CarManufacturers (
	Id INT auto_increment PRIMARY KEY,
	Name VARCHAR(255)
);

DROP TABLE IF EXISTS CarModels;
CREATE TABLE CarModels(
	Id INT auto_increment PRIMARY KEY,
	CarManufacturer INT,
	Name VARCHAR(255),
	Type VARCHAR(15),
	FOREIGN KEY (CarManufacturer) REFERENCES CarManufacturers(Id)
);

