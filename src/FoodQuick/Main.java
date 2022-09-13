package FoodQuick;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Main {
	//global variable and constants declarations
	static Statement statement;
	static PreparedStatement ps;
	static Connection conn;

	/**
	 * program main method
	 * @param args
	 */
	public static void main(String[] args) 
	{		
		DisplayMenu();
	}		
	
	/**
	 * displays an interactive GUI menu through which the user interacts with the program
	 */
	public static void DisplayMenu()
	{
		try
		{
			int option = Integer.parseInt(JOptionPane.showInputDialog("Welcome to Quick Foods!\n-----------------\nPlease select an option from the menu using the numbers provided\n\n1. Order now!\n2. View incomplete orders\n 3. Update customer information\n4. Finalise order\n5. View order information"));
			
			//checks user input to see which option they selected and performs the relevant functions
			switch(option)
			{
				case 1:
					AddOrder();
					break;
				case 2:
					Order.SelectUnfinished();
					break;
				case 3:
					Customer.UpdateInfo(JOptionPane.showInputDialog("Enter the ID of the customer you wish to update the information for: "));
					break;
				case 4:
					Order.FinaliseOrder(JOptionPane.showInputDialog("Enter the ID of the order you wish to finalize: "));
					break;
				case 5:
					int op = Integer.parseInt(JOptionPane.showInputDialog("Please select an option using the numbers below\n1. Get order with ID number\n2. View all orders for a customer"));
					switch(op)
					{
						case 1:
							Order.GetOrderByID(JOptionPane.showInputDialog("Enter the ID of the order you wish to view"));
							break;
						case 2:
							Order.GetOrderByCustomer(JOptionPane.showInputDialog("Enter the name of the customer you wish to view the orders for"));
							break;
						default:
							break;
					}
				break;
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "An error has occurred !\nERROR: "+e.getMessage());
		}

	}
	
	/**
	 * adds a new order to the database along with any new customer, driver, or restaurant information where applicable
	 */
	public static void AddOrder()
	{
		try
		{
			ConnectDB();
			Order order = new Order();
			order.OrderID = JOptionPane.showInputDialog("Enter the unique number for this order:");
			
			Customer customer = new Customer();
			int option = Integer.parseInt(JOptionPane.showInputDialog("Are you a returning customer?\n1. Yes\n2. No"));
			while((option != 1) && (option != 2))
			{
				option = Integer.parseInt(JOptionPane.showInputDialog("Invalid option! Please re-select below:\n1. I am a returning customer\n2. I am a new customer"));
			}
			
			statement = conn.createStatement();
			
			//adding a new customer or populating the current customer with information from the database
			if(option == 1)
			{				
				ResultSet rs = statement.executeQuery("SELECT * FROM customer WHERE CustID='"+JOptionPane.showInputDialog("Enter the customer's ID: ")+"'");

				while(rs.next())
				{
					order.CustID = rs.getString("CustID");
					JOptionPane.showMessageDialog(null, "Welcome back "+rs.getString("name"));
				}
				rs.close();
			}
			else
			{
				customer.ID = JOptionPane.showInputDialog("Enter customer ID: ");
				customer.name = JOptionPane.showInputDialog("Enter customer name: ");
				customer.contactNum = JOptionPane.showInputDialog("Enter "+customer.name+"'s contact number: ");
				customer.address = JOptionPane.showInputDialog("Enter "+customer.name+"'s address: ");
				customer.city = JOptionPane.showInputDialog("What city is "+customer.name+" currently living in? ");
				customer.email = JOptionPane.showInputDialog("Enter "+customer.name+"'s email address: ");
				customer.Insert();
				order.CustID = customer.ID;
			}			
			
			//adding a new restaurant or populating the current restaurant with information from the database
			Restaurant restaurant = new Restaurant();
			restaurant.ID = JOptionPane.showInputDialog("Enter restaurant ID: ");
			
			Statement restIDStatement = conn.createStatement();
			ResultSet rs = restIDStatement.executeQuery("SELECT COUNT(RestID) FROM restaurant WHERE RestID='"+restaurant.ID+"'");
			
			while(rs.next())
			{
				//checks to see if a matching record is found in the database and performing the necessary operations
				if(rs.getInt(1) == 1)
				{
					Statement attributeStatement = conn.createStatement();
					ResultSet attributeSet = attributeStatement.executeQuery("SELECT * FROM restaurant WHERE RestID='"+restaurant.ID+"'");
					while(attributeSet.next())
					{
						restaurant.name = attributeSet.getString(2);
						restaurant.contactNum = attributeSet.getString(4);
						restaurant.city = attributeSet.getString(3);
						order.RestID = restaurant.ID;
					}
				}
				else
				{
					restaurant.name = JOptionPane.showInputDialog("Enter restaurant name: ");
					restaurant.contactNum = JOptionPane.showInputDialog("Enter "+restaurant.name+"'s contact number: ");
					restaurant.city = JOptionPane.showInputDialog("What city is "+restaurant.name+" based in?");	
					restaurant.Insert();
					order.RestID = restaurant.ID;
				}	
			}	
			
			//adding a new driver or populating the current driver with information from the database
			Driver driver = new Driver();
			driver.ID = JOptionPane.showInputDialog("Enter driver ID: ");
			
			Statement driverIDStatement = conn.createStatement();
			ResultSet driverIDResults = driverIDStatement.executeQuery("SELECT COUNT(DriverID) FROM driver WHERE DriverID='"+driver.ID+"'");
			
			while(driverIDResults.next())
			{
				//checks to see if a matching record is found in the database and performing the necessary operations
				if(driverIDResults.getInt(1) == 1)
				{
					Statement attributeStatement = conn.createStatement();
					ResultSet attributeSet = attributeStatement.executeQuery("SELECT * FROM driver WHERE DriverID='"+driver.ID+"'");
					while(attributeSet.next())
					{
						driver.name = attributeSet.getString(2);
						driver.city = attributeSet.getString(3);
						order.DriverID = driver.ID;
					}
				}
				else
				{
					driver.name = JOptionPane.showInputDialog("Enter driver name: ");
					driver.city = JOptionPane.showInputDialog("What city is "+driver.name+" currently working in?");
					driver.Insert();
					order.DriverID = driver.ID;
				}	
			}			
			
			order.instructions = JOptionPane.showInputDialog("Are there any special preparation instructions?");			
			
			order.foodItems = new ArrayList<>();
			FoodItem item = new FoodItem();
			
			int counter = 1;
			String itemName = "";
			
			//continuously adding items to the order until the user enter's the character 'x'
			while(true)
			{				
				itemName = JOptionPane.showInputDialog("Enter order item #"+counter+" name or 'x' to stop adding items:");
				if((itemName.toLowerCase().equals("x")))
					break;
				
				//creating a new FoodItem object and adding it to the order
				item = new FoodItem();
				item.name = itemName;
				item.ID = JOptionPane.showInputDialog("Enter ID for "+item.name+":");
				item.price = Double.parseDouble(JOptionPane.showInputDialog("Enter price of "+item.name+":"));
				item.qty =Integer.parseInt(JOptionPane.showInputDialog("How many "+item.name+"'s would you like?"));
				order.foodItems.add(item);			
				item.Insert();
				++counter;
			}	
			System.out.println();
			
			JOptionPane.showMessageDialog(null, "Your order contains "+order.foodItems.size()+" items");
						
			//displaying relevant information about the order, calculating the total cost of the order, and inserting the relevant values into the database
			order.CalcCost();
			order.ToString();
			order.PrintOrderItems();
			order.Insert();
			order.InsertFoodItems();
			
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "An error occurred while adding your order!\nERROR: "+e.getMessage());
		}
	}
	
	/**
	 * establishes a connection to the database
	 */
	public static void ConnectDB()
	{
		try
		{			
			//connects to the database specified in the connection string using the provided credentials		
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/quickfoodms?allowPublicKeyRetrieval=true",
					"excalibur",
					"letmein"
						);			
				
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error connecting to database!");
			e.printStackTrace();
		}
	}

}
