package com.basiscomponents.db.exporter;

import com.basis.bbj.datatypes.TemplatedString;
import com.basis.filesystem.ConnectionMgr;
import com.basis.filesystem.FilePosition;
import com.basis.filesystem.Filesystem;
import com.basis.filesystem.FilesystemException;
import com.basis.startup.type.BBjException;
import com.basiscomponents.db.DataRow;

import java.io.PrintWriter;

public class JlibResultSetExporter {
	private final TemplatedString bbjtemplate;
	String fileName;
	String template;
	public JlibResultSetExporter(String fileName,String template){
		this.fileName=fileName;
		this.template =template;
		try {
			this.bbjtemplate = new TemplatedString(template);
		} catch (BBjException e) {
		    throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	public DataRow write(DataRow dr) throws FilesystemException {

		ConnectionMgr connectionMgr = Filesystem.getConnectionMgr();
		connectionMgr.initLocalConnection();

		PrintWriter out = new PrintWriter(System.out);

		String filepath = fileName;

		FilePosition pos = null;



		pos = connectionMgr.open(filepath,false,true);

	//	byte[] myrec = dr.toByteArray(bbjtemplate);//new String("000999\nSongs in the Attic\nBilly Joel\nEMI\n99\nAAA\nRock\nXX99\n10        4         6.87      10.99\n");
	//	pos.writeWithoutPosition(myrec, 0, myrec.length, 1, false, 0, false);

		//printHeader(pos,out);

		out.flush();



		//printData(pos,out);

		out.flush();



		pos.close();
		out.flush();
		return dr;
	}
}
