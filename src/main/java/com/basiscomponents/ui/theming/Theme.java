package com.basiscomponents.ui.theming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.Properties;

import com.basis.bbj.proxies.BBjAPI;
import com.basis.bbj.proxies.BBjSysGui;
import com.basis.bbj.proxies.sysgui.BBjFont;
import com.basis.bbj.proxies.sysgui.BBjImage;
import com.basis.startup.type.BBjException;
import com.basis.startup.type.sysgui.BBjColor;


public class Theme {
	
	public static final int PRIMARY=100;
	public static final int SECONDARY=102;
	public static final int ON_PRIMARY=103;
	public static final int ON_SECONDARY=104;
	
	private static String propertyPath;
	private static BBjAPI bbjapi;
	private static Theme themeInstance = null; 
	
	private ColorShader primaryColorShader;
	private ColorShader secondaryColorShader;
	private ColorShader errorColorShader;
	
	private BBjColor backgroundColor;
	
	private Properties prop = new Properties();
	
	private BBjFont primaryFont;
	
	private Theme() {};
	
	public Theme(String propertyPath) throws IOException {
		InputStream input = new FileInputStream(propertyPath);
        prop.load(input);
        primaryColorShader = new ColorShader(prop.getProperty("PRIMARY_COLOR"));
        secondaryColorShader = new ColorShader(prop.getProperty("SECONDARY_COLOR"));
        errorColorShader = new ColorShader(prop.getProperty("ERROR_COLOR"));
	}
	
	public ColorShader getPrimaryColorPalette() {
		return primaryColorShader;
	}

	public ColorShader getSecondaryColorPalette() {
		return secondaryColorShader;
	}
	
	public ColorShader getErrorColorPalette() {
		return errorColorShader;
	}	
	
	public BBjColor getBBjBackgroundColor() {
		
		if (backgroundColor==null) {
			long f = Long.parseLong(getBackgroundColor().substring(1), 16);
		    Long R = f >> 16;
		    Long G = f >> 8 & 0x00FF;
		    Long B = f & 0x0000FF;
		    backgroundColor = new BBjColor(R.intValue(),G.intValue(),B.intValue());
		}
		return backgroundColor;
	}
	
	public String getProperty(String propkey) {
		return prop.getProperty(propkey);
	}
	
	public Double getNumProperty(String propkey) {
		return Double.parseDouble(prop.getProperty(propkey));
	}
	
	public String getBackgroundColor() {
		return prop.getProperty("BACKGROUND_COLOR");
	}
	
	public BBjFont getBBjFont() throws BBjException {
		return getBBjFont("body",0);
	}

	public BBjFont getBBjFont(String type) throws BBjException {
		return getBBjFont(type,0);
	}
	
	public BBjFont getBBjFont(String type, int style) throws BBjException {
		
		String font;
		switch (type) {
			case "h1":
			case "h2":
			case "h3":
			case "h4":
			case "h5":
			case "h6":
				font=prop.getProperty("HEADLINES_FONT");
				break;
			default:
				font=prop.getProperty("BODY_FONT");
				break;
		}

		int sz;
		switch (type) {
			case "h1":
				sz=96;
				break;
			case "h2":
				sz=60;
				break;			
			case "h3":
				sz=48;
				break;			
			case "h4":
				sz=34;
				break;			
			case "h5":
				sz=20;
				break;			
			case "h6":
				sz=13;
				break;				
			default:
				sz=12;
				break;
		}	
		
		return bbjapi.getSysGui().makeFont(font,sz,style);
	}
	
	//-------------------icons------------------------------------------------
	
	public String getImagePath(String iconPropertyKey) throws BBjException {
		String path = bbjapi.getFileSystem().resolvePath(prop.getProperty(iconPropertyKey));
		return path;
	}
	
	public BBjImage getBBjImage(String iconPropertyKey) throws BBjException, IOException {
		return getBBjImage(iconPropertyKey,-1);
	}
	
	public BBjImage getBBjImage(String iconPropertyKey, int onWhichBackground) throws BBjException, IOException {
		String path =prop.getProperty(iconPropertyKey);
		if (bbjapi != null)
			path = bbjapi.getFileSystem().resolvePath(path);

		File ico=null;
		System.out.println(onWhichBackground);
		switch (onWhichBackground) {
			case PRIMARY:
				ico=IconColorizer.recolorImage(path, getPrimaryColorPalette().getHexColor());
				break;
			case SECONDARY:
				ico=IconColorizer.recolorImage(path, getSecondaryColorPalette().getHexColor());
				break;
			case ON_PRIMARY:
				ico=IconColorizer.recolorImage(path, getPrimaryColorPalette().getForeColor());
				break;
			case ON_SECONDARY:
				ico=IconColorizer.recolorImage(path, getSecondaryColorPalette().getForeColor());
				break;
			
			default:
				return bbjapi.getSysGui().getImageManager().loadImageFromFile(path);
				//do nothing special; simply return the icon as requested
		}
		return bbjapi.getSysGui().getImageManager().loadImageFromFile(ico.getAbsolutePath());
	}
	
	public static void setPropertyPath(String path) {
		propertyPath = path;
		themeInstance = null;
	}

	public static void setBBjAPI(BBjAPI api) throws BBjException {
		Theme.bbjapi = api;
		themeInstance = null;
	}
	
	public static Theme getInstance(String propertyPath, BBjAPI bbjapi) throws IOException, BBjException {
		setPropertyPath(propertyPath);
		Theme.bbjapi = bbjapi;
		return getInstance();
	}

	public static Theme getInstance() throws IOException {
		if (themeInstance == null) {
			
			if (propertyPath == null)
				throw new InvalidPathException("null","File Path not set!");

			themeInstance = new Theme(propertyPath);
		}
		return themeInstance;
	}
	
	public static void main(String[] args) throws Exception {
		Theme.setPropertyPath("C:\\bbx\\plugins\\BusinessUIComponents\\configurations\\ui.properties");
		Theme t = Theme.getInstance();
		System.out.println(t.getBBjBackgroundColor());
		System.out.println(t.getBBjImage("IC_NEW",Theme.ON_PRIMARY));
	}

}
