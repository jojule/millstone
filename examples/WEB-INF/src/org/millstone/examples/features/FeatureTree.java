package org.millstone.examples.features;

import org.millstone.base.data.util.BeanItem;
import org.millstone.base.data.util.FilesystemContainer;
import org.millstone.base.ui.*;
import org.millstone.base.event.Action;
import java.io.File;

public class FeatureTree extends Feature implements Action.Handler {

	private Tree t;

	public FeatureTree() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		File root = null;

		// Use root given as parameter
		if ((this.getApplication() != null)
			&& (this.getApplication().getProperty("exampleroot") != null)) {
			root = new File(this.getApplication().getProperty("exampleroot"));
		}

		FilesystemContainer fs = new FilesystemContainer(root, false);
		Panel show = new Panel("Tree component");
		t = new Tree("Caption", fs);
		if (root == null) {
			t.setDescription(
				"The tree is empty, because \"exampleroot\" parameter is not specified."
					+ " Please declare this at server.xml or in web.xml.");
		}

		// Handle actions for the tree
		t.addActionHandler(this);

		show.addComponent(t);
		l.addComponent(show);

		// Configuration
		ItemEditor cpp =
			(ItemEditor) createPropertyPanel(t,
				new String[] {
					"enabled",
					"visible",
					"readOnly",
					"immediate",
					"selectable",
					"multiSelect",
					"writeThrough",
					"readThrough",
					"caption",
					"style",
					"description" });

		Select t =
			createSelect(
				"Style",
				new String[] { "default", "menu","dropmenu" },
				new String[] { "Default", "Menu","Dropmenu" });
		t.setNewItemsAllowed(true);
		cpp.setPropertyEditor("style", t);
		l.addComponent(cpp);

		return l;
	}

	protected String getExampleSrc() {
		return "Tree t = new Tree(\"Caption\",datasource);\n";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[] {
			"Tree",
			"This is the Tree component.<br/>"
				+ "It is used to display hierarchical data, such as for instance a menu or filesystem.<br/>"
				+ "<br/>On the demo tab you can try out how the different properties "
				+ "affect the presentation of the component.",
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