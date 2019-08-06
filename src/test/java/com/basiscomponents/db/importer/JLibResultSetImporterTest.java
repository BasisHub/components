package com.basiscomponents.db.importer;

import org.junit.Test;

import com.basiscomponents.db.ResultSet;

public class JLibResultSetImporterTest {

	@Test
	public void jlibImporterTest() throws IndexOutOfBoundsException, NoSuchFieldException, Exception {
		String template = "CDNUMBER:C(6*=10):DESCRIPTION=CD_Inventory_Number:,TITLE:C(50*=10):DESCRIPTION=CD_Title:,ARTIST:C(50*=10):DESCRIPTION=Artist_Name:,LABEL:C(50*=10):DESCRIPTION=Recording_Label:,PLAYINGTIME:C(6*=10):DESCRIPTION=CD_Playing_Time:,RECORDINGTYPE:C(3*=10):DESCRIPTION=CD_Recording_Type:,MUSICTYPE:C(15*=10):DESCRIPTION=Type_of_Music:,BINLOCATION:C(10*=10):DESCRIPTION=Location_of_Bin_containing_the_CD:,NUMBEROFTRACKS:N(10):DESCRIPTION=Number_of_Tracks:,ONHAND:N(10):DESCRIPTION=Quantity_on_Hand:,COST:N(10):DESCRIPTION=Wholesale_cost_of_CD:,RETAIL:N(10):DESCRIPTION=Retail_Price_of_CD:";
		JLibResultSetImporter jri = new JLibResultSetImporter();
		jri.setFile("C:\\bbj\\demos\\cdstore\\data", template);
		ResultSet rs = jri.retrieve();
		System.out.println(rs.toJson());
	}
}
