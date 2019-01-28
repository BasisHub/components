package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.basiscomponents.bc.util.CloseableWrapper;
import com.basiscomponents.bc.util.SqlConnectionHelper;
import com.basiscomponents.configuration.TracingConfiguration;
import com.basiscomponents.db.ResultSet;

public class SqlQueryBC {

	private SqlConnectionHelper connectionHelper;

	/**
	 * default Constructor only with url
	 * 
	 * @param url
	 *            the {@code url} to connect to
	 */
	public SqlQueryBC(String url) {
		this.connectionHelper = new SqlConnectionHelper(url);
	}

	/**
	 * default constuctor setting fields
	 * 
	 * @param driver
	 *            the classname of the driver in use
	 * @param url
	 *            the {@code url} to connect to
	 * @param user
	 *            username for the given sql connection
	 * @param password
	 *            password for the given connection
	 * @throws ClassNotFoundException
	 *             if the given driver can not be found by {@link ClassLoader}
	 */
	public SqlQueryBC(String driver, String url, String user, String password) throws ClassNotFoundException {
		this.connectionHelper = new SqlConnectionHelper(url, user, password, driver);
	}

	/**
	 * Contructor with a {@link Connection} to use
	 * 
	 * @param con
	 *            the {@link Connection} to use to interact with
	 * @throws SQLException
	 *             if {@link Connection#isClosed()} throws a {@link SQLException}
	 */
	public SqlQueryBC(Connection con) throws SQLException {
		this.connectionHelper = new SqlConnectionHelper(con);
	}

	public ResultSet retrieve(String sql) throws SQLException {
		return retrieve(sql, null);
	}

	public ResultSet retrieve(String sql, List<Object> params) throws SQLException {
		ResultSet brs = null;
		Connection connection = null;

		try (CloseableWrapper<Connection> connw = getConnection()) {
			connection = connw.getCloseable();
			try (PreparedStatement prep = connection.prepareStatement(sql)) {

				// Set params if there are any
				if (params != null) {
					int i = 1;
					for (Object p : params) {
						prep.setObject(i, p);
						i++;
					}
				}

				traceSqlStatement(SqlQueryBcLogger.Method.RETRIEVE, prep.toString());

				brs = new ResultSet(prep.executeQuery());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return brs;
	}

	public Boolean execute(String sql) throws SQLException {
		return execute(sql, null);
	}

	public Boolean execute(String sql, List<Object> params) throws SQLException {
		Connection connection = null;
		Boolean b = false;

		try (CloseableWrapper<Connection> connw = getConnection()) {
			connection = connw.getCloseable();
			PreparedStatement prep = connection.prepareStatement(sql);
			// Set params if there are any
			if (params != null) {
				int i = 1;
				for (Object p : params) {
					prep.setObject(i, p);
					i++;
				}
			}
			traceSqlStatement(SqlQueryBcLogger.Method.EXECUTE, prep.toString());
			b = prep.execute() || prep.getUpdateCount() > 0;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return b;
	}

	/**
	 * helper Method to get the last SQL Statementperformed to turn on tracing see
	 * {@link TracingConfiguration.SQL}
	 * 
	 * @return the last performed and logged SQL statement. Can be {@code null}
	 */
	public String getLastSql() {
		return SqlQueryBcLogger.getLastSQL();
	}

	/**
	 * helper Method to get the last SQL Statemen tperformed in
	 * {@link #retrieve(String, List)} to turn on tracing see
	 * {@link TracingConfiguration.SQL}
	 * 
	 * @return the last performed and logged SQL statement by
	 *         {@link #retrieve(String, List)}. Can be {@code null}
	 */
	public String getLastRetrieve() {
		return SqlQueryBcLogger.getLastRetrieve();
	}

	/**
	 * helper Method to get the last SQL Statementperformed in
	 * {@link #execute(String, List)} to turn on tracing see
	 * {@link TracingConfiguration.SQL}
	 * 
	 * @return the last performed and logged SQL statement by
	 *         {@link #retrieve(String, List)}. Can be {@code null}
	 */
	public String getLastExecute() {
		return SqlQueryBcLogger.getLastExecute();
	}

	private CloseableWrapper<Connection> getConnection() throws SQLException {
		return connectionHelper.getConnection();
	}

	private static void traceSqlStatement(SqlQueryBcLogger.Method method, String string) {
		switch (method) {
		case RETRIEVE:
			SqlQueryBcLogger.traceRetrieve(string);
			break;
		case EXECUTE:
			SqlQueryBcLogger.traceExecute(string);
			break;
		default:
			// do nothing?
			break;
		}
	}

	private static class SqlQueryBcLogger {
		public enum Method {
			RETRIEVE, EXECUTE
		}

		private static String lastSQL;
		private static String lastRetrieve;
		private static String lastExecute;

		public static void traceExecute(final String string) {
			traceSql(string);
			if (TracingConfiguration.isTraceLastExecute()) {
				lastExecute = string;
			}
		}

		private static void traceSql(final String string) {
			if (TracingConfiguration.isTraceSql()) {
				lastSQL = string;
			}
		}

		public static void traceRetrieve(String string) {
			if (TracingConfiguration.isTraceLastRetrieve()) {
				lastExecute = string;
			}
		}

		public static String getLastSQL() {
			return lastSQL;
		}

		public static String getLastRetrieve() {
			return lastRetrieve;
		}

		public static String getLastExecute() {
			return lastExecute;
		}

		private SqlQueryBcLogger() {
			// should not be invoked, this is only a static helper class
		}
	}
}
