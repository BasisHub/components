package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataField;

public class SqlQueryBC {
	
		private String Driver;  
	    private String Url;
		private String User;
		private String Password;
		
		private SqlQueryBC(){
		};
		
		public SqlQueryBC(String Driver, String Url, String User, String Password) throws ClassNotFoundException{
			this.Driver 	= Driver;
			this.Url 		= Url;
			this.User 		= User;
			this.Password 	= Password;
			
			Class.forName(Driver);

		}
		

		
		
		public ResultSet retrieve(String sql) {
			com.basiscomponents.db.ResultSet brs=null;
			Connection conn=null;
		try {
			conn = DriverManager.getConnection(Url,User,Password);
		      Statement stmt = conn.createStatement();
		      
		      
		     
		      PreparedStatement prep = conn.prepareStatement(sql);
		      //System.out.println(sql);
		      
		     
		      
		      java.sql.ResultSet rs = prep.executeQuery();
		      brs = new ResultSet(rs);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		finally{
			if (conn != null){
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

		public Boolean execute(String sql) {

			Connection conn=null;
			Boolean b=false;
			
		try {
			conn = DriverManager.getConnection(Url,User,Password);
		      Statement stmt = conn.createStatement();
		      PreparedStatement prep = conn.prepareStatement(sql);
		      b = prep.execute();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		finally{
			if (conn != null){
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
