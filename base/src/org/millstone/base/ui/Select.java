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

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;

import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.ErrorMessage;
import org.millstone.base.terminal.SystemError;
import org.millstone.base.terminal.PaintTarget;
import org.millstone.base.terminal.KeyMapper;
import org.millstone.base.terminal.Resource;
import org.millstone.base.data.Item;
import org.millstone.base.data.Property;
import org.millstone.base.data.Container;
import org.millstone.base.data.util.IndexedContainer;
import org.millstone.base.terminal.KeyMapper;

/** <p>A class representing a selection of items the user has selected in a
 * UI. The set of choices is presented as a set of
 * {@link org.millstone.base.data.Item}s in a 
 * {@link org.millstone.base.data.Container}.</p>
 * 
 * <p>A <code>Select</code> component may be in single- or multiselect mode.
 * Multiselect mode means that more than one item can be selected
 * simultaneously.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Select
	extends AbstractField
	implements
		Container,
		Container.Viewer,
		Container.PropertySetChangeListener,
		Container.PropertySetChangeNotifier,
		Container.ItemSetChangeNotifier,
		Container.ItemSetChangeListener {

	/* Predefined styles *********************************************** */

	/** Predefined optiongroup style. 
	 * A group of radiobuttons or checkboxes depending on the select mode.
	 */
	public static final String STYLE_OPTIONGROUP = "optiongroup";

	/* Caption modes *************************************************** */

	/** Item caption mode: Item's ID's <code>String</code> representation
	 * is used as caption.
	 */
	public static final int ITEM_CAPTION_MODE_ID = 0;

	/** Item caption mode: Item's <code>String</code> representation is
	 * used as caption.
	 */
	public static final int ITEM_CAPTION_MODE_ITEM = 1;

	/** Item caption mode: Index of the item is used as caption. The
	 * index mode can only be used with the containers implementing the
	 * {@link org.millstone.base.data.Container.Indexed} interface.
	 */
	public static final int ITEM_CAPTION_MODE_INDEX = 2;

	/** Item caption mode: If an Item has a caption it's used, if not,
	 * Item's ID's <code>String</code> representation is used as caption.
	 * <b>This is the default</b>.
	 */
	public static final int ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID = 3;

	/** Item caption mode: Captions must be explicitly specified.
	 */
	public static final int ITEM_CAPTION_MODE_EXPLICIT = 4;

	/** Item caption mode: Only icons are shown, captions are hidden.
	 */
	public static final int ITEM_CAPTION_MODE_ICON_ONLY = 5;

	/** Item caption mode: Item captions are read from property specified 
	 * with <code>setItemCaptionPropertyId</code>.
	 */
	public static final int ITEM_CAPTION_MODE_PROPERTY = 6;

	/** Is the select in multiselect mode? */
	private boolean multiSelect = false;

	/** Select options */
	protected Container items;

	/** Is the user allowed to add new options? */
	private boolean allowNewOptions;

	/** Keymapper used to map key values */
	protected KeyMapper itemIdMapper = new KeyMapper();

	/** Item icons */
	private Hashtable itemIcons = new Hashtable();

	/** Item captions */
	private Hashtable itemCaptions = new Hashtable();

	/** Item caption mode */
	private int itemCaptionMode = ITEM_CAPTION_MODE_ID;

	/** Item caption source property id */
	private Object itemCaptionPropertyId = null;

	/** Item icon source property id */
	private Object itemIconPropertyId = null;
	
	/** List of property set change event listeners */
	private LinkedList propertySetEventListeners = null;

	/** List of item set change event listeners */
	private LinkedList itemSetEventListeners = null;

	/* Constructors ********************************************************* */

	/** Creates an empty Select.
	 * The caption is not used.
	 */
	public Select() {
		setContainerDataSource(new IndexedContainer());
	}

	/** Creates an empty Select with caption.
	 */
	public Select(String caption) {
		setContainerDataSource(new IndexedContainer());
		setCaption(caption);
	}

	/** Creates a new select wthat is connected to a data-source.
	 * @param dataSource Container datasource to be selected from by this select.
	 * @param caption Caption of the component.
	 * @param selected Selected item id or null, if none selected.
	 */
	public Select(String caption, Container dataSource) {
		setCaption(caption);
		setContainerDataSource(dataSource);
	}

	/** Creates a new select that is filled from a collection of option values.
	 * @param caption Caption of this field.
	 * @param options Collection containing the options.
	 * @param selected Selected option or null, if none selected.
	 */
	public Select(String caption, Collection options) {

		// Create options container and add given options to it
		Container c = new IndexedContainer();
		if (options != null)
			for (Iterator i = options.iterator(); i.hasNext();)
				c.addItem(i.next());

		setCaption(caption);
		setContainerDataSource((Container) c);
	}

	/* Component methods **************************************************** */

	/** Paint the content of this component.
	 * @param event PaintEvent.
	 * @throws PaintException The paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		// Paint select attributes
		if (isMultiSelect())
			target.addAttribute("selectmode", "multi");
		if (isNewItemsAllowed())
			target.addAttribute("allownewitem", true);

		// Paint options and create array of selected id keys
		String[] selectedKeys;
		if (isMultiSelect())
			selectedKeys = new String[((Set) getValue()).size()];
		else
			selectedKeys = new String[(getValue() == null ? 0 : 1)];
		int keyIndex = 0;
		target.startTag("options");
		for (Iterator i = getItemIds().iterator(); i.hasNext();) {

			// Get the option attribute values
			Object id = i.next();
			String key = itemIdMapper.key(id);
			String caption = getItemCaption(id);
			Resource icon = getItemIcon(id);

			// Paint option
			target.startTag("so");
			if (icon != null)
				target.addAttribute("icon", icon);
			target.addAttribute("caption", caption);
			target.addAttribute("key", key);
			if (isSelected(id)) {
				target.addAttribute("selected", true);
				selectedKeys[keyIndex++] = key;
			}
			target.endTag("so");
		}
		target.endTag("options");

		// Paint variables
		target.addVariable(this, "selected", selectedKeys);
		if (isNewItemsAllowed())
			target.addVariable(this, "newitem", "");
	}

	/** Invoked when the value of a variable has changed.
	 * @param event Variable change event containing the information about
	 * the changed variable.
	 */
	public void changeVariables(Object source, Map variables) {

		// Try to set the property value
		try {

			// New option entered (and it is allowed)
			String newitem = (String) variables.get("newitem");
			if (newitem != null && newitem.length() > 0) {

				// Check for readonly
				if (isReadOnly())
					throw new Property.ReadOnlyException();

				// Add new option
				if (addItem(newitem) != null) {

					// Select new option
					if (isMultiSelect()) {
						Set s = new HashSet((Set) getValue());
						s.add(newitem);
						setValue(s);
					} else
						setValue(newitem);
				}
			}

			// Selection change (when no new options are sent)
			else if (variables.containsKey("selected")) {
				String[] ka = (String[]) variables.get("selected");

				// Multiselect mode
				if (isMultiSelect()) {

					// Convert the key-array to id-set
					LinkedList s = new LinkedList();
					for (int i = 0; i < ka.length; i++) {
						Object id = itemIdMapper.get(ka[i]);
						if (id != null && containsId(id))
							s.add(id);
					}

					// Limit the deselection to the set of visible items
					// (non-visible items can not be deselected)
					Collection visible = getVisibleItemIds();
					if (visible != null) {
						Set current = (Set) getValue();
						current.removeAll(visible);
						current.addAll(s);
						setValue(s);
					}
				}

				// Single select mode
				else {
					if (ka.length == 0) {

						// Allow deselection only if the deselected item is visible
						Object current = getValue();
						Collection visible = getVisibleItemIds();
						if (visible != null && visible.contains(current))
							setValue(null);
					} else
						setValue(itemIdMapper.get(ka[0]));
				}
			}

		} catch (Throwable e) {
			if (e instanceof ErrorMessage)
				setComponentError((ErrorMessage) e);
			else
				setComponentError(new SystemError(e));
		}
	}

	/** Get component UIDL tag.
	 * @return Component UIDL tag as string.
	 */
	public String getTag() {
		return "select";
	}

	/** Get the visible item ids. In Select, this returns list of all item ids, 
	 * but can be overriden in subclasses if they paint only part of the items 
	 * to the terminal or null if no items is visible.
	 */
	public Collection getVisibleItemIds() {
		if (isVisible())
			return getItemIds();
		return null;
	}

	/* Property methods ***************************************************** */

	/** Return the type of the property.
	 * getValue and setValue functions must be compatible with this type:
	 * one can safely cast getValue() to given type and pass any variable
	 * assignable to this type as a parameter to setValue().
	 * @return type Type of the property.
	 */
	public Class getType() {
		if (isMultiSelect())
			return Set.class;
		else
			return Object.class;
	}

	/** Get the selected item id or in multiselect mode a set of selected ids.
	 */
	public Object getValue() {
		Object retValue = super.getValue();

		if (isMultiSelect()) {

			// If the return value is not a set
			if (retValue == null)
				return new HashSet();
			if (!Set.class.isAssignableFrom(retValue.getClass())) {
				Set s = new HashSet();
				if (items.containsId(retValue))
					s.add(retValue);
				return s;
			}

			return retValue;

		} else {

			// For single selects, chech that the retvalue is in set
			if (retValue == null || !items.containsId(retValue))
				return null;
		}

		return retValue;
	}

	/** Set the visible value of the property.
	 *
	 * @param newValue New value of the property. This should be assignable to
	 * the type returned by getType(), but also String type should be supported.
	 */
	public void setValue(Object newValue)
		throws Property.ReadOnlyException, Property.ConversionException {

		if (isMultiSelect()) {
			if (newValue != null
				&& Set.class.isAssignableFrom(newValue.getClass())) {
				super.setValue(newValue);
			}
		} else if (newValue == null || items.containsId(newValue))
			super.setValue(newValue);
	}

	/* Container methods **************************************************** */

	/** Get the item from the container with given id.
	 * If the container does not contain the requested item, null is returned.
	 */
	public Item getItem(Object itemId) {
		return items.getItem(itemId);
	}

	/** Get item Id collection from the container.
	 * @return Collection of item ids.
	 */
	public Collection getItemIds() {
		return items.getItemIds();
	}

	/** Get property Id collection from the container.
	 * @return Collection of property ids.
	 */
	public Collection getPropertyIds() {
		return items.getPropertyIds();
	}

	/** Get property type.
	 * @param id Id identifying the of the property.
	 */
	public Class getType(Object propertyId) {
		return items.getType(propertyId);
	}

	/** Get the number of items in the container.
	 * @return Number of items in the container.
	 */
	public int size() {
		return items.size();
	}

	/** Test, if the collection contains an item with given id.
	 * @param itemId Id the of item to be tested.
	 */
	public boolean containsId(Object itemId) {
		return items.containsId(itemId);
	}

	/**
	 * @see org.millstone.base.data.Container#getProperty(Object, Object)
	 */
	public Property getProperty(Object itemId, Object propertyId) {
		return items.getProperty(itemId, propertyId);
	}

	/* Container.Managed methods ******************************************** */

	/** Add new property to all items.
	 * Adds a property with given id, type and default value to all items
	 * in the container.
	 *
	 * This functionality is optional. If the function is unsupported, it always
	 * returns false.
	 *
	 * @return True iff the operation succeeded.
	 */
	public boolean addProperty(
		Object propertyId,
		Class type,
		Object defaultValue)
		throws UnsupportedOperationException {

		boolean retval = items.addProperty(propertyId, type, defaultValue);
		if (retval) {
			fireValueChange();
			firePropertySetChange();	
		}
		return retval;
	}

	/** Remove all items from the container.
	 *
	 * This functionality is optional. If the function is unsupported, it always
	 * returns false.
	 *
	 * @return True iff the operation succeeded.
	 */
	public boolean removeAllItems() throws UnsupportedOperationException {

		boolean retval = items.removeAllItems();
		if (retval) {
			setValue(isMultiSelect() ? new HashSet() : null);
			fireValueChange();
			fireItemSetChange();
		}
		return retval;
	}

	/** Create a new item into container with container managed id.
	 * The id of the created new item is returned. The item can be fetched with
	 * getItem() method.
	 * if the creation fails, null is returned.
	 *
	 * @return Id of the created item or null in case of failure.
	 */
	public Object addItem() throws UnsupportedOperationException {

		Object retval = items.addItem();
		if (retval != null)
			fireItemSetChange();
		return retval;
	}

	/** Create a new item into container.
	 * The created new item is returned and ready for setting property values.
	 * if the creation fails, null is returned. In case the container already
	 * contains the item, null is returned.
	 *
	 * This functionality is optional. If the function is unsupported, it always
	 * returns null.
	 *
	 * @param itemId Identification of the item to be created.
	 * @return Created item with the given id, or null in case of failure.
	 */
	public Item addItem(Object itemId) throws UnsupportedOperationException {

		Item retval = items.addItem(itemId);
		if (retval != null)
			fireItemSetChange();
		return retval;
	}

	/** Remove item identified by Id from the container.
	 * This functionality is optional. If the function is not implemented,
	 * the functions allways returns false.
	 *
	 * @return True iff the operation succeeded.
	 */
	public boolean removeItem(Object itemId)
		throws UnsupportedOperationException {

		unselect(itemId);
		boolean retval = items.removeItem(itemId);
		if (retval)
			fireItemSetChange();
		return retval;
	}

	/** Remove property from all items.
	 * Removes a property with given id from all the items in the container.
	 *
	 * This functionality is optional. If the function is unsupported, it always
	 * returns false.
	 *
	 * @return True iff the operation succeeded.
	 */
	public boolean removeProperty(Object propertyId)
		throws UnsupportedOperationException {

		boolean retval = items.removeProperty(propertyId);
		if (retval)
			firePropertySetChange();
		return retval;
	}

	/* Container.Viewer methods ********************************************* */

	/** Set the container as data-source for viewing.  */
	public void setContainerDataSource(Container newDataSource) {
		if (newDataSource == null)
			newDataSource = new IndexedContainer();

		if (items != newDataSource) {

			// Remove listeners from the old datasource
			if (items != null) {
				try {
					((Container.ItemSetChangeNotifier) items).removeListener(
						(Container.ItemSetChangeListener) this);
				} catch (ClassCastException ignored) {
					// Ignored
				}
				try {
					(
						(
							Container
								.PropertySetChangeNotifier) items)
								.removeListener(
						(Container.PropertySetChangeListener) this);
				} catch (ClassCastException ignored) {
					// Ignored
				}
			}

			items = newDataSource;
			
			// Add listeners
			if (items != null) {
				try {
					((Container.ItemSetChangeNotifier) items).addListener(
						(Container.ItemSetChangeListener) this);
				} catch (ClassCastException ignored) {
					// Ignored
				}
				try {
					(
						(
							Container
								.PropertySetChangeNotifier) items)
								.addListener(
						(Container.PropertySetChangeListener) this);
				} catch (ClassCastException ignored) {
					// Ignored
				}
			}
			
			
			fireValueChange();
		}
	}

	/** Get viewing data-source container.  */
	public Container getContainerDataSource() {
		return items;
	}

	/* Select attributes **************************************************** */

	/** Is the select in multiselect mode? In multiselect mode
	 * @return Value of property multiSelect.
	 */
	public boolean isMultiSelect() {
		return this.multiSelect;
	}

	/** Set the multiselect mode.
	 * Setting multiselect mode false may loose selection information: if
	 * selected items set contains one or more selected items, only one of the
	 * selected items is kept as selected.
	 *
	 * @param multiSelect New value of property multiSelect.
	 */
	public void setMultiSelect(boolean multiSelect) {

		if (multiSelect != this.multiSelect) {

			// Selection before mode change		
			Object oldValue = getValue();

			this.multiSelect = multiSelect;

			// Convert the value type
			if (multiSelect) {
				Set s = new HashSet();
				if (oldValue != null)
					s.add(oldValue);
				setValue(s);
			} else {
				Set s = (Set) oldValue;
				if (s == null || s.isEmpty())
					setValue(null);
				else

					// Set the single select to contain only the first 
					// selected value in the multiselect
					setValue(s.iterator().next());
			}

			requestRepaint();
		}
	}

	/** Does the select allow adding new options by the user.
	 * If true, the new options can be added to the Container. The text entered
	 * by the user is used as id. No that data-source must allow adding new
	 * items (it must implement Container.Managed).
	 * @return True iff additions are allowed.
	 */
	public boolean isNewItemsAllowed() {

		return this.allowNewOptions;
	}

	/** Enable or disable possibility to add new options by the user.
	 * @param allowNewOptions New value of property allowNewOptions.
	 */
	public void setNewItemsAllowed(boolean allowNewOptions) {

		// Only handle change requests
		if (this.allowNewOptions != allowNewOptions) {

			this.allowNewOptions = allowNewOptions;

			requestRepaint();
		};
	}

	/** Override the caption of an item.
	 * Setting caption explicitly overrides id, item and index captions.
	 *
	 * @param itemId The id of the item to be recaptioned.
	 * @param caption New caption.
	 */
	public void setItemCaption(Object itemId, String caption) {
		if (itemId != null);
		itemCaptions.put(itemId, caption);
		requestRepaint();
	}

	/** Get the caption of an item.
	 * The caption is generated as specified by the item caption mode. See
	 * <code>setItemCaptionMode()</code> for more details.
	 *
	 * @param itemId The id of the item to be queried.
	 * @return caption for specified item.
	 */
	public String getItemCaption(Object itemId) {

		// Null items can not be found
		if (itemId == null)
			return null;

		String caption = null;

		switch (getItemCaptionMode()) {

			case ITEM_CAPTION_MODE_ID :
				caption = itemId.toString();
				break;

			case ITEM_CAPTION_MODE_INDEX :
				try {
					caption =
						String.valueOf(
							((Container.Indexed) items).indexOfId(itemId));
				} catch (ClassCastException ignored) {
				}
				break;

			case ITEM_CAPTION_MODE_ITEM :
				Item i = getItem(itemId);
				if (i != null)
					caption = i.toString();
				break;

			case ITEM_CAPTION_MODE_EXPLICIT :
				caption = (String) itemCaptions.get(itemId);
				break;

			case ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID :
				caption = (String) itemCaptions.get(itemId);
				if (caption == null)
					caption = itemId.toString();
				break;

			case ITEM_CAPTION_MODE_PROPERTY :
				Property p = getProperty(itemId, getItemCaptionPropertyId());
				if (p != null)
					caption = p.toString();
				break;
		}

		// All items must have some captions
		return caption != null ? caption : "";
	}

	/** Set icon for an item.
	 *
	 * @param itemId The id of the item to be assigned an icon.
	 * @param icon New icon.
	 */
	public void setItemIcon(Object itemId, Resource icon) {
		if (itemId != null);
		if (icon == null)
			itemIcons.remove(itemId);
		else
			itemIcons.put(itemId, icon);
		requestRepaint();
	}

	/** Get the item icon.
	 *
	 * @param itemId The id of the item to be assigned an icon.
	 * @return Icon for the item or null, if not specified.
	 */
	public Resource getItemIcon(Object itemId) {
		Resource explicit = (Resource) itemIcons.get(itemId);
		if (explicit != null)
			return explicit;

		if (getItemIconPropertyId() == null)
			return null;

		Property ip = getProperty(itemId, getItemIconPropertyId());
		if (ip == null)
			return null;
		Object icon = ip.getValue();
		if (icon instanceof Resource)
			return (Resource) icon;

		return null;
	}

	/** Set the item caption mode.
	 *
	 * <p>The mode can be one of the following ones:
	 * <ul>
	 *  <li><code>ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID</code> : Items
	 *      Id-objects <code>toString()</code> is used as item caption. If
	 *      caption is explicitly specified, it overrides the id-caption.
	 *  <li><code>ITEM_CAPTION_MODE_ID</code> : Items Id-objects
	 *      <code>toString()</code> is used as item caption.</li>
	 *   <li><code>ITEM_CAPTION_MODE_ITEM</code> : Item-objects
	 *      <code>toString()</code> is used as item caption.</li>
	 *   <li><code>ITEM_CAPTION_MODE_INDEX</code> : The index of the item is
	 *      used as item caption. The index mode can
	 *      only be used with the containers implementing
	 *      <code>Container.Indexed</code> interface.</li>
	 *   <li><code>ITEM_CAPTION_MODE_EXPLICIT</code> : The item captions
	 *       must be explicitly specified.</li>
	 *   <li><code>ITEM_CAPTION_MODE_PROPERTY</code> : The item captions
	 *       are read from property, that must be specified with 
	 *       <code>setItemCaptionPropertyId()</code>.</li>
	 * </ul>
	 * The <code>ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID</code> is the default
	 * mode.
	 * </p>
	 *
	 * @param mode One of the modes listed above.
	 */
	public void setItemCaptionMode(int mode) {
		if (ITEM_CAPTION_MODE_ID <= mode
			&& mode <= ITEM_CAPTION_MODE_PROPERTY) {
			itemCaptionMode = mode;
			requestRepaint();
		}
	}

	/** Get the item caption mode.
	 *
	 * <p>The mode can be one of the following ones:
	 * <ul>
	 *  <li><code>ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID</code> : Items
	 *      Id-objects <code>toString()</code> is used as item caption. If
	 *      caption is explicitly specified, it overrides the id-caption.
	 *  <li><code>ITEM_CAPTION_MODE_ID</code> : Items Id-objects
	 *      <code>toString()</code> is used as item caption.</li>
	 *   <li><code>ITEM_CAPTION_MODE_ITEM</code> : Item-objects
	 *      <code>toString()</code> is used as item caption.</li>
	 *   <li><code>ITEM_CAPTION_MODE_INDEX</code> : The index of the item is
	 *      used as item caption. The index mode can
	 *      only be used with the containers implementing
	 *      <code>Container.Indexed</code> interface.</li>
	 *   <li><code>ITEM_CAPTION_MODE_EXPLICIT</code> : The item captions
	 *       must be explicitly specified.</li>
	 *   <li><code>ITEM_CAPTION_MODE_PROPERTY</code> : The item captions
	 *       are read from property, that must be specified with 
	 *       <code>setItemCaptionPropertyId()</code>.</li>
	 * </ul>
	 * The <code>ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID</code> is the default
	 * mode.
	 * </p>
	 *
	 * @return One of the modes listed above.
	 */
	public int getItemCaptionMode() {
		return itemCaptionMode;
	}

	/** Set the item caption property.
	 * 
	 * <p>Setting the id to a existing property implicitly sets 
	 * the item caption mode to <code>ITEM_CAPTION_MODE_PROPERTY</code>.
	 * If the object is in <code>ITEM_CAPTION_MODE_PROPERTY</code>
	 * mode, setting caption property id null resets the 
	 * item caption mode to <code>ITEM_CAPTION_EXPLICIT_DEFAULTS_ID</code>.</p>
	
	 * <p>Setting the property id to null disables this feature. The 
	 * id is null by default</p>.
	 * 
	 */
	public void setItemCaptionPropertyId(Object propertyId) {
		if (getPropertyIds().contains(propertyId)) {
			itemCaptionPropertyId = propertyId;
			setItemCaptionMode(ITEM_CAPTION_MODE_PROPERTY);
			requestRepaint();
		} else {
			itemCaptionPropertyId = null;
			if (getItemCaptionMode() == ITEM_CAPTION_MODE_PROPERTY)
				setItemCaptionMode(ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID);
			requestRepaint();
		}
	}

	/** Get the item caption property.
	 * 
	 * @return Id of the property used as item caption source.
	 */
	public Object getItemCaptionPropertyId() {
		return itemCaptionPropertyId;
	}

	/** Set the item icon property.
	 * 
	 * <p>If the property id is set to a valid value, each item is given 
	 * an icon got from the given property of the items. The type
	 * of the property must be assignable to Icon.</p>
	 * 
	 * <p>Note that the icons set with <code>setItemIcon</code>
	 * function override the icons from the property.</p>
	 * 
	 * <p>Setting the property id to null disables this feature. The 
	 * id is null by default</p>.
	 * 
	 * @param propertyId Id of the property that specifies icons for
	 * items.
	 */
	public void setItemIconPropertyId(Object propertyId) {
		if (getPropertyIds().contains(propertyId)
			&& Resource.class.isAssignableFrom(getType(propertyId))) {
			itemIconPropertyId = propertyId;
			requestRepaint();
		} else
			itemIconPropertyId = null;
	}

	/** Get the item icon property.
	 * 
	 * <p>If the property id is set to a valid value, each item is given 
	 * an icon got from the given property of the items. The type
	 * of the property must be assignable to Icon.</p>
	 * 
	 * <p>Note that the icons set with <code>setItemIcon</code>
	 * function override the icons from the property.</p>
	 * 
	 * <p>Setting the property id to null disables this feature. The 
	 * id is null by default</p>.
	 * 
	 * @return Id of the property containing the item icons.
	 */
	public Object getItemIconPropertyId() {
		return itemIconPropertyId;
	}

	/** Test if an item is selected
	 * @param itemId Id the of the item to be tested
	 */
	public boolean isSelected(Object itemId) {
		if (itemId == null)
			return false;
		return (!isMultiSelect() && itemId.equals(getValue()))
			|| (isMultiSelect() && ((Set) getValue()).contains(itemId));
	}

	/** Select an item.
	 * @param itemId Item to be selected.
	 */
	public void select(Object itemId) {
		if (!isSelected(itemId) && items.containsId(itemId)) {
			if (isMultiSelect()) {
				Set s = new HashSet((Set) getValue());
				s.add(itemId);
				setValue(s);
			} else
				setValue(itemId);
		}
	}

	/** Unselect an item.
	 * @param itemId Item to be unselected.
	 */
	public void unselect(Object itemId) {
		if (isSelected(itemId)) {
			if (isMultiSelect()) {
				Set s = new HashSet((Set) getValue());
				s.remove(itemId);
				setValue(s);
			} else
				setValue(null);
		}
	}
	
	/**
	 * @see org.millstone.base.data.Container.PropertySetChangeListener#containerPropertySetChange(PropertySetChangeEvent)
	 */
	public void containerPropertySetChange(
		Container.PropertySetChangeEvent event) {
		firePropertySetChange();
	}

	/**
	 * @see org.millstone.base.data.Container.PropertySetChangeNotifier#addListener(PropertySetChangeListener)
	 */
	public void addListener(Container.PropertySetChangeListener listener) {
		if (propertySetEventListeners == null)
			propertySetEventListeners = new LinkedList();
		propertySetEventListeners.add(listener);
	}

	/**
	 * @see org.millstone.base.data.Container.PropertySetChangeNotifier#removeListener(PropertySetChangeListener)
	 */
	public void removeListener(Container.PropertySetChangeListener listener) {
		if (propertySetEventListeners != null) {
			propertySetEventListeners.remove(listener);
			if (propertySetEventListeners.isEmpty()) propertySetEventListeners = null;				
		}
	}

	/**
	 * @see org.millstone.base.data.Container.ItemSetChangeNotifier#addListener(ItemSetChangeListener)
	 */
	public void addListener(Container.ItemSetChangeListener listener) {
		if (itemSetEventListeners == null)
			itemSetEventListeners = new LinkedList();
		itemSetEventListeners.add(listener);	
	}

	/**
	 * @see org.millstone.base.data.Container.ItemSetChangeNotifier#removeListener(ItemSetChangeListener)
	 */
	public void removeListener(Container.ItemSetChangeListener listener) {
		if (itemSetEventListeners != null) {
			itemSetEventListeners.remove(listener);
			if (itemSetEventListeners.isEmpty()) itemSetEventListeners = null;	
		}
	}

	/**
	 * @see org.millstone.base.data.Container.ItemSetChangeListener#containerItemSetChange(ItemSetChangeEvent)
	 */
	public void containerItemSetChange(Container.ItemSetChangeEvent event) {
		fireItemSetChange();		
	}

	/** Fire property set change event */
	protected void firePropertySetChange() {
		if (propertySetEventListeners != null && !propertySetEventListeners.isEmpty())	{
			Container.PropertySetChangeEvent event = new PropertySetChangeEvent();
			Object[] listeners = propertySetEventListeners.toArray();
			for (int i=0; i<listeners.length; i++)
				((Container.PropertySetChangeListener)listeners[i]).containerPropertySetChange(event);
		}
		requestRepaint();
	}

	/** Fire item set change event */
	protected void fireItemSetChange() {
		if (itemSetEventListeners != null && !itemSetEventListeners.isEmpty())	{
			Container.ItemSetChangeEvent event = new ItemSetChangeEvent();
			Object[] listeners = itemSetEventListeners.toArray();
			for (int i=0; i<listeners.length; i++)
				((Container.ItemSetChangeListener)listeners[i]).containerItemSetChange(event);
		}
		requestRepaint();
	}

	/** Implementation of item set change event */	
	private class ItemSetChangeEvent implements Container.ItemSetChangeEvent {
		
		/**
		 * @see org.millstone.base.data.Container.ItemSetChangeEvent#getContainer()
		 */
		public Container getContainer() {
			return Select.this;
		}

	}

	/** Implementation of property set change event */	
	private class PropertySetChangeEvent implements Container.PropertySetChangeEvent {
		
		/**
		 * @see org.millstone.base.data.Container.PropertySetChangeEvent#getContainer()
		 */
		public Container getContainer() {
			return Select.this;
		}

	}
}
