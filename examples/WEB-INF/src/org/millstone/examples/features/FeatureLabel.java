package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureLabel extends Feature {

	public FeatureLabel() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Label component");
		Label lab = new Label("Label text");
	
		show.addComponent(lab);
		l.addComponent(show);

		// Configuration
		l.addComponent(
			createPropertyPanel(
				lab,
				new String[] {
					"enabled",
					"visible",
					"caption",
					"description",
					"style" }));

		return l;
	}

	protected String getExampleSrc() {
		return "Label l = new Label(\"Caption\");\n";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"Label",
				"Millstone Labels are used not only for captions and plain text but also for embedding "+
				"more complicated content such as for instance XHTML."+
				"<br/>"+
				"On the demo tab you can try out how the different properties affect "+
				"the presentation of the component.","label.jpg"}; 
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */
