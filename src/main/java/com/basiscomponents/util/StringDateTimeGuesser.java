package com.basiscomponents.util;

import com.basiscomponents.db.util.DataFieldConverter;

public class StringDateTimeGuesser {

	public static Object guess(String input) {
	
		try {
			
			switch (input.length()) {
			
			case 8:
				return DataFieldConverter.convertType(input,92);
		
			case 25:
				if (input.startsWith("1970-01-01T") && input.substring(19,20).equals("+"))
					return DataFieldConverter.convertType(input,92);
				
				if (input.substring(10,19).equals("T00:00:00"))
					return DataFieldConverter.convertType(input,91);
				
				return DataFieldConverter.convertType(input,93);
			
			case 29:
				
				return DataFieldConverter.convertType(input,93);
				
			}
		} catch (Exception e) {}
		
		return input;
		
		
	}



	public static int guessType(String input) {
	try {
			
			switch (input.length()) {
			
			case 8:
				 DataFieldConverter.convertType(input,92);
				 return 92;
			case 25:
				if (input.startsWith("1970-01-01T") && input.substring(19,20).equals("+")) {
					DataFieldConverter.convertType(input,92);
					return 92;
					}
				if (input.substring(10,19).equals("T00:00:00")) {
					DataFieldConverter.convertType(input,91);
					return 91;
					}
				DataFieldConverter.convertType(input,93);
				return 93;
			
			case 29:
				
				DataFieldConverter.convertType(input,93);
				return 93;
				
			}
		} catch (Exception e) {}
		
		return java.sql.Types.VARCHAR;
	}
	
}
