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

public class FeaturePanel extends Feature {

	public FeaturePanel() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Panel caption");
		show.addComponent(new Label("Label in Panel"));

		l.addComponent(show);

		// Configuration
		Hashtable alternateEditors = new Hashtable();

		Select t =
			createSelect(
				"Style",
				new String[] { "", "light","strong" },
				new String[] { "Default", "Light","Strong" });

		alternateEditors.put("style", t);

		l.addComponent(
		 createPropertyPanel(show,
				new String[] {
					"width",
					"height"},alternateEditors));

		return l;
	}

	protected String getExampleSrc() {
		return "Panel show = new Panel(\"Panel caption\");\n"
			+ "show.addComponent(new Label(\"Label in Panel\"));";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"Panel",
			"The Panel is a container for other components, it usually draws a frame around it's "+
			"extremities and may have a caption to clarify the nature of the contained components purpose."+
			"A panel always contains firstly a layout onto which the actual contained components are added, "+
			"this layout may be switched on the fly. <br/><br/>"+
			"On the demo tab you can try out how the different properties "+
			"affect the presentation of the component.",
			"panel.jpg" };
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */