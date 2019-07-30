package com.basiscomponents.db.importer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class FileSystemScanner {

	private FileSystemScanner() {
	}

	/**
	 * This method returns the given path as an ResultSet. For every file in the
	 * path, a DataRow is created in the ResultSet which represents it with the
	 * file's path and size.
	 * 
	 * @param syspath : The directory which should be represented. Empty directories
	 *                are not converted. Only files will be represented.
	 * @param filter  : A String which is used to filter the file names.
	 * @return A ResultSet with every file as one DataRow
	 * @throws Exception
	 */
	public static ResultSet getDataSystemAsResultSet(String syspath, String filter) throws Exception {

		if (filter == null) {
			filter = "";
		}

		ResultSet rs = new ResultSet();
		FileSystemView fsv = FileSystemView.getFileSystemView();

		createDataRowsFromDir(new File(syspath), rs, fsv, filter);

		return rs;
	}

	private static void createDataRowsFromDir(File dir, ResultSet rs, FileSystemView fsv, String filter)
			throws ParseException, IOException {

		List<File> childs = Arrays.asList(fsv.getFiles(dir, true));

		// A file has to be written
		if (childs.isEmpty()) {
			if (!filter.isEmpty()) {
				if (dir.getName().contains(filter)) {
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

