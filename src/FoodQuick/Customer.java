package FoodQuick;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class Customer implements SQL_Methods{
	
	public static Connection conn;
	public static Statement statement;
	public static PreparedStatement ps;
	
	//class attributes
	public String ID;
	public String name, contactNum, address, city, email;
		
	
	/** parameterized class constructor
	 * 
	 * @param iD			ID number for the customer
	 * @param name			The customer's name
	 * @param contactNum	The customer's mobile number
	 * @param address		The customer's home address
	 * @param city			The customer's city
	 * @param email			The customer's email address
	 */
	public Customer(String iD, String name, String contactNum, String address, String city, String email) {
		ID = iD;
		this.name = name;
		this.contactNum = contactNum;
		this.address = address;
		this.city = city;
		this.email = email;
	}

	//default empty class constructor
	public Customer() {
		
	}		
	
	public Customer(String iD) {
		ID = iD;
	}
	
	/**
	 * updates a customer row in the database with new new information from the user
	 * @param custID	the ID number of the customer that is being updated
	 */
	public static void UpdateInfo(String custID)
	{
		try
		{
			ConnectDB();
			ps = conn.prepareStatement("UPDATE customer SET name=? , contactnum=? , address=? , city=? , email=? WHERE CustID=?");
			
			String name = JOptionPane.showInputDialog("Enter customer name: ");
			ps.setObject(1, name);			
			ps.setObject(2, JOptionPane.showInputDialog("Enter "+name+"'s  contact number: "));
			ps.setObject(3, JOptionPane.showInputDialog("Enter "+name+"'s address: "));
			ps.setObject(4, JOptionPane.showInputDialog("What city is "+name+" currently living in? "));
			ps.setObject(5, JOptionPane.showInputDialog("Enter "+name+"'s email address: "));
			ps.setObject(6, custID);
			
			System.out.println("Customer updated!\nRows affected: "+ps.executeUpdate());
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error updating customer information!\nERROR: "+e.getMessage());
		}
	}
	
	/**
	 * inserts a new customer into the database
	 */
	public void Insert()
	{
		try
		{
			ConnectDB();
			ps = conn.prepareStatement("INSERT INTO customer VALUES(?, ?, ?, ?, ?, ?)");
			
			ps.setObject(1, this.ID);
			ps.setObject(2, this.name);
			ps.setObject(3, this.contactNum);
			ps.setObject(4, this.address);
			ps.setObject(5, this.city);
			ps.setObject(6, this.email);
			
			System.out.println("Customer inserted!\nRows affected: "+ps.executeUpdate());
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error inserting customer!\nERROR: "+e.getMessage());
		}
	}
	
	/**
	 * displays all customer information from the database
	 */
	public static void SelectAll()
	{
		
		try
		{
			statement = conn.createStatement();
			ResultSet rs = ps.executeQuery("SELECT * FROM customer");
			
			while(rs.next())
			{
				System.out.println(rs.getString(1)+" , "+rs.getString(2));
			}
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error selecting all customers!\nERROR: "+e.getMessage());
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
