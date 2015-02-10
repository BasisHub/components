/**
 * 
 */
package com.basiscomponents.ui.layout;

import java.awt.Dimension;

import com.basis.bbj.proxies.sysgui.BBjControl;
import com.basis.bbj.proxies.sysgui.BBjFont;

/**
 * @author rlance
 * 
 */
public class BBComponent {

	private BBjControl control;
	private Dimension origMinimumSize;
	private Dimension origPreferredSize;
	private Dimension origMaximumSize;
	private Dimension minimumSize;
	private Dimension preferredSize;
	private Dimension maximumSize;
	private BBjFont preferredFont;

	public BBComponent(BBjControl control) {
		setControl(control);
	}

	/**
	 * @return the control
	 */
	public BBjControl getControl() {
		return control;
	}

	/**
	 * @param control
	 *            the control to set
	 */
	private void setControl(BBjControl control) {
		this.control = control;
	}

	/**
	 * @return the minimumSize
	 */
	public Dimension getOrigMinimumSize() {
		return origMinimumSize;
	}

	/**
	 * @param minimumSize
	 *            the minimumSize to set
	 */
	public void setOrigMinimumSize(Dimension origMinimumSize) {
		this.origMinimumSize = origMinimumSize;
	}

	/**
	 * @return the preferredSize
	 */
	public Dimension getOrigPreferredSize() {
		return origPreferredSize;
	}

	/**
	 * @param preferredSize
	 *            the preferredSize to set
	 */
	public void setOrigPreferredSize(Dimension origPreferredSize) {
		this.origPreferredSize = origPreferredSize;
	}

	/**
	 * @return the maximumSize
	 */
	public Dimension getOrigMaximumSize() {
		return origMaximumSize;
	}

	/**
	 * @param maximumSize
	 *            the maximumSize to set
	 */
	public void setOrigMaximumSize(Dimension origMaximumSize) {
		this.origMaximumSize = origMaximumSize;
	}

	/**
	 * @return the minimumSize
	 */
	public Dimension getMinimumSize() {
		return minimumSize;
	}

	/**
	 * @param minimumSize
	 *            the minimumSize to set
	 */
	public void setMinimumSize(Dimension minimumSize) {
		this.minimumSize = minimumSize;
	}

	/**
	 * @return the preferredSize
	 */
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	/**
	 * @param preferredSize
	 *            the preferredSize to set
	 */
	public void setPreferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
	}

	/**
	 * @return the maximumSize
	 */
	public Dimension getMaximumSize() {
		return maximumSize;
	}

	/**
	 * @param maximumSize
	 *            the maximumSize to set
	 */
	public void setMaximumSize(Dimension maximumSize) {
		this.maximumSize = maximumSize;
	}

	/**
	 * @return the preferred BBjFont
	 */
	public BBjFont getPreferredFont() {
		return preferredFont;
	}

	/**
	 * @param preferredFont
	 *            the preferred BBjFont to set
	 */
	public void setPreferredFont(BBjFont preferredFont) {
		this.preferredFont = preferredFont;
	}

}
