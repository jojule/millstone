package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureTabSheet extends Feature {

	public FeatureTabSheet() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("TabSheet component");
		
		TabSheet ts = new TabSheet();
		ts.addTab(new Label("Tab 1 Body"),"Tab 1 caption",null);
		ts.addTab(new Label("Tab 2 Body"),"Tab 2 caption",null);
		ts.addTab(new Label("Tab 3 Body"),"Tab 3 caption",null);
				
		show.addComponent(ts);
		l.addComponent(show);

		// Configuration
		l.addComponent(
			createPropertyPanel(
				ts,
				new String[] {
					"enabled",
					"visible",
					"caption",
					"description" }));

		return l;
	}

	protected String getExampleSrc() {
		return "TabSheet ts = new TabSheet();"+
		"ts.addTab(new Label(\"Tab 1 Body\"),\"Tab 1 caption\",null);"+
		"ts.addTab(new Label(\"Tab 2 Body\"),\"Tab 2 caption\",null);"+
		"ts.addTab(new Label(\"Tab 3 Body\"),\"Tab 3 caption\",null);";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"TabSheet","A multicomponent container with tabs for switching between them.<br/>"+
				"In the normal case, one would place a layout component on each tab.<br/><br />"+
				"On the demo tab you can try out how the different properties affect "+
				"the presentation of the component.","tabsheet.jpg"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */