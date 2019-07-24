package com.basiscomponents.treeview;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class BasisTreeView {

	private BasisTreeView() {
	}

	public static void main(String[] args) throws Exception {
		ResultSet rs = getDataSystemAsResultSet("C:\\AMD", "exe");
	}

	public static ResultSet getDataSystemAsResultSet(String syspath, String filter) throws Exception {

		if (filter == null) {
			filter = "";
		}

		ResultSet rs = new ResultSet();
		FileSystemView fsv = FileSystemView.getFileSystemView();

		createDataRowsFromDir(new File(syspath), rs, fsv, filter);

		System.out.println(rs.toJson());
		return rs;
	}

	private static void createDataRowsFromDir(File dir, ResultSet rs, FileSystemView fsv, String filter)
			throws ParseException, IOException {

		List<File> childs = Arrays.asList(fsv.getFiles(dir, true));
		
		// A file has to be written
		if (childs.isEmpty()) {
			if (!filter.isEmpty()) {
				if (dir.getAbsolutePath().contains(filter)) {
					DataRow dr = new DataRow();
					dr.setFieldValue("filepath", dir.getAbsolutePath());
					dr.setFieldValue("size", dir.length());
					rs.add(dr);
				}
			} else {
				DataRow dr = new DataRow();
				dr.setFieldValue("filepath", dir.getAbsolutePath());
				dr.setFieldValue("size", dir.length());
				rs.add(dr);
			}
		}

		// A folder has to be written
		if (!childs.isEmpty()) {
			for (File f : childs) {
				createDataRowsFromDir(f, rs, fsv, filter);
			}
		}
	}
}
