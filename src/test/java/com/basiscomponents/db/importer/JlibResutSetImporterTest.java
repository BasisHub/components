package com.basiscomponents.db.importer;

import com.basiscomponents.db.ResultSet;
import org.junit.jupiter.api.Test;

public class JlibResutSetImporterTest {
	@Test
	public void testInitialisation() throws Exception {
		JLibResultSetImporter importer = new JLibResultSetImporter();

		importer.setFile("/opt/bbj/demos/cdstore/data/cdstore","CDNUMBER:C(6*=10):DESCRIPTION=CD_Inventory_Number:,TITLE:C(50*=10):DESCRIPTION=CD_Title:,ARTIST:C(50*=10):DESCRIPTION=Artist_Name:,LABEL:C(50*=10):DESCRIPTION=Recording_Label:,PLAYINGTIME:C(6*=10):DESCRIPTION=CD_Playing_Time:,RECORDINGTYPE:C(3*=10):DESCRIPTION=CD_Recording_Type:,MUSICTYPE:C(15*=10):DESCRIPTION=Type_of_Music:,BINLOCATION:C(10*=10):DESCRIPTION=Location_of_Bin_containing_the_CD:,NUMBEROFTRACKS:N(10):DESCRIPTION=Number_of_Tracks:,ONHAND:N(10):DESCRIPTION=Quantity_on_Hand:,COST:N(10):DESCRIPTION=Wholesale_cost_of_CD:,RETAIL:N(10):DESCRIPTION=Retail_Price_of_CD:");
		ResultSet rs = importer.retrieve();
	}
}
