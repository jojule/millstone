package org.millstone.examples.features;

import java.util.Hashtable;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.terminal.ExternalResource;
import org.millstone.base.ui.*;

public class FeatureLink extends Feature {

	public FeatureLink() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Link component");
		Link lnk =
			new Link(
				"Link caption",
				new ExternalResource("http://www.itmill.com"));
		show.addComponent(lnk);
		l.addComponent(show);

		Hashtable alternateEditors = new Hashtable();

		Select s =
			createSelect(
				"Border",
				new Integer[] {
					new Integer(Link.TARGET_BORDER_DEFAULT),
					new Integer(Link.TARGET_BORDER_MINIMAL),
					new Integer(Link.TARGET_BORDER_NONE)},
				new String[] { "Horizontal", "Vertical", "Flow" });
		alternateEditors.put("targetBorder", s);

		// Configuration
		l.addComponent(
			createPropertyPanel(lnk,
			new String[] {
				"enabled",
				"visible",
				"caption",
				"description",
				"targetName",
				"targetWidth",
				"targetHeight",
				"targetBorder",
				"style" },
			alternateEditors));

		return l;
	}

	protected String getExampleSrc() {
		return "Link lnk = new Link(\"Link caption\",new ExternalResource(\"http://www.itmill.com\"));\n";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"Link",
			"The link feature allows for making refences to both internal and external resources. "
				+ "The link can open the new resource in a new window, allowing for control of the newly "
				+ "opened windows attributes, such as size and border.<br/>"
				+ "<br/>"
				+ "On the demo tab you can try out how the different properties affect "
				+ "the presentation of the component.",
			"link.jpg" };
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */