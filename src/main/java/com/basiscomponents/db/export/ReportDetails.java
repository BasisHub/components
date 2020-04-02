package com.basiscomponents.db.export;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class ReportDetails {
	
	public static final int REPORT_LANGUAGE_DE_DE = 0;
	public static final int REPORT_LANGUAGE_EN_US = 1;
	public static final int REPORT_LANGUAGE_EN_GB = 2;
	public static final int REPORT_LANGUAGE_NL_NL = 3;
	
	private String reportName;
	private String tableName;
	private String reportID;
	private Date queryTimestamp;
	private Date processDate;
	private int reportLanguage;
	
	private String dateMask;
	private String timestampMask;
	private char numberGroupSeparator;
	private char numberDecimalSeparator;

	/**
	 * Initializes the report details
	 * 
	 * @param reportLanguage	identifier for the report language
	 * @param reportName		the report's name
	 * @param tableName			the table's name that is used in the report
	 * @param reportID			the report's id
	 */
	public ReportDetails(int reportLanguage, String reportName, String tableName, String reportID) {
		this(reportLanguage, reportName, tableName, reportID, new Date());
	}

	/**
	 * Initializes the report details
	 * 
	 * @param reportLanguage	identifier for the report language
	 * @param reportName		the report's name
	 * @param tableName			the table's name that is used in the report
	 * @param reportID			the report's id
	 * @param queryTimestamp	the query's execution time stamp 
	 */
	public ReportDetails(int reportLanguage, String reportName, String tableName, String reportID, Date queryTimestamp){
		this.reportName = reportName;
		this.tableName = tableName;
		this.reportID = reportID;
		this.processDate = new Date();
		if (queryTimestamp == null) {
			this.queryTimestamp = new Date();
		}else {
			this.queryTimestamp = queryTimestamp;
		}
		this.reportLanguage = reportLanguage;
		this.setMasks(this.reportLanguage);
	}

	/**
	 * Returns the report name
	 * 
	 * @return	the report name
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * Sets the report name
	 * 
	 * @param reportName	the report name
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * Returns the table name
	 * 
	 * @return	the table name
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Sets the table name
	 * 
	 * @param tableName	the table name
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Returns the report id
	 * 
	 * @return	the report id
	 */
	public String getReportID() {
		return reportID;
	}

	/**
	 * Sets the report id
	 * 
	 * @param reportID the report id
	 */
	public void setReportID(String reportID) {
		this.reportID = reportID;
	}

	/**
	 * Returns the query time stamp
	 * 
	 * @return	the query time stamp
	 */
	public Date getQueryTimestamp() {
		return queryTimestamp;
	}
	
	/**
	 * Sets the query time stamp
	 * 
	 * @param queryTimestamp the query time stamp
	 */
	public void setQueryTimestamp(Date queryTimestamp) {
		this.queryTimestamp = queryTimestamp;
	}
	
	/**
	 * Returns the process date
	 * 
	 * @return	the process date
	 */
	public Date getProcessDate() {
		return this.processDate;
	}
	
	/**
	 * Returns the formatted query time stamp
	 * 
	 * @return	the formatted query time stamp
	 */
	public String getFormattedQueryTimestamp() {
		return new SimpleDateFormat(this.timestampMask).format(this.queryTimestamp);
	}
	
	/**
	 * Returns the formatted process date
	 * 
	 * @return	the formatted process date
	 */
	public String getFormattedProcessDate() {
		return new SimpleDateFormat(this.dateMask).format(this.processDate);
	}
	
	/**
	 * Returns the date mask
	 * 
	 * @return	the date mask
	 */
	public String getDateMask() {
		return dateMask;
	}

	/**
	 * Returns the time stamp mask
	 * 
	 * @return	the time stamp mask
	 */
	public String getTimestampMask() {
		return timestampMask;
	}

	/**
	 * Returns the number group separator
	 * 
	 * @return	the number group separator
	 */
	public char getNumberGroupSeparator() {
		return numberGroupSeparator;
	}

	/**
	 * Returns the number decimal separator
	 * 
	 * @return	the number decimal separator
	 */
	public char getNumberDecimalSeparator() {
		return numberDecimalSeparator;
	}
	
	/**
	 * Sets all the masks
	 * 
	 * @param reportLanguage	the report's language
	 */
	private void setMasks(int reportLanguage) {
		switch(reportLanguage) {
		case REPORT_LANGUAGE_DE_DE:
			this.dateMask = "dd.MM.YYYY";
			this.timestampMask = "dd.MM.YYYY HH:mm:ss";
			this.numberGroupSeparator = '.';
			this.numberDecimalSeparator = ',';
			break;
		case REPORT_LANGUAGE_EN_US:
			this.dateMask = "MM/dd/YYYY";
			this.timestampMask = "MM/dd/YYYY hh:mm:ss";
			this.numberGroupSeparator = ',';
			this.numberDecimalSeparator = '.';
			break;
		case REPORT_LANGUAGE_EN_GB:
			this.dateMask = "dd/MM/YYYY";
			this.timestampMask = "dd/MM/YYYY hh:mm:ss";
			this.numberGroupSeparator = ',';
			this.numberDecimalSeparator = '.';
			break;
		case REPORT_LANGUAGE_NL_NL:
			this.dateMask = "dd-MM-YYYY";
			this.timestampMask = "dd-MM-YYYY hh:mm:ss";
			this.numberGroupSeparator = '.';
			this.numberDecimalSeparator = ',';
			break;
		default:
			this.dateMask = "MM/dd/YYYY";
			this.timestampMask = "MM/dd/YYYY hh:mm:ss";
			this.numberGroupSeparator = ',';
			this.numberDecimalSeparator = '.';
			break;
		}
	}
	
}
