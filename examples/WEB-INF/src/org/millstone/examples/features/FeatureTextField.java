package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureTextField extends Feature {

	public FeatureTextField() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("TextField component");
		TextField tf = new TextField("Caption");
		tf.setValue("Contents");
		show.addComponent(tf);
		l.addComponent(show);

		// Configuration
		l.addComponent(
			createPropertyPanel(
				tf,
				new String[] {
					"enabled",
					"visible",
					"columns",
					"rows",
					"wordwrap",
					"readOnly",
					"writeThrough",
					"readThrough",
					"caption",
					"description",
					"secret",
					"style" }));

		return l;
	}

	protected String getExampleSrc() {
		return "TextField tf = new TextField(\"Caption\");\n"
			+ "tf.setValue(\"Contents\");";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"TextField","This is the TextField component. <br/>"+
		"It is used both for one line and multi-line text-entry.<br/>"+
		"It can be bound to an underlying data source, both directly or in a buffered (asynchronous)"+
		"way. In buffered mode its background color will change to indicate"+
		"that the value has changed but is not committed.<br/>"+
		"<br/>Furthermore a validator may be bound to the component to check and validate the given input.<br/>"+
		"<br/>On the demo tab you can try out how the different properties affect "+
		"the presentation of the component.","textfield.gif"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */