package com.basiscomponents.configuration;

public class TracingConfiguration {
	private TracingConfiguration() {
	}

	private static boolean traceSql = false;

	private static boolean traceLastExecute = false;
	private static boolean traceLastRetrieve = false;
	private static int traceLastXStatements = 0;

	public static boolean isTraceSql() {
		return traceSql;
	}

	public static int getTraceLastXStatements() {
		return traceLastXStatements;
	}

	public static boolean isTraceLastExecute() {
		return traceLastExecute;
	}

	public static final boolean isTraceLastRetrieve() {
		return traceLastRetrieve;
	}

	public static void setTraceSql(boolean traceSql) {
		TracingConfiguration.traceSql = traceSql;
	}

	public static void setTraceLastExecute(boolean traceLastExecute) {
		TracingConfiguration.traceLastExecute = traceLastExecute;
	}

	public static void setTraceLastRetrieve(boolean traceLastRetrieve) {
		TracingConfiguration.traceLastRetrieve = traceLastRetrieve;
	}

	public static void setTraceLastXStatements(int traceLastXStatements) {
		TracingConfiguration.traceLastXStatements = traceLastXStatements;
	}

}
