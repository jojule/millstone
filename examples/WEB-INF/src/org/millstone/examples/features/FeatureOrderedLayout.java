package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureOrderedLayout extends Feature {

	public FeatureOrderedLayout() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("OrderedLayout component");
		OrderedLayout ol = new OrderedLayout();
		ol.addComponent(new Label("Label 1 in OrderedLayout"));
		ol.addComponent(new Label("Label 2 in OrderedLayout"));
		ol.addComponent(new Label("Label 3 in OrderedLayout"));
		ol.addComponent(new Label("Label 4 in OrderedLayout"));

		show.addComponent(ol);
		l.addComponent(show);

		// Configuration

		ItemEditor cpp =
			(ItemEditor) createPropertyPanel(ol,
				new String[] {
					"enabled",
					"visible",
					"caption",
					"orientation",
					"description",
					"style" });

		Select s = createSelect("Orientation", 
			new Integer[]{new Integer(OrderedLayout.ORIENTATION_HORIZONTAL),
						  new Integer(OrderedLayout.ORIENTATION_VERTICAL),
						  new Integer(OrderedLayout.ORIENTATION_FLOW)},
			new String[]{"Horizontal","Vertical","Flow"});
		cpp.setPropertyEditor("orientation", s);
		
		Select t = createSelect("Style", 
			new String[]{"default", "form"},						  
			new String[]{"Default","Form"});
		cpp.setPropertyEditor("style", t);
		
		l.addComponent(cpp);

		return l;
	}

	protected String getExampleSrc() {
		return "OrderedLayout ol = new OrderedLayout(OrderedLayout.ORIENTATION_FLOW);\n"
			+ "ol.addComponent(new Label(\"Label 1 in OrderedLayout \"));\n"
			+ "ol.addComponent(new Label(\"Label 2 in OrderedLayout \"));\n"
			+ "ol.addComponent(new Label(\"Label 3 in OrderedLayout \"));\n";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"OrderedLayout","An container that lays out the components it contains either<br/>"
			+ "vertically, horizontally or flowingly.<br/>"
			+ "The legal values for the orientation property are 0,1 and 2. <br/>"
			+ "<br/>"
			+ "On the demo tab you can try out how the different properties "+
			"affect the presentation of the component.","orderedlayout.jpeg"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */