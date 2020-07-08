package com.basiscomponents.ui.theming;


import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.IndexColorModel;
import java.awt.image.RGBImageFilter;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class IconColorizer {
	
	
	public static File recolorImage(String srcfilepath, String color) throws IOException {

        BufferedImage mapImage = ImageIO.read(new File(srcfilepath));
        Image newMapImage = Toolkit.getDefaultToolkit().createImage(
                          new FilteredImageSource(mapImage.getSource(),
                                   new ReColorFilter(color)));

        BufferedImage bimage = new BufferedImage(newMapImage.getWidth(null), newMapImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(newMapImage, 0, 0, null);
        bGr.dispose();
        
        File newImg = File.createTempFile("ico_", ".png");
        ImageIO.write(bimage ,"png",newImg);
        return newImg;
	}
}	

class ReColorFilter extends RGBImageFilter {
	
	   int newColor;
		
	   private ReColorFilter() {};
	   
	   public ReColorFilter(String color) {
		    long f = Long.parseLong(color.substring(1), 16);
		    Long r = f >> 16;
		    Long g = f >> 8 & 0x00FF;
		    Long b = f & 0x0000FF;
		    newColor = new Color(r.intValue(),g.intValue(),b.intValue()).getRGB();
		}

		public int filterRGB(int x, int y, int rgb) {
			   Color c = new Color(rgb);
			   if (rgb<0)
				   return (newColor);
			   else 
				   return rgb;
				}
		}	// inner class	