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
					"style" },null));

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
		return new String[]{"TextField",
		"Being one of the most quintessential components of any business application, "+
		"the ubiquitus Textfield is featured in Millstone with a variety of styles and modes. <br/><br/>"+
		"Millstone combines the logic of both the single line text-entry field and the multi-line "+
		"text-area into one component. "+
		"As with all Data-components of Millstone, the Textfield can also be bound to an "+
		"underlying data source, both directly or in a buffered (asynchronous) "+
		"mode. In buffered mode its background color will change to indicate "+
		"that the value has changed but is not committed.<br/>"+
		"<br/>Furthermore a validator may be bound to the component to check and validate the given input.<br/>"+
		"<br/>On the demo tab you can try out how the different properties affect "+
		"the presentation of the component.","textfield.gif"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */