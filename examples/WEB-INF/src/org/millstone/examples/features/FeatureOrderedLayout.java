package org.millstone.examples.features;

import java.util.Hashtable;

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
		ol.addComponent(new TextField("TextField caption"));
		ol.addComponent(new Label("Label"));
		Select sel = new Select("Select caption");
		sel.addItem("Value 1");
		sel.addItem("Value 2");
		sel.addItem("Value 3");
		ol.addComponent(sel);
		ol.addComponent(new Label("Label 2"));

		show.addComponent(ol);
		l.addComponent(show);

		// Configuration

		Hashtable alternateEditors = new Hashtable();

		Select s =
			createSelect(
				"Orientation",
				new Integer[] {
					new Integer(OrderedLayout.ORIENTATION_HORIZONTAL),
					new Integer(OrderedLayout.ORIENTATION_VERTICAL),
					new Integer(OrderedLayout.ORIENTATION_FLOW)},
				new String[] { "Horizontal", "Vertical", "Flow" });

		alternateEditors.put("orientation", s);

		Select t =
			createSelect(
				"Style",
				new String[] { "", "form" },
				new String[] { "Default", "Form" });
		alternateEditors.put("style", t);

		l.addComponent(
			createPropertyPanel(ol,
			new String[] {
				"orientation"},
			alternateEditors));

		return l;
	}

	protected String getExampleSrc() {
		return "OrderedLayout ol = new OrderedLayout(OrderedLayout.ORIENTATION_FLOW);\n"
			+ "ol.addComponent(new TextField(\"Textfield caption\"));\n"
			+ "ol.addComponent(new Label(\"Label\"));\n";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"OrderedLayout",
			"This feature provides a container for laying out components either "
				+ "vertically, horizontally or flowingly. The orientation may be changed "
				+ "during runtime. It also defines a special style for themes to implement called \"form\""
				+ "that is used for input forms where the components are layed-out side-by-side "
				+ "with their captions."
				+ "<br/><br/>"
				+ "On the demo tab you can try out how the different properties "
				+ "affect the presentation of the component.",
			"orderedlayout.jpg" };
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */