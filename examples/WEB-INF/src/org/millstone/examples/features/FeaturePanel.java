package org.millstone.examples.features;

import java.util.Hashtable;

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
		Hashtable alternateEditors = new Hashtable();

		Select t =
			createSelect(
				"Style",
				new String[] { "", "light","strong" },
				new String[] { "Default", "Light","Strong" });

		alternateEditors.put("style", t);

		l.addComponent(
		 createPropertyPanel(show,
				new String[] {
					"width",
					"height"},alternateEditors));

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
		return new String[] {
			"Panel",
			"The Panel is a container for other components, it usually draws a frame around it's "+
			"extremities and may have a caption to clarify the nature of the contained components purpose."+
			"A panel always contains firstly a layout onto which the actual contained components are added, "+
			"this layout may be switched on the fly. <br/><br/>"+
			"On the demo tab you can try out how the different properties "+
			"affect the presentation of the component.",
			"panel.jpg" };
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */