package com.basiscomponents.db.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.basiscomponents.db.DataRow;

public class DataTree{

	private Node rootNode = new Node("root");
	
	public DataTree() {
	}
	
	public Node getRoot(){
		return rootNode;
	}

	public String toJson() {

		StringBuilder rowData = new StringBuilder();
		rowData.append("[");
		traverseChildren(rootNode,rowData);
		rowData.append("]");
		return rowData.toString();
	}

	private void traverseChildren(Node node, StringBuilder rowData) {
		Boolean first=true;
		List<Node> children = node.getChildren();
		Iterator<Node> it = children.iterator();
		while (it.hasNext()){
			Node child = it.next();
			
			if (!first)
				rowData.append(",");
			else
				first=false;
			
			try {
				String tmp = child.getRowData().toJson(false);
				tmp=tmp.substring(1,tmp.length()-2);
				rowData.append(tmp);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (child.hasChildren() || child.hasResultSet()){
				rowData.append(",\"__node__children\":[");
			
				if (child.hasChildren()){
					traverseChildren(child, rowData);
					}
			
				if (child.hasResultSet()){
					try {
						
						String tmp = child.getResultSet().toJson(false);
						tmp=tmp.substring(1,tmp.length()-1);
						rowData.append(tmp);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				
				}
				rowData.append("]");
			}
			rowData.append("}");
			
			

		}
	
	}

	public DataRow getFirstRecord() {
		
		DataRow r = this.getFirstRecord(this.getRoot());
		if (r != null)
			return r;
		else
			return new DataRow();
			
				
	}
	
	private DataRow getFirstRecord(Node n){
		Iterator<Node> it = n.getChildren().iterator();
		while (it.hasNext()){
			Node c = it.next();
			if (c.hasResultSet() && !c.getResultSet().isEmpty())
				return c.getResultSet().get(0);
		}
		
		//none of these nodes had a good resultset, so start descending into nodes
		DataRow r;
		it = n.getChildren().iterator();
		while (it.hasNext()){
			Node c = it.next();
			
			if (c.hasChildren()){
				r = getFirstRecord(c);
				if (r != null)
					return r;
			}
		}
		
		return null;
	}
	

}
