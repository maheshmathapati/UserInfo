package com.wsdemo.connectionUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil implements WSDemoConstants{

	public static Connection getConnection() throws SQLException
	{
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		Connection conn = DriverManager.getConnection(
				CONN_STRING, USERNAME, PASSWORD);

		return conn;
	}
}
