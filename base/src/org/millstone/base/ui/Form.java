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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.millstone.base.data.Item;
import org.millstone.base.data.Buffered;
import org.millstone.base.data.Property;
import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.PaintTarget;

/** Form component provides easy way of creating and managing sets fields.
 * 
 * <p>Form is a container for fields extending {@link AbstractField} class.
 * It provides support for any layouts and provides buffering interface for
 * easy connection of commit- and discard buttons. All the form
 * fields can be customized by adding validators, setting captions and icons, 
 * setting immediateness, etc. Also direct mechanism for replacing existing
 * fields with selections is given.</p>
 * 
 * <p>Form provides customizable editor for classes implementing
 * {@link org.millstone.base.data.Item} interface. Also the form itself
 * implements this interface for easier connectivity to other items.
 * To use the form as editor for an item, just connect the item to
 * form with {@link Form#setItemDataSource(Item)}. If only a part of the
 * item needs to be edited, {@link Form#setItemDataSource(Item,Collection)}
 * can be used instead. After the item has been connected to the form,
 * the automatically created fields can be customized and new fields can
 * be added. If you need to connect a class that does not implement
 * {@link org.millstone.base.data.Item} interface, most properties of any
 * class following bean pattern, can be accessed trough 
 * {@link org.millstone.base.data.util.BeanItem}.</p>
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */public class Form
	extends AbstractComponent
	implements Item.Editor, Buffered, Item {

	/** Layout of the form */
	private Layout layout;

	/** Item connected to this form as datasource */
	private Item datasource;

	/** Ordered list of property ids in this editor */
	private LinkedList propertyIds = new LinkedList();

	/** Current buffered source exception */
	private Buffered.SourceException currentBufferedSourceException = null;

	/** Is the form in write trough mode */
	private boolean writeThrough = true;

	/** Is the form in read trough mode */
	private boolean readThrough = true;

	/** Mapping from propertyName to corresponding field */
	private HashMap fields = new HashMap();

	/** Contruct a new form with default layout.
	 * 
	 * <p>By default the form uses <code>OrderedLayout</code>
	 * with <code>form</code>-style.
	 * 
	 * @param formLayout The layout of the form.
	 */
	public Form() {
		this(new OrderedLayout());
		getLayout().setStyle("form");
	}

	/** Contruct a new form with given layout.
	 * 
	 * @param formLayout The layout of the form.
	 */
	public Form(Layout formLayout) {
		super();
		layout = formLayout;
		layout.setParent(this);
	}

	/* Documented in interface */
	public String getTag() {
		return "component";
	}

	/* Documented in interface */
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		layout.paint(target);

	}

	/* Commit changes to the data source
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void commit() throws Buffered.SourceException {

		LinkedList problems = null;

		// Try to commit all
		for (Iterator i = propertyIds.iterator(); i.hasNext();)
			try {
				((AbstractField) fields.get(i.next())).commit();
			} catch (Buffered.SourceException e) {
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
		for (Iterator i = propertyIds.iterator(); i.hasNext();)
			try {
				((AbstractField) fields.get(i.next())).discard();
			} catch (Buffered.SourceException e) {
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
		for (Iterator i = propertyIds.iterator(); i.hasNext();)
			if (((AbstractField) fields.get(i.next())).isModified())
				return true;
		return false;
	}

	/* Is the editor in a read-through mode?
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public boolean isReadThrough() {
		return readThrough;
	}

	/* Is the editor in a write-through mode?
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public boolean isWriteThrough() {
		return writeThrough;
	}

	/* Sets the editor's read-through mode to the specified status.
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void setReadThrough(boolean readThrough) {
		if (readThrough != this.readThrough) {
			this.readThrough = readThrough;
			for (Iterator i = propertyIds.iterator(); i.hasNext();)
				((AbstractField) fields.get(i.next())).setReadThrough(
					readThrough);
		}
	}

	/* Sets the editor's read-through mode to the specified status.
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void setWriteThrough(boolean writeThrough) {
		if (writeThrough != this.writeThrough) {
			this.writeThrough = writeThrough;
			for (Iterator i = propertyIds.iterator(); i.hasNext();)
				((AbstractField) fields.get(i.next())).setWriteThrough(
					writeThrough);
		}
	}

	/** Add a new property to form and create corresponding field.
	 * 
	 * @see org.millstone.base.data.Item#addItemProperty(Object, Property)
	 */
	public boolean addItemProperty(Object id, Property property) {

		// Check inputs
		if (id == null || property == null)
			throw new NullPointerException("Id and property must be non-null");

		// Check that the property id is not reserved
		if (propertyIds.contains(id))
			return false;

		// Get suitable field
		AbstractField field = AbstractField.constructField(property.getType());
		if (field == null)
			return false;

		// Configure the field
		try {
			field.setPropertyDataSource(property);
			String caption = id.toString();
			if (caption.length() > 50)
				caption = caption.substring(0, 47) + "...";
			if (caption.length() > 0)
				caption =
					""
						+ Character.toUpperCase(caption.charAt(0))
						+ caption.substring(1, caption.length());
			field.setCaption(caption);
		} catch (Throwable ignored) {
			return false;
		}

		addField(id, field);

		return true;
	}

	/** Add abstract field to form. 
	 * 
	 * <p>The property id must not be already used in the form.  
	 * </p>
	 * 
	 * <p>This field is added to the form layout in the default position
	 * (the position used by {@link Layout#addComponent(Component)} method.
	 * In the special case that the underlying layout is a custom layout,
	 * string representation of the property id is used instead of the
	 * default location.</p>
	 * 
	 * @param propertyId Property id the the field.
	 * @param field New field added to the form.
	 */
	public void addField(Object propertyId, AbstractField field) {

		this.dependsOn(field);
		field.dependsOn(this);
		fields.put(propertyId, field);
		propertyIds.addLast(propertyId);
		field.setReadThrough(readThrough);
		field.setWriteThrough(writeThrough);

		if (layout instanceof CustomLayout)
			((CustomLayout) layout).addComponent(field, propertyId.toString());
		else
			layout.addComponent(field);

		requestRepaint();
	}

	/** The property identified by the property id.
	 * 
	 * <p>The property data source of the field specified with
	 * property id is returned. If there is a (with specified property id) 
	 * having no data source,
	 * the field is returned instead of the data source.</p>
	 * 
	 * @see org.millstone.base.data.Item#getItemProperty(Object)
	 */
	public Property getItemProperty(Object id) {
		AbstractField field = (AbstractField) fields.get(id);
		if (field == null)
			return null;
		Property property = field.getPropertyDataSource();

		if (property != null)
			return property;
		else
			return field;
	}

	/** Get the field identified by the propertyid */
	public AbstractField getField(Object propertyId) {
		return (AbstractField) fields.get(propertyId);
	}

	/* Documented in interface */
	public Collection getItemPropertyIds() {
		return Collections.unmodifiableCollection(propertyIds);
	}

	/** Removes the property and corresponding field from the form.
	 * 
	 * @see org.millstone.base.data.Item#removeItemProperty(Object)
	 */
	public boolean removeItemProperty(Object id) {

		AbstractField field = (AbstractField) fields.get(id);

		if (field != null) {
			propertyIds.remove(id);
			fields.remove(id);
			this.removeDirectDependency(field);
			field.removeDirectDependency(this);
			layout.removeComponent(field);
			return true;
		}

		return false;
	}

	/** Removes all properties and fields from the form.
	 * 
	 * @return Success of the operation. Removal of all fields succeeded 
	 * if (and only if) the return value is true.
	 */
	public boolean removeAllProperties() {
		Object[] properties = propertyIds.toArray();
		boolean success = true;

		for (int i = 0; i < properties.length; i++)
			if (!removeItemProperty(properties[i]))
				success = false;

		return success;
	}

	/* Documented in the interface */
	public Item getItemDataSource() {
		return datasource;
	}

	/** Set the item datasource for the form.
	 * 
	 * <p>Setting item datasource clears any fields, the form might contain
	 * and adds all the properties as fields to the form.</p>
	 * 
	 * @see org.millstone.base.data.Item.Viewer#setItemDataSource(Item)
	 */
	public void setItemDataSource(Item newDataSource) {
		setItemDataSource(
			newDataSource,
			newDataSource != null ? newDataSource.getItemPropertyIds() : null);
	}

	/** Set the item datasource for the form, but limit the form contents
	 * to specified properties of the item.
	 * 
	 * <p>Setting item datasource clears any fields, the form might contain
	 * and adds the specified the properties as fields to the form, in the
	 * specified order.</p>
	 * 
	 * @see org.millstone.base.data.Item.Viewer#setItemDataSource(Item)
	 */
	public void setItemDataSource(Item newDataSource, Collection propertyIds) {

		// Remove all fields first from the form
		removeAllProperties();

		// Set the datasource
		datasource = newDataSource;

		//If the new datasource is null, just set null datasource
		if (datasource == null)
			return;

		// Add all the properties to this form
		for (Iterator i = propertyIds.iterator(); i.hasNext();) {
			Object id = i.next();
			Property property = newDataSource.getItemProperty(id);
			if (id != null && property != null)
				addItemProperty(id, property);
		}
	}

	/** Get the layout of the form. 
	 * 
	 * <p>By default form uses <code>OrderedLayout</code> with <code>form</code>-style.</p>
	 * 
	 * @return Layout of the form.
	 */
	public Layout getLayout() {
		return layout;
	}

	/** Set a form field to be selectable from static list of changes.
	 * 
	 * <p>The list values and descriptions are given as array. The value-array must contain the 
	 * current value of the field and the lengths of the arrays must match. Null values are not
	 * supported.</p>
	 * 
	 * @return The select property generated
	 */
	public Select replaceWithSelect(
		Object propertyId,
		Object[] values,
		Object[] descriptions) {

		// Check the parameters
		if (propertyId == null || values == null || descriptions == null)
			throw new NullPointerException("All parameters must be non-null");
		if (values.length != descriptions.length)
			throw new IllegalArgumentException("Value and description list are of different size");

		// Get the old field
		AbstractField oldField = (AbstractField) fields.get(propertyId);
		if (oldField == null)
			throw new IllegalArgumentException(
				"Field with given propertyid '"
					+ propertyId.toString()
					+ "' can not be found.");
		Object value = oldField.getValue();

		// Check that the value exists
		boolean found = false;
		for (int i = 0; i < values.length && !found; i++)
			if (values[i] == value
				|| (value != null && value.equals(values[i])))
				found = true;
		if (value != null && !found)
			throw new IllegalArgumentException(
				"Current value '"
					+ value
					+ "' of property '"
					+ propertyId.toString()
					+ "' was not found");

		// Create new field matching to old field parameters
		Select newField = new Select();
		newField.setCaption(oldField.getCaption());
		newField.setReadOnly(oldField.isReadOnly());
		newField.setReadThrough(oldField.isReadThrough());
		newField.setWriteThrough(oldField.isWriteThrough());

		// Create options list
		newField.addContainerProperty("desc", String.class, "");
		newField.setItemCaptionPropertyId("desc");
		for (int i = 0; i < values.length; i++) {
			Object id = values[i];
			if (id == null) {
				id = new Object();
				newField.setNullSelectionItemId(id);
			}
			Item item = newField.addItem(id);
			if (item != null)
				item.getItemProperty("desc").setValue(descriptions[i].toString());
		}

		// Set the property data source
		Property property = oldField.getPropertyDataSource();
		oldField.setPropertyDataSource(null);
		newField.setPropertyDataSource(property);

		// Replace the old field with new one
		layout.replaceComponent(oldField, newField);
		fields.put(propertyId, newField);
		this.removeDirectDependency(oldField);
		oldField.removeDirectDependency(this);
		this.dependsOn(newField);
		newField.dependsOn(this);

		return newField;
	}
	
	/**
	 * @see org.millstone.base.ui.Component#attach()
	 */
	public void attach() {
		super.attach();
		layout.attach();
	}

	/**
	 * @see org.millstone.base.ui.Component#detach()
	 */
	public void detach() {
		super.detach();
		layout.detach();
	}

}
