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

import org.millstone.base.data.Item;
import org.millstone.base.data.util.*;
import org.millstone.base.ui.*;
import org.millstone.base.event.Action;

public class FeatureTable extends Feature implements Action.Handler {

	private Table t;

	private boolean actionsActive = false;
	private Button actionHandlerSwitch =
		new Button("Activate actions", this, "toggleActions");
	public FeatureTable() {
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

		IndexedContainer ic = new IndexedContainer();

		String[] firstnames =
			new String[] {
				"John",
				"Mary",
				"Joe",
				"Sarah",
				"Jeff",
				"Jane",
				"Peter",
				"Marc",
				"Josie",
				"Linus" };
		String[] lastnames =
			new String[] {
				"Torvalds",
				"Smith",
				"Jones",
				"Beck",
				"Sheridan",
				"Picard",
				"Hill",
				"Fielding",
				"Einstein" };
		String[] eyecolors = new String[] { "Blue", "Green", "Brown" };
		String[] haircolors =
			new String[] { "Brown", "Black", "Red", "Blonde" };

		ic.addContainerProperty("Firstname", String.class, "");
		ic.addContainerProperty("Lastname", String.class, "");
		ic.addContainerProperty("Age", String.class, "");
		ic.addContainerProperty("Eyecolor", String.class, "");
		ic.addContainerProperty("Haircolor", String.class, "");

		for (int j = 0; j < 50; j++) {
			Item i = ic.getItem(ic.addItem());
			i.getItemProperty("Firstname").setValue(
				firstnames[(int) (Math.random() * 9)]);
			i.getItemProperty("Lastname").setValue(
				lastnames[(int) (Math.random() * 9)]);
			i.getItemProperty("Age").setValue(
				new Integer((int) (Math.random() * 80)));
			i.getItemProperty("Eyecolor").setValue(
				eyecolors[(int) (Math.random() * 3)]);
			i.getItemProperty("Haircolor").setValue(
				haircolors[(int) (Math.random() * 4)]);
		}
		t = new Table("Table component", ic);
		t.setPageLength(10);
		l.addComponent(t);

		// Configuration
		Hashtable alternateEditors = new Hashtable();

		Select s =
			createSelect(
				"Column Header Mode",
				new Integer[] {
					new Integer(Table.COLUMN_HEADER_MODE_EXPLICIT),
					new Integer(Table.COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID),
					new Integer(Table.COLUMN_HEADER_MODE_HIDDEN),
					new Integer(Table.COLUMN_HEADER_MODE_ID)},
				new String[] {
					"Explicit",
					"Explicit defaults ID",
					"Hidden",
					"ID" });
		alternateEditors.put("columnHeaderMode", s);

		Select ts =
			createSelect(
				"Row Header Mode",
				new Integer[] {
					new Integer(Table.ROW_HEADER_MODE_EXPLICIT),
					new Integer(Table.ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID),
					new Integer(Table.ROW_HEADER_MODE_HIDDEN),
					new Integer(Table.ROW_HEADER_MODE_ICON_ONLY),
					new Integer(Table.ROW_HEADER_MODE_ID),
					new Integer(Table.ROW_HEADER_MODE_INDEX),
					new Integer(Table.ROW_HEADER_MODE_ITEM),
					new Integer(Table.ROW_HEADER_MODE_PROPERTY)},
				new String[] {
					"Explicit",
					"Explicit defaults ID",
					"Hidden",
					"Icon only",
					"ID",
					"Index",
					"Item",
					"Property" });
		alternateEditors.put("rowHeaderMode", ts);

		Select u =
			createSelect(
				"Style",
				new String[] { "", "list" },
				new String[] { "Default", "List" });

		alternateEditors.put("style", u);

		l.addComponent(
			createPropertyPanel(
				t,
				new String[] {
					"multiSelect",
					"pageLength",
					"selectable",
					"columnHeaderMode",
					"rowHeaderMode" },
				alternateEditors));

		l.addComponent(this.actionHandlerSwitch);
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
		return new String[] {
			"Table",
			"The Table feature caters for displaying large volumes of tabular data, "
				+ "in multiple pages where needed.<br/><br/> "
				+ "Selection of the displayed data is supported both in selecting exclusively one row "
				+ "or multiple rows at the same time. For each row, there may be a set of actions associated, "
				+ "depending on the skin implementation these actions may be displayed either as a drop-down "
				+ "menu for each row or a set of command buttons. <br/><br/>"
				+ "As with all Millstone data-components , so also the Table may be bound to an underlying "
				+ "datasource, such as for instance a database table.<br/><br/>"
				+ "On the demo tab you can try out how the different properties "
				+ "affect the presentation of the component.",
			"table.jpg" };
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
				+ t.getItem(target).getItemProperty("Column 1")
				+ "'");
	}

}
