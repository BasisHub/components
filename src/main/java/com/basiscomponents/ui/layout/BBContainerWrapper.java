/**
 * 
 */
package com.basiscomponents.ui.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.basis.bbj.client.sysgui.datatypes.BBjColor;
import com.basis.bbj.client.util.BBjException;
import com.basis.bbj.proxies.sysgui.BBjControl;
import com.basis.bbj.proxies.sysgui.BBjDrawPanel;
import com.basis.bbj.proxies.sysgui.BBjWindow;
import com.basis.util.common.BasisNumber;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;

/**
 * @author rlance
 * 
 */
public class BBContainerWrapper extends BBComponentWrapper implements
		ContainerWrapper {

	private BBContainer container;
	private Object layout = null;
	private boolean leftToRight = true;

	private static final BBjColor DB_CELL_OUTLINE = new BBjColor(255, 0, 0);

	private List<BBComponentWrapper> componentWrapperList = new ArrayList<>();
	private Map<BBComponent, BBComponentWrapper> componentToComponentWrapperMap = new WeakHashMap<>();
	private Map<BBjControl, BBComponentWrapper> controlToComponentWrapperMap = new WeakHashMap<>();
	private Map<ComponentWrapper, CC> componentWrapperToCCMap = new WeakHashMap<>();
	private Map<BBComponent, Integer> componentToHashcodeMap = new WeakHashMap<>();

	/**
	 * Constructor
	 */
	public BBContainerWrapper(BBContainer container) {
		super(container);
		this.container = container;
	}

	/**
	 * Constructor
	 */
	public BBContainerWrapper(BBContainer container, Object layout) {
		super(container);
		this.container = container;
		this.layout = layout;
	}

	/**
	 * @return the container
	 */
	public BBContainer getContainer() {
		return container;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.miginfocom.layout.ContainerWrapper#getComponents()
	 */
	@Override
	public ComponentWrapper[] getComponents() {
		return this.componentWrapperList.toArray(new BBComponentWrapper[] {});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.miginfocom.layout.ContainerWrapper#getComponentCount()
	 */
	@Override
	public int getComponentCount() {
		return this.componentWrapperList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.miginfocom.layout.ContainerWrapper#getLayout()
	 */
	@Override
	public Object getLayout() {
		return this.layout;
	}

	/**
	 * @param layout
	 *            the layout to set
	 */
	public void setLayout(Object layout) {
		this.layout = layout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.miginfocom.layout.ContainerWrapper#isLeftToRight()
	 */
	@Override
	public boolean isLeftToRight() {
		return leftToRight;
	}

	public void setLeftToRight(boolean l2r) {
		leftToRight = l2r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.miginfocom.layout.ContainerWrapper#paintDebugCell(int, int, int,
	 * int)
	 */
	@Override
	public void paintDebugCell(int x, int y, int width, int height) {
		BBjWindow window = this.container.getWindow();
		if (window != null) {
			try {
				BBjDrawPanel drawpanel = window.getDrawPanel();
				drawpanel.setPenWidth(BasisNumber.createBasisNumber(1));
				drawpanel.setPenColor(DB_CELL_OUTLINE);
				drawpanel.setPattern(BBjDrawPanel.DASHED_LINE);
				drawpanel.drawRect(x, y, width, height);
			} catch (BBjException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getComponentType(boolean disregardScrollPane) {
		return TYPE_CONTAINER;
	}

	/**
	 * @return the componentWrapperList
	 */
	public List<BBComponentWrapper> getComponentWrapperList() {
		return componentWrapperList;
	}

	/**
	 * @param componentWrapperList
	 *            the componentWrapperList to set
	 */
	public void setComponentWrapperList(
			List<BBComponentWrapper> componentWrapperList) {
		this.componentWrapperList = componentWrapperList;
	}

	/**
	 * @return the componentToComponentWrapperMap
	 */
	public Map<BBComponent, BBComponentWrapper> getComponentToComponentWrapperMap() {
		return componentToComponentWrapperMap;
	}

	/**
	 * @param componentToComponentWrapperMap
	 *            the componentToComponentWrapperMap to set
	 */
	public void setComponentToComponentWrapperMap(
			Map<BBComponent, BBComponentWrapper> componentToComponentWrapperMap) {
		this.componentToComponentWrapperMap = componentToComponentWrapperMap;
	}

	/**
	 * @return the controlToComponentWrapperMap
	 */
	public Map<BBjControl, BBComponentWrapper> getControlToComponentWrapperMap() {
		return controlToComponentWrapperMap;
	}

	/**
	 * @param controlToComponentWrapperMap
	 *            the controlToComponentWrapperMap to set
	 */
	public void setControlToComponentWrapperMap(
			Map<BBjControl, BBComponentWrapper> controlToComponentWrapperMap) {
		this.controlToComponentWrapperMap = controlToComponentWrapperMap;
	}

	/**
	 * @return the componentWrapperToCCMap
	 */
	public Map<ComponentWrapper, CC> getComponentWrapperToCCMap() {
		return componentWrapperToCCMap;
	}

	/**
	 * @param componentWrapperToCCMap
	 *            the componentWrapperToCCMap to set
	 */
	public void setComponentWrapperToCCMap(
			Map<ComponentWrapper, CC> componentWrapperToCCMap) {
		this.componentWrapperToCCMap = componentWrapperToCCMap;
	}

	/**
	 * @return the componentToHashcodeMap
	 */
	public Map<BBComponent, Integer> getComponentToHashcodeMap() {
		return componentToHashcodeMap;
	}

	/**
	 * @param componentToHashcodeMap
	 *            the componentToHashcodeMap to set
	 */
	public void setComponentToHashcodeMap(
			Map<BBComponent, Integer> componentToHashcodeMap) {
		this.componentToHashcodeMap = componentToHashcodeMap;
	}

	// add component wrapper information
	public void add(BBComponent component, CC cc) {
		BBComponentWrapper componentWrapper = new BBComponentWrapper(component);
		componentWrapper.setContainerWrapper(this);
		this.componentWrapperList.add(componentWrapper);
		this.componentToComponentWrapperMap.put(component, componentWrapper);
		this.controlToComponentWrapperMap.put(component.getControl(),
				componentWrapper);
		if (cc == null)
			cc = new CC();
		this.componentWrapperToCCMap.put(componentWrapper, cc);
	}

}
