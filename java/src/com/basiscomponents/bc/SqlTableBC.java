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

public class SqlTableBC implements BusinessComponent{
	
		private String Driver;  
	    private String Url;
		private String User;
		private String Password;
		private String Table;
		private ArrayList<String> PrimaryKeys;
		
		private DataRow 	Filter;
		private DataRow 	FieldSelection;
		private ResultSet 	LastResultSet;		
		
		
		private SqlTableBC(){};
		
		public SqlTableBC(String Driver, String Url, String User, String Password) throws ClassNotFoundException{
			this.Driver 	= Driver;
			this.Url 		= Url;
			this.User 		= User;
			this.Password 	= Password;
			
			Class.forName(Driver);

		}
		
		public void setTable(String Table){
			this.Table = Table;
			this.PrimaryKeys = new ArrayList<>();
			
			Connection conn = null;
			try {
				
			conn = DriverManager.getConnection(Url,User,Password);
			
			DatabaseMetaData dmd = conn.getMetaData();
			java.sql.ResultSet rs = dmd.getPrimaryKeys(null, null, Table);

			while(rs.next()){
				String primaryKey = rs.getString("COLUMN_NAME");
				PrimaryKeys.add(primaryKey);
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	
		}
		
		
		
		public void setFilter(DataRow filter){
			this.Filter = filter;
			this.LastResultSet = null;
		}
		
		public void setFieldSelection(DataRow FieldSelection){
			this.FieldSelection = FieldSelection;
			this.LastResultSet = null;
		}		
		
		public ResultSet retrieve() {
			
			if (this.LastResultSet != null){
				return this.LastResultSet;
			}
			
			
			  Connection conn=null;
		try {
			conn = DriverManager.getConnection(Url,User,Password);
		      Statement stmt = conn.createStatement();
		      String sql;
		      
		      if (this.FieldSelection == null || this.FieldSelection.getFieldNames().size()==0){
		    	  sql = "SELECT * FROM "+this.Table;
		      } 
		      else {
		    	  sql = "SELECT ";
		    	  Iterator<String> it = this.FieldSelection.getFieldNames().iterator();
		    	  Boolean first=true;
		    	  while (it.hasNext()){
		    		  String f=it.next();
		    		  if (!first){
		    			  sql +=",";
		    		  }
		    		  sql+="`"+f+"`";
		    		  first=false;
		    	  }
		    	  sql+= " FROM "+this.Table;
		    	  
		      }
		      
		      
		      ArrayList<Integer> ParamTypes = new ArrayList<>();
		      ArrayList<DataField> Params = new ArrayList<>();
		      
		      if (Filter != null){
		    	  String wh="";
		    	  
		    	  Iterator it = Filter.getFieldNames().iterator();
		    	  while (it.hasNext()){
		    		  String f = (String) it.next();
		    		  DataField field;
					try {
						field = Filter.getField(f);
						if (wh.length()>0){
							wh+=" AND ";
						}
						
						
						
							
						wh += " `"+f+"`=? ";
						ParamTypes.add(Filter.getFieldType(f));
						Params.add(field);
												
						

			    		  
			    		  
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

		    	  }
		    	  if (wh.length()>0)
		    		  sql+=" WHERE "+wh;
		      }
		      
		      PreparedStatement prep = conn.prepareStatement(sql);
		      //System.out.println(sql);
		      
		      Iterator<Integer> it = ParamTypes.iterator();
		      Iterator<DataField> it1 = Params.iterator();
		      int index = 1;
		      while (it.hasNext()){
		    	  Integer type = it.next();
		    	  DataField o = it1.next();
		    	  
		    	  switch (type){
		    	  		case 4:
		    	  			prep.setInt(index, o.getInt());
		    	  			index++;
		    	  			break;
		    	  		case 12:
		    	  			prep.setString(index, o.getString());
		    	  			index++;
		    	  			break;
		    	  		case 93:
		    	  			prep.setTimestamp(index, o.getTimestamp());
		    	  			index++;
		    	  			break;		
		    	  		default:
		    	  			System.out.println("todo: "+type);
		    	  }
		    	  
		    	  
		      }
		      
		      java.sql.ResultSet rs = prep.executeQuery();
		      this.LastResultSet = new ResultSet(rs);
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
		      
			return LastResultSet; 			 
		}

		public DataRow write(DataRow r) throws Exception {
			
			Iterator<String> it = PrimaryKeys.iterator();
			Boolean pk_present=true;
			Boolean write_pk=false;
			BBArrayList fields = r.getFieldNames();
			while (it.hasNext()){
				String pk = it.next();
				if (!fields.contains(pk)){
					pk_present=false;
					break;
				}
			}
			//System.out.println("PK Present: "+pk_present);

			ArrayList<Integer> ParamTypes = new ArrayList<>();
		    ArrayList<DataField> Params = new ArrayList<>();
		    String sql="";
		      
		    if (pk_present){
		    	//check if record exists already
		    	sql="SELECT COUNT(*) FROM "+Table+" ";
				@SuppressWarnings("unchecked")
				Iterator<String> itf = fields.iterator();
				Boolean first=true;
			    	  String wh="";
			    	  
			    	  Iterator<String> it2 = PrimaryKeys.iterator();
			    	  while (it2.hasNext()){
			    		  String pkfieldname = it2.next();
			    		  
						
							DataField pkfield = r.getField(pkfieldname);
							if (wh.length()>0){
								wh+=" AND ";
							}
	
							ParamTypes.add(r.getFieldType(pkfieldname));
							Params.add(pkfield);
							wh += " `"+pkfieldname+"`=? ";
	
				    		  
			    	  		}  
			
		    	  if (wh.length()>0)
		    		  sql+=" WHERE "+wh;		
		    	  
		    	  System.out.println(sql);
		    	  Connection conn = DriverManager.getConnection(Url,User,Password); 
			      PreparedStatement prep = conn.prepareStatement(sql);
			      
			      Iterator<Integer> itp = ParamTypes.iterator();
			      Iterator<DataField> it1 = Params.iterator();
			      int index = 1;
			      while (itp.hasNext()){
			    	  Integer type = itp.next();
			    	  DataField o = it1.next();
			    	  
			    	  switch (type){
			    	  		case 4:
			    	  			prep.setInt(index, o.getInt());
			    	  			index++;
			    	  			break;
			    	  		case 12:
			    	  			prep.setString(index, o.getString());
			    	  			index++;
			    	  			break;
			    	  		case 93:
			    	  			prep.setTimestamp(index, o.getTimestamp());
			    	  			index++;
			    	  			break;	
			    	  		default:
			    	  			System.out.println(type);
			    	  }
			      }
			      java.sql.ResultSet rs = prep.executeQuery();
			      rs.first();
			      if (rs.getInt(1)==0)
			      {
			    	  pk_present=false;
			    	  write_pk = true;
			      }
			      	
			      
		    }
		    
		    sql="";
			Params.clear();
			ParamTypes.clear();

			
			if (pk_present){
			// update
				sql="UPDATE "+Table+" SET ";
				
				@SuppressWarnings("unchecked")
				Iterator<String> itf = fields.iterator();
				Boolean first=true;
				while (itf.hasNext()){
					String field = itf.next();
					if (PrimaryKeys.contains(field))
						continue;
					if (!first)
						sql+=",";
					else
						first=false;
					sql=sql+"`"+field+"`=? ";
					
		
						
					DataField f = r.getField(field);
					ParamTypes.add(r.getFieldType(field));
					Params.add(f);
						
				}	
				
						
				    	  String wh="";
				    	  
				    	  Iterator<String> it2 = PrimaryKeys.iterator();
				    	  while (it2.hasNext()){
				    		  String pkfieldname = it2.next();
				    		  
							
								DataField pkfield = r.getField(pkfieldname);
								if (wh.length()>0){
									wh+=" AND ";
								}

								ParamTypes.add(r.getFieldType(pkfieldname));
								Params.add(pkfield);
								wh += " `"+pkfieldname+"`=? ";

					    		  
				    	  		}  
				
			    	  if (wh.length()>0)
			    		  sql+=" WHERE "+wh;
				
			}
			else{
			// insert
				
				
				sql="INSERT INTO "+Table+" SET ";
				
				Iterator<String> itf = fields.iterator();
				Boolean first=true;
				while (itf.hasNext()){
					String field = itf.next();
					if (!write_pk && PrimaryKeys.contains(field))
						continue;
					if (!first)
						sql+=",";
					else
						first=false;
					sql=sql+"`"+field+"`=? ";
					
		
						
						DataField f = r.getField(field);
						ParamTypes.add(r.getFieldType(field));
						Params.add(f);
//						System.out.println(field+" "+f+" "+r.getFieldType(field));

				}	
						
						
			}
			
			System.out.println(sql);
			Connection conn = DriverManager.getConnection(Url,User,Password); 
		      PreparedStatement prep = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
		      
		      Iterator<Integer> itp = ParamTypes.iterator();
		      Iterator<DataField> it1 = Params.iterator();
		      int index = 1;
		      while (itp.hasNext()){
		    	  Integer type = itp.next();
		    	  DataField o = it1.next();
		    	  switch (type){
		    	  		case 4:
		    	  			prep.setInt(index, o.getInt());
		    	  			index++;
		    	  			break;
		    	  		case 12:
		    	  			prep.setString(index, o.getString());
		    	  			index++;
		    	  			break;
		    	  		case 93:
		    	  			prep.setTimestamp(index, o.getTimestamp());
		    	  			index++;
		    	  			break;	
		    	  		default:
		    	  			System.out.println(type);
		    	  }
		      }
		      prep.execute();
		      			
		      
		      this.LastResultSet=null;

		    //return generated keys 
		    //TODO re-read the full record and return it!!!
		    com.basiscomponents.db.ResultSet gkeys = new com.basiscomponents.db.ResultSet(prep.getGeneratedKeys());
		     
  	        DataRow ret = r.clone();
    	    it = PrimaryKeys.iterator();
    	    int i=0;
			fields = ret.getFieldNames();
			while (it.hasNext()){
				String pk = it.next();
				if (!fields.contains(pk)){
					ret.setFieldValue(pk,gkeys.getItem(i).getField("GENERATED_KEY"));
					i++;
				}
			}
		
//			System.out.println(sql);
//			System.out.println(ret);
			return ret;
		}
		
		public DataRow getAttributesRecord(){
			DataRow ar = retrieve().getItem(0);
			return ar;
			//TODO: do a solid implementation based on the JDBC table metadata
		}
		

}
