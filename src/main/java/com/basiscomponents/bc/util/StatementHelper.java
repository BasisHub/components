package com.basiscomponents.bc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * internal Helper Class, do not use, because it might be changed heavily in the
 * future
 * 
 * @author David Amore
 *
 */
public class StatementHelper {
	private StatementHelper() {

	}
	public static CloseableWrapper<PreparedStatement> prepareStatement(Connection conn, String sql)
			throws SQLException {
		return new CloseableWrapper<>(conn.prepareStatement(sql), true);
	}

}
