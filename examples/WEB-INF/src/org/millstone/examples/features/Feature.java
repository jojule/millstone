package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.terminal.ClassResource;
import org.millstone.base.ui.*;

public abstract class Feature extends CustomComponent {

	private TabSheet ts;

	public Feature() {
		ts = new TabSheet();
		setCompositionRoot(ts);
	}

	public void attach() {

		// Optional description
		String[] desc = getDescriptionXHTML();
		if (desc != null) {
			GridLayout gl = new GridLayout(2, 1);
			gl.addComponent(
				new Embedded(
					"",
					new ClassResource(desc[2], this.getApplication())));
			gl.addComponent(
				new Label(
					"<h2>" + desc[0] + "</h2>" + desc[1],
					Label.CONTENT_XHTML));
			ts.addTab(gl, "Description", null);
		}

		// Demo
		ts.addTab(getDemoComponent(), "Demo", null);

		// Example source
		String example = getExampleSrc();
		if (example != null) {
			OrderedLayout l = new OrderedLayout();
			l.addComponent(
				new Label(
					"<h2>" + getClass().getName() + " example</h2>",
					Label.CONTENT_XHTML));
			l.addComponent(new Label(example, Label.CONTENT_PREFORMATTED));
			ts.addTab(l, "Code Sample", null);
		}
	}

	// Returns, "name","desc", and optional image name string to display
	protected String[] getDescriptionXHTML() {
		return null;
	}

	protected String getExampleSrc() {
		return null;
	}

	abstract protected Component getDemoComponent();

	protected Component createPropertyPanel(
		Object objectToConfigure,
		String[] propertyNames) {

		ItemEditor properties = new ItemEditor("Properties", "Set", null);
		properties.setItemDataSource(new BeanItem(objectToConfigure));
		Object[] ids = properties.getPropertyIds().toArray();
		for (int i = 0; i < ids.length; i++) {
			boolean found = false;
			for (int j = 0; j < propertyNames.length; j++)
				if (ids[i].equals(propertyNames[j]))
					found = true;
			if (!found)
				properties.removeProperty(ids[i]);
		}

		((OrderedLayout)properties.getLayout()).setStyle("form");

		return properties;
	}

	protected Select createSelect(
		String caption,
		Object[] keys,
		String[] names) {
		Select s = new Select(caption);
		s.addProperty("name", String.class, "");
		for (int i = 0; i < keys.length; i++) {
		 if (Integer.class.isAssignableFrom(keys[i].getClass())) {
			s
				.addItem((Integer)keys[i])
				.getProperty("name")
				.setValue(names[i]);
		 }
		 if (String.class.isAssignableFrom(keys[i].getClass())) {
		 	s
				.addItem((String)keys[i])
				.getProperty("name")
				.setValue(names[i]);	
		 }		 
		}
		s.setItemCaptionPropertyId("name");
		return s;
	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */