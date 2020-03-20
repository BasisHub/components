package com.basiscomponents.db.config.export;

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

	public ReportDetails(int reportLanguage, String reportName, String tableName, String reportID) {
		this(reportLanguage, reportName, tableName, reportID, new Date());
	}

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

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getReportID() {
		return reportID;
	}

	public void setReportID(String reportID) {
		this.reportID = reportID;
	}

	public Date getQueryTimestamp() {
		return queryTimestamp;
	}
	
	public void setQueryTimestamp(Date queryTimestamp) {
		this.queryTimestamp = queryTimestamp;
	}
	
	public Date getProcessDate() {
		return this.processDate;
	}
	
	public String getFormattedQueryTimestamp() {
		return new SimpleDateFormat(this.timestampMask).format(this.queryTimestamp);
	}
	
	public String getFormattedProcessDate() {
		return new SimpleDateFormat(this.dateMask).format(this.processDate);
	}
	
	public String getDateMask() {
		return dateMask;
	}

	public String getTimestampMask() {
		return timestampMask;
	}

	public char getNumberGroupSeparator() {
		return numberGroupSeparator;
	}

	public char getNumberDecimalSeparator() {
		return numberDecimalSeparator;
	}
	
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
