package org.millstone.examples.features;

import org.millstone.base.data.Item;
import org.millstone.base.data.util.*;
import org.millstone.base.ui.*;
import org.millstone.base.event.Action;

public class FeatureTable extends Feature implements Action.Handler {

	private Table t;

	public FeatureTable() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		IndexedContainer ic = new IndexedContainer();

		ic.addProperty("Column 1", String.class, "");
		ic.addProperty("Column 2", String.class, "");
		ic.addProperty("Column 3", String.class, "");
		ic.addProperty("Column 4", String.class, "");
		ic.addProperty("Column 5", String.class, "");

		for (int j = 0; j < 50; j++) {
			Item i = ic.getItem(ic.addItem());
			i.getProperty("Column 1").setValue("[Row: " + j + " Col: 1]");
			i.getProperty("Column 2").setValue("[Row: " + j + " Col: 2]");
			i.getProperty("Column 3").setValue("[Row: " + j + " Col: 3]");
			i.getProperty("Column 4").setValue("[Row: " + j + " Col: 4]");
			i.getProperty("Column 5").setValue("[Row: " + j + " Col: 5]");
		}
		t = new Table("Table component", ic);
		t.setPageLength(10);
		l.addComponent(t);

		// Handle the table actions
		t.addActionHandler(this);

		// Configuration
		ItemEditor cpp = 
			(ItemEditor)createPropertyPanel(
				t,
				new String[] {
					"enabled",
					"visible",
					"multiSelect",
					"pageLength",
					"immediate",
					"selectable",
					"columnHeaderMode",
					"rowHeaderMode",
					"caption",
					"style",
					"description" });
		cpp.setLayout(new OrderedLayout());
		Select s = createSelect("Column Header Mode", 
			new Integer[]{new Integer(Table.COLUMN_HEADER_MODE_EXPLICIT),
						  new Integer(Table.COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID),
						  new Integer(Table.COLUMN_HEADER_MODE_HIDDEN),
						  new Integer(Table.COLUMN_HEADER_MODE_ID)},
			new String[]{"Explicit","Explicit defaults ID","Hidden","ID"});
		cpp.setPropertyEditor("columnHeaderMode", s);
		
		Select t = createSelect("Row Header Mode", 
			new Integer[]{new Integer(Table.ROW_HEADER_MODE_EXPLICIT),
						  new Integer(Table.ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID),
						  new Integer(Table.ROW_HEADER_MODE_HIDDEN),
						  new Integer(Table.ROW_HEADER_MODE_ICON_ONLY),
						  new Integer(Table.ROW_HEADER_MODE_ID),
						  new Integer(Table.ROW_HEADER_MODE_INDEX),
						  new Integer(Table.ROW_HEADER_MODE_ITEM),
						  new Integer(Table.ROW_HEADER_MODE_PROPERTY)},
			new String[]{"Explicit","Explicit defaults ID","Hidden","Icon only","ID","Index","Item","Property"});
		cpp.setPropertyEditor("rowHeaderMode", t);
					
					
		l.addComponent(cpp);
		return l;
	}

	protected String getExampleSrc() {
		return "IndexedContainer ic = new IndexedContainer();\n"
			+ "ic.addProperty(\"Column 1\", String.class, \"\");\n"
			+ "ic.addProperty(\"Column 2\", String.class, \"\");\n"
			+ "\n"
			+ "for (int j = 0; j < 10; j++) {\n"
			+ "Item i = ic.getItem(ic.addItem());\n"
			+ "i.getProperty(\"Column 1\").setValue(\"Testdata 1\");\n"
			+ "i.getProperty(\"Column 2\").setValue(\"Testdata 2\");\n"
			+ "}\n"
			+ "Table t = new Table(\"Table component\",ic);\n"
			+ "t.setPageLength(10);\n";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String[] getDescriptionXHTML() {
		return new String[]{"Table","This is the Table component."
			+ "It is used for displaying data in rows and columns "
			+ "on multiple pages when neccessary.<br/>"
			+ "It can be bound to an underlying datasource, such as for instance a database table.<br/>"
			+ "<br/>"
			+ "On the demo tab you can try out how the different properties "+
			"affect the presentation of the component.","table.jpg"};
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
				+ t.getItem(target).getProperty("Column 1")
				+ "'");
	}

}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */