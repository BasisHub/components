package com.basiscomponents.db.util;

import com.basis.bbj.datatypes.TemplatedString;
import com.basis.startup.type.BBjException;
import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;

public class JLibUtilTest {
	private static final String templateString ="CDNUMBER:C(6*=10):DESCRIPTION=CD_Inventory_Number:,TITLE:C(50*=10):DESCRIPTION=CD_Title:,ARTIST:C(50*=10):DESCRIPTION=Artist_Name:,LABEL:C(50*=10):DESCRIPTION=Recording_Label:,PLAYINGTIME:C(6*=10):DESCRIPTION=CD_Playing_Time:,RECORDINGTYPE:C(3*=10):DESCRIPTION=CD_Recording_Type:,MUSICTYPE:C(15*=10):DESCRIPTION=Type_of_Music:,BINLOCATION:C(10*=10):DESCRIPTION=Location_of_Bin_containing_the_CD:,NUMBEROFTRACKS:N(10):DESCRIPTION=Number_of_Tracks:,ONHAND:N(10):DESCRIPTION=Quantity_on_Hand:,COST:N(10):DESCRIPTION=Wholesale_cost_of_CD:,RETAIL:N(10):DESCRIPTION=Retail_Price_of_CD:";
	@Test
	public void testToByteArray() throws BBjException {
		DataRow dr = new DataRow();
		dr.addDataField("CDNUMBER", new DataField("999999"));
		dr.addDataField("TITLE", new DataField("Never Mind the Bollocks, Here’s the Sex Pistols"));
		dr.addDataField("ARTIST", new DataField("Sex Pistols"));
		dr.addDataField("LABEL", new DataField("Virgin Records"));
		dr.addDataField("PLAYINGTIME", new DataField(39));
		dr.addDataField("RECORDINGTYPE", new DataField("AAD"));
		dr.addDataField("MUSICTYPE", new DataField("Punk"));
		dr.addDataField("BINLOCATION", new DataField("N128"));
		dr.addDataField("NUMBEROFTRACKS", new DataField(12.0));
		dr.addDataField("ONHAND", new DataField(33.0));
		dr.addDataField("COST", new DataField(6.42));
		dr.addDataField("RETAIL", new DataField(12.55));
		byte[] result =JLibUtil.toByteArray(dr, new TemplatedString(templateString));
		Assert.assertEquals("999999\nNever Mind the Bollocks, Here’s the Sex Pistols\nSex Pistols\nVirgin Records\n39\nAAD\nPunk\nN128\n12       33         6.42      12.55\n",new String(result));
	}
}
