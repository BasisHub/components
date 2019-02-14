/**
 * 
 */
package com.basiscomponents.ui.layout;

import java.awt.Dimension;

import com.basis.bbj.client.util.BBjException;
import com.basis.bbj.proxies.sysgui.BBjControl;
import com.basis.bbj.proxies.sysgui.BBjFont;
import com.basis.bbj.proxies.sysgui.BBjListButton;
import com.basis.bbj.proxies.sysgui.BBjListEdit;
import com.basis.util.common.BasisNumber;

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
	private int x;
	private int y;
	private int width;
	private int height;

	public BBComponent(BBjControl control) {
		this.control = control;
		try {
			this.x = control.getX().intValue();
			this.y = control.getY().intValue();
			this.width = control.getWidth().intValue();
			if (control.getControlType() == BBjControl.LISTBUTTON_TYPE) {
				this.height = ((BBjListButton) control).getFieldHeight()
						.intValue();
			} else if (control.getControlType() == BBjControl.LISTEDIT_TYPE) {
				this.height = ((BBjListEdit) control).getFieldHeight()
						.intValue();
			} else {
				this.height = control.getHeight().intValue();
			}
		} catch (BBjException e) {
			e.printStackTrace();
		}
		this.origMinimumSize = new Dimension((int) (this.width / 2),
				(int) (this.height / 2));
		this.origPreferredSize = new Dimension(this.width, this.height);
		this.origMaximumSize = new Dimension(this.width * 5, this.height * 5);
		this.minimumSize = new Dimension((int) (this.width / 2),
				(int) (this.height / 2));
		this.preferredSize = new Dimension(this.width, this.height);
		this.maximumSize = new Dimension(this.width * 5, this.height * 5);
	}

	/**
	 * @return the control
	 */
	public BBjControl getControl() {
		return control;
	}

	/**
	 * @return the minimumSize
	 */
	public Dimension getOrigMinimumSize() {
		return origMinimumSize;
	}

	/**
	 * @param origMinimumSize
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
	 * @param origPreferredSize
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
	 * @param origMaximumSize
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

	/**
	 * @return the current x coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the current x coordinate
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the current y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the current y coordinate
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @param x
	 *            the current x coordinate
	 * @param y
	 *            the current y coordinate
	 */
	public void setLocation(int x, int y) {
		BasisNumber bx = BasisNumber.createBasisNumber(x);
		BasisNumber by = BasisNumber.createBasisNumber(y);
		try {
			this.control.setLocation(bx, by);
		} catch (BBjException e) {
			e.printStackTrace();
		}
		setX(x);
		setY(y);
	}

	/**
	 * @return the current width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the current width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the current height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the current height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @param width
	 *            the current width to set
	 * @param height
	 *            the current height to set
	 */
	public void setSize(int width, int height) {
		BasisNumber bw = BasisNumber.createBasisNumber(width);
		BasisNumber bh = BasisNumber.createBasisNumber(height);
		try {
			if (control.getControlType() == BBjControl.LISTBUTTON_TYPE) {
				this.control.setSize(bw, this.control.getHeight());
				((BBjListButton) control).setFieldHeight(bh);
			} else if (control.getControlType() == BBjControl.LISTEDIT_TYPE) {
				this.control.setSize(bw, this.control.getHeight());
				((BBjListEdit) control).setFieldHeight(bh);
			} else {
				this.control.setSize(bw, bh);
			}
		} catch (BBjException e) {
			e.printStackTrace();
		}
		setWidth(width);
		setHeight(height);
	}

}
