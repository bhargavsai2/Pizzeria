--  SQL Script developed by Bhargav Sai Gajula and Sai Praneeth Dulam 

CREATE SCHEMA Pizzeria;
USE Pizzeria;

CREATE TABLE IF NOT EXISTS customer (
  CustomerID INT NOT NULL AUTO_INCREMENT,
  CustomerFName VARCHAR(15) DEFAULT NULL,
  CustomerLName VARCHAR(15) DEFAULT NULL,
  CustomerPhNum VARCHAR(15) DEFAULT NULL,
  PRIMARY KEY (CustomerID),
  UNIQUE (CustomerID)
)AUTO_INCREMENT=1001;

CREATE TABLE IF NOT EXISTS ordertable (
  OrderID INT NOT NULL AUTO_INCREMENT,
  CustomerID INT NOT NULL,
  OrderType ENUM('dinein', 'delivery', 'pickup') NOT NULL,
  OrderTimeStamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  OrderCompleteState INT NOT NULL,
  OrderCostToBus DECIMAL(6,2) NOT NULL,
  OrderPriceToCust DECIMAL(6,2) NOT NULL,
  PRIMARY KEY (OrderID),
  FOREIGN KEY (CustomerID) REFERENCES customer(CustomerID)
)AUTO_INCREMENT=10001;


CREATE TABLE IF NOT EXISTS dinein (
  OrderID INT NOT NULL,
  DineInTableNum INT NOT NULL,
  DineInCount INT DEFAULT NULL,
  PRIMARY KEY (OrderID),
  CONSTRAINT FK_OrderID_dinein FOREIGN KEY (OrderID) REFERENCES ordertable(OrderID)
);

CREATE TABLE IF NOT EXISTS pickup (
  OrderID INT NOT NULL,
  `Status` VARCHAR(18) NOT NULL,
  PRIMARY KEY (OrderID),
  CONSTRAINT FK_OrderID_pickup FOREIGN KEY (OrderID) REFERENCES ordertable (OrderID)
);


CREATE TABLE IF NOT EXISTS delivery (
  OrderID INT NOT NULL,
  Status VARCHAR(18),
  CustomerAddress VARCHAR(45) NOT NULL,
  PRIMARY KEY (OrderID),
  CONSTRAINT FK_OrderID_delivery FOREIGN KEY (OrderID) REFERENCES ordertable(OrderID)
);


CREATE TABLE IF NOT EXISTS pizzabaseprice (
  PizzaSize ENUM('Small', 'Medium', 'Large','XLarge') NOT NULL,
  PizzaCrustType VARCHAR(16) NOT NULL,
  PizzaBasePrice DECIMAL(6,2) NOT NULL,
  PizzaBaseCost DECIMAL(6,2) NOT NULL,
  PRIMARY KEY (PizzaSize, PizzaCrustType)
);


CREATE TABLE IF NOT EXISTS topping (
  ToppingID int NOT NULL AUTO_INCREMENT,
  ToppingName VARCHAR(45) NOT NULL,
  ToppingPriceToCust DECIMAL(3,2) NOT NULL,
  ToppingCostToBus DECIMAL(3,2) NOT NULL,
  ToppingInventory INT NOT NULL,
  ToppingAmtUsedS DECIMAL(3,2) NOT NULL,
  ToppingAmtUsedM DECIMAL(3,2) NOT NULL,
  ToppingAmtUsedL DECIMAL(3,2) NOT NULL,
  ToppingAmtUsedXL DECIMAL(3,2) NOT NULL,
  PRIMARY KEY (ToppingID)
) AUTO_INCREMENT=1;



CREATE TABLE IF NOT EXISTS pizza (
  PizzaID INT NOT NULL AUTO_INCREMENT,
  OrderID INT NOT NULL,
  PizzaSize ENUM('Small', 'Medium', 'Large','XLarge') NOT NULL,
  PizzaCompleteState VARCHAR(30) NOT NULL,
  PizzaTimeStamp DATETIME DEFAULT NULL,
  PizzaCostToBus DECIMAL(6,2) NOT NULL,
  PizzaPriceToCust DECIMAL(6,2) NOT NULL,
  PizzaCrustType VARCHAR(40) NOT NULL,
  PRIMARY KEY (PizzaID),
  INDEX OrderID_idx (OrderID),
  INDEX PizzaSize_idx (PizzaSize, PizzaCrustType),
  CONSTRAINT FK_OrderID_pizza FOREIGN KEY (OrderID) REFERENCES ordertable (OrderID),
  CONSTRAINT FK_PizzaSize_pizza FOREIGN KEY (PizzaSize, PizzaCrustType) REFERENCES pizzabaseprice (PizzaSize, PizzaCrustType)
)AUTO_INCREMENT=2001;

-- CREATE TABLE IF NOT EXISTS pizza (
--   PizzaID INT NOT NULL AUTO_INCREMENT,
--   OrderID INT NOT NULL,
--   PizzaSize ENUM('Small', 'Medium', 'Large','XLarge') NOT NULL,
--   PizzaCompleteState BOOLEAN NOT NULL DEFAULT FALSE,
--   PizzaTimeStamp DATETIME DEFAULT NULL,
--   PizzaCostToBus DECIMAL(6,2) NOT NULL,
--   PizzaPriceToCust DECIMAL(6,2) NOT NULL,
--   PizzaCrustType VARCHAR(40) NOT NULL,
--   PRIMARY KEY (PizzaID, OrderID),
--   INDEX PizzaSize_idx (PizzaSize, PizzaCrustType),
--   CONSTRAINT FK_OrderID_pizza FOREIGN KEY (OrderID) REFERENCES ordertable (OrderID),
--   CONSTRAINT FK_PizzaSize_pizza FOREIGN KEY (PizzaSize, PizzaCrustType) REFERENCES pizzabaseprice (PizzaSize, PizzaCrustType)
-- )AUTO_INCREMENT=2001;


-- CREATE TABLE IF NOT EXISTS discount (
--   DiscountID INT NOT NULL AUTO_INCREMENT,
--   DiscountType ENUM('Amt','Percent') NOT NULL,
--   DiscountAmtFlag BOOLEAN,
--   DiscountAmtOff DECIMAL(6,2),
--   PRIMARY KEY (DiscountID)
-- )AUTO_INCREMENT=101;

CREATE TABLE IF NOT EXISTS discount (
  DiscountID INT NOT NULL AUTO_INCREMENT,
  DiscountType VARCHAR(40) NOT NULL,
  DiscountAmtFlag BOOLEAN,
  DiscountAmtOff DECIMAL(6,2),
  PRIMARY KEY (DiscountID)
)AUTO_INCREMENT=101;



CREATE TABLE  IF NOT EXISTS pizzadiscount (
  PizzaID int NOT NULL,
  DiscountID int NOT NULL,
  PRIMARY KEY (PizzaID,DiscountID),
  CONSTRAINT FK_PizzaID_pizzadiscount FOREIGN KEY (PizzaID) REFERENCES pizza (PizzaID),
  CONSTRAINT FK_DiscountID_pizzadiscount FOREIGN KEY (DiscountID) REFERENCES discount (DiscountID)
);



CREATE TABLE IF NOT EXISTS orderdiscount (
  OrderID int NOT NULL,
  DiscountID int NOT NULL,
  PRIMARY KEY (OrderID, DiscountID),
  CONSTRAINT FK_OrderID_orderdiscount FOREIGN KEY (OrderID) REFERENCES ordertable (OrderID),
  CONSTRAINT FK_DiscountID_orderdiscount FOREIGN KEY (DiscountID) REFERENCES discount (DiscountID)
);

CREATE TABLE IF NOT EXISTS pizzatopping (
  PizzaID int NOT NULL,
  ToppingID int NOT NULL,
  ExtraTopping BOOLEAN NOT NULL,
  PRIMARY KEY (PizzaID, ToppingID),
  CONSTRAINT FK_PizzaID_pizzatopping FOREIGN KEY (PizzaID) REFERENCES pizza (PizzaID),
  CONSTRAINT FK_ToppingID_pizzatopping FOREIGN KEY (ToppingID) REFERENCES topping (ToppingID)
);
