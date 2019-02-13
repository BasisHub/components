package com.basiscomponents.util;

public class StringHelper {
	private StringHelper() {
	}
	/**
	 * Returns a String with the inverted bytes of the given input String.
	 * 
	 * @param in
	 *            The String whose bytes will be inverted.
	 * 
	 * @return A String with the inverted bytes of the given input String.
	 */
	public static String invert(String in) {
		StringBuilder out = new StringBuilder();
		byte[] b = in.getBytes();
		for (int i = 0; i < b.length; i++) {
			out.append(255 - b[i]);
		}
		return out.toString();
	}
}
