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

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.ui.*;

public class FeatureTextField extends Feature {

	public FeatureTextField() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Test component
		TextField tf = new TextField("Caption");
		Panel test = new Panel("TextField Component");
		test.addComponent(tf);
		l.addComponent(test);

		// Properties
		PropertyPanel p = new PropertyPanel(tf);
		l.addComponent(p);
		Form f =
			p.createBeanPropertySet(
				new String[] {
					"columns",
					"rows",
					"wordwrap",
					"writeThrough",
					"readThrough",
					"nullRepresentation",
					"nullSettingAllowed",
					"secret" });
		p.addProperties("Text field properties", f);

		return l;
	}

	protected String getExampleSrc() {
		return "TextField tf = new TextField(\"Caption\");\n"
			+ "tf.setValue(\"Contents\");";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"TextField",
			"Being one of the most quintessential components of any business application, "
				+ "the ubiquitus Textfield is featured in Millstone with a variety of styles and modes. <br/><br/>"
				+ "Millstone combines the logic of both the single line text-entry field and the multi-line "
				+ "text-area into one component. "
				+ "As with all Data-components of Millstone, the Textfield can also be bound to an "
				+ "underlying data source, both directly or in a buffered (asynchronous) "
				+ "mode. In buffered mode its background color will change to indicate "
				+ "that the value has changed but is not committed.<br/>"
				+ "<br/>Furthermore a validator may be bound to the component to check and validate the given input.<br/>"
				+ "<br/>On the demo tab you can try out how the different properties affect "
				+ "the presentation of the component.",
			"textfield.gif" };
	}

}
