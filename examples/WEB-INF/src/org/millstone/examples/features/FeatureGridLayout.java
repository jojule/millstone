package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureGridLayout extends Feature {

	public FeatureGridLayout() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("OrderedLayout component");
		GridLayout gl = new GridLayout(2,2);
		gl.addComponent(new Label("Label 1 in GridLayout"));
		gl.addComponent(new Label("Label 2 in GridLayout"));
		gl.addComponent(new Label("Label 3 in GridLayout"));
		gl.addComponent(new Label("Label 4 in GridLayout"));
		
		show.addComponent(gl);
		l.addComponent(show);

		// Configuration
		l.addComponent(
			createPropertyPanel(
				gl,
				new String[] {
					"enabled",
					"visible",
					"caption",
					"width",
					"height",
					"description" }));

		return l;
	}

	protected String getExampleSrc() {
		return "GridLayout gl = new GridLayout(2,2);\n"+
		"gl.addComponent(new Label(\"Label 1 in GridLayout\"));\n"+
		"gl.addComponent(new Label(\"Label 2 in GridLayout\"));\n"+
		"gl.addComponent(new Label(\"Label 3 in GridLayout\"));\n"+
		"gl.addComponent(new Label(\"Label 4 in GridLayout\"));\n";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"Gridlayout","An container that lays out the components it contains in a grid of preset size.<br/>"+
				"<br/>"+
				"On the demo tab you can try out how the different properties affect "+
				"the presentation of the component.","gridlayout.jpg"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */
