DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS parking_transactions;

CREATE TABLE vehicle(
  id INT AUTO_INCREMENT  PRIMARY KEY,
  license_plate VARCHAR(25) NOT NULL,
  parking_pass BOOLEAN DEFAULT false,
  prepaid NUMERIC DEFAULT 0,
  discount NUMERIC DEFAULT 0,
  direction VARCHAR(5) NOT NULL,
  transaction_time VARCHAR(25) NOT NULL
);

CREATE TABLE parking_transactions(
 id INT AUTO_INCREMENT PRIMARY KEY,
 transaction_amount NUMERIC NOT NULL,
 license_plate VARCHAR(10) NOT NULL,
 direction VARCHAR(5) NOT NULL,
 departure_time VARCHAR(25) NOT NULL
);
