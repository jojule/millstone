package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.terminal.ClassResource;
import org.millstone.base.ui.*;

import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Vector;

import org.millstone.base.data.Property;
import org.millstone.base.data.util.MethodProperty;

public abstract class Feature extends CustomComponent {

	private TabSheet ts;

	/** These are properties common to all features. */
	private String[] commonProperties = new String[]{"enabled","visible","caption","readOnly",
																						 "immediate","disabled","description","style"};

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
		String[] propertyNames,
		Hashtable alternateEditors) {

		Panel properties = new Panel("Properties",new GridLayout(2, 1));

		try {
			
			// Create bean information
			BeanInfo info =
				Introspector.getBeanInfo(objectToConfigure.getClass());
			PropertyDescriptor[] pd = info.getPropertyDescriptors();

			Vector checkBoxes = new Vector();
			Vector fields = new Vector();
			Vector others = new Vector();

			// Add common properties to the beginning of propertyNames
			String[] tmp = propertyNames;
			propertyNames = new String[propertyNames.length+commonProperties.length];
			for (int i=0;i<commonProperties.length;i++) {
				propertyNames[i] = commonProperties[i];	
			}
			for (int i=0;i<tmp.length;i++) {
				propertyNames[i+commonProperties.length] = tmp[i];	
			}
			
			// Add all the bean properties as MethodProperties to this Item
			for (int k = 0; k < propertyNames.length; k++) {
				for (int i = 0; i < pd.length; i++) {
					if (i == (pd.length-1) && !propertyNames[k].equals(pd[i].getName())) {
						System.out.println("!!! Property : "+	propertyNames[k] + " was not found in object!!");
						System.out.println("Object class is: "+objectToConfigure.getClass().toString());
						System.out.print("Available properties in object are: ");
						for (int y=0;y<pd.length;y++) {
							System.out.print(pd[y].getName()+" ");
						}
						System.out.print("\n");
					}
					// Skip till we find the property in question from bean property descriptor array.
					if (!propertyNames[k].equals(pd[i].getName())) {
						continue;
					}
					Method getMethod = pd[i].getReadMethod();
					Method setMethod = pd[i].getWriteMethod();
					Class type = pd[i].getPropertyType();
					String name = pd[i].getName();

					Property p =
						new MethodProperty(
							type,
							objectToConfigure,
							getMethod,
							setMethod);

					if (alternateEditors != null && alternateEditors.containsKey(name)) {
						Property.Editor editor = (Property.Editor)alternateEditors.get(name);
						editor.setPropertyDataSource(p);
						others.add(editor);
					} else {
						// Create a field of appropriate type
						if (java.util.Date.class.isAssignableFrom(p.getType()))
							others.add(new DateField(captionize(name), p));
						else if (Boolean.class.isAssignableFrom(p.getType()))
							checkBoxes.add(new Button(captionize(name), p));
						else
							fields.add(new TextField(captionize(name), p));
					}
					break;
				}
			}

			for (int i = 0; i < checkBoxes.size(); i++) {
				properties.addComponent((Component) checkBoxes.get(i));
			}
			if ((checkBoxes.size() % 2) != 0) {properties.addComponent(new Label(""));}

			for (int i = 0; i < fields.size(); i++) {
				properties.addComponent((Component) fields.get(i));
			}
			if ((fields.size() % 2) != 0) {properties.addComponent(new Label(""));}

			for (int i=0;i<others.size();i++) {
				properties.addComponent((Component)others.get(i));	
			}
			if ((others.size() % 2) != 0) {properties.addComponent(new Label(""));}			

		} catch (java.beans.IntrospectionException ignored) {
		}

		properties.addComponent(new Button("Set"));
		return properties;
	}

	private String captionize(String s) {
		return (s.substring(0,1).toUpperCase())+s.substring(1,s.length());
	}

	protected Select createSelect(
		String caption,
		Object[] keys,
		String[] names) {
		Select s = new Select(caption);
		s.addProperty("name", String.class, "");
		for (int i = 0; i < keys.length; i++) {
			if (Integer.class.isAssignableFrom(keys[i].getClass())) {
				s.addItem((Integer) keys[i]).getProperty("name").setValue(
					names[i]);
			}
			if (String.class.isAssignableFrom(keys[i].getClass())) {
				s.addItem((String) keys[i]).getProperty("name").setValue(
					names[i]);
			}
		}
		s.setItemCaptionPropertyId("name");
		return s;
	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */