package FoodQuick;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Order implements SQL_Methods
{
	public static Connection conn;
	public static Statement statement;
	public static PreparedStatement ps;
	
	public String OrderID, CustID, RestID, DriverID, instructions;
	public double cost;
	
	public ArrayList<FoodItem> foodItems = new ArrayList<>();
	
	//parameterized constructor
	public Order(String orderID, String custID, String restID, String driverID, String instructions) {
		super();
		OrderID = orderID;
		CustID = custID;
		RestID = restID;
		DriverID = driverID;
		this.instructions = instructions;
		CalcCost();
	}

	//default empty constructor
	public Order() {
		super();
	}
	
	/**
	 * retrieves and displays an order's information using its unique ID number
	 * @param orderID
	 */
	public static void GetOrderByID(String orderID)
	{
		try
		{			
			ConnectDB();				
			statement = conn.createStatement();			
			
			ResultSet rs = statement.executeQuery("SELECT o.OrderID as orderid, o.RestID as restid, c.name as custname,  c.contactnum as contactnum, c.email as email, o.cost as cost FROM customer c, orders o WHERE ((o.OrderID='"+orderID+"') AND (c.custid = o.custid))");			
			
			while(rs.next())
			{
				System.out.println("INFORMATION FOR ORDER "+rs.getString("orderid")+": ");
				System.out.println("Restaurant ID: "+rs.getString("restid"));
				System.out.println("Customer name: "+rs.getString("custname"));
				System.out.println("Customer contact number: "+rs.getString("contactnum"));
				System.out.println("Customer email address: "+rs.getString("email"));
				System.out.println("Order total: R"+rs.getDouble("cost"));
			}

		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error retrieving order!\nERROR: "+e.getMessage());
		}
	}
	
	/**
	 * retrieves and displays a list of all orders ordered by a specific customer
	 * @param custName	name of the customer for which the orders should be returned
	 */
	public static void GetOrderByCustomer(String custName)
	{
		try
		{			
			ConnectDB();
			statement = conn.createStatement();			
			
			ResultSet rs = statement.executeQuery("SELECT o.OrderID as orderid, o.RestID as restid, c.name as custname,  c.contactnum as contactnum, c.email as email, o.cost as cost FROM customer c, orders o WHERE ((c.name='"+custName+"') AND (c.custid = o.custid))");			
			
			System.out.println("INFORMATION FOR "+custName+"'s orders: ");
			while(rs.next())
			{
				System.out.println("Order ID: "+rs.getString("orderid"));
				System.out.println("Restaurant ID: "+rs.getString("restid"));
				System.out.println("Customer contact number: "+rs.getString("contactnum"));
				System.out.println("Customer email address: "+rs.getString("email"));
				System.out.println("Order total: R"+rs.getDouble("cost"));
			}

		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error retrieving order!\nERROR: "+e.getMessage());
		}
	}
	
	/**
	 * calculates the total for this order and binds it to the relevant attribute
	 */
	public void CalcCost()
	{
		for (int x = 0; x < foodItems.size(); x++) {
			this.cost += (foodItems.get(x).price * foodItems.get(x).qty);
		}
	}
			
	
	/**
	 * displays all customer information from the database
	 */
	public static void SelectAll()	
	{
		
		try
		{
			ConnectDB();
			statement = conn.createStatement();
			ResultSet rs = ps.executeQuery("SELECT * FROM orders");
			
			while(rs.next())
			{
				System.out.println("ID: "+rs.getString(1));
			}
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error selecting all orders!\nERROR: "+e.getMessage());
		}
	}
	
	/**
	 * generates an invoice with the relevant customer and order information in the form of a formatted console output string
	 * @param orderID	the ID of the order for which the invoice is being generated
	 */
	public static void GenerateInvoice(String orderID)
	{
		try
		{			
			ConnectDB();			
			
			statement = conn.createStatement();			
			ResultSet rs = statement.executeQuery("SELECT o.OrderID as orderid, c.name as custname,  c.contactnum as contactnum, c.email as email, o.cost as cost FROM customer c, orders o WHERE ((c.custid='"+orderID+"') AND (c.custid=o.custid))");			
						
			while(rs.next())
			{
				//printing the relevant order information to the console
				System.out.println("INVOICE INFORMATION FOR "+rs.getString("orderid")+": ");
				System.out.println("Customer name: "+rs.getString("custname"));
				System.out.println("Customer contact number: "+rs.getString("contactnum"));
				System.out.println("Customer email address: "+rs.getString("email"));
				System.out.println("Order total: R"+rs.getDouble("cost"));
			}
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error selecting unfinished orders items!\nERROR: "+e.getMessage());
		}
	}
	
	/**
	 * displays a list of all order ID's that relate to incomplete orders
	 */
	public static void SelectUnfinished()
	{
		try
		{			
			ConnectDB();
			
			//list of IDs for orders that have been completed (appear in the finalisedorders table)
			ArrayList<String> completedOrderIDs = new ArrayList<String>();			
			
			statement = conn.createStatement();			
			ResultSet rs = statement.executeQuery("SELECT o.OrderID FROM orders o, finalisedorders fo WHERE o.OrderID=fo.OrderID");		
			
			//Continuously adding order IDs of completed orders to the list
			while(rs.next())
			{
				completedOrderIDs.add(rs.getString(1));
			}
			
			Statement newStatement = conn.createStatement();
			ResultSet orderSet = newStatement.executeQuery("SELECT OrderID FROM orders");
			
			//checking each order to see if it appears in the finalisedorders table and displaying its information if it does not
			System.out.println("The following orders still need to be completed:");
			while(orderSet.next())
			{
				if(!completedOrderIDs.contains(orderSet.getString(1)))
				{
					System.out.println("Order "+orderSet.getString(1));
				}
			}

		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error selecting unfinished orders items!\nERROR: "+e.getMessage());
		}
	}
	
	/**
	 * finalizes an order in the system
	 * @param orderID
	 */
	public static void FinaliseOrder(String orderID)
	{
		try
		{						
			ConnectDB();
			LocalDate ld = LocalDate.now();			
			
			ps = conn.prepareStatement("INSERT INTO finalisedorders VALUES(?, ?)");
			ps.setObject(1, orderID);
			ps.setObject(2, ld);
			
			System.out.println("Order finalised!\nRows affected: "+ps.executeUpdate());
			
			GenerateInvoice(orderID);
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error finalising the order!\nERROR: "+e.getMessage());
		}
	}
	
	/**
	 * prints out a list of this orders current order items
	 */
	public void PrintOrderItems()
	{
		System.out.println("\nItems for "+this.OrderID+"\n--------------");
		
		for (int x = 0; x < this.foodItems.size(); ++x)
		{
			System.out.println(foodItems.get(x).name+" , R"+foodItems.get(x).price+" , "+foodItems.get(x).qty+" orders");
		}
		System.out.println();
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
			JOptionPane.showMessageDialog(null, "Error connecting to database!\nERROR: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * inserts all of this order's order items into the database
	 */
	public void InsertFoodItems()
	{
		try
		{
			ConnectDB();
			ps = conn.prepareStatement("INSERT INTO orderitem VALUES(?, ?, ?)");
			
			for(int x = 0; x < foodItems.size(); x++)
			{
				ps.setObject(1, this.OrderID);
				ps.setObject(2, this.foodItems.get(x).ID);
				ps.setObject(3, this.foodItems.get(x).qty);
				//JOptionPane.showMessageDialog(null, "Order item #"+x+" inserted!\nRows affected: "+ps.executeUpdate());
			}						
			
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error inserting order items!\nERROR: "+e.getMessage());
		}
	}

	@Override
	/**
	 * adds a new order to the database
	 */
	public void Insert() {
		try
		{
			ConnectDB();
			ps = conn.prepareStatement("INSERT INTO orders VALUES(?, ?, ?, ?, ?, ?)");
			
			ps.setObject(1, this.OrderID);
			ps.setObject(2, this.CustID);
			ps.setObject(3, this.RestID);
			ps.setObject(4, this.DriverID);
			ps.setObject(5, this.instructions);
			ps.setObject(6, this.cost);
			
			System.out.println("Order inserted!\nRows affected: "+ps.executeUpdate());
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error inserting order!\nERROR: "+e.getMessage());
		}
		
	}
	
	/**
	 * displays all relevant order information in the console
	 */
	public void ToString()
	{
		System.out.println("Order ID: "+this.OrderID+"\nCustomer ID: "+this.CustID+"\nRestaurant ID: "+this.RestID+"\nDriver ID: "+this.DriverID+"\nInstructions: "+this.instructions+"\nTotal: "+this.cost);
	}
	
}
