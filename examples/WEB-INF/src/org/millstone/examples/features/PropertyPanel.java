/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000,2001,2002 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package org.millstone.examples.features;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.millstone.base.terminal.*;
import org.millstone.base.ui.*;
import org.millstone.base.data.*;
import org.millstone.base.data.util.*;

public class PropertyPanel
	extends Panel
	implements Button.ClickListener, Property.ValueChangeListener {

	private Select addComponent;
	private OrderedLayout formsLayout = new OrderedLayout();
	private LinkedList forms = new LinkedList();
	private Button setButton = new Button("Set", this);
	private Button discardButton = new Button("Discard changes", this);
	private Button showAllProperties =
		new Button("List of All Properties", this);
	private Table allProperties = new Table();
	private Object objectToConfigure;
	private BeanItem config;

	public PropertyPanel(Object objectToConfigure) {
		super();

		// Layout
		setCaption("Properties");
		addComponent(formsLayout);

		// Target object
		this.objectToConfigure = objectToConfigure;
		config = new BeanItem(objectToConfigure);

		// Control buttons
		OrderedLayout buttons =
			new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
		buttons.addComponent(setButton);
		buttons.addComponent(discardButton);
		addComponent(buttons);

		// Add default properties
		addBasicComponentProperties();
		if (objectToConfigure instanceof Select)
			addSelectProperties();
		if (objectToConfigure instanceof AbstractField)
			addFieldProperties();
		if ((objectToConfigure instanceof AbstractComponentContainer) && 
		!(objectToConfigure instanceof FrameWindow))
			addComponentContainerProperties();

		// The list of all properties
		addComponent(showAllProperties);
		showAllProperties.setSwitchMode(true);
		allProperties.setVisible(false);
		allProperties.addContainerProperty("Name", String.class, "");
		allProperties.addContainerProperty("Type", String.class, "");
		allProperties.addContainerProperty("R/W", String.class, "");
		allProperties.addContainerProperty("Demo", String.class, "");
		allProperties.setColumnAlignments(
			new String[] {
				Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,
				Table.ALIGN_CENTER });
		allProperties.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_ID);
		updatePropertyList();
		addComponent(allProperties);
	}

	public void addProperties(String propertySetCaption, Form properties) {
		Panel p = new Panel();
		p.setCaption(propertySetCaption);
		p.setStyle("light");
		p.addComponent(properties);
		formsLayout.addComponent(p);
		setButton.dependsOn(properties);
		discardButton.dependsOn(properties);
		properties.setWriteThrough(false);
		properties.setReadThrough(true);
		forms.add(properties);
		updatePropertyList();
	}

	public void buttonClick(Button.ClickEvent event) {

		if (event.getButton() == setButton) {
			for (Iterator i = forms.iterator(); i.hasNext();)
				 ((Form) i.next()).commit();
		}

		if (event.getButton() == discardButton) {
			for (Iterator i = forms.iterator(); i.hasNext();)
				 ((Form) i.next()).discard();
		}

		if (event.getButton() == showAllProperties) {
			allProperties.setVisible(
				((Boolean) showAllProperties.getValue()).booleanValue());
		}

	}

	public void updatePropertyList() {
		allProperties.removeAllItems();
		HashSet listed = new HashSet();
		for (Iterator i = forms.iterator(); i.hasNext();)
			listed.addAll(((Form) i.next()).getItemPropertyIds());
		BeanInfo info;
		try {
			info = Introspector.getBeanInfo(objectToConfigure.getClass());
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		PropertyDescriptor[] pd = info.getPropertyDescriptors();
		for (int i = 0; i < pd.length; i++) {
			allProperties.addItem(
				new Object[] {
					pd[i].getName(),
					pd[i].getPropertyType().getName(),
					(pd[i].getWriteMethod() == null ? "R" : "R/W"),
					(listed.contains(pd[i].getName()) ? "x" : "")},
				pd[i]);
		}
	}

	private void addBasicComponentProperties() {
		Form set =
			createBeanPropertySet(
				new String[] {
					"caption",
					"enabled",
					"icon",
					"visible",
					"description",
					"readOnly",
					"componentError",
					"immediate",
					 "style"});
		set.replaceWithSelect(
			"icon",
			new Object[] { null, new ThemeResource("icon/files/file.gif")},
			new Object[] { "No icon", "Sample icon" });
		Throwable sampleException;
		try {
			throw new NullPointerException("sample exception");
		} catch (NullPointerException e) {
			sampleException = e;
		}
		set.replaceWithSelect(
			"componentError",
			new Object[] {
				null,
				new UserError("Sample text error message."),
				new UserError(
					"<h3>Error message formatting</h3><p>Error messages can contain any UIDL "
						+ "formatting, like: <ul><li><b>Bold</b></li><li><i>Italic</i></li></ul></p>",
					UserError.CONTENT_UIDL,
					ErrorMessage.INFORMATION),
				new SystemError(
					"This is an example of exception error reposting",
					sampleException)},
			new Object[] {
				"No error",
				"Sample text error",
				"Sample Formatted error",
				"Sample System Error" });
		set
			.replaceWithSelect(
				"style",
				new Object[] { null },
				new Object[] { "Default" })
			.setNewItemsAllowed(true);

		addProperties("Component Basics", set);
	}

	private void addSelectProperties() {
		Form set =
			createBeanPropertySet(
				new String[] { "multiSelect", "newItemsAllowed" });
		addProperties("Select Properties", set);
	}

	private void addFieldProperties() {
		Form set = new Form(new GridLayout(2, 1));
		set.addField("focus", new Button("Focus", objectToConfigure, "focus"));
		addProperties("Field Features", set);
	}

	private void addComponentContainerProperties() {
		Form set = new Form(new GridLayout(2, 1));

		addComponent = new Select();
		addComponent.setImmediate(true);
		addComponent.addItem("Add component to container");
		addComponent.setNullSelectionItemId("Add field");
		addComponent.addItem("Text field");
		addComponent.addItem("Time");
		addComponent.addItem("Option group");
		addComponent.addItem("Calendar");
		addComponent.addListener(this);

		set.addField("component adder", addComponent);
		set.addField(
			"remove all components",
			new Button(
				"Remove all components",
				objectToConfigure,
				"removeAllComponents"));

		addProperties("ComponentContainer Features", set);
	}

	public void valueChange(Property.ValueChangeEvent event) {

		if (event.getProperty() == addComponent) {

			String value = (String) addComponent.getValue();

			if (value != null) {
				if (value.equals("Text field"))
					(
						(
							AbstractComponentContainer) objectToConfigure)
								.addComponent(
						new TextField("Test field"));
				if (value.equals("Time")) {
					DateField d = new DateField("Time", new Date());
					d.setDescription(
						"This is a DateField-component with text-style");
					d.setResolution(DateField.RESOLUTION_MIN);
					d.setStyle("text");
					(
						(
							AbstractComponentContainer) objectToConfigure)
								.addComponent(
						d);
				}
				if (value.equals("Calendar")) {
					DateField c = new DateField("Calendar", new Date());
					c.setDescription(
						"DateField-component with calendar-style and day-resolution");
					c.setStyle("calendar");
					c.setResolution(DateField.RESOLUTION_DAY);
					(
						(
							AbstractComponentContainer) objectToConfigure)
								.addComponent(
						c);
				}
				if (value.equals("Option group")) {
					Select s = new Select("Options");
					s.setDescription("Select-component with optiongroup-style");
					s.addItem("Linux");
					s.addItem("Windows");
					s.addItem("Solaris");
					s.addItem("Symbian");
					s.setStyle("optiongroup");

					(
						(
							AbstractComponentContainer) objectToConfigure)
								.addComponent(
						s);
				}

				addComponent.setValue(null);
			}
		}
	}

	public Form createBeanPropertySet(String names[]) {

		Form set = new Form(new GridLayout(2, 1));

		for (int i = 0; i < names.length; i++) {
			Property p = config.getItemProperty(names[i]);
			if (p != null)
				set.addItemProperty(names[i], p);
		}

		return set;
	}

	public AbstractField getField(Object propertyId) {
		for (Iterator i = forms.iterator(); i.hasNext();) {
			Form f = (Form) i.next();
			AbstractField af = f.getField(propertyId);
			if (af != null)
				return af;
		}
		return null;
	}
}
