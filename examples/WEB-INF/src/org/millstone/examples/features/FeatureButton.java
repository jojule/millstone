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
import org.millstone.base.terminal.ClassResource;
import org.millstone.base.ui.*;

public class FeatureButton extends Feature {

	public FeatureButton() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Button component");
		Button b = new Button("Caption");
		show.addComponent(b);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(b);
		Select themes = (Select) p.getField("style");
		themes.addItem("link").getItemProperty(themes.getItemCaptionPropertyId()).setValue("link");
		Form ap = p.createBeanPropertySet(new String[] {"switchMode"});
		p.addProperties("Button Properties", ap);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "Button b = new Button(\"Caption\");\n";

	}

	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		new ClassResource("button.gif", this.getApplication());
		return new String[] {
			"Button",
			"In Millstone, buttons may function either as a pushbuttons or switches. (checkboxes)<br/><br/>"
				+ "On the demo tab you can try out how the different properties affect "
				+ "the presentation of the component.",
			"button.gif" };
	}

}
