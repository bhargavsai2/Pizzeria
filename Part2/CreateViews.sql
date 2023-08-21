-- SQL Script developed by Bhargav Sai Gajula and Sai Praneeth Dulam 
USE Pizzeria;



CREATE VIEW ToppingPopularity AS
SELECT T.ToppingName AS Topping, COUNT(*) + SUM(PT.ExtraTopping) AS ToppingCount
FROM topping T
Left JOIN pizzatopping PT ON T.ToppingID = PT.ToppingID
GROUP BY T.ToppingName
ORDER BY ToppingCount DESC;




CREATE VIEW ProfitByPizza AS
SELECT p.PizzaSize AS 'Pizza Size',
       p.PizzaCrustType AS 'Pizza Crust',
       SUM(p.PizzaPriceToCust - p.PizzaCostToBus) AS Profit,
       MAX(DATE_FORMAT(p.PizzaTimeStamp, '%M %e %Y')) AS LastOrderDate
FROM pizza p
JOIN pizzabaseprice pbp ON p.PizzaSize = pbp.PizzaSize AND p.PizzaCrustType = pbp.PizzaCrustType
GROUP BY p.PizzaSize, p.PizzaCrustType
ORDER BY Profit DESC;




CREATE VIEW ProfitByOrderType AS
SELECT
    OrderType AS CustomerType,
    DATE_FORMAT(MAX(OrderTimeStamp), '%Y %M') AS OrderDate,
    SUM(OrderPriceToCust) AS TotalOrderPrice,
    SUM(OrderCostToBus) AS TotalOrderCost,
    SUM(OrderPriceToCust - OrderCostToBus) AS Profit
FROM ordertable
GROUP BY CustomerType 
UNION
SELECT
    '',
    'Grand Total',
    SUM(OrderPriceToCust) AS TotalOrderPrice,
    SUM(OrderCostToBus) AS TotalOrderCost,
    SUM(OrderPriceToCust - OrderCostToBus) AS Profit
FROM ordertable;


-- Drop view ToppingPopularity;
-- Drop View ProfitByPizza;
-- Drop view ProfitByOrderType;

select * from ToppingPopularity;
select * from ProfitByPizza;
select * from ProfitByOrderType;


