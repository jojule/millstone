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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.millstone.base.terminal.*;
import org.millstone.base.ui.*;
import org.millstone.base.data.*;
import org.millstone.base.data.util.*;

public class PropertyPanel extends Panel implements Button.ClickListener {

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
					"icon",
					"description",
					"style",
					"enabled",
					"visible",
					"readOnly",
					"immediate" });
		set.replaceWithSelect(
				"icon",
				new Object[] { null, new ThemeResource("icon/files/file.gif")},
				new Object[] { "No icon", "Sample icon" });
		set.replaceWithSelect(
				"style",
				new Object[] { null },
				new Object[] { "Default"}).setNewItemsAllowed(true);
		
		addProperties("Component basics", set);
	}

	private void addSelectProperties() {
		Form set =
			createBeanPropertySet(
				new String[] {
					"multiSelect",
					"newItemsAllowed"  });
		addProperties("Select properties", set);
	}

	private void addFieldProperties() {
		Form set = new Form(new GridLayout(2,1));
		set.addField("focus",new Button("Focus",objectToConfigure,"focus"));
		addProperties("Field features", set);
	}

	public Form createBeanPropertySet(String names[]) {

		Form set = new Form(new GridLayout(2,1));

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
