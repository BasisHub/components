package com.basiscomponents.util;

public class StringHelper {
	/**
	 * Returns a String with the inverted bytes of the given input String.
	 * 
	 * @param in
	 *            The String whose bytes will be inverted.
	 * 
	 * @return A String with the inverted bytes of the given input String.
	 */
	public static String invert(String in) {
		String out = "";
		byte[] b = in.getBytes();
		for (int i = 0; i < b.length; i++) {
			out += 255 - b[i];
		}
		return out;
	}
}
