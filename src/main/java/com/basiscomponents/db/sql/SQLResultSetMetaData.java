package com.basiscomponents.db.sql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.basiscomponents.db.ResultSet;

public class SQLResultSetMetaData implements ResultSetMetaData {
	
	private ResultSet rs;

	public SQLResultSetMetaData(ResultSet rs){
		this.rs = rs;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		return rs.getCatalogName(column - 1);
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		return rs.getColumnClassName(column - 1);
	}

	@Override
	public int getColumnCount() throws SQLException {
		return rs.getColumnCount();
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		return rs.getColumnDisplaySize(column - 1);
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		return rs.getColumnLabel(column - 1);
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		return rs.getColumnName(column - 1);
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		return rs.getColumnType(column - 1);
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		return rs.getColumnTypeName(column - 1);
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		return rs.getPrecision(column - 1);
	}

	@Override
	public int getScale(int column) throws SQLException {
		return rs.getScale(column - 1);
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		return rs.getSchemaName(column - 1);
	}

	@Override
	public String getTableName(int column) throws SQLException {
		return rs.getTableName(column - 1);
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		return rs.isAutoIncrement(column - 1);
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		return rs.isCaseSensitive(column - 1);
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		return rs.isCurrency(column - 1);
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return rs.isDefinitelyWritable(column - 1);
	}

	@Override
	public int isNullable(int column) throws SQLException {
		return rs.isNullable(column - 1);
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		return rs.isReadOnly(column - 1);
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		return rs.isSearchable(column - 1);
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		return rs.isSigned(column - 1);
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		return rs.isWritable(column - 1);
	}

}
