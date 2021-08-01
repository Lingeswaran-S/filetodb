package com.csv.filetodb.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@org.springframework.stereotype.Service
public class Service {

	public boolean dbcall(String query) {
		boolean flag = false;
		try {
			Connection con = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "admin");
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.executeUpdate();
			con.close();
			flag = true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return flag;
	}
	
}
