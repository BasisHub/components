/**
 * 
 */
package com.basiscomponents.ui.layout;

import com.basis.bbj.proxies.sysgui.BBjWindow;

/**
 * @author rlance
 * 
 */
public class BBContainer extends BBComponent {

	private BBjWindow window;

	/**
	 * @param window
	 */
	public BBContainer(BBjWindow window) {
		super(window);
		this.window = window;
	}

	/**
	 * @return the window
	 */
	public BBjWindow getWindow() {
		return window;
	}

}
