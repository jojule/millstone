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

import java.util.Hashtable;
import java.util.Vector;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureSelect extends Feature {

	public FeatureSelect() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Select component");

		Vector options = new Vector();
		options.add("Item 1");
		options.add("Item 2");
		options.add("Item 3");
		options.add("Item 4");

		Select s = new Select("Caption", options);

		show.addComponent(s);
		l.addComponent(show);

		// Configuration
		Hashtable alternateEditors = new Hashtable();

		Select t =
			createSelect(
				"Style",
				new String[] { "", "optiongroup" },
				new String[] { "Default", "Optiongroup" });

		alternateEditors.put("style", t);

		l.addComponent(
			createPropertyPanel(s,
			new String[] {
				"writeThrough",
				"readThrough",
				"multiSelect",
				"newItemsAllowed"},
			alternateEditors));

		return l;
	}

	protected String getExampleSrc() {
		return "Vector options = new Vector();\n"
			+ "options.add(\"Item 1\");\n"
			+ "options.add(\"Item 2\");\n"
			+ "Select s = new Select(\"Caption\", options);";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"Select",
			"The select feature embodies  two different modes of item selection.  "
				+ "Firstly it presents the single selection mode which is usually represented as "
				+ "either a drop-down menu or a radio-group of switches, secondly it "
				+ "allows for multiple item selection, this is usually represented as either a "
				+ "listbox of selectable items or as a group of checkboxes.<br/><br/>"
				+ "On the demo tab you can try out how the different properties affect the"
				+ " presentation of the component.",
			"select.jpg" };
	}

}
