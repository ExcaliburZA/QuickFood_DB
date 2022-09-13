package FoodQuick;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class Driver implements SQL_Methods
{
	public static Connection conn;
	public static Statement statement;
	public static PreparedStatement ps;
	
	String ID, name, city;
	
	public Driver()	{

	}
		
	/**
	 * parameterized class constructor
	 * @param iD		unique ID number for the driver
	 * @param name		full name of the driver
	 * @param city		city the driver is currently working in
	 */
	public Driver(String iD, String name, String city) {
		ID = iD;
		this.name = name;
		this.city = city;
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



	@Override
	/**
	 * inserts the current driver object in the database
	 */
	public void Insert() {
		try
		{
			ConnectDB();
			ps = conn.prepareStatement("INSERT INTO driver VALUES(?, ?, ?)");
			
			ps.setObject(1, this.ID);
			ps.setObject(2, this.name);
			ps.setObject(3, this.city);
			
			System.out.println("Driver inserted!\nRows affected: "+ps.executeUpdate());
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, "Error inserting driver!\nERROR: "+e.getMessage());
		}
		
	}
	
	
}
