package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.terminal.ClassResource;
import org.millstone.base.ui.*;

public class FeatureButton extends Feature {

	public FeatureButton() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Button component");		
		
		Button b = new Button("Caption");
		
		
		show.addComponent(b);
		l.addComponent(show);

		// Configuration
		l.addComponent(
			createPropertyPanel(
				b,
				new String[] {
					"enabled",
					"visible",
					"readOnly",
					"immediate",
					"style",
					"switchMode",
					"caption",
					"description" }));

		return l;
	}

	protected String getExampleSrc() {
		return "Button b = new Button(\"Caption\");\n";
			
	}
	
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		new ClassResource("button.gif",this.getApplication());
		return new String[]{"Button","<img src=\"res(button.gif)\"/>This is the Button control.<br/>"+
				"It can function either as a pushbutton or switch. (checkbox)<br/>"+
				"<br/>"+
				"On the demo tab you can try out how the different properties affect "+
				"the presentation of the component.","button.gif"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */