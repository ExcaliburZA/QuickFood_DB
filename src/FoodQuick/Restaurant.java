package FoodQuick;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class Restaurant implements SQL_Methods{
	
	public static Connection conn;
	public static Statement statement;
	public static PreparedStatement ps;
	
	public String ID, name, city, contactNum;

	/**parameterized class constructor
	 * 
	 * @param ID			unique ID number of the restaurant
	 * @param name			name of the restaurant	
	 * @param location		city the restaurant is based in
	 * @param contactNum	contact number of the restaurant
	 */
	public Restaurant(String ID, String name, String location, String contactNum) {
		this.ID = ID;
		this.name = name;
		this.city = location;
		this.contactNum = contactNum;
	}

	//default empty class constructor
	public Restaurant() 
	{
		
	}	
	
	/**
	 * selects and displays all restaurants from the database
	 */
	public static void SelectAll()
	{
		try
		{
			ConnectDB();
			statement = conn.createStatement();
			
			ResultSet rs = statement.executeQuery("SELECT * FROM restaurant");
			
			while(rs.next())
			{
				System.out.println("ID: "+rs.getString(1)+"\nName: "+rs.getString(2)+"\nCity: "+rs.getString(3)+"\nContact Number: "+rs.getString(4)+"\n");
			}
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error selecting all restaurants!\nERROR: "+e.getMessage());
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

	@Override
	/**
	 * inserts the current restaurant object into the database
	 */
	public void Insert() {
		try
		{
			ConnectDB();
			ps = conn.prepareStatement("INSERT INTO restaurant VALUES(?, ?, ?, ?)");
			
			ps.setObject(1, this.ID);
			ps.setObject(2, this.name);
			ps.setObject(3, this.city);
			ps.setObject(4, this.contactNum);
			
			System.out.println("Restaurant inserted!\nRows affected: "+ps.executeUpdate());
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error inserting restaurant!\nERROR: "+e.getMessage());
		}
		
	}

}
