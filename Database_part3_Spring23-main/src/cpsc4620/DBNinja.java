package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;
	private static Statement st;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "small";
	public final static String size_m = "medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";



	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	
	public static void addOrder(Order o) throws SQLException, IOException
	{
		connect_to_db();

		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 */
		//String query = "insert into ordertable values(?,?,?,?,?,?)";

		// Prepare Statement
		//PreparedStatement myStmt = conn.prepareStatement(query);
		String ord_st = "insert into order(OrderID, CustomerID, OrderType, OrderTimeStamp, OrderCompleteState, OrderCostToBus, OrderPriceToCust) values (?, ?, ?, NOW(), ?, ?, ?);";
		String dine_st = "insert into dinein(OrderID, DineInTableNum) values (?, ?);";
		String pick_st = "insert into pickup(OrderID, Status) values(?, ?);";
		String deli_st = "insert into delivery(OrderID, Status, CustomerAddress) values (?, ?, ?)";

		try{

			// Getting max

			boolean order_compl = false;
			if(o.getIsComplete() == 1) order_compl = true;
			//Date oDate = DBNinja.Date_formatter.parse(o.getDate());



			// int, int, String, String,
			// (CustomerOrderID, CustomerOrderCustomerID, CustomerOrderType, CustomerOrderTimeStamp, CustomerOrderTotalprice, CustomerOrderTotalcost, CustomerOrderIsComplete)
			PreparedStatement prepStatement = conn.prepareStatement(ord_st);

			prepStatement.setInt(1, o.getOrderID());
			prepStatement.setInt(2, o.getCustID());

			prepStatement.setString(3, o.getOrderType());

			prepStatement.setBoolean(4, order_compl);

			prepStatement.setDouble(5, o.getCustPrice());
			prepStatement.setDouble(6, o.getBusPrice());

			int flag_st = prepStatement.executeUpdate();
			if(flag_st == 0) {
				System.out.println("Error adding order to customerOrder to able");
			}
			else {
				//System.out.println("Adding Order to customerOrder table successful");

			}

			if(o instanceof DineinOrder){
				// "insert into dineIn(DineInCustomerOrderID, DineInTableNumber) values (?, ?);"
				PreparedStatement din_st = conn.prepareStatement(dine_st);
				din_st.setInt(1, o.getOrderID());
				din_st.setInt(2, ((DineinOrder) o).getTableNum() );

				flag_st = din_st.executeUpdate();
				if(flag_st == 0) {
					System.out.println("Error at adding Dine-In Order");
				}
			}
			else if(o instanceof PickupOrder){
				// "insert into pickup(PickupCustomerOrderID, PickupTimestamp) values(?, ?);";


				PreparedStatement pic_st = conn.prepareStatement(pick_st);
				pic_st.setInt(1, o.getOrderID());
				pic_st.setInt(2, o.getIsComplete());

				flag_st = pic_st.executeUpdate();
				if(flag_st == 0) {
					System.out.println("Error at adding Pickup Order");
				}

			}
			else {

				PreparedStatement del_st = conn.prepareStatement(deli_st);
				del_st.setInt(1, o.getOrderID());
				del_st.setInt(2, o.getIsComplete());
				del_st.setString(3, getAddressById(o.getOrderID()));

				flag_st = del_st.executeUpdate();
				if(flag_st == 0) {
					System.out.println("Error at adding Delivery Order");
				}
			}





			if(flag_st != 0) {
				System.out.println("Adding DineIn/Pick/Delivery Successful");

				// Adding all pizza
				for (Pizza p : o.getPizzaList()) {
					DBNinja.addPizza(p, o.getOrderID());
				}

				// Adding all discounts
				for (Discount d : o.getDiscountList()) {
					DBNinja.addOrderDiscount(o.getOrderID(), d.getDiscountID());
				}
			}

		}
		catch (SQLException e) {
			System.out.println("Error adding Order - addOrder");
			while (e != null) {
				System.out.println("Message     : " + e.getMessage());
				e = e.getNextException();
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();



		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void addPizzaDiscount(int pizzaID, int discountID) throws IOException, SQLException {
		connect_to_db();

		try {
			String topCurr_stmt = "insert into pizzadiscount(PizzaID, DiscountID) values(?, ?)";

			PreparedStatement prepStmt = conn.prepareStatement(topCurr_stmt);
			prepStmt.setInt(1, pizzaID);
			prepStmt.setInt(2, discountID);
			prepStmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("Error adding pizzaDiscount");
			while (e != null) {
				System.out.println("Message     : " + e.getMessage());
				e = e.getNextException();
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void addOrderDiscount(int orderID, int discountID) throws IOException, SQLException {
		connect_to_db();

		try {
			String topCurr_stmt = "insert into orderdiscount(OrderID, DiscountID) values (?, ?)";

			PreparedStatement prepStmt = conn.prepareStatement(topCurr_stmt);
			prepStmt.setInt(1, orderID);
			prepStmt.setInt(2, discountID);

			prepStmt.executeUpdate();



		} catch (SQLException e) {
			System.out.println("Error adding orderDiscount");
			while (e != null) {
				System.out.println("Message     : " + e.getMessage());
				e = e.getNextException();
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}
	
	public static void addPizza(Pizza p, int orderID) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts to that bridge table and 
		 * instance of topping usage to that bridge table if you have't accounted
		 * for that somewhere else.
		 */
		String st_pizz = "insert into pizza(PizzaCrustType, PizzaSize, PizzaCompleteState, PizzaTimeStamp, PizzaPriceToCust, PizzaCostToBus, OrderID) VALUES" + "(?,?,?,?,?,?,?);";
		PreparedStatement ps = conn.prepareStatement(st_pizz);
		//ps.setInt(1,p.getPizzaID());
		ps.setString(1, p.getCrustType());
		ps.setString(2, p.getSize());
		ps.setBoolean(3, true);
		ps.setString(4, p.getPizzaDate());
		ps.setDouble(5,p.getCustPrice());
		ps.setDouble(6,p.getBusPrice());
		ps.setInt(7,orderID);
		ps.executeUpdate();

		//if (p.getSize() == "small")



		conn.close();
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static int getMaxPizzaID() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * A function I needed because I forgot to make my pizzas auto increment in my DB.
		 * It goes and fetches the largest PizzaID in the pizza table.
		 * You wont need to implement this function if you didn't forget to do that
		 */
		String mx_st = "SELECT MAX(pizzaID) FROM pizza;";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(mx_st);
		int m = 0;
		while (rs.next()){
			m = rs.getInt(1);
		}

		conn.close();
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return m;
	}
	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this function will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		connect_to_db();
		/*
		 * This function should 2 two things.
		 * We need to update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * and we need to add that instance of topping usage to the pizza-topping bridge if we haven't done that elsewhere
		 * Ideally, you should't let toppings go negative. If someone tries to use toppings that you don't have, just print
		 * that you've run out of that topping.
		 */
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		String ins_st = "INSERT INTO pizzadiscount (PizzaID, DiscountID) " +
				"VALUES (?, ?);";
		PreparedStatement stmt = conn.prepareStatement(ins_st);
		stmt.setInt(1, p.getPizzaID());
		stmt.setInt(2, d.getDiscountID());

		stmt.executeUpdate();

		/*sql = "UPDATE customer_order SET CustOrderPrice = CustOrderPrice + ? WHERE CustOrderID = ?;";
		stmt = conn.prepareStatement(sql);

		stmt.setDouble(1, p.getBusPrice());
		stmt.setInt(2, p.getOrderID());

		stmt.executeUpdate();*/
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	


	
	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This should add a customer to the database
		 */
		try {
			String query = "insert into customer VALUES" + "(?,?,?,?);";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, c.getCustID());
			ps.setString(2, c.getFName());
			ps.setString(3, c.getLName());
			ps.setString(4, c.getPhone());
			ps.executeUpdate();

		}
		catch (SQLException e){
			System.out.println("Could not perform task");
			SQLException ne = e.getNextException();
			while (ne != null) {
				System.out.println("Message: " + ne.getMessage());
				ne = ne.getNextException();
			}
		}


		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	
	public static void CompleteOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to mark an order as complete in the DB. You may have a boolean field
		 * for this, or maybe a completed time timestamp. However you have it.
		 */
		String query = "Update ordertable SET OrderCompleteState = ? WHERE OrderID = ?;";
		PreparedStatement ps = conn.prepareStatement(query);
		int o_Id = o.getOrderID();
		ps.setBoolean(1, true);
		ps.setInt(2, o_Id);
		ps.executeUpdate();

		conn.close();


		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	
	
	public static void AddToInventory(Topping t, double toAdd) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Adds toAdd amount of topping to topping t.
		 */
		try {
			String upi_st = "UPDATE topping set ToppingInventory = ? where ToppingID = ?;";
			t.setCurINVT((int)(t.getCurINVT() + toAdd));
			PreparedStatement ps = conn.prepareStatement(upi_st);
			ps.setInt(1, t.getCurINVT());
			ps.setInt(2, t.getTopID());

			ps.executeUpdate();
		}
		catch (SQLException e){
				System.out.println("Could not perform task");
				SQLException ne = e.getNextException();
				while (ne != null) {
					System.out.println("Message: " + ne.getMessage());
					ne = ne.getNextException();
				}
			}



		conn.close();
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	

	public static void printInventory() throws SQLException, IOException {
		connect_to_db();
		
		/*
		 * I used this function to PRINT (not return) the inventory list.
		 * When you print the inventory (either here or somewhere else)
		 * be sure that you print it in a way that is readable.
		 * 
		 * 
		 * 
		 * The topping list should also print in alphabetical order
		 */
		ArrayList<Topping> t = DBNinja.getInventory();
		System.out.printf("%-10s %-25s %-30s\n","ID","Name","CurINVT");
		for (Topping tp : t){
			System.out.printf("%-10s %-25s %-30s\n",tp.getTopID(),tp.getTopName(),
					tp.getCurINVT());
		}

		conn.close();
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION		
	}
	
	
	public static ArrayList<Topping> getInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * This function actually returns the toppings. The toppings
		 * should be returned in alphabetical order if you don't
		 * plan on using a printInventory function
		 */
		String g_st = "Select * from topping ORDER BY ToppingName ASC;";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(g_st);

		ArrayList<Topping> tp = new ArrayList<Topping>();
		while (rs.next()){
			tp.add(new Topping(rs.getInt("ToppingID"), rs.getString("ToppingName")
					,rs.getInt("ToppingAmtUsedS"),
					rs.getDouble("ToppingAmtUsedM"), rs.getDouble("ToppingAmtUsedL"), rs.getDouble("ToppingAmtUsedXL"),rs.getDouble("ToppingCostToBus") ,rs.getDouble("ToppingPriceToCust")
					,0,rs.getInt("ToppingInventory")));
		}

		conn.close();

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return tp;
	}


	public static ArrayList<Order> getCurrentOrders(String datefilter) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This function should return an arraylist of all of the orders.
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Also, like toppings, whenever we print out the orders using menu function 4 and 5
		 * these orders should print in order from newest to oldest.
		 */
		ArrayList<Order> Ordlist = new ArrayList<>();
		try{
			/* Taking parameter date filter */
			String ord_st = (datefilter == "fake param")? "SELECT * FROM ordertable ORDER BY OrderTimeStamp DESC;" : "SELECT * FROM ordertable where OrderTimeStamp >= '" + datefilter + "' ORDER BY OrderTimeStamp DESC;";

			conn = DBConnector.make_connection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(ord_st);

			while(rs.next()){
				int OrderID = rs.getInt("OrderID");
				int CustomerID = rs.getInt("CustomerID");
				String OrderType = rs.getString("OrderType");
				String OrderTimeStamp = rs.getTimestamp("OrderTimeStamp").toString();
				int OrderCompleteState = rs.getInt("OrderCompleteState");
				double OrderCostToBus = rs.getDouble("OrderCostToBus");
				double OrderPriceToCust = rs.getDouble("OrderPriceToCust");
				Ordlist.add(new Order(OrderID, CustomerID, OrderType, OrderTimeStamp,OrderPriceToCust, OrderCostToBus , OrderCompleteState));
			}
		}
		catch (SQLException e){
			System.out.println("Could not perform task");
			SQLException ne = e.getNextException();
			while (ne != null) {
				System.out.println("Message: " + ne.getMessage());
				ne = ne.getNextException();
			}
		}


		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return Ordlist;
	}

	public static Order getOrderById(int orderID) throws SQLException, IOException
	{
		connect_to_db();

		try {
			String ord_id_st = "Select * from ordertable where OrderID = " + orderID + ";";
			/*System.out.println(ord_id_st);*/
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(ord_id_st);
			String tempType = "";
			String print = "";

			while (rs.next()) {
				int OrderID = rs.getInt("OrderID");
				int CustomerID = rs.getInt("CustomerID");
				String OrderType = rs.getString("OrderType");
				String OrderTimeStamp = rs.getTimestamp("OrderTimeStamp").toString();
				int OrderCompleteState = rs.getInt("OrderCompleteState");
				double OrderCostToBus = rs.getDouble("OrderCostToBus");
				double OrderPriceToCust = rs.getDouble("OrderPriceToCust");

				Order o = new Order(OrderID, CustomerID, OrderType, OrderTimeStamp, OrderPriceToCust, OrderCostToBus, OrderCompleteState);
                return o;
			}
		}
		catch (SQLException e) {
			System.out.println("Could not perform task");
			SQLException ne = e.getNextException();
			while (ne != null) {
				System.out.println("Message: " + ne.getMessage());
				ne = ne.getNextException();
			}
		}
			conn.close();
			return null;
	}

	public static String getAddressById(int orderID) throws SQLException, IOException {
		connect_to_db();

		String ga_st = "Select * from delivery where OrderID = " + orderID + ";";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(ga_st);

		String addr = "";
		while (rs.next()) {
			addr = rs.getString(3);


		}

		conn.close();

		return addr;

	}

	public static ArrayList<Order> sortOrders(ArrayList<Order> list)
	{
		/*
		 * This was a function that I used to sort my arraylist based on date.
		 * You may or may not need this function depending on how you fetch
		 * your orders from the DB in the getCurrentOrders function.
		 */
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;
		
	}
	
	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		//Helper function I used to help sort my dates. You likely wont need these
		
		
		
		
		
		
		
		
		return false;
	}
	
	
	/*
	 * The next 3 private functions help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}



	
	
	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base price (for the customer) for that size and crust pizza Depending on how
		// you store size & crust in your database, you may have to do a conversion
		String bc_st = "Select PizzaBasePrice from pizzabaseprice WHERE PizzaSize='" + size + "' and PizzaCrustType='" + crust + "';";
		//System.out.println(bc_st);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(bc_st);

		while (rs.next()){
			bp = rs.getDouble(1);
		}

		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}
	
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
		/*
		 *This is a helper function I used to fetch the name of a customer
		 *based on a customer ID. It actually gets called in the Order class
		 *so I'll keep the implementation here. You're welcome to change
		 *how the order print statements work so that you don't need this function.
		 */
		connect_to_db();
		String retrn = "";
		String cn_st = "Select CustomerFName, CustomerLName From customer WHERE CustomerID=" + CustID + ";";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(cn_st);
		
		while(rs.next())
		{
			retrn = rs.getString(1) + " " + rs.getString(2);
		}
		conn.close();
		return retrn;
	}
	
	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base cost (for the business) for that size and crust pizza Depending on how
		// you store size and crust in your database, you may have to do a conversion
		String bb_st = "Select PizzaBaseCost from pizzabaseprice WHERE PizzaSize='" + size + "' and PizzaCrustType='" + crust + "';";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(bb_st);

		while (rs.next()){
			bp = rs.getDouble(1);
		}

		conn.close();
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}

	
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		ArrayList<Discount> discs = new ArrayList<Discount>();
		connect_to_db();
		//returns a list of all the discounts.
		try{
			String dis_st = "SELECT * FROM discount";
			conn = DBConnector.make_connection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(dis_st);

			while(rs.next()){
				discs.add(new Discount(rs.getInt("DiscountID"), rs.getString("DiscountType"), rs.getDouble("DiscountAmtOff"), rs.getBoolean("DiscountAmtFlag")));
			}
		}
		catch (SQLException e){
			System.out.println("Could not perform task");
			SQLException ne = e.getNextException();
			while (ne != null) {
				System.out.println("Message: " + ne.getMessage());
				ne = ne.getNextException();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return discs;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		ArrayList<Customer> custs = new ArrayList<Customer>();
		connect_to_db();
		/*
		 * return an arrayList of all the customers. These customers should
		 *print in alphabetical order, so account for that as you see fit.
		*/


		try
		{
			String cus_st = "SELECT * FROM customer";
			conn = DBConnector.make_connection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(cus_st);

			while(rs.next())
			{
				// System.out.println("CustID="+res.getInt("CustomerId")+" | Name= "+res.getString("First_Name")+" "+res.getString("Last_Name")+", Phone= "+res.getString("Phone_Number"));
				/*Customer c = new Customer(rs.getInt("CustomerID"),rs.getString("CustomerFName"), rs.getString("CustomerLName"), rs.getString("CustomerPhNum"));*/
				custs.add(new Customer(rs.getInt("CustomerID"),rs.getString("CustomerFName"), rs.getString("CustomerLName"), rs.getString("CustomerPhNum")));
				//custs.add(c);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Could not get customers");
			SQLException ne = e.getNextException();
			while (ne != null) {
				System.out.println("Message: " + ne.getMessage());
				ne = ne.getNextException();
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return custs;
	}
	
	public static int getNextOrderID() throws SQLException, IOException
	{
		/*
		 * A helper function I had to use because I forgot to make
		 * my OrderID auto increment...You can remove it if you
		 * did not forget to auto increment your orderID.
		 */
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return -1;
	}
	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print (other than that it should
		 * be in alphabetical order by name), just make sure it's readable.
		 */
		String pt_st = "select * from ToppingPopularity;";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(pt_st);
		System.out.printf("%-20s %-25s\n","Topping", "ToppingCount");
		while (rs.next()){
			System.out.printf("%-20s %-20s\n",rs.getString(1), rs.getInt(2));
			System.out.println();
		}

		conn.close();

		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByPizza view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */
		String pb_st = "select * from ProfitByPizza;";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(pb_st);
		System.out.printf("%-20s %-20s %-20s %-20s\n","Size","Crust" ,"Profit", "Last Order Date");
		while (rs.next()){
			System.out.printf("%-20s %-20s %-20s %-20s\n", rs.getString(1), rs.getString(2),rs.getString(3),rs.getString(4));
		}

		conn.close();
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */
		String pp_st = "select * from ProfitByOrderType;";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(pp_st);
		System.out.printf("%-20s %-20s %-20s %-20s %-20s\n","Customer Type", "Order Month", "Total Order Price", "Total Order Cost","Profit");
		while (rs.next()){
			System.out.printf("%-20s %-20s %-20s %-20s %-20s\n", rs.getString(1), rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5));
		}

		conn.close();
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION	
	}
	
	public static void DBInsert(String sqlQuery) throws SQLException, IOException
	{
//		System.out.println(sqlQuery);
		connect_to_db();
		if (connect_to_db()) {
			conn = DBConnector.make_connection();
			st = conn.createStatement();
			st.execute(sqlQuery);

		} else {
			System.out.print("Not connected to database");
		}

		conn.close();
	}

	

	public static void ToppingInsert(ArrayList<Topping> getToppings) throws SQLException, IOException
	{
		ArrayList<Integer> t_id = new ArrayList<Integer>();

		for(int i=0;i<getToppings.size();i++)
		{
			t_id.add(getToppings.get(i).getTopID());

		}
		Dictionary<Integer, Integer> pizzaToppingDetails = new Hashtable<Integer, Integer>();

		for (int i = 0; i < t_id.size(); i++) {
			for (int j = 0; j < (t_id.size() - 1 - i); j++) {
				if (t_id.get(j) > t_id.get(j + 1)) {
					int temp = t_id.get(j);
					t_id.set(j, t_id.get(j + 1));
					t_id.set(j + 1, temp);

				}
			}
		}

		for (int i = 0; i < t_id.size(); i++) {
			int duplicateCount = 0;
			int k = 0;
			int x = t_id.get(i);
			for (int j = i + 1; j < t_id.size(); j++) {
				int y = t_id.get(j);
				if (x == y) {
					duplicateCount++;

					k = j;
				}
			}
			pizzaToppingDetails.put(t_id.get(i), duplicateCount);
			if (k != 0) {
				i = k;
			}

		}


		int pizzaOrderId =getMaxPizzaID();
//		System.out.println(pizzaOrderId);
		for (Enumeration enn = pizzaToppingDetails.keys(); enn.hasMoreElements();) {
			int keyValue = Integer.parseInt(enn.nextElement().toString());
			String pizzaSqlQuery = "Insert into pizzatopping(PizzaID,ToppingID,ExtraTopping)value('"
					+ pizzaOrderId + "','" + keyValue + "','" + pizzaToppingDetails.get(keyValue) + "');";
			 //System.out.println(pizzaSqlQuery);
			DBInsert(pizzaSqlQuery);
		}

	}

	public static void PriceAndCostCalculation(int orderID, int pizzaID, double p,double c,ArrayList<Topping> getToppings, ArrayList<Discount> arraydic  ) throws SQLException, IOException {
		connect_to_db();

		double pizzaPrice = p;
			double pizzaCost = c;
//		System.out.println(c+" 0 "+p+ "pizzaID "+pizzaID);
			double tp = 0.0;
			double tc=0.0;
//			System.out.println(getToppings);
			for(int i=0; i<getToppings.size();i++) {
				tc += getToppings.get(i).getCustPrice();
				tp += getToppings.get(i).getBusPrice();
				//System.out.println("get herer" + getToppings.get(i).getCustPrice() + "tc "+tc+" tp " +tp);
			}
			p+=tp;
			c+=tc;
			//System.out.println(tc+" "+tp);
//		System.out.println(p+" 1 "+c);
//		System.out.println(arraydic);

		//ArrayList<Discount> d = new ArrayList<Discount>();
		for (Discount d : arraydic) {
//			System.out.println()
//			Technically isPercent checks whether it is discount amount or not.
			if (d.isPercent() == false) {
				p =p * (1 - d.getAmount()) / 100;
//				System.out.println(p+" 2 "+c);

			} else {

				p-=d.getAmount();
//				System.out.println(p+" 3 "+c);
			}
		}
//		System.out.println(c + " 4 " + p + "orderID"+orderID);
		//String sql = "UPDATE pizza SET PizzaCostToBus = ?, PizzaPriceToCust = ? WHERE (PizzaID = ?)";

		String u_st = "UPDATE Pizzeria.pizza SET PizzaCostToBus = "+c+", PizzaPriceToCust = "+p+" WHERE PizzaID ="+orderID+";";
//		updateqry(u_st, p, c, pizzaID,orderID);
		connect_to_db();
		try {
			Connection conn = DBConnector.make_connection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(u_st);
			//System.out.println("Update successful.u_st");
		} catch (SQLException e) {
			System.out.println("Error updating database: " + e.getMessage());
		}
		conn.close();

		String sqlupdate = "update ordertable set OrderCostToBus =OrderCostToBus+"+c+" , OrderPriceToCust=OrderPriceToCust+"+p+" where OrderID="+orderID+";";
		connect_to_db();
		try {
			Connection conn = DBConnector.make_connection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sqlupdate);
			//System.out.println("Update successful.");
		} catch (SQLException e) {
			System.out.println("Error updating database: " + e.getMessage());
		}
		conn.close();

	/*if (d.isPercent() == true){
		Double cPrice = o.getCustPrice();
		Double percOff = d.getAmount() / 100;
		Double discount = 1 - percOff;
		o.setCustPrice(cPrice * discount);
	}
	else{
		Double cPrice = o.getCustPrice();
		o.setCustPrice(cPrice - d.getAmount());
	}*/
	}
	public static void updateqry(String st_qry, Double p, Double c, int pizzaID, int orderID) throws SQLException, IOException {
		connect_to_db();
//		try {
//			String u_st = "UPDATE Pizzeria.pizza SET PizzaCostToBus = ?, PizzaPriceToCust = ? WHERE PizzaID = ?";
//
//			PreparedStatement ps = conn.prepareStatement(u_st);
//			ps.setDouble(1, c);
//			ps.setDouble(2, p);
//			ps.setInt(3, pizzaID);
//
//			ps.executeUpdate();
//		}
//		catch (SQLException e){
//			System.out.println(e);
//			SQLException ne = e.getNextException();
//			while (ne != null) {
//				System.out.println("Message: " + ne.getMessage());
//				ne = ne.getNextException();
//			}
//		}
//
		String sql = "UPDATE Pizzeria.pizza SET PizzaCostToBus = " + c + ", PizzaPriceToCust = " + p + " WHERE PizzaID = " + pizzaID + " AND OrderID = " + orderID;

		try {
			Connection conn = DBConnector.make_connection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			//System.out.println("Update successful.");
		} catch (SQLException e) {
			System.out.println("Error updating database: " + e.getMessage());
		}
		conn.close();

	}
	public static void updateToppingTable(String sqlQuery) throws SQLException, IOException {
		if (connect_to_db()) {
			conn = DBConnector.make_connection();
			st = conn.createStatement();
			st.executeUpdate(sqlQuery);
		} else {
			System.out.print("Not connected to db");
		}

		conn.close();
	}
}