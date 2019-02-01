package com.basiscomponents.db.constants;

public interface ConstantsResolver {
	
	
	/**
	 * resolveConstants: search for [[CONSTANT]] and replace it with the value of CONSTANT in the constants table
	 * @param in
	 * @return: the processed string
	 */
	public String resolveConstants(String in);
}
