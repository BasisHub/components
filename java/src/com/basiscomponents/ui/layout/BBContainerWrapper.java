/**
 * 
 */
package com.basiscomponents.ui.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;

import com.basis.bbj.proxies.sysgui.BBjControl;

/**
 * @author rlance
 * 
 */
public class BBContainerWrapper extends BBComponentWrapper implements
		ContainerWrapper {

	private Object layout = null;
	private boolean leftToRight = true;

	private List<BBComponentWrapper> componentWrapperList = new ArrayList<BBComponentWrapper>();
	private Map<BBComponent, BBComponentWrapper> componentToComponentWrapperMap = new WeakHashMap<BBComponent, BBComponentWrapper>();
	private Map<BBjControl, BBComponentWrapper> controlToComponentWrapperMap = new WeakHashMap<BBjControl, BBComponentWrapper>();
	private Map<ComponentWrapper, CC> componentWrapperToCCMap = new WeakHashMap<ComponentWrapper, CC>();
	private Map<BBComponent, Integer> componentToHashcodeMap = new WeakHashMap<BBComponent, Integer>();

	/**
	 * Constructor
	 */
	public BBContainerWrapper(BBContainer container) {
		super(container);
	}

	/**
	 * Constructor
	 */
	public BBContainerWrapper(BBContainer container, Object layout) {
		super(container);
		this.layout = layout;
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
		// TODO Auto-generated method stub

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
		this.controlToComponentWrapperMap.put(component.getControl(), componentWrapper);
		if (cc == null)
			cc = new CC();
		this.componentWrapperToCCMap.put(componentWrapper, cc);
	}

}
