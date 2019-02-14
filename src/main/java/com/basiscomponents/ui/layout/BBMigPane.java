package com.basiscomponents.ui.layout;

import java.awt.Dimension;
import java.util.List;

import com.basis.bbj.client.datatypes.BBjVector;
import com.basis.bbj.client.sysgui.datatypes.BBjColor;
import com.basis.bbj.client.util.BBjException;
import com.basis.bbj.proxies.BBjSysGui;
import com.basis.bbj.proxies.CustomObject;
import com.basis.bbj.proxies.sysgui.BBjControl;
import com.basis.bbj.proxies.sysgui.BBjFont;
import com.basis.bbj.proxies.sysgui.BBjPopupMenu;
import com.basis.bbj.proxies.sysgui.BBjWindow;
import com.basis.util.common.BBjNumber;
import com.basis.util.common.BasisNumber;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.LayoutUtil;

/**
 * Manages BBj graphical components with MigLayout
 *
 * @author R.Lance, MigLayout author Mikael Grev
 */
public class BBMigPane implements BBjControl {

	private BBContainer container;
	private BBContainerWrapper containerWrapper;
	private boolean debug = false;
	private Grid migGrid;
	private boolean valid = false;
	private LC layoutConstraints;
	private AC columnConstraints;
	private AC rowConstraints;

	private Dimension preferredSize = new Dimension();

	// ======================================================
	// CONSTRUCTOR

	/**
	 *
	 * @throws BBjException
	 */
	public BBMigPane(BBjWindow window) throws BBjException {
		this.container = new BBContainer(window);
		construct();
	}

	/**
	 *
	 * @throws BBjException
	 */
	public BBMigPane(BBjWindow window, LC layoutConstraints) throws BBjException {
		this.container = new BBContainer(window);
		setLayoutConstraints(layoutConstraints);
		construct();
	}

	/**
	 *
	 * @throws BBjException
	 */
	public BBMigPane(BBjWindow window, LC layoutConstraints, AC colConstraints) throws BBjException {
		this.container = new BBContainer(window);
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		construct();
	}

	/**
	 *
	 * @throws BBjException
	 */
	public BBMigPane(BBjWindow window, LC layoutConstraints, AC colConstraints, AC rowConstraints) throws BBjException {
		this.container = new BBContainer(window);
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);
		construct();
	}

	/**
	 *
	 * @throws BBjException
	 */
	public BBMigPane(BBjWindow window, String layoutConstraints) throws BBjException {
		this.container = new BBContainer(window);
		setLayoutConstraints(ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(layoutConstraints)));
		construct();
	}

	/**
	 *
	 * @throws BBjException
	 */
	public BBMigPane(BBjWindow window, String layoutConstraints, String colConstraints) throws BBjException {
		this.container = new BBContainer(window);
		setLayoutConstraints(ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(layoutConstraints)));
		setColumnConstraints(ConstraintParser.parseColumnConstraints(ConstraintParser.prepare(colConstraints)));
		construct();
	}

	/**
	 *
	 * @throws BBjException
	 */
	public BBMigPane(BBjWindow window, String layoutConstraints, String colConstraints, String rowConstraints)
			throws BBjException {
		this.container = new BBContainer(window);
		setLayoutConstraints(ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(layoutConstraints)));
		setColumnConstraints(ConstraintParser.parseColumnConstraints(ConstraintParser.prepare(colConstraints)));
		setRowConstraints(ConstraintParser.parseRowConstraints(ConstraintParser.prepare(rowConstraints)));
		construct();
	}

	/**
	 *
	 * @throws BBjException
	 */
	private void construct() throws BBjException {
		// defaults
		if (getLayoutConstraints() == null)
			setLayoutConstraints(new LC());
		if (getRowConstraints() == null)
			setRowConstraints(new AC());
		if (getColumnConstraints() == null)
			setColumnConstraints(new AC());

		this.container.setPreferredFont(this.container.getControl().getFont());

		// the container wrapper
		this.containerWrapper = new BBContainerWrapper(this.container, this);

		// create the initial grid so it won't be null
		createMigLayoutGrid();
	}

	// ======================================================
	// CONSTRAINTS

	/**
	 *
	 * @return Layout constraints as LC object
	 */
	public LC getLayoutConstraints() {
		return this.layoutConstraints;
	}

	/**
	 *
	 * @param constraints
	 *            Layout constraints as String
	 */
	public void setLayoutConstraints(String constraints) {
		setLayoutConstraints(ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(constraints)));
	}

	/**
	 *
	 * @param constraints
	 *            Layout constraints as LC object
	 */
	public void setLayoutConstraints(LC constraints) {
		this.layoutConstraints = constraints;
		debug = constraints != null && constraints.getDebugMillis() > 0;
	}

	/**
	 *
	 * @return Column constraints as AC object
	 */
	public AC getColumnConstraints() {
		return this.columnConstraints;
	}

	/**
	 *
	 * @param constraints
	 *            Column constraints as String
	 */
	public void setColumnConstraints(String constraints) {
		setColumnConstraints(ConstraintParser.parseColumnConstraints(ConstraintParser.prepare(constraints)));
	}

	/**
	 *
	 * @param constraints
	 *            Column constraints as AC object
	 */
	public void setColumnConstraints(AC constraints) {
		this.columnConstraints = constraints;
	}

	/**
	 *
	 * @return Row constraints as AC object
	 */
	public AC getRowConstraints() {
		return this.rowConstraints;
	}

	/**
	 *
	 * @param constraints
	 *            Row constraints as String
	 */
	public void setRowConstraints(String constraints) {
		setRowConstraints(ConstraintParser.parseRowConstraints(ConstraintParser.prepare(constraints)));
	}

	/**
	 *
	 * @param constraints
	 *            Row constraints as AC object
	 */
	public void setRowConstraints(AC constraints) {
		this.rowConstraints = constraints;
	}

	/**
	 *
	 * @param control
	 *            BBj control object
	 * @return Component constraints as CC object
	 */
	public CC getComponentConstraints(BBjControl control) {
		BBComponentWrapper componentWrapper = getComponentWrapper(control);
		return this.containerWrapper.getComponentWrapperToCCMap().get(componentWrapper);
	}

	/**
	 *
	 * @param control
	 *            BBj control object
	 * @param constraints
	 *            Component constraints as String
	 */
	public void setComponentConstraints(BBjControl control, String constraints) {
		if (isManagingComponent(control)) {
			BBComponentWrapper componentWrapper = getComponentWrapper(control);
			setComponentConstraintsImpl(componentWrapper, constraints);
		}
	}

	/**
	 *
	 * @param control
	 *            BBj control object
	 * @param constraints
	 *            Component constraints as CC object
	 */
	public void setComponentConstraints(BBjControl control, CC constraints) {
		if (isManagingComponent(control)) {
			BBComponentWrapper componentWrapper = getComponentWrapper(control);
			setComponentConstraintsImpl(componentWrapper, constraints);
		}
	}

	/**
	 *
	 * @param componentWrapper
	 *            BBComponentWrapper object
	 * @param constr
	 *            Component constraints as either String or CC object
	 * @throws IllegalArgumentException
	 */
	private void setComponentConstraintsImpl(BBComponentWrapper componentWrapper, Object constr) {
		if (constr == null || constr instanceof String) {
			String cStr = ConstraintParser.prepare((String) constr);
			this.containerWrapper.getComponentWrapperToCCMap().put(componentWrapper,
					ConstraintParser.parseComponentConstraint(cStr));
		} else if (constr instanceof CC) {
			this.containerWrapper.getComponentWrapperToCCMap().put(componentWrapper, (CC) constr);
		} else {
			throw new IllegalArgumentException(
					"Constraint must be String or ComponentConstraint (CC): " + constr.getClass().toString());
		}
		invalidateMigLayoutGrid();
	}

	// ======================================================
	// CONTROLS (COMPONENTS)

	/**
	 *
	 * @return container BBContainer object
	 */
	public BBContainer getContainer() {
		return this.container;
	}

	/**
	 *
	 * @return containerWrapper BBContainerWrapper object
	 */
	public BBContainerWrapper getContainerWrapper() {
		return this.containerWrapper;
	}

	/**
	 *
	 * @return BBjWindow BBj window object
	 */
	public BBjWindow getWnd() {
		return this.container.getWindow();
	}

	/**
	 *
	 * @return int Preferred width of container
	 */
	public int getPreferredWidth() {
		return this.preferredSize.width;
	}

	/**
	 *
	 * @return int Preferred height of container
	 */
	public int getPreferredHeight() {
		return this.preferredSize.height;
	}

	/**
	 *
	 * @param control
	 *            BBj control object
	 * @param cc
	 *            Column constraints object
	 * @throws BBjException
	 */
	public void add(BBjControl control, CC cc) throws BBjException {
		BBComponent component = new BBComponent(control);
		component.setPreferredFont(control.getFont());
		this.containerWrapper.add(component, cc);
	}

	/**
	 *
	 * @param control
	 *            BBj control object
	 * @throws BBjException
	 */
	public void add(BBjControl control) throws BBjException {
		add(control, new CC());
	}

	/**
	 *
	 * @param control
	 *            BBj control object
	 * @param cc
	 *            Column constraints string
	 * @throws BBjException
	 */
	public void add(BBjControl control, String cc) throws BBjException {
		CC lCC = ConstraintParser.parseComponentConstraint(ConstraintParser.prepare(cc));
		add(control, lCC);
	}

	/**
	 *
	 * @param control
	 *            BBj control object
	 * @return componentWrapper BBComponentWrapper object
	 */
	public BBComponentWrapper getComponentWrapper(BBjControl control) {
		return (BBComponentWrapper) this.containerWrapper.getControlToComponentWrapperMap().get(control);
	}

	/**
	 *
	 * @param control
	 *            BBj control object
	 * @return If this layout manager is currently managing this control.
	 */
	public boolean isManagingComponent(BBjControl control) {
		return this.containerWrapper.getControlToComponentWrapperMap().containsKey(control);
	}

	/**
	 *
	 * @param control
	 *            BBj control object
	 */
	public void removeLayoutComponent(BBjControl control) {
		if (isManagingComponent(control)) {
			BBComponentWrapper componentWrapper = getComponentWrapper(control);
			BBComponent component = (BBComponent) componentWrapper.getComponent();
			this.containerWrapper.getComponentWrapperList().remove(componentWrapper);
			this.containerWrapper.getComponentToComponentWrapperMap().remove(component);
			this.containerWrapper.getControlToComponentWrapperMap().remove(control);
			this.containerWrapper.getComponentWrapperToCCMap().remove(componentWrapper);
			invalidateMigLayoutGrid();
		}
	}

	// ======================================================
	// LAYOUT

	/**
	 * This is where the actual layout happens.
	 *
	 * @throws BBjException
	 * @throws IllegalArgumentException
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public void layoutChildren() throws BBjException {
		// validate if the grid should be recreated
		validateMigLayoutGrid();

		// only contact with the UI
		this.container.setX(this.container.getWindow().getX().intValue());
		this.container.setY(this.container.getWindow().getY().intValue());
		this.container.setWidth(this.container.getWindow().getWidth().intValue());
		this.container.setHeight(this.container.getWindow().getHeight().intValue());

		// this will exercise BBComponentWrapper.setBounds to actually place the
		// components
		int[] lBounds = new int[] { Integer.valueOf(0), Integer.valueOf(0), this.container.getWidth(),
				this.container.getHeight() };
		this.migGrid.layout(lBounds, getLayoutConstraints().getAlignX(), getLayoutConstraints().getAlignY(),
				this.debug);

		if (this.debug) {
			getWnd().getDrawPanel().clearDrawing();
			this.migGrid.paintDebug();
		}

		if (this.preferredSize.width == 0 && this.preferredSize.height == 0) {
			this.preferredSize.width = this.container.getWidth();
			this.preferredSize.height = this.container.getHeight();
		}
	}

	/**
	 *
	 * @throws BBjException
	 * @throws NumberFormatException
	 *
	 */
	public void createMigLayoutGrid() throws BBjException {
		this.migGrid = new Grid(this.containerWrapper, getLayoutConstraints(), getRowConstraints(),
				getColumnConstraints(), this.containerWrapper.getComponentWrapperToCCMap(), null);
		this.setLayoutSizes();
		this.valid = true;
	}

	/**
	 *
	 * @throws BBjException
	 * @throws NumberFormatException
	 * @see Swing MigLayout: maximumLayoutSize(); minimumLayoutSize();
	 *      preferredLayoutSize();
	 */
	private void setLayoutSizes() throws BBjException {
		Dimension d;
		d = new Dimension(LayoutUtil.getSizeSafe(this.migGrid.getWidth(), LayoutUtil.MIN),
				LayoutUtil.getSizeSafe(this.migGrid.getHeight(), LayoutUtil.MIN));
		this.container.setMinimumSize(d);
		d = new Dimension(LayoutUtil.getSizeSafe(this.migGrid.getWidth(), LayoutUtil.MAX),
				LayoutUtil.getSizeSafe(this.migGrid.getHeight(), LayoutUtil.MAX));
		this.container.setMaximumSize(d);
		d = new Dimension(LayoutUtil.getSizeSafe(this.migGrid.getWidth(), LayoutUtil.PREF),
				LayoutUtil.getSizeSafe(this.migGrid.getHeight(), LayoutUtil.PREF));
		this.container.setPreferredSize(d);
		// resize container itself with preferred sizes
		this.container.setSize(d.width, d.height);
	}

	/**
	 * The grid is valid if all component hashcodes are unchanged.
	 *
	 * @throws BBjException
	 * @throws NumberFormatException
	 */
	private void validateMigLayoutGrid() throws NumberFormatException, BBjException {
		// only needed if the grid is valid
		if (isMiglayoutGridValid()) {
			List<BBComponentWrapper> componentWrapperList = this.containerWrapper.getComponentWrapperList();
			if (componentWrapperList != null && componentWrapperList.size() > 0) {
				for (int i = 0; i < componentWrapperList.size(); i++) {
					BBComponentWrapper componentWrapper = componentWrapperList.get(i);
					BBComponent component = (BBComponent) componentWrapper.getComponent();
					// if this component is managed by MigLayout
					if (this.containerWrapper.getComponentToComponentWrapperMap().containsKey(component)) {
						// get its previous hashcode
						int lPreviousHashcode = 0;
						if (this.containerWrapper.getComponentToHashcodeMap().get(component) != null)
							lPreviousHashcode = this.containerWrapper.getComponentToHashcodeMap().get(component);
						// calculate its current hashcode
						int lCurrentHashcode = componentWrapper.getLayoutHashCode();
						// if it is not the same
						if (lPreviousHashcode == 0 || lPreviousHashcode != lCurrentHashcode) {
							// invalidate the grid
							invalidateMigLayoutGrid();
							// remember the new hashcode
							this.containerWrapper.getComponentToHashcodeMap().put(component, lCurrentHashcode);
						}
					}
				}
			}
		}
		// if invalid, create
		if (!isMiglayoutGridValid()) {
			createMigLayoutGrid();
		}
	}

	/**
	 * Mark the grid as invalid
	 */
	private void invalidateMigLayoutGrid() {
		this.valid = false;
	}

	/**
	 *
	 * @return If the grid is valid
	 */
	private boolean isMiglayoutGridValid() {
		return this.valid;
	}

	/**
	 * Scale layout
	 *
	 * @throws BBjException
	 * @throws NumberFormatException
	 */
	public void scaleLayout(BBjNumber scale, BBjSysGui sysgui) throws NumberFormatException, BBjException {
		double s = scale.doubleValue();
		List<BBComponentWrapper> componentWrapperList = this.containerWrapper.getComponentWrapperList();
		if (componentWrapperList != null && componentWrapperList.size() > 0) {
			for (int i = 0; i < componentWrapperList.size(); i++) {
				BBComponentWrapper componentWrapper = componentWrapperList.get(i);
				BBComponent component = (BBComponent) componentWrapper.getComponent();
				if (this.containerWrapper.getComponentToComponentWrapperMap().containsKey(component)) {
					Dimension d;
					d = component.getOrigMinimumSize();
					component.setMinimumSize(new Dimension((int) ((d.width * s) / 2), (int) ((d.height * s) / 2)));
					d = component.getOrigPreferredSize();
					component.setPreferredSize(new Dimension((int) (d.width * s), (int) (d.height * s)));
					d = component.getOrigMaximumSize();
					component.setMaximumSize(new Dimension((int) ((d.width * s) * 5), (int) ((d.height * s) * 5)));

					int sz = component.getPreferredFont().getSize();
					if (sz > 0 && s > 0.2d) {
						sz = (int) (sz * (s - 0.2d));
						BBjFont f = component.getPreferredFont();
						component.getControl().setFont(sysgui.makeFont(f.getName(), sz, f.getStyle()));
					}

					this.containerWrapper.getComponentToHashcodeMap().put(component,
							componentWrapper.getLayoutHashCode());
					invalidateMigLayoutGrid();
				}
			}
		}
		// if invalid, create anew
		if (!isMiglayoutGridValid()) {
			createMigLayoutGrid();
		}
	}

	// ======================================================
	// DEBUG - TODO (see Swing and/or JavaFX implementations)
	//
	//
	// ======================================================
	// Methods of BBjControl interface

	@Override
	public BBjPopupMenu addPopupMenu() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addStyle(String arg0) throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearCallback(int arg0) throws BBjException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearStyles() throws BBjException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() throws BBjException {
		// TODO Auto-generated method stub

	}

	@Override
	public void focus() throws BBjException {
		// TODO Auto-generated method stub

	}

	@Override
	public BBjColor getBackColor() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getCausesControlValidation() throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getClientEdge() throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getContextID() throws BBjException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getControlType() throws BBjException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BBjFont getFont() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BBjColor getForeColor() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BBjNumber getHeight() throws BBjException {
		return BasisNumber.createBasisNumber(this.container.getHeight());
	}

	@Override
	public int getID() throws BBjException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLongCue() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getNoEdge() throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BBjPopupMenu getPopupMenu() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getRaisedEdge() throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getScreenX() throws BBjException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScreenY() throws BBjException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortCue() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BBjVector getStyles() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getUserData() throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BBjNumber getWidth() throws BBjException {
		return BasisNumber.createBasisNumber(this.container.getWidth());
	}

	@Override
	public BBjNumber getX() throws BBjException {
		return BasisNumber.createBasisNumber(this.container.getX());
	}

	@Override
	public BBjNumber getY() throws BBjException {
		return BasisNumber.createBasisNumber(this.container.getY());
	}

	@Override
	public boolean isDestroyed() throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpaque() throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVisible() throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removePopupMenu() throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean removeStyle(String arg0) throws BBjException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBackColor(BBjColor arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCallback(int arg0, String arg1) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCallback(int arg0, CustomObject arg1, String arg2) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCausesControlValidation(boolean arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setClientEdge(boolean arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCursor(int arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setEnabled(boolean arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setFont(BBjFont arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setForeColor(BBjColor arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLocation(BBjNumber x, BBjNumber y) throws BBjException {
		this.container.setLocation(x.intValue(), y.intValue());
	}

	@Override
	public void setLongCue(String arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setName(String arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setNoEdge(boolean arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setOpaque(boolean arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setPopupMenu(BBjPopupMenu arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setRaisedEdge(boolean arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setShortCue(String arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSize(BBjNumber w, BBjNumber h) throws BBjException {
		this.container.setSize(Math.min(this.preferredSize.width, w.intValue()),
				Math.min(this.preferredSize.height, h.intValue()));
		layoutChildren();
	}

	@Override
	public void setText(String arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setToolTipText(String arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setUserData(Object arg0) throws BBjException {
		// TODO Auto-generated method stub
	}

	public void setVisible(boolean visible) throws BBjException {
		this.container.getWindow().setVisible(visible);
	}

	@Override
	public void showToolTipText() throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public void showToolTipText(BBjNumber arg0, BBjNumber arg1) throws BBjException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getAttribute(String arg0) throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String arg0, String arg1) throws BBjException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getClientProperty(Object arg0) throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putClientProperty(Object arg0, Object arg1) throws BBjException {
		// TODO Auto-generated method stub

	}

	@Override
	public BBjWindow getParentWindow() throws BBjException {
		return this.container.getWindow();
	}

	@Override
	public String getComputedStyle(String arg0) throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStyle(String arg0) throws BBjException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStyle(String arg0, String arg1) throws BBjException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isGrouped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGrouped(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

}
