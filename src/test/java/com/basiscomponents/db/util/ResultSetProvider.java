package com.basiscomponents.db.util;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class ResultSetProvider {

	public static ResultSet createDefaultResultSet() throws Exception {
		return createDefaultResultSet(false);
	}

  public static ResultSet createDefaultResultSet(boolean nullAllFields) throws Exception {
    ResultSet result = new ResultSet();
    result.add(DataRowProvider.buildSampleDataRow(nullAllFields));
    return result;
	}
  
  public static ResultSet createMultipleDataRowResultSet() throws Exception {
	    ResultSet result = new ResultSet();
	    result.add(DataRowProvider.buildSampleDataRow(false));
	    result.add(DataRowProvider.buildSampleDataRow(false));
	    result.add(DataRowProvider.buildSampleDataRow(false));
	    return result;
	  }

  public static ResultSet createDefaultResultSetMinMax() throws Exception {
	    ResultSet result = new ResultSet();
	    result.add(DataRowProvider.buildSampleDataRowMinMax());
	    return result;
	  }
  
  public static ResultSet createStringOnlyResultSet() throws Exception {
	  ResultSet result = new ResultSet();
	  result.add(DataRowProvider.buildStringOnlyDataRow());
	  return result;
  }
  
	public static ResultSet createNestedDataRowsResultSet() throws Exception {
		ResultSet result = new ResultSet();
		result.add(DataRowProvider.buildNestedDataRowWithDataRows());
		return result;
	}

	public static ResultSet createNestedResultSetsResultSet() throws Exception {
		ResultSet result = new ResultSet();
		result.add(DataRowProvider.buildNestedDataRowWithResultSets());
		return result;
	}

	public static ResultSet createNestedResultSetsWithMultipleDataRowsResultSet() throws Exception {
		ResultSet result = new ResultSet();
		result.add(DataRowProvider.buildNestedDataRowWithMultipleDataRowsResultSet());
		return result;
	}

	public static ResultSet createToJsonOnlyResultSet() throws Exception {
		ResultSet result = new ResultSet();
		result.add(DataRowProvider.buildToJsonOnlyDataRow());
		return result;
	}

	public static ResultSet createLeftResultSetForLeftJoinTesting() throws ParseException {
		
		ResultSet rs = new ResultSet();
		DataRow dr1 = new DataRow();
		DataRow dr2 = new DataRow();
		DataRow dr3 = new DataRow();
		DataRow dr4 = new DataRow();

		dr1.setFieldValue("Name", "Heinz");
		dr1.setFieldValue("Alter", 53);
		dr1.setFieldValue("PLZ", "66132");
		rs.add(dr1);

		dr2.setFieldValue("Name", "Frank");
		dr2.setFieldValue("Alter", 52);
		dr2.setFieldValue("PLZ", "66122");
		rs.add(dr2);

		dr3.setFieldValue("Name", "Laura");
		dr3.setFieldValue("Alter", 50);
		dr3.setFieldValue("PLZ", "66156");
		rs.add(dr3);
		
		dr4.setFieldValue("Name", "unkown");
		dr4.setFieldValue("Alter", 49);
		dr4.setFieldValue("PLZ", "66000");
		rs.add(dr4);

		return rs;

	}

	public static ResultSet createRightResultSetForLeftJoinTesting() throws ParseException {

		ResultSet rs = new ResultSet();
		DataRow dr1 = new DataRow();
		DataRow dr2 = new DataRow();
		DataRow dr3 = new DataRow();
		DataRow dr4 = new DataRow();
		DataRow dr5 = new DataRow();
		DataRow dr6 = new DataRow();
		DataRow dr7 = new DataRow();
		DataRow dr8 = new DataRow();
		DataRow dr9 = new DataRow();
		DataRow dr10 = new DataRow();
		DataRow dr11 = new DataRow();
		DataRow dr12 = new DataRow();
		DataRow dr13 = new DataRow();
		DataRow dr14 = new DataRow();
		DataRow dr15 = new DataRow();
		DataRow dr16 = new DataRow();
		DataRow dr17 = new DataRow();
		DataRow dr18 = new DataRow();
		DataRow dr19 = new DataRow();
		DataRow dr20 = new DataRow();
		DataRow dr21 = new DataRow();

		dr1.setFieldValue("PLZ", "66132");
		dr1.setFieldValue("Ort", "Saarbruecken");
		dr1.setFieldValue("Buergermeister", "Elias");
		rs.add(dr1);

		dr2.setFieldValue("PLZ", "66122");
		dr2.setFieldValue("Ort", "St. Wendel");
		dr2.setFieldValue("Buergermeister", "Sascha");
		rs.add(dr2);

		dr3.setFieldValue("PLZ", "66156");
		dr3.setFieldValue("Ort", "Dillingen");
		dr3.setFieldValue("Buergermeister", "Dude");
		rs.add(dr3);

		dr4.setFieldValue("PLZ", "66133");
		dr4.setFieldValue("Ort", "Saarbruecken");
		dr4.setFieldValue("Buergermeister", "Elias");
		rs.add(dr4);

		dr5.setFieldValue("PLZ", "66134");
		dr5.setFieldValue("Ort", "St. Wendel");
		dr5.setFieldValue("Buergermeister", "Sascha");
		rs.add(dr5);

		dr6.setFieldValue("PLZ", "66135");
		dr6.setFieldValue("Ort", "Dillingen");
		dr6.setFieldValue("Buergermeister", "Dude");
		rs.add(dr6);

		dr7.setFieldValue("PLZ", "66136");
		dr7.setFieldValue("Ort", "Saarbruecken");
		dr7.setFieldValue("Buergermeister", "Elias");
		rs.add(dr7);

		dr8.setFieldValue("PLZ", "66137");
		dr8.setFieldValue("Ort", "St. Wendel");
		dr8.setFieldValue("Buergermeister", "Sascha");
		rs.add(dr8);

		dr9.setFieldValue("PLZ", "66138");
		dr9.setFieldValue("Ort", "Dillingen");
		dr9.setFieldValue("Buergermeister", "Dude");
		rs.add(dr9);

		dr10.setFieldValue("PLZ", "66139");
		dr10.setFieldValue("Ort", "Saarbruecken");
		dr10.setFieldValue("Buergermeister", "Elias");
		rs.add(dr10);

		dr11.setFieldValue("PLZ", "66140");
		dr11.setFieldValue("Ort", "St. Wendel");
		dr11.setFieldValue("Buergermeister", "Sascha");
		rs.add(dr11);

		dr12.setFieldValue("PLZ", "66141");
		dr12.setFieldValue("Ort", "Dillingen");
		dr12.setFieldValue("Buergermeister", "Dude");
		rs.add(dr12);

		dr13.setFieldValue("PLZ", "66142");
		dr13.setFieldValue("Ort", "Saarbruecken");
		dr13.setFieldValue("Buergermeister", "Elias");
		rs.add(dr13);

		dr14.setFieldValue("PLZ", "66143");
		dr14.setFieldValue("Ort", "St. Wendel");
		dr14.setFieldValue("Buergermeister", "Sascha");
		rs.add(dr14);

		dr15.setFieldValue("PLZ", "66144");
		dr15.setFieldValue("Ort", "Dillingen");
		dr15.setFieldValue("Buergermeister", "Dude");
		rs.add(dr15);

		dr16.setFieldValue("PLZ", "66145");
		dr16.setFieldValue("Ort", "Saarbruecken");
		dr16.setFieldValue("Buergermeister", "Elias");
		rs.add(dr16);

		dr17.setFieldValue("PLZ", "66146");
		dr17.setFieldValue("Ort", "St. Wendel");
		dr17.setFieldValue("Buergermeister", "Sascha");
		rs.add(dr17);

		dr18.setFieldValue("PLZ", "66147");
		dr18.setFieldValue("Ort", "Dillingen");
		dr18.setFieldValue("Buergermeister", "Dude");
		rs.add(dr18);

		dr19.setFieldValue("PLZ", "66148");
		dr19.setFieldValue("Ort", "Saarbruecken");
		dr19.setFieldValue("Buergermeister", "Elias");
		rs.add(dr19);

		dr20.setFieldValue("PLZ", "66149");
		dr20.setFieldValue("Ort", "St. Wendel");
		dr20.setFieldValue("Buergermeister", "Sascha");
		rs.add(dr20);

		dr21.setFieldValue("PLZ", "66150");
		dr21.setFieldValue("Ort", "Dillingen");
		dr21.setFieldValue("Buergermeister", "Dude");
		rs.add(dr21);

		return rs;

	}

	public static ResultSet createAnotherRightResultSetForLeftJoinTesting() throws ParseException {

		ResultSet rs = new ResultSet();
		DataRow dr1 = new DataRow();
		DataRow dr2 = new DataRow();
		DataRow dr3 = new DataRow();

		dr1.setFieldValue("PLZ", "66132");
		dr1.setFieldValue("Stadt", "Moskau");
		dr1.setFieldValue("Minister", "Dimitri");
		rs.add(dr1);

		dr2.setFieldValue("PLZ", "66122");
		dr2.setFieldValue("Stadt", "Wien");
		dr2.setFieldValue("Minister", "Huber");
		rs.add(dr2);

		dr3.setFieldValue("PLZ", "66156");
		dr3.setFieldValue("Stadt", "Konz");
		dr3.setFieldValue("Minister", "Peter");
		rs.add(dr3);

		return rs;

	}

	public static ResultSet createMoreTypesRightResultSetForLeftJoinTesting() throws ParseException {

		ResultSet rs = new ResultSet();
		DataRow dr1 = new DataRow();
		DataRow dr2 = new DataRow();
		DataRow dr3 = new DataRow();

		dr1.setFieldValue("PLZ", "66132");
		dr1.setFieldValue("Double", 54.45);
		dr1.setFieldValue("Long", Long.valueOf("5454544355464354"));
		dr1.setFieldValue("Date", new Date(System.currentTimeMillis()));
		dr1.setFieldValue("List", new ArrayList<String>().add("hi"));
		rs.add(dr1);

		dr2.setFieldValue("PLZ", "66122");
		dr2.setFieldValue("Double", 54.45);
		dr2.setFieldValue("Long", Long.valueOf("5454544355464354"));
		dr2.setFieldValue("Date", new Date(System.currentTimeMillis()));
		dr2.setFieldValue("List", new ArrayList<String>().add("hi"));
		rs.add(dr2);

		dr3.setFieldValue("PLZ", "66156");
		dr3.setFieldValue("Double", 54.45);
		dr3.setFieldValue("Long", Long.valueOf("5454544355464354"));

		dr3.setFieldValue("Date", new Date(System.currentTimeMillis()));
		dr3.setFieldValue("List", new ArrayList<String>().add("hi"));
		rs.add(dr3);

		return rs;

	}

}
