package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeaturePanel extends Feature {

	public FeaturePanel() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Panel caption");
		show.addComponent(new Label("Label in Panel"));
	
		l.addComponent(show);

		// Configuration

		ItemEditor cpp =
			(ItemEditor) createPropertyPanel(show,
				new String[] {
					"enabled",
					"visible",
					"caption",
					"style",
					"width",
					"height",
					"description"});
		l.addComponent(cpp);

		return l;
	}

	protected String getExampleSrc() {
		return "Panel show = new Panel(\"Panel caption\");\n"
			+ "show.addComponent(new Label(\"Label in Panel\"));";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"Panel","The panel container is a bordered and captioned container for components.<br/>"
			+ "<br/>"
			+ "On the demo tab you can try out how the different properties "+
			"affect the presentation of the component.","panel.jpg"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */