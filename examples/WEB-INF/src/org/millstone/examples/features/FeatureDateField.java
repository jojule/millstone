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

import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.data.util.MethodProperty;
import org.millstone.base.ui.*;

public class FeatureDateField extends Feature {

	public FeatureDateField() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("DateField component");
		DateField df = new DateField("Caption");
		df.setValue(new java.util.Date());
		show.addComponent(df);
		l.addComponent(show);

		// Create locale selector
		Select selector = new Select("Application Locale");
		selector.addContainerProperty("name", String.class, "");
		selector.setItemCaptionPropertyId("name");
		Locale[] locales = Locale.getAvailableLocales();
		for (int i = 0; i < locales.length; i++)
			selector.addItem(locales[i]).getItemProperty("name").setValue(
				locales[i].getDisplayName());
		selector.setImmediate(true);
		selector.setPropertyDataSource(
			new MethodProperty(this.getApplication(), "locale"));
		l.addComponent(selector);

		// Properties
		PropertyPanel p = new PropertyPanel(df);
		Form ap = p.createBeanPropertySet(new String[] { "resolution" });
		ap.replaceWithSelect(
			"resolution",
			new Object[] {
				new Integer(DateField.RESOLUTION_YEAR),
				new Integer(DateField.RESOLUTION_MONTH),
				new Integer(DateField.RESOLUTION_DAY),
				new Integer(DateField.RESOLUTION_HOUR),
				new Integer(DateField.RESOLUTION_MIN),
				new Integer(DateField.RESOLUTION_SEC),
				new Integer(DateField.RESOLUTION_MSEC)},
			new Object[] {
				"Year",
				"Month",
				"Day",
				"Hour",
				"Minute",
				"Second",
				"Millisecond" });
		Select themes = (Select) p.getField("style");
		themes
			.addItem("text")
			.getItemProperty(themes.getItemCaptionPropertyId())
			.setValue("text");
		themes
			.addItem("calendar")
			.getItemProperty(themes.getItemCaptionPropertyId())
			.setValue("calendar");
		p.addProperties("DateField Properties", ap);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "DateField df = new DateField(\"Caption\");\n"
			+ "df.setValue(new java.util.Date());\n";

	}

	protected String getDescriptionXHTML() {
		return "Representing Dates and times and providing a way to select or enter some specific date or time "
			+ "is an oft recuring need in data-entry userinterfaces. Millstone provides a DateField feature that "
			+ "is both clear and easy to use and yet powerfull in its task allowing for granularity control and "
			+ "full support for different locales. "
			+ "A validator may be bound to the component to check and "
			+ "validate the given input.<br/>"
			+ "<br/>On the demo tab you can try out how the different properties affect the "
			+ "presentation of the component.";
	}

	protected String getImage() {
		return "datefield.jpg";
	}

	protected String getTitle() {
		return "DateField";
	}

}
