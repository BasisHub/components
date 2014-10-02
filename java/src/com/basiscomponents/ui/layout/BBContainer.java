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
		setWindow(window);
	}

	/**
	 * @return the window
	 */
	public BBjWindow getWindow() {
		return window;
	}

	/**
	 * @param window
	 *            the window to set
	 */
	private void setWindow(BBjWindow window) {
		this.window = window;
	}

}
