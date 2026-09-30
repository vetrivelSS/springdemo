package com.Dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;

public class dbconn {
	public static Connection getConnection() {
	    Connection con = null;
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "rootasroot");
	        System.out.println("Connection created successfully: " + con);
	    } catch (Exception e) {
	        System.err.println("Error creating connection: ");
	        e.printStackTrace();
	    }
	    return con;
	}

}
