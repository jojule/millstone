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
import org.millstone.base.terminal.Sizeable;
import org.millstone.base.ui.*;

public class FeatureEmbedded extends Feature {

	public FeatureEmbedded() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Embedded component");
		Embedded emb = new Embedded("Embedded Caption");
		emb.setClassId("clsid:F08DF954-8592-11D1-B16A-00C0F0283628");
		emb.setWidth(100);
		emb.setHeight(50);
		emb.setParameter("BorderStyle", "1");
		emb.setParameter("MousePointer", "1");
		emb.setParameter("Enabled", "1");
		emb.setParameter("Min", "1");
		emb.setParameter("Max", "10");

		show.addComponent(emb);
		l.addComponent(show);

		Hashtable alternateEditors = new Hashtable();
		
		Select s = new Select("heightUnits");
		s.addContainerProperty("name", String.class, "");
		s.setItemCaptionPropertyId("name");
		for (int i = 0; i < Sizeable.UNIT_SYMBOLS.length; i++) {
			s.addItem(new Integer(i)).getItemProperty("name").setValue(
				Sizeable.UNIT_SYMBOLS[i]);

		}
		alternateEditors.put("heightUnits",s);

		s = new Select("widthUnits");
		s.addContainerProperty("name", String.class, "");
		s.setItemCaptionPropertyId("name");
		for (int i = 0; i < Sizeable.UNIT_SYMBOLS.length; i++) {
			s.addItem(new Integer(i)).getItemProperty("name").setValue(
				Sizeable.UNIT_SYMBOLS[i]);

		}
		
		alternateEditors.put("widthUnits", s);

		// Configuration
		l.addComponent(
				createPropertyPanel(emb,
					new String[] {
						"type",
						"classId",
						"width",
						"height",
						"widthUnits",
						"heightUnits" },alternateEditors));

		

		return l;
	}

	protected String getExampleSrc() {
		return "Embedded emb = new Embedded(\"Caption\");\n"
			+ "emb.setClassId(\"clsid:F08DF954-8592-11D1-B16A-00C0F0283628\");\n"
			+ "emb.setWidth(100);\n"
			+ "emb.setHeight(50);\n"
			+ "\n//Set optional object parameters\n"
			+ "emb.setParameter(\"BorderStyle\",\"1\");\n"
			+ "emb.setParameter(\"MousePointer\",\"1\");\n"
			+ "emb.setParameter(\"Enabled\",\"1\");\n"
			+ "emb.setParameter(\"Min\",\"1\");\n"
			+ "emb.setParameter(\"Max\",\"10\");\n";

	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"Embedded",
			"The embedding feature allows for adding multimedia and other non-specified content to your application. "
			+ "The feature has provisions for embedding both applets and Active X controls. "
			+ "Actual support for embedded media types is left to the terminal."
			+ "<br/>"
			+ "On the demo tab you can try out how the different properties affect the presentation "
			+ "of the component.","embedded.jpg"};
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */