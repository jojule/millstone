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

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureOrderedLayout extends Feature {

	public FeatureOrderedLayout() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("OrderedLayout component");
		OrderedLayout ol = new OrderedLayout();
		ol.addComponent(new TextField("TextField caption"));
		ol.addComponent(new Label("Label"));
		Select sel = new Select("Select caption");
		sel.addItem("Value 1");
		sel.addItem("Value 2");
		sel.addItem("Value 3");
		ol.addComponent(sel);
		ol.addComponent(new Label("Label 2"));

		show.addComponent(ol);
		l.addComponent(show);

		// Configuration

		Hashtable alternateEditors = new Hashtable();

		Select s =
			createSelect(
				"Orientation",
				new Integer[] {
					new Integer(OrderedLayout.ORIENTATION_HORIZONTAL),
					new Integer(OrderedLayout.ORIENTATION_VERTICAL),
					new Integer(OrderedLayout.ORIENTATION_FLOW)},
				new String[] { "Horizontal", "Vertical", "Flow" });

		alternateEditors.put("orientation", s);

		Select t =
			createSelect(
				"Style",
				new String[] { "", "form" },
				new String[] { "Default", "Form" });
		alternateEditors.put("style", t);

		l.addComponent(
			createPropertyPanel(
				ol,
				new String[] { "orientation" },
				alternateEditors));

		return l;
	}

	protected String getExampleSrc() {
		return "OrderedLayout ol = new OrderedLayout(OrderedLayout.ORIENTATION_FLOW);\n"
			+ "ol.addComponent(new TextField(\"Textfield caption\"));\n"
			+ "ol.addComponent(new Label(\"Label\"));\n";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "This feature provides a container for laying out components either "
			+ "vertically, horizontally or flowingly. The orientation may be changed "
			+ "during runtime. It also defines a special style for themes to implement called \"form\""
			+ "that is used for input forms where the components are layed-out side-by-side "
			+ "with their captions."
			+ "<br/><br/>"
			+ "On the demo tab you can try out how the different properties "
			+ "affect the presentation of the component.";
	}

	protected String getImage() {
		return "orderedlayout.jpg";
	}

	protected String getTitle() {
		return "OrderedLayout";
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */