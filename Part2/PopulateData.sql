

--  SQL Script developed by Bhargav Sai Gajula and Sai Praneeth Dulam 

USE Pizzeria;
-- CustomerID STARTS FROM 1001
-- OrderID STARTS  FROM 10001
-- ToppingID STARTS FROM 1 
-- PizzaID STARTS FROM 2001
-- DiscountID STASRTS FROM 101  

INSERT INTO topping (ToppingName, ToppingPriceToCust, ToppingCostToBus, ToppingInventory, ToppingAmtUsedS, ToppingAmtUsedM, ToppingAmtUsedL, ToppingAmtUsedXL)
VALUES ('Pepperoni', 1.25, 0.2, 100, 2, 2.75, 3.5, 4.5),
       ('Sausage', 1.25, 0.15, 100, 2.5, 3, 3.5, 4.25),
       ('Ham', 1.5, 0.15, 78, 2, 2.5, 3.25, 4),
       ('Chicken', 1.75, 0.25, 56, 1.5, 2, 2.25, 3),
       ('Green Pepper', 0.5, 0.02, 79, 1, 1.5, 2, 2.5),
       ('Onion', 0.5, 0.02, 85, 1, 1.5, 2, 2.75),
       ('Roma Tomato', 0.75, 0.03, 86, 2, 3, 3.5, 4.5),
       ('Mushrooms', 0.75, 0.1, 52, 1.5, 2, 2.5, 3),
       ('Black Olives', 0.6, 0.1, 39, 0.75, 1, 1.5, 2),
       ('Pineapple', 1, 0.25, 15, 1, 1.25, 1.75, 2),
       ('Jalapenos', 0.5, 0.05, 64, 0.5, 0.75, 1.25, 1.75),
       ('Banana Peppers', 0.5, 0.05, 36, 0.6, 1, 1.3, 1.75),
       ('Regular Cheese', 1.5, 0.12, 250, 2, 3.5, 5, 7),
       ('Four Cheese Blend', 2, 0.15, 150, 2, 3.5, 5, 7),
       ('Feta Cheese', 2, 0.18, 75, 1.75, 3, 4, 5.5),
       ('Goat Cheese', 2, 0.2, 54, 1.6, 2.75, 4, 5.5),
       ('Bacon', 1.5, 0.25, 89, 1, 1.5, 2, 3);



INSERT INTO discount (DiscountType, DiscountAmtFlag, DiscountAmtOff) 
VALUES 
    ('Employee', false, 0.15), 
    ('Lunch Special Medium', true, 1), 
    ('Lunch Special Large', true, 2), 
    ('Speciality Pizza', true, 1.5), 
    ('Gameday Special', false, 0.20);


INSERT INTO pizzabaseprice (PizzaSize, PizzaCrustType, PizzaBasePrice, PizzaBaseCost) 
VALUES 
('Small', 'Thin', 3, 0.5),
('Small', 'Original', 3, 0.75),
('Small', 'Pan', 3.5, 1),
('Small', 'Gluten-Free', 4, 2),
('Medium', 'Thin', 5, 1),
('Medium', 'Original', 5, 1.5),
('Medium', 'Pan', 6, 2.25),
('Medium', 'Gluten-Free', 6.25, 3),
('Large', 'Thin', 8, 1.25),
('Large', 'Original', 8, 2),
('Large', 'Pan', 9, 3),
('Large', 'Gluten-Free', 9.5, 4),
('XLarge', 'Thin', 10, 2),
('XLarge', 'Original', 10, 3),
('XLarge', 'Pan', 11.5, 4.5),
('XLarge', 'Gluten-Free', 12.5, 6);



-- Order 1

-- On March 5th at 12:03 pm there was a dine-in order for a large Thin crust pizza with Regular Cheese
-- (extra), Pepperoni, and Sausage (Price: 13.50, Cost: 3.68 ). They used the “Lunch Special Large” discount
-- They sat at Table 14.
-- Order1 

INSERT INTO customer (CustomerID, CustomerFName, CustomerLName, CustomerPhNum)
VALUES ('1001', 'DINEIN', 'CUSTOMER', '111-111-1111');

INSERT INTO ordertable(CustomerID, OrderType, OrderPriceToCust, OrderCostToBus, OrderTimeStamp, OrderCompleteState)
VALUES (1001, 'dinein', 13.50, 3.68, '2023-03-05 12:03:00', 1);

INSERT INTO dinein(OrderID, DineInTableNum)
VALUES (10001, 14);

INSERT INTO pizza(OrderID, PizzaSize, PizzaCompleteState, PizzaPriceToCust, PizzaCostToBus, PizzaTimeStamp, PizzaCrustType)
VALUES (10001, 'Large', 'Completed By Kitchen', 13.5, 3.68, '2023-03-05 12:03:00', 'Thin');

INSERT INTO pizzatopping(PizzaID, ToppingID, ExtraTopping)
VALUES (2001, 13, True), (2001, 1, False), (2001, 2, False);

INSERT INTO orderdiscount(OrderID, DiscountID)
VALUES (10001, 102);

-- INSERT INTO pizzadiscount(PizzaID, DiscountID)
-- VALUES (2001, 102);


-- Order 2

-- On March 3rd at 12:05 pm there was a dine-in order At table 4. They ordered a medium Pan pizza with
-- Feta Cheese, Black Olives, Roma Tomatoes, Mushrooms and Banana Peppers (P: 10.60, C: 3.23). They get
-- the “Lunch Special Medium”and the “Specialty Pizza” discounts. They also ordered a small Original crust
-- pizza with Regular Cheese, Chicken and Banana Peppers (P: 6.75, C: 1.40).

INSERT INTO customer (CustomerID, CustomerFName, CustomerLName, CustomerPhNum)
VALUES ('1002', 'DINEIN', 'CUSTOMER', '111-111-1111');

INSERT INTO ordertable(CustomerID, OrderType, OrderPriceToCust, OrderCostToBus, OrderTimeStamp, OrderCompleteState)
VALUES (1002, 'dinein', 17.35, 4.63, '2023-03-03 12:05:00', 1);

INSERT INTO dinein(OrderID, DineInTableNum)
VALUES (10002, 4);


INSERT INTO pizza(OrderID, PizzaSize, PizzaCompleteState, PizzaPriceToCust,PizzaCostToBus, PizzaTimeStamp, PizzaCrustType)
VALUES (10002, 'Medium', 'Completed By Kitchen', 10.6, 3.23, '2023-03-03 12:05:00', 'Pan'),
	   (10002, 'Small', 'Completed By Kitchen', 6.75, 1.40, '2023-03-03 12:05:00', 'Original');

INSERT INTO pizzatopping (PizzaID, ToppingID, ExtraTopping)
SELECT 2002, ToppingID, False FROM topping WHERE ToppingName IN ('Feta Cheese', 'Black Olives', 'Roma Tomato', 'Mushrooms', 'Banana Peppers')
UNION
SELECT 2003, ToppingID, False FROM topping WHERE ToppingName IN ('Regular Cheese', 'Chicken', 'Banana Peppers');



INSERT INTO orderdiscount (OrderID, DiscountID)
SELECT 10002, DiscountID FROM discount WHERE DiscountType IN ('Lunch Special Medium') ;
-- , 'Speciality Pizza');



INSERT INTO pizzadiscount(PizzaID, DiscountID)
-- SELECT 2002, DiscountID FROM discount WHERE DiscountType ='Lunch Special Medium'
-- UNION
SELECT 2003, DiscountID FROM discount WHERE DiscountType ='Speciality Pizza'
;

-- order 3
-- On March 3rd at 9:30 pm Ellis Beck places an order for pickup of 6 large Original crust pizzas with Regular
-- Cheese and Pepperoni (Price: 10.75, Cost:3.30 each). Ellis’ phone number is Ellis Beck .


INSERT INTO customer (CustomerID, CustomerFName, CustomerLName, CustomerPhNum)
VALUES ('1003', 'Ellis', 'Beck', '864-254-5861');


INSERT INTO ordertable(CustomerID, OrderType, OrderPriceToCust, OrderCostToBus, OrderTimeStamp, OrderCompleteState)
VALUES (1003, 'pickup', 64.5, 19.8, '2023-03-03 21:30:00', 0);

INSERT INTO pickup (OrderID, `Status`)
VALUES (10003, 'picked-up');


-- 2004-2009 
INSERT INTO pizza(OrderID, PizzaSize, PizzaCompleteState, PizzaTimeStamp, PizzaPriceToCust, PizzaCostToBus, PizzaCrustType)
VALUES
(10003, 'Large', 'Completed By Kitchen', '2023-03-03 21:30:00', 10.75, 3.30, 'Original'),
(10003, 'Large', 'Completed By Kitchen', '2023-03-03 21:30:00', 10.75, 3.30, 'Original'),
(10003, 'Large', 'Completed By Kitchen', '2023-03-03 21:30:00', 10.75, 3.30, 'Original'),
(10003, 'Large', 'Completed By Kitchen', '2023-03-03 21:30:00', 10.75, 3.30, 'Original'),
(10003, 'Large', 'Completed By Kitchen', '2023-03-03 21:30:00', 10.75, 3.30, 'Original'),
(10003, 'Large', 'Completed By Kitchen', '2023-03-03 21:30:00', 10.75, 3.30, 'Original');


INSERT INTO pizzatopping (PizzaID, ToppingID, ExtraTopping)
SELECT 2004, ToppingID, False FROM topping WHERE ToppingName IN ('Regular Cheese', 'Pepperoni')
UNION
SELECT 2005, ToppingID, False FROM topping WHERE ToppingName IN ('Regular Cheese', 'Pepperoni')
UNION
SELECT 2006, ToppingID, False FROM topping WHERE ToppingName IN ('Regular Cheese', 'Pepperoni')
UNION
SELECT 2007, ToppingID, False FROM topping WHERE ToppingName IN ('Regular Cheese', 'Pepperoni')
UNION
SELECT 2008, ToppingID, False FROM topping WHERE ToppingName IN ('Regular Cheese', 'Pepperoni')
UNION
SELECT 2009, ToppingID, False FROM topping WHERE ToppingName IN ('Regular Cheese', 'Pepperoni');



-- order 4 

-- On March 5th at 7:11 pm there was a delivery order made by Ellis Beck for 1 x-large pepperoni and
-- Sausage pizza (P 14.50, C 5.59), one x-large pizza with Ham (extra) and Pineapple (extra) pizza (P: 17, C:
-- 5.59), and one x-large Jalapeno and Bacon pizza (P: 14.00, C: 5.68). All the pizzas have the Four Cheese
-- Blend on it and are Original crust. The order has the “Gameday Special” discount applied to it, and the
-- ham and pineapple pizza has the “Specialty Pizza” discount applied to it. The pizzas were delivered to 115
-- Party Blvd, Anderson SC 29621. His phone number is the same as before.

-- INSERT INTO customer (CustomerID, CustomerFName, CustomerLName, CustomerPhNum)
-- VALUES ('1003', 'Ellis', 'Beck', '864-254-5861');


-- Inserting data into ordertable
INSERT INTO ordertable (CustomerID, OrderType, OrderPriceToCust, OrderCostToBus, OrderTimeStamp, OrderCompleteState)
VALUES (1003, 'delivery', 45.5, 16.86, '2023-03-05 19:11:00', 1);

-- Inserting data into delivery table
INSERT INTO delivery (OrderID, Status, CustomerAddress) 
VALUES (10004, 'Delivered', '115 Party Blvd, Anderson SC 29621');

-- Inserting data into pizza table for pizzas 2010-2012
INSERT INTO pizza (OrderID, PizzaSize, PizzaCompleteState, PizzaTimeStamp, PizzaPriceToCust, PizzaCostToBus, PizzaCrustType)
VALUES 
    (10004, 'XLarge', 'Completed By Kitchen', '2023-03-05 19:11:00', 14.50, 5.59, 'Original'),
    (10004, 'XLarge', 'Completed By Kitchen', '2023-03-05 19:11:00', 17.00, 5.59, 'Original'),
    (10004, 'XLarge', 'Completed By Kitchen', '2023-03-05 19:11:00', 14.00, 5.68, 'Original');


INSERT INTO pizzatopping (PizzaID, ToppingID, ExtraTopping)
SELECT 2010, ToppingID, False FROM topping 
WHERE ToppingName IN ('Pepperoni', 'Sausage', 'Four Cheese Blend')
UNION
SELECT 2011, ToppingID, True FROM topping 
WHERE ToppingName IN ('Ham', 'Pineapple')
UNION
SELECT 2011, ToppingID, False FROM topping 
WHERE ToppingName IN ('Four Cheese Blend')
UNION
SELECT 2012, ToppingID, False FROM topping 
WHERE ToppingName IN ('Jalapenos', 'Bacon', 'Four Cheese Blend');


INSERT INTO orderdiscount (OrderID, DiscountID)
SELECT 10004, DiscountID FROM discount WHERE DiscountType IN ('Gameday Special');

INSERT INTO pizzadiscount(PizzaID, DiscountID)
SELECT 2011, DiscountID FROM discount WHERE DiscountType ='Speciality Pizza';

-- order 5

-- On March 2nd at 5:30 pm Kurt McKinney placed an order for pickup for an x-large pizza with Green Pepper,
-- Onion, Roma Tomatoes, Mushrooms, and Black Olives on it. He wants the Goat Cheese on it, and a Gluten
-- Free Crust (P: 16.85, C:7.85). The “Specialty Pizza” discount is applied to the pizza. Kurt’s phone number is
-- 864-474-9953.

-- Insert customer information
INSERT INTO customer (CustomerID, CustomerFName, CustomerLName, CustomerPhNum)
VALUES ('1004','Kurt','Mckinney','864-474-9953');

-- Insert order information
INSERT INTO ordertable(CustomerID, OrderType, OrderPriceToCust, OrderCostToBus, OrderTimeStamp, OrderCompleteState)
VALUES (1004, 'pickup', 16.85, 7.85, '2023-03-02 17:30:00', 0);

-- Insert pickup information
INSERT INTO pickup (OrderID, Status)
VALUES (10005, 'picked-up');

-- Insert pizza information 2013
INSERT INTO pizza(OrderID, PizzaSize, PizzaCompleteState, PizzaTimeStamp, PizzaPriceToCust, PizzaCostToBus, PizzaCrustType)
VALUES (10005, 'XLarge', 'Completed By Kitchen', '2023-03-02 17:30:00',16.85, 7.85, 'Gluten-Free');

-- Insert pizza topping information
INSERT INTO pizzatopping (PizzaID, ToppingID, ExtraTopping)
SELECT 2013, ToppingID, False FROM topping WHERE ToppingName IN ('Green Pepper', 'Onion', 'Roma Tomato', 'Mushrooms', 'Black Olives', 'Goat Cheese');

-- INSERT INTO orderdiscount (OrderID, DiscountID)
-- SELECT 100005, DiscountID FROM discount WHERE DiscountType='Speciality Pizza';

-- Insert pizza discount information
INSERT INTO pizzadiscount(PizzaID, DiscountID)
SELECT 2013, DiscountID FROM discount WHERE DiscountType ='Speciality Pizza';


-- order 6

-- On March 2nd at 6:17 pm Calvin Sanders places on order for delivery of one large pizza with Chicken,
-- Green Peppers, Onions, and Mushrooms. He wants the Four Cheese Blend (extra) and Thin crust (P:
-- 13.25, C: 3.20). The pizza was delivered to 6745 Wessex St Anderson SC 29621. Calvin’s phone number is
-- 864-232-8944.

-- Insert customer information
INSERT INTO customer (CustomerID, CustomerFName, CustomerLName, CustomerPhNum)
VALUES ('1005','Calvin','Sanders','864-232-8944');

-- Insert order information
INSERT INTO ordertable (CustomerID, OrderType, OrderPriceToCust, OrderCostToBus, OrderTimeStamp, OrderCompleteState)
VALUES (1005, 'delivery', 13.25, 3.20, '2023-03-02 18:17:00', 0);

-- Inserting data into delivery table
INSERT INTO delivery (OrderID, Status, CustomerAddress) 
VALUES (10006, 'Delivered', '6745 Wessex St Anderson SC 29621');

-- Insert pizza information 2014
INSERT INTO pizza(OrderID, PizzaSize, PizzaCompleteState, PizzaTimeStamp, PizzaPriceToCust, PizzaCostToBus, PizzaCrustType)
VALUES (10006, 'Large', 'Completed By Kitchen', '2023-03-02 18:17:00',13.25, 3.20, 'Thin');

INSERT INTO pizzatopping (PizzaID, ToppingID, ExtraTopping)
SELECT 2014, ToppingID, False FROM topping WHERE ToppingName IN ('Chicken', 'Green Pepper', 'Onion', 'Mushrooms')
UNION 
SELECT 2014, ToppingID, True FROM topping WHERE ToppingName = 'Four Cheese Blend';


-- order 7

-- On March 6th at 8:32 pm Lance Benton ordered two large Thin crust pizzas. One had the Four Cheese
-- Blend on it (extra) (P: 12, C: 3.75), the other was Regular Cheese and Pepperoni (extra) (P:12, C: 2.55). He
-- used the “Employee” discount on his order. He had them delivered to 8879 Suburban Home, Anderson,
-- SC 29621. His phone number is 864-878-5679.


-- Insert customer information
INSERT INTO customer (CustomerID, CustomerFName, CustomerLName, CustomerPhNum)
VALUES ('1006','Lance','Benton','864-878-5679');

-- Insert order information
INSERT INTO ordertable (CustomerID, OrderType, OrderPriceToCust, OrderCostToBus, OrderTimeStamp, OrderCompleteState)
VALUES (1006, 'delivery', 24.00, 6.30, '2023-03-06 20:32:00', 1);

-- Inserting data into delivery table
INSERT INTO delivery (OrderID, Status, CustomerAddress) 
VALUES (10007, 'Delivered', '8879 Suburban Home, Anderson, SC 29621');

-- Insert pizza information 2015,2016
INSERT INTO pizza(OrderID, PizzaSize, PizzaCompleteState, PizzaTimeStamp, PizzaPriceToCust, PizzaCostToBus, PizzaCrustType)
VALUES (10007, 'Large', 'Completed By Kitchen', '2023-03-06 20:32:00',12.00, 3.75, 'Thin'),
(10007, 'Large', 'Completed By Kitchen', '2023-03-06 20:32:00',12.00, 2.55, 'Thin');

INSERT INTO pizzatopping (PizzaID, ToppingID, ExtraTopping)
SELECT 2016, ToppingID, False FROM topping WHERE ToppingName IN ('Regular Cheese')
UNION 
SELECT 2016, ToppingID, True FROM topping WHERE ToppingName IN ('Pepperoni')
UNION 
SELECT 2015, ToppingID, True FROM topping WHERE ToppingName = 'Four Cheese Blend';

INSERT INTO orderdiscount (OrderID, DiscountID)
SELECT 10007, DiscountID FROM discount WHERE DiscountType IN ('Employee');
