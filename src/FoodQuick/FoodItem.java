package FoodQuick;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class FoodItem implements SQL_Methods
{
	public static Connection conn;
	public static Statement statement;
	public static PreparedStatement ps;
	
	public String ID, name;
	public double price;
	public int qty;
	
	/**parameterized class constructor
	 * 
	 * @param ID		unique ID number of the item
	 * @param name		name of the item	
	 * @param price		price of the item
	 * @param qty		number of the item being ordered
	 */
	public FoodItem(String ID, String name, double price, int qty) 
	{
		this.ID = ID;
		this.name = name;
		this.price = price;
		this.qty = qty;
	}


	//default empty class constructor
	public FoodItem() 
	{
		
	}
	
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

	@Override
	/**
	 * inserts the current food item object into the database
	 */
	public void Insert() {
		try
		{
			ConnectDB();
			ps = conn.prepareStatement("INSERT INTO fooditem VALUES(?, ?, ?)");
			
			ps.setObject(1, this.ID);
			ps.setObject(2, this.name);
			ps.setObject(3, this.price);
			
			System.out.println(this.name+" inserted!\nRows affected: "+ps.executeUpdate());
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error inserting item!\nERROR: "+e.getMessage());
		}
		
	}
}
