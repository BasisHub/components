package com.basiscomponents.db.constants;

import com.basis.bbj.client.util.BBjException;

public interface ConstantsResolver {
	
	
	/**
	 * resolveConstants: search for [[CONSTANT]] and replace it with the value of CONSTANT in the constants table
	 * @param in
	 * @return: the processed string
	 */
	public String resolveConstants(String in);
}
