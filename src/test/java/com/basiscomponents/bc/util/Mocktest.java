package com.basiscomponents.bc.util;

import com.basiscomponents.bc.SqlTableBC;
import com.basiscomponents.db.ResultSet;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.basiscomponents.db.util.ResultSetProvider.createDefaultResultSet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mocktest {
	@Mock
	SqlTableBC tbc;


	@Test
	public void testMock()throws Exception{
		tbc = mock(SqlTableBC.class);
		when(tbc.retrieve()).thenReturn(createDefaultResultSet(false));
		ResultSet resultSet= tbc.retrieve();
		resultSet.print();


	}

}
