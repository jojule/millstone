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
		return new String[]{"Window","Window support in Millstone includes features for: <ul>"+
		"<li>Opening windows</li>"+
		"<li>Multiple window border styles</li>"+
		"<li>Closing windows</li>"+
		"<li>Refresing window contents from other windows</li>"+
		"<li>Resizing windows</li>"+
		"<li>Scrolling window contents</li>"+
		"</ul>\n","window.jpg"};
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
					"enabled",
					"visible",
					"style",
					"width",
					"height",
					"scrollOffsetX",
					"scrollOffsetY",
					"border",
					"caption"});
				
				demoComponent.addComponent(conf);
			}
		}
	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */