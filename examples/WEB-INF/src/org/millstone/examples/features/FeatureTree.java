package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.data.util.FilesystemContainer;
import org.millstone.base.data.util.ObjectProperty;
import org.millstone.base.ui.*;
import org.millstone.base.event.Action;
import java.io.File;
import java.util.Hashtable;

public class FeatureTree extends Feature implements Action.Handler {

	private Tree t;

	private boolean actionsActive = false;
	private Button actionHandlerSwitch = new Button("Activate actions",this,"toggleActions");

	public FeatureTree() {
		super();
	}

	public void toggleActions() {
		if (actionsActive) {
			t.removeActionHandler(this);
			actionsActive = false;	
			actionHandlerSwitch.setCaption("Activate Actions");
		} else {
			t.addActionHandler(this);
			actionsActive = true;	
			actionHandlerSwitch.setCaption("Deactivate Actions");
		}
	}
	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Panel show = new Panel("Tree component");
		t = new Tree("Caption");
		for (int i=0;i<10;i++) {
			t.addItem("Parent "+i);
			t.addItem("Child One "+i);
		    t.setParent("Child One "+i, "Parent "+i);
		    t.addItem("Child Two "+i);
		    t.setParent("Child Two "+i, "Child One "+i);
		    t.addItem("Child Three "+i);
		    t.setParent("Child Three "+i,"Child Two "+i);
		    t.addItem("Child Four "+i);
		    t.setParent("Child Four "+i,"Child Three "+i);
		   	t.setChildrenAllowed("Child Four "+i, false);
		}

		show.addComponent(t);
		l.addComponent(show);

		// Configuration
		Hashtable alternateEditors = new Hashtable();
		
		Select s =
			createSelect(
				"Style",
				new String[] { "default", "menu","dropmenu" },
				new String[] { "Default", "Menu","Dropmenu" });

		alternateEditors.put("style", s);
		
		l.addComponent(
			 createPropertyPanel(t,
				new String[] {
					"selectable",
					"multiSelect",
					"writeThrough",
					"readThrough"},alternateEditors));

		l.addComponent(this.actionHandlerSwitch);

		return l;
	}

	protected String getExampleSrc() {
		return "t = new Tree(\"Caption\");\n"+
					"for (int i=0;i<10;i++) {\n"+
					" t.addItem(\"Parent \"+i);"+
					" t.addItem(\"Child\"+i);\n"+
		    		"t.setParent(\"Child\"+i, \"Parent \"+i);\n"+
		   			"t.setChildrenAllowed(\"Child \"+i, false);";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"Tree",
			"A tree is a natural way to represent datasets that have hierarchical relationships, like for instance a filesystems. "+
			"Millstone features a versatile and powerfull Tree component that works much like the tree components "+
			"of most modern operating system userinterfaces, only regardeless of the terminal in question. "+
			"The most prominent use of the Tree component is to use it for displaying a hierachical menu, like the "+
			"menu on the left side of the screen for instance, or to display filesystems.<br/><br/>"+
			"The tree, again like all Millstone data-components, may be bound to an underlying datasource like for "+
			"a database or a filesystem.<br/>"+
			"<br/>On the demo tab you can try out how the different properties "+
			"affect the presentation of the component.",
			"tree.jpg" };
	}

	private Action ACTION1 = new Action("Action 1");
	private Action ACTION2 = new Action("Action 2");
	private Action ACTION3 = new Action("Action 3");

	private Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

	/**
	 * @see org.millstone.base.event.Action.Handler#getActions(Object)
	 */
	public Action[] getActions(Object target, Object sender) {
		return actions;
	}

	/**
	 * @see org.millstone.base.event.Action.Handler#handleAction(Action, Object, Object)
	 */
	public void handleAction(Action action, Object sender, Object target) {
		t.setDescription(
			"Last action clicked was '"
				+ action.getCaption()
				+ "' on item '"
				+ t.getItem(target).getProperty(
					FilesystemContainer.PROPERTY_NAME)
				+ "'");

	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */