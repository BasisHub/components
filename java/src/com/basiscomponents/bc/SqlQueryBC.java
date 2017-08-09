package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.basiscomponents.db.ResultSet;


public class SqlQueryBC {

	private String Url;
	private String User;
	private String Password;


	public SqlQueryBC(String Driver, String Url, String User, String Password) throws ClassNotFoundException {
		this.Url 		= Url;
		this.User 		= User;
		this.Password	= Password;

		Class.forName(Driver);
	}


	public ResultSet retrieve(String sql) {
		return retrieve(sql,null);
	}
	
	public ResultSet retrieve(String sql, ArrayList<Object> params) {
		ResultSet brs = null;
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(Url,User,Password);
			PreparedStatement prep = conn.prepareStatement(sql);

			// Set params if there are any
			if (params != null) {
				int i = 1;
				for (Object p : params) {
					prep.setObject(i, p);
					i++;
				}
			}

			brs = new ResultSet(prep.executeQuery());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return brs;
	}

	public Boolean execute(String sql){
		return execute(sql,null);
	}

	public Boolean execute(String sql, ArrayList<Object> params) {
		Connection conn = null;
		Boolean b = false;

		try {
			conn = DriverManager.getConnection(Url,User,Password);
			PreparedStatement prep = conn.prepareStatement(sql);

			// Set params if there are any
			if (params != null) {
				int i = 1;
				for (Object p : params) {
					prep.setObject(i, p);
					i++;
				}
			}

			b = prep.execute();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return b;
	}

}
