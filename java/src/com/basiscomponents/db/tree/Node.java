package com.basiscomponents.db.tree;


import java.util.ArrayList;
import java.util.List;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class Node {

	    private List<Node> children = new ArrayList<>();
	    private ResultSet resultset = new ResultSet();
	    private Node parent = null;
	    private DataRow rowData = new DataRow();

		public Node(String text) {
	        this.rowData.setFieldValue("__node__name", text);
	    }

	    public Node(String text, Node parent) {
	    	this.rowData.setFieldValue("__node__name", text);
	        this.parent = parent;
	    }

	    public List<Node> getChildren() {
	        return children;
	    }
	    
	    public ResultSet getResultSet() {
	        return resultset;
	    }

	    public void setParent(Node n){
	    	this.parent = n;
	    }
	    
	    public Node addChild(String text) {
	        Node child = new Node(text);
	        child.setParent(this);
	        this.children.add(child);
	        return child;
	    }


	    public String getText() {
			try {
				return this.rowData.getFieldAsString("__node__name");
			} catch (Exception e) {
				return "...";
			}
		}

		public void setText(String text) {
			this.rowData.setFieldValue("__node__name", text);
		}
	    
	    public boolean isRoot() {
	        return (this.parent == null);
	    }

	    public boolean isLeaf() {
	        if(this.children.size() == 0) 
	            return true;
	        else 
	            return false;
	    }

	    public void removeParent() {
	        this.parent = null;
	    }
	    
	    public void setResultSet(ResultSet rs){
	    	if (rs != null)
	    		this.resultset = rs;
	    }
	    
	    public void add(DataRow rec){
	    	if (rec != null)
	    		this.resultset.add(rec);
	    }

		public boolean hasChildren() {
			return !this.children.isEmpty();
		}

		public DataRow getRowData() {
			return this.rowData;
		}

		public void setFieldValue(String string, Object value) {
			this.rowData.setFieldValue(string, value);
			
		}

		public boolean hasResultSet() {
			return !this.resultset.isEmpty();
		}

}
