package com.basiscomponents.ui.theming;

import java.util.ArrayList;

import com.basis.bbj.proxies.BBjAPI;
import com.basis.bbj.proxies.BBjRuntime;
import com.basis.startup.type.sysgui.BBjColor;

public class ColorShader {

	private String mainColor;
	private String[] colorShades = new String[14];
	private BBjColor[] BBjColors = new BBjColor[14];
	private BBjColor[] BBjForeColors= new BBjColor[14];
	
	private ColorShader() {};

	public ColorShader(String color) {
		this.mainColor = color;
		calculateMaterialColors(color);
	};
	
	private String[] calculateMaterialColors(String color){

	    colorShades[0] = shadeColor(color, 0.9   ); //----> 50
	    colorShades[1] = shadeColor(color, 0.7   ); //----> 100
	    colorShades[2] = shadeColor(color, 0.5   ); //----> 200
	    colorShades[3] = shadeColor(color, 0.333 ); //----> 300
	    colorShades[4] = shadeColor(color, 0.166 ); //----> 400
	    colorShades[5] = shadeColor(color, 0     ); //----> 500
	    colorShades[6] = shadeColor(color, -0.125); //----> 600
	    colorShades[7] = shadeColor(color, -0.25 ); //----> 700
	    colorShades[8] = shadeColor(color, -0.375); //----> 800
	    colorShades[9] = shadeColor(color, -0.5  ); //----> 900

	    colorShades[10] = shadeColor(color, 0.7  ); //----> A100
	    colorShades[11] = shadeColor(color, 0.5  ); //----> A200
	    colorShades[12] = shadeColor(color, 0.166); //----> A400
	    colorShades[13] = shadeColor(color, -0.25); //----> A700

	    return colorShades;
	}

	private static String shadeColor(String color, double percent) {
	    long f = Long.parseLong(color.substring(1), 16);
	    double t = percent < 0 ? 0 : 255;
	    double p = percent < 0 ? percent * -1 : percent;
	    long R = f >> 16;
	    long G = f >> 8 & 0x00FF;
	    long B = f & 0x0000FF;
	    int red = (int) (Math.round((t - R) * p) + R);
	    int green = (int) (Math.round((t - G) * p) + G);
	    int blue = (int) (Math.round((t - B) * p) + B);
	    String x = String.format("#%02x%02x%02x", red, green, blue);
	    return x;
	}
	

	public static ArrayList<String> enumShades(){
		ArrayList<String> s = new ArrayList<>();
		s.add("50");
		s.add("100");
		s.add("200");
		s.add("300");
		s.add("400");
		s.add("500");
		s.add("600");
		s.add("700");
		s.add("800");
		s.add("900");
		s.add("A100");
		s.add("A200");
		s.add("A400");		
		s.add("A700");
		return s;
	}

	private int getShadeIndex(String shade) {
		switch (shade)
		{
		case "50":
			return 0;
		case "100":
			return 1;
		case "200":
			return 2;
		case "300":
			return 3;
		case "400":
			return 4;			
		default:
			//also: 500
			return 5;
		case "600":
			return 6;
		case "700":
			return 7;
		case "800":
			return 8;
		case "900":
			return 9;
		case "A100":
			return 10;			
		case "A200":
			return 11;	
		case "A400":
			return 12;	
		case "A700":
			return 13;				
		}
	}

	public String getHexColor() {
		return getHexColor("500");
	}
	
	public String getHexColor(String shade) {
		int idx = getShadeIndex(shade);
		return colorShades[idx];
	}	
	
	public BBjColor getBBjColor() {
		return getBBjColor("500");
	}
	
	public BBjColor getBBjColor(String shade) {
		
		int idx = getShadeIndex(shade);
		BBjColor c = BBjColors[idx];
		
		if (c == null) {
		    long f = Long.parseLong(colorShades[idx].substring(1), 16);
		    Long R = f >> 16;
		    Long G = f >> 8 & 0x00FF;
		    Long B = f & 0x0000FF;
		    c = new BBjColor(R.intValue(),G.intValue(),B.intValue());
		    BBjColors[idx]=c;
		}
		return c;
	}
	
	
	public BBjColor getBBjForeColor() {
		return getBBjForeColor("500");
	}

	public BBjColor getBBjForeColor(String shade) {
		int idx = getShadeIndex(shade);
	    long f = Long.parseLong(colorShades[idx].substring(1), 16);
	    Long R = f >> 16;
	    Long G = f >> 8 & 0x00FF;
	    Long B = f & 0x0000FF;
	      if (R*0.299 + G*0.587 + B*0.114 <= 186)
		  	return new BBjColor(255,255,255);
		  else
			return new BBjColor(0,0,0);
	}

	public String getForeColor() {
		return getForeColor("500");
	}

	public String getForeColor(String shade) {
		int idx = getShadeIndex(shade);
	    long f = Long.parseLong(colorShades[idx].substring(1), 16);
	    Long R = f >> 16;
	    Long G = f >> 8 & 0x00FF;
	    Long B = f & 0x0000FF;
	      if (R*0.299 + G*0.587 + B*0.114 <= 186)
		  	return "#FFFFFF";
		  else
			return "#000000";
	}
	
	
}
