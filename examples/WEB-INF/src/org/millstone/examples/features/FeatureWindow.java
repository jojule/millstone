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
import org.millstone.base.Application;


public class FeatureWindow extends Feature {
	Button addButton = new Button("Add to application",this,"addWin");
	Button removeButton = new Button("Remove from application",this,"delWin");
	Component conf = null;
	OrderedLayout demoComponent = null;

	public FeatureWindow() {
		super();
	}

	protected Component getDemoComponent() {

		if (demoComponent != null) return demoComponent;

		demoComponent = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Window component control");		
		((OrderedLayout)show.getLayout()).setOrientation(OrderedLayout.ORIENTATION_HORIZONTAL);
		show.addComponent(addButton);		
		show.addComponent(removeButton);		
		updateWinStatus();
		demoComponent.addComponent(show);

		return demoComponent;
	}

	protected String getExampleSrc() {
		return "Window win = new Window();\n"+
		"getApplication().addWindow(win);\n";
			
	}

	protected String[] getDescriptionXHTML() {
		return new String[]{"Window",
		"The window support of Millstone allows for opening and closing windows, "+
		"refreshing one window from another (for asynchronous terminals), "+
		"resizing windows and scrolling window content. "+
		"There are also a number of preset window border styles defined by "+
		"this feature.",
		"window.jpg"};
	}

	public void addWin() {
		Window win = new Window("Test window");
		win.setName("FeatureTestWindow");
		win.addComponent(new Label("This is a test window "+
		"demonstrating windowing features of Millstone"));
		win.addComponent(new Button("update"));
		getApplication().addWindow(win);
		updateWinStatus();	
	}
	
	public void delWin() {
		Window win = getApplication().getWindow("FeatureTestWindow");
		if (win != null)
		getApplication().removeWindow(win);
		updateWinStatus();	
	}
	
	private void updateWinStatus() {
		Application app = getApplication();
		if (app == null) {
			addButton.setEnabled(true);
			removeButton.setEnabled(false);	
		} else {
			Window win = app.getWindow("FeatureTestWindow");
			
			if (win == null) {
				addButton.setEnabled(true);
				removeButton.setEnabled(false);	
			} else {
				addButton.setEnabled(false);
				removeButton.setEnabled(true);	
			}
			
			if (win == null && conf != null && demoComponent != null) {
				demoComponent.removeComponent(conf);
				conf = null;
			}
			
			if (win != null && conf == null && demoComponent != null) {

				conf = createPropertyPanel(
				win,
				new String[] {
					"width",
					"height",
					"scrollOffsetX",
					"scrollOffsetY",
					"border"},null);
				
				demoComponent.addComponent(conf);
			}
		}
	}
}
