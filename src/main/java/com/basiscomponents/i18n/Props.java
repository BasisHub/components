package com.basiscomponents.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Props {

	private static Props instance;
	private static String locale="en";
	private static String propertydir;

	private static String propertyPath;
	private Properties prop = new Properties();
	
	
	private Props() throws IOException {
		InputStream input = new FileInputStream(propertyPath);
        prop.load(input);
	}


	public static void setLocale(String loc) {
		locale = loc;
		instance=null;
	}
	
	
	private static Boolean tryProperty(String propfile) {
		if (new File(propfile).exists()) {
			propertyPath = propfile;
			return true;
		}
		return false;
	}
	
	public static void setProperyDir(String dir) throws FileNotFoundException {
		if ( !(dir.endsWith("/") || dir.endsWith("\\") ) )
				dir +=File.separator;
		
		
		String locale_short = locale.substring(0,2);
		
		
		if (! (tryProperty(dir+locale+".properties") || tryProperty(dir+locale_short+".properties") || tryProperty(dir+"en.properties")) ) {
			throw new FileNotFoundException("No properties file found in "+dir+" for locale "+locale+"!");
		}
		
		propertydir = dir;
		instance=null;
	}

	public static Props getInstance(String loc, String propDir) throws IOException {
		
		if (propDir != propertydir || locale != loc) {
			setProperyDir(propDir);
			locale = loc;
			instance = null;
		}
		
		return getInstance();
	}

	
	public static Props getInstance() throws IOException {
		
		if (instance == null) {
			instance = new Props();
		}
		return instance;
	}
	public String getText(String propkey) {
		String val = prop.getProperty(propkey);
		if (val == null) 
			val = "?"+propkey+"?";
		return val;
	}
	
	private Double getNumber(String propkey) {
		String val = prop.getProperty(propkey);
		if (val == null) 
			return 0.0;
		
		return Double.parseDouble(val);
	}
	
	public static void main(String[] args) throws IOException {
		
		Props.setLocale("de_DE");
		Props.setProperyDir("C:\\bbx\\plugins\\BusinessUIComponents\\configurations\\textProperties");
		
		System.out.println(Props.getInstance().getText("CONFIG_WIDGET_BTN_ADMIN_DISABLED_SHORT_CUE"));
		System.out.println(Props.getInstance().getText("A_NUMBER"));
		System.out.println(Props.getInstance().getNumber("A_NUMBER"));

	}
}
