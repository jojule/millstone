package org.millstone.examples.features;

import java.util.Hashtable;
import java.util.Vector;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureSelect extends Feature {

	public FeatureSelect() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Select component");

		Vector options = new Vector();
		options.add("Item 1");
		options.add("Item 2");
		options.add("Item 3");
		options.add("Item 4");

		Select s = new Select("Caption", options);

		show.addComponent(s);
		l.addComponent(show);

		// Configuration
		Hashtable alternateEditors = new Hashtable();

		Select t =
			createSelect(
				"Style",
				new String[] { "default", "optiongroup" },
				new String[] { "Default", "Optiongroup" });

		alternateEditors.put("style", t);

		l.addComponent(
			createPropertyPanel(s,
			new String[] {
				"enabled",
				"visible",
				"readOnly",
				"writeThrough",
				"readThrough",
				"immediate",
				"multiSelect",
				"newItemsAllowed",
				"style",
				"caption",
				"description" },
			alternateEditors));

		return l;
	}

	protected String getExampleSrc() {
		return "Vector options = new Vector();\n"
			+ "options.add(\"Item 1\");\n"
			+ "options.add(\"Item 2\");\n"
			+ "Select s = new Select(\"Caption\", options);";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"Select",
			"The select feature embodies  two different modes of item selection.  "
				+ "Firstly it presents the single selection mode which is usually represented as "
				+ "either a drop-down menu or a radio-group of switches, secondly it "
				+ "allows for multiple item selection, this is usually represented as either a "
				+ "listbox of selectable items or as a group of checkboxes.<br/><br/>"
				+ "On the demo tab you can try out how the different properties affect the"
				+ " presentation of the component.",
			"select.jpg" };
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */