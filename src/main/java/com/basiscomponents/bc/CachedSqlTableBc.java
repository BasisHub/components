package com.basiscomponents.bc;

import com.basiscomponents.bc.config.CachingConfiguration;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

import java.sql.Connection;
import java.sql.SQLException;

public class CachedSqlTableBc extends SqlTableBC {
	private long created;
	private CachingConfiguration configuration;
	private ResultSet cachedRs;

	public CachedSqlTableBc(final String url, CachingConfiguration configuration) {
		super(url);
		this.configuration = configuration;
	}

	public CachedSqlTableBc(final String driver, final String url, final String user, final String password, CachingConfiguration configuration) throws ClassNotFoundException {
		super(driver, url, user, password);
		this.configuration = configuration;
	}

	public CachedSqlTableBc(final Connection con, CachingConfiguration configuration) throws SQLException {
		super(con);
		this.configuration = configuration;
	}
	
	@Override
	public ResultSet retrieve() throws Exception {
		long currentTimeMillis =System.currentTimeMillis();
		if(created ==0 || ((currentTimeMillis - created)>configuration.getTimeToLive())) {
			this.created = currentTimeMillis;
			this.cachedRs = super.retrieve();
		}
		return cachedRs.clone();
	}

	@Override
	public DataRow write(final DataRow dr) throws Exception {
		this.created = 0;
		return super.write(dr);
	}

	@Override
	public void remove(final DataRow r) throws Exception {
		this.created = 0;
		super.remove(r);
	}
}
