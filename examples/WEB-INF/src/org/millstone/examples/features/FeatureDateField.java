package org.millstone.examples.features;

import java.util.Date;
import java.util.Hashtable;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureDateField extends Feature {

	public FeatureDateField() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("DateField component");

		DateField df = new DateField("Caption");
		df.setValue(new java.util.Date());
		df.setStyle("calendar");

		show.addComponent(df);
		l.addComponent(show);

		Hashtable alternateEditors = new Hashtable();

		Select s =
			createSelect(
				"Style",
				new String[] { "default", "text", "calendar" },
				new String[] { "Default", "Text", "Calendar" });

		alternateEditors.put("style", s);

		// Configuration
		l.addComponent(
			createPropertyPanel(df,
				new String[] {
					"enabled",
					"visible",
					"readOnly",
					"immediate",
					"caption",
					"description",
					"style" },
				alternateEditors));

		return l;
	}

	protected String getExampleSrc() {
		return "DateField df = new DateField(\"Caption\");\n"
			+ "df.setValue(new java.util.Date());\n";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"Datefield",
			"Representing Dates and times and providing a way to select or enter some specific date or time "
				+ "is an oft recuring need in data-entry userinterfaces. Millstone provides a DateField feature that "
				+ "is both clear and easy to use and yet powerfull in its task allowing for granularity control and "
				+ "full support for different locales. "
				+ "A validator may be bound to the component to check and "
				+ "validate the given input.<br/>"
				+ "<br/>On the demo tab you can try out how the different properties affect the "
				+ "presentation of the component.",
			"datefield.jpg" };
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */