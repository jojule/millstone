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

package org.millstone.base.ui;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.millstone.base.data.Item;
import org.millstone.base.data.util.PropertysetItem;
import org.millstone.base.data.Buffered;
import org.millstone.base.data.Property;
import org.millstone.base.terminal.*;
import org.millstone.base.terminal.PaintTarget;
import org.millstone.base.terminal.PaintException;
import org.millstone.base.ui.Button.ClickEvent;

/** <p>A simple property editor for any bindable object.</p>
 *
 * <p>Editors of this class are always in
 * {@link org.millstone.base.data.Buffered buffered} mode, so activating
 * the {@link #commit() commit} method (usually through pressing the 
 * save button) is neccessary save the changes made with the editor.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class ItemEditor
	extends AbstractComponent
	implements Item.Editor, Buffered, Item, Button.ClickListener {

	/** Edited item */
	private Item item;

	/** Panel this item editor resides in */
	private Panel panel;

	/** Commit button or null if not shown */
	private Button commitButton = null;

	/** Discard button or null if not shown */
	private Button discardButton = null;

	/** Layout of the fields */
	private Layout fieldLayout = new OrderedLayout();

	/** Mapping from propertyIds to corresponding property editors */
	private Hashtable fields = new Hashtable();

	/** Mapping from propertyIds to corresponding properties */
	private Hashtable properties = new Hashtable();

	/** Ordered list of property ids in this editor */
	private LinkedList propertyIds = new LinkedList();

	/** Current buffered source exception */
	private Buffered.SourceException currentBufferedSourceException = null;

	/** Is the item editor in write trough mode */
	private boolean writeTrough = false;

	/** Is the item editor in read trough mode */
	private boolean readTrough = true;

	/** Buttons panel */
	private OrderedLayout buttons =
		new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);

	/** Create an empty item editor that can be used to edit properties that
	 * are added later.
	 */
	public ItemEditor() {
		this(null, null, null, new PropertysetItem());
	}

	/** Create an empty item editor that can be used to edit properties that
	 * are added later.
	 * 
	 * @param caption Editor title
	 */
	public ItemEditor(String caption) {
		this(caption, null, null, new PropertysetItem());
	}

	/** Create an empty item editor that can be used to edit properties that
	 * are added later.
	 * 
	 * @param caption Editor title
	 * @param commitButtonCaption the string that's displayed on the button
	 * that will save the local changes to the data source
	 * @param discardButtonCaption the string that's displayed on the button
	 * that will close the editor without saving the local changes to the
	 * data source
	 */
	public ItemEditor(
		String caption,
		String commitButtonCaption,
		String discardButtonCaption) {
		this(
			caption,
			commitButtonCaption,
			discardButtonCaption,
			new PropertysetItem());
	}

	/** Create a new item editor and attach it to the given data source. The
	 * constructor adds all properties in <code>dataSource</code> to the
	 * local set of edited properties. By activating the <code>commit</code>
	 * method the user can copy all changes to the local properties to the
	 * ones in the <code>dataSource</code>.
	 * 
	 * @param caption Editor title
	 * @param commitButtonCaption the string that's displayed on the button
	 * that will save the local changes to the data source
	 * @param discardButtonCaption the string that's displayed on the button
	 * that will close the editor without saving the local changes to the
	 * data source
	 */
	public ItemEditor(
		String caption,
		String commitButtonCaption,
		String discardButtonCaption,
		Item dataSource) {

		// Create layout
		panel = new Panel(caption);
		panel.addComponent(fieldLayout);
		panel.addComponent(buttons);

		// Create the buttons
		setCommitButtonCaption(commitButtonCaption);
		setDiscardButtonCaption(discardButtonCaption);

		// Set the datasource and add fields to layout
		setItemDataSource(dataSource);
	}

	/** Adds a new property to the editor.
	 * 
	 * @param id Id of the property to add
	 * @param property the property to be added and associated with id.
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean addProperty(Object id, Property property) {

		// Nulls are not allowed
		if (id == null || property == null)
			return false;

		// Add property to editor
		properties.put(id, property);
		propertyIds.add(id);

		// Add property field
		addField(id, null);

		return true;
	}

	/* Commit changes to the data source
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void commit() throws Buffered.SourceException {

		LinkedList problems = null;

		// Try to commit all
		for (Iterator i = fieldLayout.getComponentIterator(); i.hasNext();)
			try {
				try {
					((Buffered) i.next()).commit();
				} catch (ClassCastException ignored) {
				}
			} catch (Throwable e) {
				if (problems == null)
					problems = new LinkedList();
				problems.add(e);
			}

		// No problems occurred
		if (problems == null) {
			if (currentBufferedSourceException != null) {
				currentBufferedSourceException = null;
				requestRepaint();
			}
			return;
		}

		// Commit problems
		Throwable[] causes = new Throwable[problems.size()];
		int index = 0;
		for (Iterator i = problems.iterator(); i.hasNext();)
			causes[index++] = (Throwable) i.next();
		Buffered.SourceException e = new Buffered.SourceException(this, causes);
		currentBufferedSourceException = e;
		requestRepaint();
		throw e;
	}

	/* Discard local changes and refresh values from the data source
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void discard() throws Buffered.SourceException {

		LinkedList problems = null;

		// Try to discard all changes
		for (Iterator i = fieldLayout.getComponentIterator(); i.hasNext();)
			try {
				try {
					((Buffered) i.next()).discard();
				} catch (ClassCastException ignored) {
				}
			} catch (Throwable e) {
				if (problems == null)
					problems = new LinkedList();
				problems.add(e);
			}

		// No problems occurred
		if (problems == null) {
			if (currentBufferedSourceException != null) {
				currentBufferedSourceException = null;
				requestRepaint();
			}
			return;
		}

		// Discard problems occurred		
		Throwable[] causes = new Throwable[problems.size()];
		int index = 0;
		for (Iterator i = problems.iterator(); i.hasNext();)
			causes[index++] = (Throwable) i.next();
		Buffered.SourceException e = new Buffered.SourceException(this, causes);
		currentBufferedSourceException = e;
		requestRepaint();
		throw e;
	}

	/* Is the object modified but not committed?
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public boolean isModified() {
		for (Iterator i = fieldLayout.getComponentIterator(); i.hasNext();)
			try {
				if (((Buffered) i.next()).isModified())
					return true;
			} catch (ClassCastException ignored) {
			}
		return false;
	}

	/* Is the editor in a read-through mode?
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public boolean isReadThrough() {
		return readTrough;
	}

	/* Is the editor in a write-through mode?
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public boolean isWriteThrough() {
		return writeTrough;
	}

	/* Paint the contents of the component.
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		panel.paint(target);
	}

	/** Removes the property identified by <code>propertyId</code> from the
	 * editor. The property is not removed from the underlying data source,
	 * only from the editor.
	 *
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean removeProperty(Object propertyId) {

		// Hide the field
		removeField(propertyId);

		// Remove the property from the editor
		if (properties.containsKey(propertyId)) {
			properties.remove(propertyId);
			propertyIds.remove(propertyId);
			return true;
		}

		return false;
	}

	/* Sets the editor's read-through mode to the specified status.
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void setReadThrough(boolean readThrough) {
		if (readThrough != this.readTrough) {
			this.readTrough = readTrough;
			for (Iterator i = fields.values().iterator(); i.hasNext();)
				try {
					((Buffered) i.next()).setReadThrough(readThrough);
				} catch (ClassCastException ignored) {
				}
		}
	}

	/** Set the write trough mode of the item editor. Setting item editor to 
	 * write trough mode automaticly hides buttons. Disabling write trough mode
	 * set the hidden buttons visible.
	 */
	public void setWriteThrough(boolean writeThrough) {
		if (writeThrough != this.writeTrough) {
			this.writeTrough = writeTrough;
			for (Iterator i = fields.values().iterator(); i.hasNext();)
				try {
					((Buffered) i.next()).setWriteThrough(writeThrough);
				} catch (ClassCastException ignored) {
				}
			if (writeThrough) {
				Button cb = getCommitButton();
				if (cb != null)
					cb.setVisible(false);
				Button db = getDiscardButton();
				if (db != null)
					db.setVisible(false);
			} else {
				Button cb = getCommitButton();
				if (cb != null)
					cb.setVisible(true);
				Button db = getDiscardButton();
				if (db != null)
					db.setVisible(true);
			}
		}
	}

	/** <p>Sets the data source for the editor. This means that the changes
	 * made through the editor apply for this Item.</p>
	 * 
	 * <p>If the editor already contains some properties they are removed
	 * from the container before the ones from the new data source are
	 * added. All changes to the old properties since the last commit are
	 * lost.</p>
	 * 
	 * @param newDataSource the new data source Item
	 */
	public void setItemDataSource(Item newDataSource) {

		// Remove all old properties
		Object[] ids = propertyIds.toArray();
		for (int i = 0; i < ids.length; i++)
			removeProperty(ids[i]);

		if (newDataSource == null)
			this.item = new PropertysetItem();
		else {
			// Add all new properties
			this.item = newDataSource;
			for (Iterator i = item.getPropertyIds().iterator(); i.hasNext();) {
				Object id = i.next();
				addProperty(id, item.getProperty(id));
			}
		}
	}

	/** Gets the data source of the editor.
	 * 
	 * @return The editor's data source Item
	 */
	public Item getItemDataSource() {
		return item;
	}

	/** Sets the component's icon which is shown with caption.
	 * 
	 * @param icon the new icon
	 */
	public void setIcon(Resource icon) {
		panel.setIcon(icon);
	}

	/** Gets the component's icon.
	 * 
	 * @return component's current icon.
	 */
	public Resource getIcon() {
		return panel.getIcon();
	}

	/** Gets the component's caption. Caption is the visible name of the
	 * component.
	 * 
	 * @return the caption as a <code>String</code>.
	 */
	public String getCaption() {
		return panel.getCaption();
	}

	/** Sets the component's caption.
	 * 
	 * @param caption the new caption string
	 */
	public void setCaption(String caption) {
		panel.setCaption(caption);
	}

	/** Gets the current layout of the fields in the editor.
	 * 
	 * @return Current layout.
	 */
	public Layout getLayout() {
		return fieldLayout;
	}

	/** Sets the layout of the fields. All fields in the editor are moved
	 * to new layout.
	 *
	 * @param layout New layout of the item editor.
	 */
	public void setLayout(Layout layout) {
		if (layout != null) {
			layout.moveComponentsFrom(fieldLayout);
			panel.removeComponent(fieldLayout);
			fieldLayout = layout;
			((OrderedLayout) panel.getLayout()).addComponentAsFirst(
				fieldLayout);
		}
	}

	/** Add a field to editor's layout. The fields are given as Properties.
	 * 
	 * The editor must implement Component interface.
	 * 
	 * @param propertyId Id of the new field
	 * @param editor Property editor that will be associated with the given property
	 * @see org.millstone.base.data.Property
	 */
	private void addField(Object propertyId, Property.Editor editor) {

		// Check that the editor is a Component
		if (editor != null && !(editor instanceof Component))
			throw new IllegalArgumentException("Editor must be a component");

		// If the editor is unknown, try to use suitable one by default
		if (editor == null) {

			// Guess the initial caption to be the property id
			String caption = propertyId.toString();

			// Create a field of appropriate type
			Property p = (Property) properties.get(propertyId);
			if (Date.class.isAssignableFrom(p.getType()))
				editor = new DateField(caption, p);
			else if (Boolean.class.isAssignableFrom(p.getType()))
				editor = new Button(caption, p);
			else
				editor = new TextField(caption, p);
		}

		// Put the field into buffered mode
		try {
			((Buffered) editor).setWriteThrough(false);
		} catch (ClassCastException ignored) {
		} catch (Buffered.SourceException e) {
			throw new RuntimeException(
				"Internal error - please report: " + e.toString());
		}

		// Add the field to layout
		fieldLayout.addComponent((Component) editor);

		// Add button dependencies
		if (commitButton != null)
			commitButton.dependsOn((Component) editor);
		if (discardButton != null)
			discardButton.dependsOn((Component) editor);

		// Add component dependency
		this.dependsOn((Component) editor);

		// If the item editor is in read-only mode, so should the field be
		 ((Component) editor).setReadOnly(isReadOnly());

		// Set the buffering mode
		try {
			((Buffered) editor).setReadThrough(isReadThrough());
			((Buffered) editor).setWriteThrough(isWriteThrough());
		} catch (ClassCastException ignored) {
		}

		// Add the field to a id-field map
		fields.put(propertyId, editor);
	}

	/** Removes a field from the editor's current layout.
	 * 
	 * @param propertyId Property ID of the field to remove
	 */
	private void removeField(Object propertyId) {
		if (fields.containsKey(propertyId)) {

			// Get the component
			Component c = (Component) fields.get(propertyId);

			// Remove from layout
			fieldLayout.removeComponent(c);

			// Remove button dependencies
			if (commitButton != null)
				commitButton.removeDirectDependency(c);
			if (discardButton != null)
				discardButton.removeDirectDependency(c);

			// Remove component dependency
			this.removeDirectDependency(c);

			// Remove from id-map
			fields.remove(propertyId);
		}
	}

	/** Sets the components read-only status to the specified value. Setting
	 * an editor to the read-only mode hides the buttons and sets all fields
	 * to read-only mode.
	 * 
	 * @param readOnly boolean value specifying if the editor should be in a
	 * read-only mode after the call or not
	 */
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);

		// Update the read-only status of the fields
		for (Iterator i = fieldLayout.getComponentIterator(); i.hasNext();)
			 ((Component) i.next()).setReadOnly(isReadOnly());

		// Show / Hide buttons
		if (commitButton != null)
			commitButton.setVisible(!isReadOnly());
		if (discardButton != null)
			discardButton.setVisible(!isReadOnly());
	}

	/* Gets the IDs of properties contained in the editor
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public Collection getPropertyIds() {
		return Collections.unmodifiableCollection(propertyIds);
	}

	/* Gets the property corresponding to the given Property ID from the
	 * editor
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public Property getProperty(Object id) {
		return (Property) properties.get(id);
	}

	/** Handle commit and discard button clicks.
	 */
	public void buttonClick(ClickEvent event) {

		// Commit / Discard
		try {
			if (event.getSource() == commitButton)
				commit();
			else if (event.getSource() == discardButton)
				discard();
		} catch (Buffered.SourceException ignored) {
			// Already handled in commit / discard
		}
	}

	/** Returns the commitButton.
	 * @return Button
	 */
	public Button getCommitButton() {
		return commitButton;
	}

	/** Returns the discardButton.
	 * @return Button
	 */
	public Button getDiscardButton() {
		return discardButton;
	}

	/** Returns collection of all the property editors.
	 * @return Unmodifiable collection of all property editors.
	 */
	public Collection getFields() {
		return Collections.unmodifiableCollection(fields.values());
	}

	/** Get the property editor for given property.
	 * @return Property editor for given property.
	 */
	public Property.Editor getPropertyEditor(Object propertyId) {
		return (Property.Editor) fields.get(propertyId);
	}

	/** Set the property editor for given property.
	 * The given property must exist in the item editor. The new item editor replaces the old one.
	 * @param propertyEditor The new property editor that will be used to edit the 
	 * given property. The property editor is automatically connected to the given property.
	 * @param propertyId Id the property to be edited.
	 */
	public void setPropertyEditor(Object propertyId, Property.Editor editor) {
		if (fields.containsKey(propertyId))
			removeField(propertyId);
		if (properties.containsKey(propertyId)) {
			addField(propertyId, editor);
			editor.setPropertyDataSource((Property) properties.get(propertyId));
		}
	}

	/** Sets the caption for a property field in the editor's layout.
	 * 
	 * @param propertyId Property ID of the field whose caption is to be set
	 * @param caption the new caption of the field as a <code>String</code>
	 */
	public void setCaption(Object propertyId, String caption) {
		try {
			AbstractComponent c = (AbstractComponent) fields.get(propertyId);
			if (c != null)
				c.setCaption(caption);
		} catch (ClassCastException ignored) {
		}
	}

	/** Sets the discard button caption. Setting caption null removes the button.
	 * @param caption New button caption.
	 */
	public void setDiscardButtonCaption(String caption) {
		if (discardButton != null)
			discardButton.setCaption(caption);
		else if (caption != null) {
			discardButton = new Button(caption, this);
			buttons.addComponent(discardButton);
		} else if (caption == null && discardButton != null) {
			discardButton.removeListener(this);
			buttons.removeComponent(discardButton);
			discardButton = null;
		}
	}

	/** Sets the commit button caption. Setting caption null removes the button.
	 * If the commit button does not exist, the item editor is set in write trough
	 * mode. When the commit button is added, the write trough mode is disabled.
	 * @param caption New button caption.
	 */
	public void setCommitButtonCaption(String caption) {
		if (commitButton != null) {
			commitButton.setCaption(caption);
			setWriteThrough(false);
		} else if (caption != null) {
			commitButton = new Button(caption, this);
			buttons.addComponent(commitButton);
			setWriteThrough(false);
		} else if (caption == null && commitButton != null) {
			commitButton.removeListener(this);
			buttons.removeComponent(commitButton);
			commitButton = null;
			setWriteThrough(true);
		}
	}

	/** The error message in the item editor is combination of
	 * errors got from parent and the current buffered source 
	 * error.
	 * @see org.millstone.base.ui.AbstractComponent#getErrorMessage()
	 */
	public ErrorMessage getErrorMessage() {
		ErrorMessage superError = super.getErrorMessage();
		if (superError == null && currentBufferedSourceException == null)
			return null;
		return new CompositeErrorMessage(
			new ErrorMessage[] { superError, currentBufferedSourceException });
	}

	/**
	 * @see org.millstone.base.ui.AbstractComponent#getTag()
	 */
	public String getTag() {
		return "component";
	}
}
