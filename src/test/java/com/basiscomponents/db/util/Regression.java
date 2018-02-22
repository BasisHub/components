package com.basiscomponents.db.util;
/**
 * Test annotation to mark tests used to explicitly check against a regression. In case of a
  * regression the original bug fix version can be easily checked/diffed against.
  *
  * Meant for documentation purposes only.
  *
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Regression {

	/**
	 * Indicator for which issue has been solved
	 *
	 * @return the issue number on Github
	 */
	String issue();

}