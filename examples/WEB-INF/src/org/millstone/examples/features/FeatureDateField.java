package org.millstone.examples.features;

import java.util.Date;

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
		df.setStyle(DateField.STYLE_CALENDAR);
		
		show.addComponent(df);
		l.addComponent(show);

		// Configuration
		ItemEditor cpp =
			(ItemEditor)createPropertyPanel(
				df,
				new String[] {
					"enabled",
					"visible",
					"readOnly",
					"immediate",
					"caption",
					"description",
					"style" });
		cpp.setLayout(new OrderedLayout());
		
		Select s = createSelect("Style", 
			new String[]{"field","text","calendar"},
			new String[]{"Field","Text","Calendar"});
		cpp.setPropertyEditor("style", s);
		l.addComponent(cpp);

		return l;
	}

	protected String getExampleSrc() {
		return "DateField df = new DateField(\"Caption\");\n"+
				"df.setValue(new java.util.Date());\n";
			
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"Datefield","This is the DateField control.<br/>"+
				"It is used to display date and time with varying granularity.<br/>"+
				"<br/>Furthermore a validator may be bound to the component to check and "+
				"validate the given input.<br/>"+
				"<br/>On the demo tab you can try out how the different properties affect the "+
				"presentation of the component.","datefield.jpeg"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */