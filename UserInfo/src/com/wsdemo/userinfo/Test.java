package com.wsdemo.userinfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Test {

	public static void main(String[] args)
	{
		final String CONN_STRING = "jdbc:mysql://localhost/test";
		final String USERNAME = "TestUser";
		final String PASSWORD = "12345";

		try(
				Connection conn = DriverManager.getConnection(
						CONN_STRING, USERNAME, PASSWORD);
				Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet rset = stmt.executeQuery("SELECT * FROM userinfo");
				)
				{
			System.out.println("success");
				}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
