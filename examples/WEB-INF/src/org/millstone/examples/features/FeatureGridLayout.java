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

public class FeatureGridLayout extends Feature {

	public FeatureGridLayout() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("OrderedLayout component");
		GridLayout gl = new GridLayout(2,2);
		gl.addComponent(new Label("Label 1 in GridLayout"));
		gl.addComponent(new Label("Label 2 in GridLayout"));
		gl.addComponent(new Label("Label 3 in GridLayout"));
		gl.addComponent(new Label("Label 4 in GridLayout"));
		
		show.addComponent(gl);
		l.addComponent(show);

		// Configuration
		l.addComponent(
			createPropertyPanel(
				gl,
				new String[] {
					"width",
					"height"},null));

		return l;
	}

	protected String getExampleSrc() {
		return "GridLayout gl = new GridLayout(2,2);\n"+
		"gl.addComponent(new Label(\"Label 1 in GridLayout\"));\n"+
		"gl.addComponent(new Label(\"Label 2 in GridLayout\"));\n"+
		"gl.addComponent(new Label(\"Label 3 in GridLayout\"));\n"+
		"gl.addComponent(new Label(\"Label 4 in GridLayout\"));\n";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "This feature provides a container that lays out components into a grid of given "+
				"width and height.<br/><br/>"+
				"On the demo tab you can try out how the different properties affect "+
				"the presentation of the component.";
	}


	protected String getImage() {
		return "gridlayout.jpg";
	}

	protected String getTitle() {
		return "GridLayout";
	}
}