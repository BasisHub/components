package com.basiscomponents.bridge.proxygen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileHandler {
	private FileHandler() {
	}
	public static String readFile(String file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			reader.close();
			return stringBuilder.toString();
		}
	}

}
