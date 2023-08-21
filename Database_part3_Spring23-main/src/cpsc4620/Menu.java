package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import init.DBIniter;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the functionality of each of these menu options' respective functions.
 * 
 * This file should need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove functions as you see necessary. But you MUST have all 8 menu functions (9 including exit)
 * 
 * Simply removing menu functions because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 * 
 */

public class Menu {
	public static void main(String[] args) throws SQLException, IOException {
		System.out.println("Welcome to Taylor's Pizzeria!");

		int menu_option = 0;

		// present a menu of options and take their selection

		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		DBIniter.init();
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
				case 1:// enter order
					EnterOrder();
					break;
				case 2:// view customers
					viewCustomers();
					break;
				case 3:// enter customer
					EnterCustomer();
					break;
				case 4:// view order
					// open/closed/date
					ViewOrders();
					break;
				case 5:// mark order as complete
					MarkOrderAsComplete();
					break;
				case 6:// view inventory levels
					ViewInventoryLevels();
					break;
				case 7:// add to inventory
					AddInventory();
					break;
				case 8:// view reports
					PrintReports();
					break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
		/*
		 * EnterOrder should do the following:
		 * Ask if the order is for an existing customer -> If yes, select the customer. If no -> create the customer (as if the menu option 2 was selected).
		 * 
		 * Ask if the order is delivery, pickup, or dinein (ask for orderType specific information when needed)
		 * 
		 * Build the pizza (there's a function for this)
		 * 
		 * ask if more pizzas should be be created. if yes, go back to building your pizza. 
		 * 
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * apply the pizza to the order (including to the DB)
		 * 
		 * return to menu
		 */

		int cus_id = 0;
		int size = 0; String od_type = "";
		int orderId =  10000+(DBNinja.getCurrentOrders("fake param").size() + 1);
		System.out.println("Is the order is for an existing customer? Answer y/n: ");
		String str_choice = rd.readLine();
		if(str_choice.equals("y") || str_choice.equals("Y")){
			System.out.println("Here's a list of current customers:");
			viewCustomers();
			System.out.println("Which customer is this order for? Enter ID Number ");
			cus_id = Integer.parseInt(rd.readLine());

		} else if(str_choice.equals("n") || str_choice.equals("N")) {
			Menu.EnterCustomer();
			System.out.println("Here's a list of current customers:");
			viewCustomers();
			cus_id = DBNinja.getCustomerList().size();
		}
//		System.out.println(cus_id);

		Customer c =null;

		System.out.println("Is this order for:");
		System.out.println("1.) Dine-In");
		System.out.println("2.) Pick-Up");
		System.out.println("3.) Delivery");
		System.out.println("Enter the number of your choice: ");

		Order o = null;
		int orderCh = Integer.parseInt(rd.readLine()), t_num=0;
//		String sqlQuery = "",sqlQuerypickup="",sqlQuerydelivery="";
		String addr = "";

		switch(orderCh) {
			case 1:
				od_type="dinein";
				System.out.println("Enter table number that you wanted: ");
				t_num = Integer.parseInt(rd.readLine());
				o = new DineinOrder(orderId, 0, "", 0, 0, 0, t_num);
				o.setOrderType(DBNinja.dine_in);


				break;
			case 2:
				od_type="pickup";
				o = new PickupOrder(orderId, cus_id, "", 0, 0, 0, 0);
				o.setOrderType(DBNinja.pickup);


				//System.out.println("done");
				break;
			case 3:
				od_type="delivery";
				System.out.println("Enter delivery address: ");
				addr = rd.readLine();
				o = new DeliveryOrder(orderId, cus_id, "", 0, 0, 0, addr);
				o.setOrderType(DBNinja.delivery);


				break;
		}
		String sqlOrderQuery = "Insert into ordertable(OrderID,CustomerID,OrderType,OrderCompleteState,OrderCostToBus,OrderPriceToCust)values("+orderId+","+cus_id+",'"+od_type+"',"+"'"+0+"',"+0.0+","+0.0+");";
//		System.out.println(sqlOrderQuery);
		DBNinja.DBInsert(sqlOrderQuery);

//
		switch(orderCh) {
			case 1:
				String sqlQuerydinein = "insert INTO dinein(OrderID,DineInTableNum)value("+orderId+","+t_num+");";
				DBNinja.DBInsert(sqlQuerydinein);
				break;
			case 2:
				String sqlQuerypickup = "Insert into pickup(OrderID,Status)value("+orderId+",'"+"picked-up"+"');";
				DBNinja.DBInsert(sqlQuerypickup);
				//System.out.println("done");
				break;
			case 3:
				String sqlQuerydelivery = "Insert into delivery(OrderID,Status,CustomerAddress)value("+orderId+",'"+"Delivered"+"','"+addr+"');";
				DBNinja.DBInsert(sqlQuerydelivery);
				break;
		}

//
		boolean flag_ordr = false;

		while (flag_ordr!=true) {
			Pizza pz = buildPizza(orderId);
			DBNinja.addPizza(pz,orderId);
			DBNinja.ToppingInsert(pz.getToppings());

			System.out.println("Enter -1 to stop adding pizzas... Enter anything else to continue adding more pizzas to the order.");
			if (rd.readLine().equals("-1")) {
				flag_ordr = true;
			}
		}


		//System.out.println("Finished adding order...Returning to menu...");
	}


	public static void viewCustomers() throws SQLException, IOException {
		/*
		 * Simply print out all of the customers from the database. 
		 */
		try {
			ArrayList<Customer> custinfo = DBNinja.getCustomerList();

			for (Customer c : custinfo) {
				System.out.println(c);
			}
		} catch(Exception e){
			e.printStackTrace();
		}

	}


	// Enter a new customer in the database
	public static void EnterCustomer() throws SQLException, IOException {
		/*
		 * Ask what the name of the customer is. YOU MUST TELL ME (the grader) HOW TO FORMAT THE FIRST NAME, LAST NAME, AND PHONE NUMBER.
		 * If you ask for first and last name one at a time, tell me to insert First name <enter> Last Name (or separate them by different print statements)
		 * If you want them in the same line, tell me (First Name <space> Last Name).
		 * 
		 * same with phone number. If there's hyphens, tell me XXX-XXX-XXXX. For spaces, XXX XXX XXXX. For nothing XXXXXXXXXXXX.
		 * 
		 * I don't care what the format is as long as you tell me what it is, but if I have to guess what your input is I will not be a happy grader
		 * 
		 * Once you get the name and phone number (and anything else your design might have) add it to the DB
		 */
		try {
			String Fname, Lname, Pnum;
			ArrayList<Customer> cus = DBNinja.getCustomerList();
			Integer cus_id = cus.size() + 1;
			//reading in user data
			BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please Enter the Customer name (First Name <Space> Last Name):");
			String[] fullname = rd.readLine().split(" ");

			//get only first and last and skip middle name if entered
			Fname = fullname[0];
			Lname = fullname[fullname.length-1];
			System.out.println("Please enter Phone number (XXX-XXX-XXXX)");
			Pnum = rd.readLine();
			//creating customer
			Customer c = new Customer(cus_id, Fname, Lname, Pnum);
			System.out.println(c.toString());
			DBNinja.addCustomer(c);
		} catch(Exception e){
			e.printStackTrace();
		}


	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException {
		/*
	 * This should be subdivided into two options: print all orders (using simplified view) and print all orders (using simplified view) since a specific date.
	 * 
	 * Once you print the orders (using either sub option) you should then ask which order I want to see in detail
	 * 
	 * When I enter the order, print out all the information about that order, not just the simplified view.
	 * 
	 */

		try {
			String str_choice = "";
			boolean flag = false;
			BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
			//Scanner readIn = new Scanner(System.in);

			while(flag != true) {
				System.out.println("Would you like to:");
				System.out.println("Press (a) to display all orders");
				System.out.println("Press (b) to display orders since a specific date");
				str_choice = rd.readLine();

				if (str_choice.equals("a") || str_choice.equals("b")) {
					flag = true;
				}
			}



            /*ArrayList<Order> viewodr = DBNinja.getCurrentOrders("2023-03-05");
			for (Order o : viewodr) {
				System.out.println(o.toSimplePrint());
			}*/

			switch(str_choice){
				case "a":
					//print all orders
					ArrayList<Order> viewodr = DBNinja.getCurrentOrders("fake param");
					//System.out.println(oList.toString());
					for (Order o : viewodr) {
						System.out.println(o.toSimplePrint());
					}

					break;
				case "b":
					System.out.println("What is the date you want to restrict by? (FORMAT = YYYY-MM-DD)");
					//rd.readLine();
					String d = rd.readLine();
					ArrayList<Order> orList = DBNinja.getCurrentOrders(d);
					for (Order o : orList){
						System.out.println(o.toSimplePrint());
					}

					break;
			}

			System.out.println("Which order would you like to see in detail? Enter the number:");
			//Integer od_id =Integer.parseInt(String.valueOf(rd.read()));
			int od_id = Integer.parseInt(rd.readLine());
			Order o = DBNinja.getOrderById(od_id);
			//System.out.println(o.toString());

			if (o.getOrderType().equals("delivery")){
				String addr = DBNinja.getAddressById(od_id);
				System.out.println(o.toString() + " | Delivered to: " + addr);

			} else{
				System.out.println(o.toString());
			}

		} catch(Exception e){
			e.printStackTrace();
		}


	}


	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException {
		/*All orders that are created through java (part 3, not the 7 orders from part 2) should start as incomplete
		 * 
		 * When this function is called, you should print all of the orders marked as complete 
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */
		try {
			ArrayList<Order> ordrs = DBNinja.getCurrentOrders("fake param");;
			int countr = 0;
			for (Order o : ordrs) {
				if (o.getIsComplete() == 0) {
					System.out.println(o.toSimplePrint());
					countr++;
				}
			}

			if (countr == 0) {
				System.out.println("There are no open orders currently... returning to menu...");
			} else {
				BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Which order would you like to mark as complete? Enter Order ID ");
				int ch_order = Integer.parseInt(rd.readLine());
				Order o = DBNinja.getOrderById(ch_order);
				DBNinja.CompleteOrder(o);
			}
		} catch(Exception e){
			e.printStackTrace();
		}


	}

	// See the list of inventory and it's current level
	public static void ViewInventoryLevels() throws SQLException, IOException {
		//print the inventory. I am really just concerned with the ID, the name, and the current inventory

		try{
        /*ArrayList<Topping> t = DBNinja.getInventory();
		System.out.printf("%-10s %-25s %-30s\n","ID","Name","CurINVT");
		for (Topping tp : t){
			System.out.printf("%-10s %-25s %-30s\n",tp.getTopID(),tp.getTopName(),
					tp.getCurINVT());
			}*/
			DBNinja.printInventory();
		} catch(Exception e){
			e.printStackTrace();
		}

	}

	// Select an inventory item and add more to the inventory level to re-stock the
	// inventory
	public static void AddInventory() throws SQLException, IOException {
		/*
		 * This should print the current inventory and then ask the user which topping they want to add more to and how much to add
		 */

		try {
			Boolean flag = false;
			DBNinja.printInventory();

			System.out.println("Which topping would you like to add inventory to? Enter Number");
			BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
			Integer t_Id = Integer.parseInt(rd.readLine());

			//loop until the user enters a topping with id between 1 and 17
			while(t_Id < 1 || t_Id > 17) {
				System.out.println("Invalid topping index. Please enter again");
				System.out.println("Which topping would you like to add inventory to? Enter Number");
				t_Id = Integer.parseInt(rd.readLine());
			}
			System.out.println("How many units would you like to add?");
			double topAmt = Double.parseDouble(rd.readLine());
			while(topAmt<=0){
				System.out.print("You cannot update inventory with negative\n");
				System.out.println("How many units would you like to add?");
				topAmt = Double.parseDouble(rd.readLine());
			}

			//Topping t = DBNinja.getInventory().get(t_Id - 1);
			ArrayList<Topping> topp = DBNinja.getInventory();
			Topping dt = topp.get(1);

			for (int i = 0; i< topp.size(); i++) {
				if (topp.get(i).getTopID() == t_Id) {
					dt = topp.get(i);

				}
			}
			//System.out.println(DBNinja.getInventory().get(t_Id - 1));
			//System.out.println(dt.toString());
			DBNinja.AddToInventory(dt, topAmt);
			System.out.println(dt.toString());
			//DBNinja.printInventory();

		} catch(Exception e) {
			e.printStackTrace();
		}


	}

	// A function that builds a pizza. Used in our add new order function
	public static Pizza buildPizza(int orderID) throws SQLException, IOException {

		/*
		 * This is a helper function for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		Pizza ret = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Order temp = DBNinja.getOrderById(orderID);
//		String datform = temp.get(orderID).getDate();


		try {

			//selecting size
			String sz = "";
			while (sz.equals("")){
				System.out.println("Let's build a pizza!");
				System.out.println("What size is the pizza?");
				System.out.println("1.) Small");
				System.out.println("2.) Medium");
				System.out.println("3.) Large");
				System.out.println("4.) X-Large");
				System.out.println("Enter the corresponding number:");

				int sz_choise = Integer.parseInt(reader.readLine());

				switch (sz_choise) {
					case 1:
						sz = "Small";
						break;
					case 2:
						sz = "Medium";
						break;
					case 3:
						sz = "Large";
						break;
					case 4:
						sz = "XLarge";
						break;
				}
			}

			//Now adding crust
			String crus = "";
			while (crus.equals("")) {
				System.out.println("What crust for this pizza?");
				System.out.println("1.) Thin");
				System.out.println("2.) Original");
				System.out.println("3.) Pan");
				System.out.println("4.) Gluten-Free");
				System.out.println("Enter the corresponding number:");
				int crus_choise = Integer.parseInt(reader.readLine());
				switch (crus_choise) {
					case 1:
						crus = "Thin";
						break;
					case 2:
						crus = "Original";
						break;
					case 3:
						crus = "Pan";
						break;
					case 4:
						crus = "Gluten-Free";
						break;
				}
			}

			//get the base price ??
			Integer p_id = DBNinja.getMaxPizzaID() + 1;
			//System.out.println(cID+ orderType+ pSize + pCrust + address + tableNo);
			Double p_cus_price_t = DBNinja.getBaseCustPrice(sz, crus);
			Double p_bus_price_t = DBNinja.getBaseBusPrice(sz,crus);
//			System.out.println(p_cus_price_t + " " + p_bus_price_t);

			ret = new Pizza(p_id, sz, crus, orderID, "incomplete", temp.getDate(), DBNinja.getBaseCustPrice(sz, crus), DBNinja.getBaseBusPrice(sz,crus));
			//add toppings to the pizza


			//need to check from here
			ArrayList<Topping> cur_inv = DBNinja.getInventory();
			ArrayList<Topping> cur_inv_Selected = new ArrayList<Topping>();
			int topn_ch = 0; boolean flag_top_inv = false;
			while (flag_top_inv!=true) {
				DBNinja.printInventory();
				System.out.println("Which topping do you want to add? Enter the TopID. Enter -1 to stop adding toppings:");
				topn_ch = Integer.parseInt(reader.readLine());

				if (topn_ch != -1) {


                    /*System.out.print("Would you like to add extra of this topping? y/n: ");
					String yn = reader.readLine();
					boolean isExtra = yn.equals("y");

					ret.addToppings(cur_inv.get(topn_ch-1), isExtra);*/

                    /*for (int i = 0; i < cur_inv.size(); i++) {
						if (cur_inv.get(i).getTopID() == topn_ch) {

							System.out.println(cur_inv.get(i).getTopID());
							//ret.addToppings(topn_ch, isExtra);

						}

					}*/
					for(Topping t_indx: cur_inv){
						if(t_indx.getTopID() == topn_ch){
							System.out.print("Would you like to add extra topping? y/n: ");
							String yn = reader.readLine();
							boolean isExtra = yn.equals("y");
							cur_inv_Selected.add(t_indx);
							if(isExtra){
								//boolean isExtra = yn.equals("y");
								ret.addToppings(t_indx, isExtra);
							} else{
								//System.out.println("here");
								ret.addToppings(t_indx, isExtra);
							}
							break;
						}
					}
				}else {
					flag_top_inv=true;

				}
			}

			ret.setToppings(cur_inv_Selected);

			//
			for (int i = 0; i < cur_inv_Selected.size(); i++) {
				String sqlQueryForToppingInvUpdate = "update topping set ToppingInventory = ToppingInventory -1 where ToppingID ="
						+ cur_inv_Selected.get(i).getTopID() + ";";
				DBNinja.updateToppingTable(sqlQueryForToppingInvUpdate);
			}
			//


			System.out.println("Do you want to add discounts to this Pizza? Enter y/n:");
			ArrayList<Discount> arraydic = new ArrayList<Discount>();
			String yn = reader.readLine();
			if (yn.equals("y")) {
				// add discounts
				int disc_ch = 0;
				ArrayList<Discount> disc = DBNinja.getDiscountList();
				boolean flag_disc = false;
				System.out.println("Getting discount list...");
				while (flag_disc!=true) {
					for (Discount d : disc) {
						System.out.println(d.toString());
					}

					System.out.println("Which discount do you want to add? Enter the number. Enter -1 to stop adding discounts: ");

					disc_ch = Integer.parseInt(reader.readLine());

					if (disc_ch != -1) {
						//if (disc_ch > 0 && disc_ch <= disc.size()) {
						//System.out.println(disc.get(disc_ch));
//						System.out.println(disc_ch);
						for(Discount d : disc) {
							if(d.getDiscountID() == disc_ch ) {
								arraydic.add(d);
							}

							//ret.addDiscounts(disc.get(disc_ch - 1));
						}
						//} else {
						//System.out.println("Incorrect entry, not an option");
						//chosen_d = Integer.parseInt(reader.readLine());
						//}
					} else if(disc_ch == -1){
						System.out.println("Do you want to add more discounts to this Pizza? Enter y/n:");
						boolean m_choice = reader.readLine().equals("y");
						if (m_choice) {
							continue;
						}
						break;
					} else{
						flag_disc=true;
					}
				}
			}
			/*for(Discount d : arraydic) {
				ret.addDiscounts(d);

			}*/
			System.out.println("Do you want to add discounts to this Order? Enter y/n:");
			ArrayList<Discount> Orderarraydic = new ArrayList<Discount>();
			String yn1 = reader.readLine();
			if (yn1.equals("y")) {
				// add discounts
				int disc_ch1 = 0;
				ArrayList<Discount> disc = DBNinja.getDiscountList();
				boolean flag_disc = false;
				System.out.println("Getting discount list...");
				while (flag_disc!=true) {
					for (Discount d : disc) {
						System.out.println(d.toString());
					}

					System.out.println("Which discount do you want to add? Enter the number. Enter -1 to stop adding discounts: ");

					disc_ch1 = Integer.parseInt(reader.readLine());

					if (disc_ch1 != -1) {
						//if (disc_ch > 0 && disc_ch <= disc.size()) {
						//System.out.println(disc.get(disc_ch));
						System.out.println(disc_ch1);
						for(Discount d : disc) {
							if(d.getDiscountID() == disc_ch1 ) {
								Orderarraydic.add(d);
							}

							//ret.addDiscounts(disc.get(disc_ch - 1));
						}
						//} else {
						//System.out.println("Incorrect entry, not an option");
						//chosen_d = Integer.parseInt(reader.readLine());
						//}
					} else if(disc_ch1 == -1){
						System.out.println("Do you want to add more discounts to this Order? Enter y/n:");
						boolean m_choice = reader.readLine().equals("y");
						if (m_choice) {
							continue;
						}
						break;
					} else{
						flag_disc=true;
					}
				}
			}
			DBNinja.PriceAndCostCalculation(ret.getOrderID(),ret.getPizzaID(),p_cus_price_t,p_bus_price_t,cur_inv_Selected, arraydic);

			//o.addPizza(ret);
			//DBNinja.updateInventory(curInventory);


		} catch (NumberFormatException e) {
			System.out.println("Invalid Input Entered !!!");
			System.out.println("Message     : " + e.getMessage());
		}

		return ret;
	}

	private static int getTopIndexFromList(int TopID, ArrayList<Topping> tops) {
		/*
		 * This is a helper function I used to get a topping index from a list of toppings
		 * It's very possible you never need to use a function like this
		 * 
		 */
		int ret = -1;


		return ret;
	}


	public static void PrintReports() throws SQLException, NumberFormatException, IOException {
		/*
		 * This function calls the DBNinja functions to print the three reports.
		 * 
		 * You should ask the user which report to print
		 */

		try{
			BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
			boolean flag = false;
			int uChoice = 0;
			while (flag!=true){
				System.out.println("Which report do you wish to print?");
				System.out.println("1.) ToppingPopularity");
				System.out.println("2.) ProfitByPizza");
				System.out.println("3.) ProfitByOrderType");
				uChoice = Integer.parseInt(rd.readLine());
				if (uChoice >= 1 && uChoice <= 3) {
					flag = true;
				}
			}

			System.out.println();
			switch(uChoice){
				case 1:
					DBNinja.printToppingPopReport();
					break;
				case 2:
					DBNinja.printProfitByPizzaReport();
					break;
				case 3:
					DBNinja.printProfitByOrderType();
					break;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


   


}


//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
//DO NOT EDIT ANYTHING BELOW HERE, I NEED IT FOR MY TESTING DIRECTORY. IF YOU EDIT SOMETHING BELOW, IT BREAKS MY TESTER WHICH MEANS YOU DO NOT GET GRADED (0)

/*
CPSC 4620 Project: Part 3 â€“ Java Application Due: Thursday 11/30 @ 11:59 pm 125 pts

For this part of the project you will complete an application that will interact with your database. Much of the code is already completed, you will just need to handle the functionality to retrieve information from your database and save information to the database.
Note, this program does little to no verification of the input that is entered in the interface or passed to the objects in constructors or setters. This means that any junk data you enter will not be caught and will propagate to your database, if it does not cause an exception. Be careful with the data you enter! In the real world, this program would be much more robust and much more complex.

Program Requirements:

Add a new order to the database: You must be able to add a new order with pizzas to the database. The user interface is there to handle all of the input, and it creates the Order object in the code. It then calls DBNinja.addOrder(order) to save the order to the database. You will need to complete addOrder. Remember addOrder will include adding the order as well as the pizzas and their toppings. Since you are adding a new order, the inventory level for any toppings used will need to be updated. You need to check to see if there is inventory available for each topping as it is added to the pizza. You can not let the inventory level go negative for this project. To complete this operation, DBNinja must also be able to return a list of the available toppings and the list of known customers, both of which must be ordered appropropriately.

View Customers: This option will display each customer and their associated information. The customer information must be ordered by last name, first name and phone number. The user interface exists for this, it just needs the functionality in DBNinja

Enter a new customer: The program must be able to add the information for a new customer in the database. Again, the user interface for this exists, and it creates the Customer object and passes it to DBNinja to be saved to the database. You need to write the code to add this customer to the database. You do need to edit the prompt for the user interface in Menu.java to specify the format for the phone number, to make sure it matches the format in your database.

View orders: The program must be able to display orders and be sorted by order date/time from most recent to oldest. The program should be able to display open orders, all the completed orders or just the completed order since a specific date (inclusive) The user interface exists for this, it just needs the functionality in DBNinja

Mark an order as completed: Once the kitchen has finished prepping an order, they need to be able to mark it as completed. When an order is marked as completed, all of the pizzas should be marked as completed in the database. Open orders should be sorted as described above for option #4. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Inventory Levels: This option will display each topping and its current inventory level. The toppings should be sorted in alphabetical order. Again, the user interface exists for this, it just needs the functionality in DBNinja

Add Inventory: When the inventory level of an item runs low, the restaurant will restock that item. When they do so, they need to enter into the inventory how much of that item was added. They will select a topping and then say how many units were added. Note: this is not creating a new topping, just updating the inventory level. Make sure that the inventory list is sorted as described in option #6. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Reports: The program must be able to run the 3 profitability reports using the views you created in Part 2. Again, the user interface exists for this, it just needs the functionality in DBNinja

Modify the package DBConnector to contain your database connection information, this is the same information you use to connect to the database via MySQL Workbench. You will use DBNinja.connect_to_db to open a connection to the database. Be aware of how many open database connections you make and make sure the database is properly closed!
Your code needs to be secure, so any time you are adding any sort of parameter to your query that is a String, you need to use PreparedStatements to prevent against SQL injections attacks. If your query does not involve any parameters, or if your queries parameters are not coming from a String variable, then you can use a regular Statement instead.

The Files: Start by downloading the starter code files from Canvas. You will see that the user interface and the java interfaces and classes that you need for the assignment are already completed. Review all these files to familiarize yourself with them. They contain comments with instructions for what to complete. You should not need to change the user interface except to change prompts to the user to specify data formats (i.e. dashes in phone number) so it matches your database. You also should not need to change the entity object code, unless you want to remove any ID fields that you did not add to your database.

You could also leave the ID fields in place and just ignore them. If you have any data types that donâ€™t match (i.e. string size options as integers instead of strings), make the conversion when you pull the information from the database or add it to the database. You need to handle data type differences at that time anyway, so it makes sense to do it then instead of making changes to all of the files to handle the different data type or format.

The Menu.java class contains the actual user interface. This code will present the user with a menu of options, gather the necessary inputs, create the objects, and call the necessary functions in DBNinja. Again, you will not need to make changes to this file except to change the prompt to tell me what format you expect the phone number in (with or without dashes).

There is also a static class called DBNinja. This will be the actual class that connects to the database. This is where most of the work will be done. You will need to complete the methods to accomplish the tasks specified.

Also in DBNinja, there are several public static strings for different crusts, sizes and order types. By defining these in one place and always using those strings we can ensure consistency in our data and in our comparisons. You donâ€™t want to have â€œSMALLâ€� â€œsmallâ€� â€œSmallâ€� and â€œPersonalâ€� in your database so it is important to stay consistent. These strings will help with that. You can change what these strings say in DBNinja to match your database, as all other code refers to these public static strings.

Start by changing the class attributes in DBConnector that contain the data to connect to the database. You will need to provide your database name, username and password. All of this is available is available in the Chapter 15 lecture materials. Once you have that done, you can begin to build the functions that will interact with the database.

The methods you need to complete are already defined in the DBNinja class and are called by Menu.java, they just need the code. Two functions are completed (getInventory and getTopping), although for a different database design, and are included to show an example of connecting and using a database. You will need to make changes to these methods to get them to work for your database.

Several extra functions are suggested in the DBNinja class. Their functionality will be needed in other methods. By separating them out you can keep your code modular and reduce repeated code. I recommend completing your code with these small individual methods and queries. There are also additional methods suggested in the comments, but without the method template that could be helpful for your program. HINT, make sure you test your SQL queries in MySQL Workbench BEFORE implementing them in codeâ€¦it will save you a lot of debugging time!

If the code in the DBNinja class is completed correctly, then the program should function as intended. Make sure to TEST, to ensure your code works! Remember that you will need to include the MySQL JDBC libraries when building this application. Otherwise you will NOT be able to connect to your database.

Compiling and running your code: The starter code that will compile and â€œrunâ€�, but it will not do anything useful without your additions. Because so much code is being provided, there is no excuse for submitting code that does not compile. Code that does not compile and run will receive a 0, even if the issue is minor and easy to correct.

Help: Use MS Teams to ask questions. Do not wait until the last day to ask questions or get started!

Submission You will submit your assignment on Canvas. Your submission must include: â€¢ Updated DB scripts from Part 2 (all 5 scripts, in a folder, even if some of them are unchanged). â€¢ All of the class code files along with a README file identifying which class files in the starter code you changed. Include the README even if it says â€œI have no special instructions to shareâ€�. â€¢ Zip the DB Scripts, the class files (i.e. the application), and the README file(s) into one compressed ZIP file. No other formats will be accepted. Do not submit the lib directory or an IntellJ or other IDE project, just the code.

Testing your submission Your project will be tested by replacing your DBconnector class with one that connects to a special test server. Then your final SQL files will be run to recreate your database and populate the tables with data. The Java application will then be built with the new DBconnector class and tested.

No late submissions will be accepted for this assignment.*/

