package com.basiscomponents.db.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.basiscomponents.db.ResultSet;

public class SQLResultSet implements java.sql.ResultSet {
	
	private ResultSet c_rs;
	
	public SQLResultSet(ResultSet rs){
		this.c_rs = rs;
	}

	@Override
	public boolean next() throws SQLException {
		return c_rs.next();
	}
	
	@Override
	public boolean first() throws SQLException {
		return c_rs.first();
	}
	
	@Override
	public String getString(int columnIndex) throws SQLException {
		return c_rs.getString(columnIndex -1);
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		return c_rs.getString(c_rs.getColumnIndex(columnLabel));
	}
	
	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		return c_rs.getClob(columnIndex -1);
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		return c_rs.getClob(c_rs.getColumnIndex(columnLabel));
	}
	
	@Override
	public Date getDate(int columnIndex) throws SQLException {
		return c_rs.getDate(columnIndex -1);
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		return c_rs.getDate(c_rs.getColumnIndex(columnLabel));
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		return c_rs.getDouble(columnIndex -1);
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		return c_rs.getDouble(c_rs.getColumnIndex(columnLabel));
	}
	
	@Override
	public float getFloat(int columnIndex) throws SQLException {
		return c_rs.getFloat(columnIndex -1);
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		return c_rs.getFloat(c_rs.getColumnIndex(columnLabel));
	}
	
	@Override
	public int getInt(int columnIndex) throws SQLException {
		return c_rs.getInt(columnIndex -1);
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		return c_rs.getInt(c_rs.getColumnIndex(columnLabel));
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		return c_rs.getLong(columnIndex -1);
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		return c_rs.getLong(c_rs.getColumnIndex(columnLabel));
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		return c_rs.getNString(columnIndex -1);
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		return c_rs.getNString(c_rs.getColumnIndex(columnLabel));
	}
	
	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return c_rs.getObject(columnIndex -1);
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		return c_rs.getRef(columnIndex -1);
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		return c_rs.getRef(c_rs.getColumnIndex(columnLabel));
	}
	
	@Override
	public int getRow() throws SQLException {
		return c_rs.getRow();
	}
	
	@Override
	public short getShort(int columnIndex) throws SQLException {
		return c_rs.getShort(columnIndex -1);		
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		return c_rs.getShort(c_rs.getColumnIndex(columnLabel));	
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		return c_rs.getTime(columnIndex -1);
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		return c_rs.getTime(c_rs.getColumnIndex(columnLabel));
	}
	
	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return c_rs.getTimestamp(columnIndex -1);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return c_rs.getTimestamp(c_rs.getColumnIndex(columnLabel));
	}
	
	@Override
	public URL getURL(int columnIndex) throws SQLException {
		return c_rs.getURL(columnIndex -1);
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		return c_rs.getURL(c_rs.getColumnIndex(columnLabel));
	}
	
	@Override
	public Array getArray(int arg0) throws SQLException {
		return c_rs.getArray(arg0 -1);
	}

	@Override
	public Array getArray(String arg0) throws SQLException {
		return c_rs.getArray(c_rs.getColumnIndex(arg0));
	}

	@Override
	public BigDecimal getBigDecimal(int arg0) throws SQLException {
		return c_rs.getBigDecimal(arg0 -1);
	}

	@Override
	public BigDecimal getBigDecimal(String arg0) throws SQLException {
		return c_rs.getBigDecimal(c_rs.getColumnIndex(arg0));
	}

	@Override
	public Blob getBlob(int arg0) throws SQLException {
		return c_rs.getBlob(arg0 -1);
	}

	@Override
	public Blob getBlob(String arg0) throws SQLException {
		return c_rs.getBlob(c_rs.getColumnIndex(arg0));
	}

	@Override
	public boolean getBoolean(int arg0) throws SQLException {
		return c_rs.getBoolean(arg0 -1);
	}

	@Override
	public boolean getBoolean(String arg0) throws SQLException {
		return c_rs.getBoolean(c_rs.getColumnIndex(arg0));
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		return c_rs.getByte(columnIndex -1);
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		return c_rs.getByte(c_rs.getColumnIndex(columnLabel));
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		return c_rs.getBytes(columnIndex -1);
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		return c_rs.getBytes(c_rs.getColumnIndex(columnLabel));
	}

	@Override
	public boolean last() throws SQLException {
		return c_rs.last();
	}

	public ResultSet getResultSet(){
		return c_rs;
	}
	
	@Override
	public void beforeFirst() throws SQLException {
		c_rs.beforeFirst();
	}
	
	@Override
	public void afterLast() throws SQLException {
		c_rs.afterLast();
	}
	
	@Override
	public int getConcurrency() throws SQLException {
		return java.sql.ResultSet.CONCUR_READ_ONLY;
	}
	
	@Override
	public void close() throws SQLException {
		
	}

	@Override
	public int getType() throws SQLException {
		return java.sql.ResultSet.TYPE_FORWARD_ONLY;
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return new SQLResultSetMetaData(c_rs);
	} 
	
	@Override
	public boolean rowDeleted() throws SQLException {
		return false;
	}
	
	@Override
	public boolean wasNull() throws SQLException {
		return false;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void deleteRow() throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return 0;
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public String getCursorName() throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new UnsupportedOperationException();
		// return 0;
	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new UnsupportedOperationException();
		// return 0;
	}

	@Override
	public int getHoldability() throws SQLException {
		throw new UnsupportedOperationException();
		// return 0;
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Statement getStatement() throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
//		throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public void insertRow() throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public boolean isClosed() throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public boolean isFirst() throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public boolean isLast() throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public boolean previous() throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public void refreshRow() throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public boolean rowInserted() throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		throw new UnsupportedOperationException();
		// return false;
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateRow() throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		throw new UnsupportedOperationException();
		
	}

}
