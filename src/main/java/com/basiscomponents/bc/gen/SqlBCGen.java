package com.basiscomponents.bc.gen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlBCGen {

	private static String Tpl;

	public static ArrayList<String> gen(String Driver, String Url, String User, String Password, String packageName,
			String outputDir) throws FileNotFoundException {

		ArrayList<String> classes = new ArrayList<>(); 
		if (Tpl == null) {
			SqlBCGen.loadTpl(SqlBCGen.class.getResourceAsStream("generator/TableBC.txt"));
		}

		try {
			Class.forName(Driver);
			try (Connection conn = DriverManager.getConnection(Url, User, Password);) {
				DatabaseMetaData dbmd = conn.getMetaData();
				String[] types = { "TABLE" };
				ResultSet rs = dbmd.getTables(null, null, "%", types);
				while (rs.next()) {
					String classname = SqlBCGen.genClass(rs.getString("TABLE_NAME"), packageName, outputDir);
					classes.add(classname);
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classes;
	}

	public static ArrayList<String> gen(String driver, String url, String user, String password, String packageName,
			String outputDir, String templatePath) throws FileNotFoundException {
		SqlBCGen.loadTpl(new java.io.FileInputStream(templatePath));
		return gen(driver, url, user, password, packageName, outputDir);
	}

	private static void loadTpl(InputStream is) {
		final char[] buffer = new char[1024];
		final StringBuilder out = new StringBuilder();
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			int rsz;
			while ((rsz = br.read(buffer)) != -1) {
				out.append(buffer, 0, rsz);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		SqlBCGen.Tpl = out.toString();
	}

	private static String  genClass(String tableName, String packageName, String outputDir) throws FileNotFoundException {

		String className = SqlBCGen.toCamelCase(tableName) + "BC";
		if (new File(outputDir + className + ".java").exists()) {
			System.out.println(outputDir + className + ".java already exists. Skipping...");
			return packageName+"."+className;
		}

		System.out.println("generating: " + tableName);
		String tmp = SqlBCGen.Tpl;
		tmp = tmp.replace("[[class]]", className);
		tmp = tmp.replace("[[package]]", packageName);
		tmp = tmp.replace("[[table]]", tableName);

		PrintWriter out = new PrintWriter(outputDir + className + ".java");
		out.println(tmp);
		out.close();
		return packageName+"."+className;
	}

	private static String toCamelCase(String tableName) {
		return tableName.substring(0, 1).toUpperCase() + tableName.substring(1).toLowerCase();
	}

	private SqlBCGen() {
		// private constructor
	}
}
