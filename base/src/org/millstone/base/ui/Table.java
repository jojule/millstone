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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.millstone.base.data.Container;
import org.millstone.base.data.Item;
import org.millstone.base.data.Property;
import org.millstone.base.data.util.ContainerOrderedWrapper;
import org.millstone.base.data.util.IndexedContainer;
import org.millstone.base.event.*;
import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.PaintTarget;
import org.millstone.base.terminal.KeyMapper;
import org.millstone.base.terminal.Resource;

/** Table component is used for representing data or components in pageable and
 * selectable table.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Table
	extends Select
	implements Action.Container, Container.Ordered {

	private static final int CELL_KEY = 0;
	private static final int CELL_HEADER = 1;
	private static final int CELL_ICON = 2;
	private static final int CELL_ITEMID = 3;
	private static final int CELL_FIRSTCOL = 4;

	/** Left column alignment.
	 * <b>This is the default behaviour.</b>
	 */
	public static final String ALIGN_LEFT = "b";

	/** Center column alignment. */
	public static final String ALIGN_CENTER = "c";

	/** Right column alignment. */
	public static final String ALIGN_RIGHT = "e";

	/** Column header mode: Column headers are hidden. 
	 * <b>This is the default behaviour.</b>
	 */
	public static final int COLUMN_HEADER_MODE_HIDDEN = -1;

	/** Column header mode: Property ID:s are used as column headers.
	 */
	public static final int COLUMN_HEADER_MODE_ID = 0;

	/** Column header mode: Column headers are explicitly specified with 
	 * <code>setColumnHeaders()</code>
	 */
	public static final int COLUMN_HEADER_MODE_EXPLICIT = 1;

	/** Column header mode: Column headers are explicitly specified with 
	 * <code>setColumnHeaders()</code>
	 */
	public static final int COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID = 2;

	/** Row caption mode: The row headers are hidden.
	 *  <b>This is the default mode.</b>
	 */
	public static final int ROW_HEADER_MODE_HIDDEN = -1;

	/** Row caption mode: Items Id-objects toString() is used as row caption.
	 */
	public static final int ROW_HEADER_MODE_ID = Select.ITEM_CAPTION_MODE_ID;

	/** Row caption mode: Item-objects toString() is used as row caption.
	 */
	public static final int ROW_HEADER_MODE_ITEM =
		Select.ITEM_CAPTION_MODE_ITEM;

	/** Row caption mode: Index of the item is used as item caption.
	 ** The index mode can only be used with the containers
	 * implementing Container.Indexed interface.
	 */
	public static final int ROW_HEADER_MODE_INDEX =
		Select.ITEM_CAPTION_MODE_INDEX;

	/** Row caption mode: Item captions are explicitly specified.
	 */
	public static final int ROW_HEADER_MODE_EXPLICIT =
		Select.ITEM_CAPTION_MODE_EXPLICIT;

	/** Row caption mode: Item captions are read from property specified with 
	 * <code>setItemCaptionPropertyId</code>.
	 */
	public static final int ROW_HEADER_MODE_PROPERTY =
		Select.ITEM_CAPTION_MODE_PROPERTY;

	/** Row caption mode: Only icons are shown, the captions are hidden.
	 */
	public static final int ROW_HEADER_MODE_ICON_ONLY =
		Select.ITEM_CAPTION_MODE_ICON_ONLY;

	/** Row caption mode: Item captions are explicitly specified, but if the
	 * caption is missing, the item id objects <code>toString()</code> is 
	 * used instead.
	 */
	public static final int ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID =
		Select.ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID;

	/* Private table extensions to Select *********************************** */

	/** Array of visible column property ids. */
	private Object[] visibleColumns = new Object[0];

	/** Array of visible column headers. */
	private String[] columnHeaders = new String[0];

	/** Array of visible column icons. */
	private Resource[] columnIcons = new Resource[0];

	/** Array of visible column alignments. */
	private String[] columnAlignments = new String[0];

	/** Holds value of property pageLength. 0 disables paging. */
	private int pageLength = 0;

	/** Id the first item on the current page. */
	private Object currentPageFirstItemId = null;

	/** Index of the first item on the current page. */
	private int currentPageFirstItemIndex = 0;

	/** Holds value of property pageBuffering. */
	private boolean pageBuffering = false;

	/** Holds value of property selectable. */
	private boolean selectable = false;

	/** Holds value of property columnHeaderMode. */
	private int columnHeaderMode = COLUMN_HEADER_MODE_HIDDEN;

	/** True iff the row captions are hidden. */
	private boolean rowCaptionsAreHidden = true;

	/** Page contents buffer used in buffered mode */
	private Object[][] pageBuffer = null;

	/** List of properties listened - the list is kept to release the listeners later. */
	private LinkedList listenedProperties = null;

	/** List of visible components - the is used for needsRepaint calculation. */
	private LinkedList visibleComponents = null;

	/** List of action handlers */
	private LinkedList actionHandlers = null;

	/** Action mapper */
	private KeyMapper actionMapper = null;

	/** Table cell editor factory */
	private FieldFactory fieldFactory = new BaseFieldFactory();

	/** Is table editable */
	private boolean editable = false;

	/* Table constructors *************************************************** */

	/** Create new empty table */
	public Table() {
		setRowHeaderMode(ROW_HEADER_MODE_HIDDEN);
	}

	/** Create new empty table with caption.*/
	public Table(String caption) {
		this();
		setCaption(caption);
	}

	/** Create new table with caption and connect it to a Container. */
	public Table(String caption, Container dataSource) {
		this();
		setCaption(caption);
		setContainerDataSource(dataSource);
	}

	/* Table functionality ************************************************** */

	/** Get the array of visible column property id:s.
	 *
	 * <p>The columns are show in the order of their appearance in this
	 * array</p>
	 * @return Value of property visibleColumns.
	 */
	public Object[] getVisibleColumns() {
		return visibleColumns;
	}

	/** Set the array of visible column property id:s.
	 *
	 * <p>The columns are show in the order of their appearance in this
	 * array</p>
	 * @param visibleColumns Array of shown property id:s.
	 */
	public void setVisibleColumns(Object[] visibleColumns) {

		// Visible columns must exist
		if (visibleColumns == null)
			throw new NullPointerException("Can not set visible columns to null value");

		// Check that the new visible columns contains no nulls and properties exist
		Collection properties = getContainerPropertyIds();
		for (int i = 0; i < visibleColumns.length; i++)
			if (visibleColumns[i] == null)
				throw new NullPointerException("Properties must be non-nulls");
			else if (!properties.contains(visibleColumns[i]))
				throw new IllegalArgumentException(
					"Properties must exist in the Container, missing property: "
						+ visibleColumns[i]);

		// Save the old icons,headers and alignments
		Hashtable icons = new Hashtable();
		Hashtable headers = new Hashtable();
		Hashtable aligns = new Hashtable();
		if (this.visibleColumns != null)
			for (int i = 0; i < this.visibleColumns.length; i++) {
				if (columnIcons[i] != null)
					icons.put(this.visibleColumns[i], columnIcons[i]);
				if (columnHeaders[i] != null)
					headers.put(this.visibleColumns[i], columnHeaders[i]);
				if (columnAlignments[i] != null)
					aligns.put(this.visibleColumns[i], columnAlignments[i]);
			}

		// Recreate icons,headers and alignments
		columnAlignments = new String[visibleColumns.length];
		columnHeaders = new String[visibleColumns.length];
		columnIcons = new Resource[visibleColumns.length];
		for (int i = 0; i < visibleColumns.length; i++) {
			columnAlignments[i] = (String) aligns.get(visibleColumns[i]);
			columnHeaders[i] = (String) headers.get(visibleColumns[i]);
			columnIcons[i] = (Resource) icons.get(visibleColumns[i]);
		}

		this.visibleColumns = visibleColumns;

		// Clear page buffer and notify about the change 
		pageBuffer = null;
		requestRepaint();
	}

	/** Get the headers of the columns.
	 *
	 * <p>The headers match the property id:s given my the set
	 * visible column headers. The table must be set in either
	 * <code>ROW_HEADER_MODE_EXPLICIT</code> or
	 * <code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code> mode
	 * to show the headers. In the defaults mode any nulls in the 
	 * headers array are replaced with id.toString() outputs when
	 * rendering.</p>
	 * @return Array of column headers.
	 */
	public String[] getColumnHeaders() {
		return this.columnHeaders;
	}

	/** Set the headers of the columns.
	 *
	 * <p>The headers match the property id:s given my the set
	 * visible column headers. The table must be set in either
	 * <code>ROW_HEADER_MODE_EXPLICIT</code> or
	 * <code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code> mode
	 * to show the headers. In the defaults mode any nulls in the 
	 * headers array are replaced with id.toString() outputs when
	 * rendering.</p>
	 * @param columnHeaders Array of column headers that match the <code>getVisibleColumns()</code>.
	 */
	public void setColumnHeaders(String[] columnHeaders) {

		if (columnHeaders.length != visibleColumns.length)
			throw new IllegalArgumentException("The length of the headers array must match the number of visible columns");

		this.columnHeaders = columnHeaders;

		// Clear page buffer and notify about the change 
		pageBuffer = null;
		requestRepaint();
	}

	/** Get the icons of the columns.
	 *
	 * <p>The icons in headers match the property id:s given my the set
	 * visible column headers. The table must be set in either
	 * <code>ROW_HEADER_MODE_EXPLICIT</code> or
	 * <code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code> mode
	 * to show the headers with icons.</p>
	 * @return Array of icons that match the <code>getVisibleColumns()</code>.
	 */
	public Resource[] getColumnIcons() {
		return columnIcons;
	}

	/** Set the icons of the columns.
	 *
	 * <p>The icons in headers match the property id:s given my the set
	 * visible column headers. The table must be set in either
	 * <code>ROW_HEADER_MODE_EXPLICIT</code> or
	 * <code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code> mode
	 * to show the headers with icons.</p>
	 * @param columnIcons Array of icons that match the <code>getVisibleColumns()</code>.
	 */
	public void setColumnIcons(Resource[] columnIcons) {

		if (columnHeaders.length != visibleColumns.length)
			throw new IllegalArgumentException("The length of the icons array must match the number of visible columns");

		this.columnIcons = columnIcons;

		// Clear page buffer and notify about the change 
		pageBuffer = null;
		requestRepaint();
	}

	/** Get array of column alignments.
	 *
	 * <p>The items in the array must match the properties
	 * identified by <code>getVisibleColumns()</code>. The
	 * possible values for the alignments include:
	 * <ul>
	 *  <li><code>ALIGN_LEFT</code> : Left alignment</li>
	 *  <li><code>ALIGN_CENTER</code> : Centered</li>
	 *  <li><code>ALIGN_LEFT</code> : Right alignment</li>
	 * </ul>
	 * The alignments default to <code>ALIGN_LEFT</code>: any null
	 * values are rendered as align lefts.
	 * </p>
	 * @return Column alignments array.
	 */
	public String[] getColumnAlignments() {
		return columnAlignments;
	}

	/** Set the column alignments.
	 * <p>The items in the array must match the properties
	 * identified by <code>getVisibleColumns()</code>. The
	 * possible values for the alignments include:
	 * <ul>
	 *  <li><code>ALIGN_LEFT</code> : Left alignment</li>
	 *  <li><code>ALIGN_CENTER</code> : Centered</li>
	 *  <li><code>ALIGN_LEFT</code> : Right alignment</li>
	 * </ul>
	 * The alignments default to <code>ALIGN_LEFT</code>
	 * </p>
	 * @param columnAlignments Column alignments array.
	 */
	public void setColumnAlignments(String[] columnAlignments) {

		if (columnHeaders.length != visibleColumns.length)
			throw new IllegalArgumentException("The length of the alignments array must match the number of visible columns");

		for (int i = 0; i < columnAlignments.length; i++)
			if (columnAlignments[i] != null
				&& !columnAlignments[i].equals(ALIGN_LEFT)
				&& !columnAlignments[i].equals(ALIGN_CENTER)
				&& !columnAlignments[i].equals(ALIGN_RIGHT)
				&& !columnAlignments[i].equals(ALIGN_LEFT))
				throw new IllegalArgumentException(
					"Unknown alignment: " + columnAlignments[i]);

		this.columnAlignments = columnAlignments;

		// Clear page buffer and notify about the change 
		pageBuffer = null;
		requestRepaint();
	}

	/** Get the page length.
	 * 
	 * <p>Setting page length 0 disables paging.</p>
	 *
	 * @return Lenght of one page.
	 */
	public int getPageLength() {
		return this.pageLength;
	}

	/** Set the page length.
	 *
	 * <p>Setting page length 0 disables paging. The page length
	 * defaults to 0 (no paging).</p>
	 *
	 * @param Lenght of one page.
	 */
	public void setPageLength(int pageLength) {
		if (pageLength >= 0 && this.pageLength != pageLength) {
			this.pageLength = pageLength;

			// Clear page buffer and notify about the change 
			pageBuffer = null;
			requestRepaint();
		}
	}

	/** Getter for property currentPageFirstItem.
	 * @return Value of property currentPageFirstItem.
	 */
	public Object getCurrentPageFirstItemId() {

		// Priorise index over id if indexes are supported
		if (items instanceof Container.Indexed) {
			int index = getCurrentPageFirstItemIndex();
			Object id = null;
			if (index >= 0 && index < size())
				id = ((Container.Indexed) items).getIdByIndex(index);
			if (id != null && !id.equals(currentPageFirstItemId))
				currentPageFirstItemId = id;
		}

		// If there is no item id at all, use the first one
		if (currentPageFirstItemId == null)
			currentPageFirstItemId = ((Container.Ordered) items).firstItemId();

		return currentPageFirstItemId;
	}

	/** Setter for property currentPageFirstItem.
	 * @param currentPageFirstItem New value of property currentPageFirstItem.
	 */
	public void setCurrentPageFirstItemId(Object currentPageFirstItemId) {

		// Get the corresponding index
		int index = -1;
		try {
			index =
				((Container.Indexed) items).indexOfId(currentPageFirstItemId);
		} catch (ClassCastException e) {

			// If the table item container does not have index, we have to 
			// calculate the index by hand
			Object id = ((Container.Ordered) items).firstItemId();
			while (id != null && !id.equals(currentPageFirstItemId)) {
				index++;
				id = ((Container.Ordered) items).nextItemId(id);
			}
			if (id == null)
				index = -1;
		}

		// If the search for item index was successfull
		if (index >= 0) {
			this.currentPageFirstItemId = currentPageFirstItemId;
			this.currentPageFirstItemIndex = index;
		}

		// Clear page buffer and notify about the change 
		pageBuffer = null;
		requestRepaint();
	}

	/** Getter for property currentPageFirstItem.
	 * @return Value of property currentPageFirstItem.
	 */
	public int getCurrentPageFirstItemIndex() {
		return this.currentPageFirstItemIndex;
	}

	/** Setter for property currentPageFirstItem.
	 * @param currentPageFirstItem New value of property currentPageFirstItem.
	 */
	public void setCurrentPageFirstItemIndex(int currentPageFirstItemIndex) {

		// Ensure that the new value is valid
		if (currentPageFirstItemIndex < 0)
			currentPageFirstItemIndex = 0;
		if (currentPageFirstItemIndex >= size())
			currentPageFirstItemIndex = size() - 1;

		// Refresh first item id
		if (items instanceof Container.Indexed) {
			try {
				currentPageFirstItemId =
					((Container.Indexed) items).getIdByIndex(
						currentPageFirstItemIndex);
			} catch (IndexOutOfBoundsException e) {
				currentPageFirstItemId = null;
			}
			this.currentPageFirstItemIndex = currentPageFirstItemIndex;
		} else {

			// For containers not supporting indexes, we must iterate the
			// container forwards / backwards
			// next available item forward or backward

			// Go forwards in the middle of the list (respect borders)
			while (this.currentPageFirstItemIndex < currentPageFirstItemIndex
				&& !((Container.Ordered) items).isLastId(
					currentPageFirstItemId)) {
				this.currentPageFirstItemIndex++;
				currentPageFirstItemId =
					((Container.Ordered) items).nextItemId(
						currentPageFirstItemId);
			}

			// If we did hit the border				
			if (((Container.Ordered) items).isLastId(currentPageFirstItemId)) {
				this.currentPageFirstItemIndex = size() - 1;
			}

			// Go backwards in the middle of the list (respect borders)
			while (this.currentPageFirstItemIndex > currentPageFirstItemIndex
				&& !((Container.Ordered) items).isFirstId(
					currentPageFirstItemId)) {
				this.currentPageFirstItemIndex--;
				currentPageFirstItemId =
					((Container.Ordered) items).prevItemId(
						currentPageFirstItemId);
			}

			// If we did hit the border				
			if (((Container.Ordered) items)
				.isFirstId(currentPageFirstItemId)) {
				this.currentPageFirstItemIndex = 0;
			}

			// Go forwards once more
			while (this.currentPageFirstItemIndex < currentPageFirstItemIndex
				&& !((Container.Ordered) items).isLastId(
					currentPageFirstItemId)) {
				this.currentPageFirstItemIndex++;
				currentPageFirstItemId =
					((Container.Ordered) items).nextItemId(
						currentPageFirstItemId);
			}

			// If for some reason we do hit border again, override
			// the user index request
			if (((Container.Ordered) items).isLastId(currentPageFirstItemId)) {
				currentPageFirstItemIndex =
					this.currentPageFirstItemIndex = size() - 1;
			}
		}

		// Clear page buffer and notify about the change 
		pageBuffer = null;
		requestRepaint();
	}

	/** Getter for property pageBuffering.
	 * @return Value of property pageBuffering.
	 */
	public boolean isPageBufferingEnabled() {
		return this.pageBuffering;
	}

	/** Setter for property pageBuffering.
	 * @param pageBuffering New value of property pageBuffering.
	 */
	public void setPageBufferingEnabled(boolean pageBuffering) {

		this.pageBuffering = pageBuffering;

		// If page buffering is disabled, clear the buffer
		if (!pageBuffering)
			pageBuffer = null;
	}

	/** Getter for property selectable.
	 * 
	 * <p>The table is not selectable by default.</p>
	 * 
	 * @return Value of property selectable.
	 */
	public boolean isSelectable() {
		return this.selectable;
	}

	/** Setter for property selectable.
	 * 
	 * <p>The table is not selectable by default.</p>
	 * 
	 * @param selectable New value of property selectable.
	 */
	public void setSelectable(boolean selectable) {
		if (this.selectable != selectable) {
			this.selectable = selectable;
			requestRepaint();
		}
	}

	/** Getter for property columnHeaderMode.
	 * @return Value of property columnHeaderMode.
	 */
	public int getColumnHeaderMode() {
		return this.columnHeaderMode;
	}

	/** Setter for property columnHeaderMode.
	 * @param columnHeaderMode New value of property columnHeaderMode.
	 */
	public void setColumnHeaderMode(int columnHeaderMode) {
		if (columnHeaderMode >= COLUMN_HEADER_MODE_HIDDEN
			&& columnHeaderMode <= COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID)
			this.columnHeaderMode = columnHeaderMode;

		// Clear page buffer and notify about the change 
		pageBuffer = null;
		requestRepaint();
	}

	/** Refresh the current page contents.
	 * If the page buffering is turned off, it is not necessary to call this
	 * explicitely.
	 */
	public void refreshCurrentPage() {

		// Clear page buffer and notify about the change 
		pageBuffer = null;
		requestRepaint();
	}

	/** Set the row header mode.
	 * <p>The mode can be one of the following ones:
	 * <ul>
	 *   <li><code>ROW_HEADER_MODE_HIDDEN</code> : The row captions are
	 *      hidden.</li>
	 *  <li><code>ROW_HEADER_MODE_ID</code> : Items Id-objects
	 *      <code>toString()</code> is used as row caption.
	 *   <li><code>ROW_HEADER_MODE_ITEM</code> : Item-objects
	 *      <code>toString()</code> is used as row caption.
	 *   <li><code>ROW_HEADER_MODE_PROPERTY</code> : Property set with
	 *     <code>setItemCaptionPropertyId()</code> is used as row header.
	 *  <li><code>ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID</code> : Items
	 *      Id-objects <code>toString()</code> is used as row header. If
	 *      caption is explicitly specified, it overrides the id-caption.
	 *   <li><code>ROW_HEADER_MODE_EXPLICIT</code> : The row headers
	 *       must be explicitly specified.</li>
	 *   <li><code>ROW_HEADER_MODE_INDEX</code> : The index of the item is
	 *      used as row caption. The index mode can
	 *      only be used with the containers implementing
	 *      <code>Container.Indexed</code> interface.</li>
	 * </ul>
	 * The default value is <code>ROW_HEADER_MODE_HIDDEN</code>
	 * </p>
	 *
	 * @param mode One of the modes listed above.
	 */
	public void setRowHeaderMode(int mode) {
		if (ROW_HEADER_MODE_HIDDEN == mode)
			rowCaptionsAreHidden = true;
		else {
			rowCaptionsAreHidden = false;
			setItemCaptionMode(mode);
		}

		// Clear page buffer and notify about the change 
		pageBuffer = null;
		requestRepaint();
	}

	/** Get the row header mode.
	 *
	 * @return Row header mode.
	 * @see #setRowHeaderMode(int)
	 */
	public int getRowHeaderMode() {
		return rowCaptionsAreHidden
			? ROW_HEADER_MODE_HIDDEN
			: getItemCaptionMode();
	}

	/** Add new row to table and fill the visible cells with given
	 * values.
	 * 
	 * @param cells Object array that is used for filling the 
	 *     visible cells new row. The types must be settable to 
	 *     visible column property types.
	 * @param itemId Id the new row. If null, a new id is automatically
	 *     assigned. If given, the table cant already have a item with given id.
	 * @return Returns item id for the new row. Returns null if operation fails.
	 */
	public Object addItem(Object[] cells, Object itemId)
		throws UnsupportedOperationException {

		Object[] cols = getVisibleColumns();

		// Check that a correct number of cells are given
		if (cells.length != cols.length)
			return null;

		// Create new item
		Item item;
		if (itemId == null) {
			itemId = items.addItem();
			if (itemId == null)
				return null;
			item = items.getItem(itemId);
		} else
			item = items.addItem(itemId);
		if (item == null)
			return null;

		// Fill the item properties
		for (int i = 0; i < cols.length; i++)
			item.getItemProperty(cols[i]).setValue(cells[i]);

		return itemId;
	}

	/* Overriding select behavior******************************************** */

	/**
	 * @see org.millstone.base.data.Container.Viewer#setContainerDataSource(Container)
	 */
	public void setContainerDataSource(Container newDataSource) {

		if (newDataSource == null)
			newDataSource = new IndexedContainer();

		// Assure that the data source is ordered by making unordered 
		// containers ordered by wrapping them
		if (newDataSource instanceof Container.Ordered)
			super.setContainerDataSource(newDataSource);
		else
			super.setContainerDataSource(
				new ContainerOrderedWrapper(newDataSource));

		// Reset page position 
		currentPageFirstItemId = null;
		currentPageFirstItemIndex = 0;

		// Set the visible properties
		setVisibleColumns(getContainerPropertyIds().toArray());

		// Assure visual refresh
		refreshCurrentPage();
	}

	/* Component basics ***************************************************** */

	/** Invoked when the value of a variable has changed.
	 * @param event Variable change event containing the information about
	 * the changed variable.
	 */
	public void changeVariables(Object source, Map variables) {

		super.changeVariables(source, variables);

		// Page start index
		if (variables.containsKey("firstvisible")) {
			setCurrentPageFirstItemIndex(
				((Integer) variables.get("firstvisible")).intValue() - 1);
		}

		// Actions
		if (variables.containsKey("action")) {
			StringTokenizer st =
				new StringTokenizer((String) variables.get("action"), ",");
			if (st.countTokens() == 2) {
				Object itemId = itemIdMapper.get(st.nextToken());
				Action action = (Action) actionMapper.get(st.nextToken());
				if (action != null
					&& containsId(itemId)
					&& actionHandlers != null)
					for (Iterator i = actionHandlers.iterator(); i.hasNext();)
						((Action.Handler) i.next()).handleAction(
							action,
							this,
							itemId);
			}
		}
	}

	/** Paint the content of this component.
	 * @param target Paint target.
	 * @throws PaintException The paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		// Focus control id
		if (this.getFocusableId() > 0) {
			target.addAttribute("focusid", this.getFocusableId());
		}

		// The tab ordering number
		if (this.getTabIndex() > 0)
			target.addAttribute("tabindex", this.getTabIndex());


		// Initialize temps
		Object[] colids = getVisibleColumns();
		int cols = colids.length;
		int first = getCurrentPageFirstItemIndex();
		int total = size();
		int pagelen = getPageLength();
		int colHeadMode = getColumnHeaderMode();
		boolean colheads = colHeadMode != COLUMN_HEADER_MODE_HIDDEN;
		boolean rowheads = getRowHeaderMode() != ROW_HEADER_MODE_HIDDEN;
		Object[][] cells = getVisibleCells();
		boolean iseditable = this.isEditable();

		// selection support
		String[] selectedKeys;
		if (isMultiSelect())
			selectedKeys = new String[((Set) getValue()).size()];
		else
			selectedKeys = new String[(getValue() == null ? 0 : 1)];
		int keyIndex = 0;

		// Table attributes
		if (isSelectable())
			target.addAttribute(
				"selectmode",
				(isMultiSelect() ? "multi" : "single"));
		else
			target.addAttribute("selectmode", "none");
		target.addAttribute("cols", cols);
		target.addAttribute("rows", cells[0].length);
		target.addAttribute("totalrows", total);
		if (pagelen != 0)
			target.addAttribute("pagelength", pagelen);
		if (colheads)
			target.addAttribute("colheaders", true);
		if (rowheads)
			target.addAttribute("rowheaders", true);

		// Columns
		target.startTag("cols");
		Resource[] icons = getColumnIcons();
		String[] heads = getColumnHeaders();
		String[] aligns = getColumnAlignments();
		for (int i = 0; i < cols; i++) {
			target.startTag("ch");
			if (colheads) {
				if (icons[i] != null)
					target.addAttribute("icon", icons[i]);
				String head = heads[i];
				if (head == null && colHeadMode != COLUMN_HEADER_MODE_EXPLICIT)
					head = colids[i].toString();
				if (head == null)
					head = "";
				target.addAttribute("caption", head);
			}
			if (aligns[i] == null)
				target.addAttribute("align", ALIGN_LEFT);
			else if (!aligns[i].equals(ALIGN_LEFT))
				target.addAttribute("align", aligns[i]);
			target.endTag("ch");
		}
		target.endTag("cols");

		// Rows
		Set actionSet = new HashSet();
		boolean selectable = isSelectable();
		boolean[] iscomponent = new boolean[cols];
		for (int i = 0; i < cols; i++) {
			Class colType = getType(colids[i]);
			iscomponent[i] =
				colType != null && Component.class.isAssignableFrom(colType);
		}
		target.startTag("rows");
		for (int i = 0; i < cells[0].length; i++) {
			target.startTag("tr");
			Object itemId = cells[CELL_ITEMID][i];

			// tr attributes
			if (rowheads) {
				if (cells[CELL_ICON][i] != null)
					target.addAttribute("icon", (Resource) cells[CELL_ICON][i]);
				if (cells[CELL_HEADER][i] != null)
					target.addAttribute(
						"caption",
						(String) cells[CELL_HEADER][i]);
			}
			if (actionHandlers != null || isSelectable()) {
				target.addAttribute("key", (String) cells[CELL_KEY][i]);
				if (isSelected(itemId) && keyIndex < selectedKeys.length) {
					target.addAttribute("selected", true);
					selectedKeys[keyIndex++] = (String) cells[CELL_KEY][i];
				}
			}

			// Actions
			if (actionHandlers != null) {
				target.startTag("al");
				for (Iterator ahi = actionHandlers.iterator();
					ahi.hasNext();
					) {
					Action[] aa =
						((Action.Handler) ahi.next()).getActions(itemId, this);
					if (aa != null)
						for (int ai = 0; ai < aa.length; ai++) {
							String key = actionMapper.key(aa[ai]);
							actionSet.add(aa[ai]);
							target.addSection("ak", key);
						}
				}
				target.endTag("al");
			}

			// cells
			for (int j = 0; j < cols; j++) {
				if (iscomponent[j]) {
					Component c = (Component) cells[CELL_FIRSTCOL + j][i];
					if (c != null)
						c.paint(target);
				} else if (iseditable) {
					Component c = (Component) cells[CELL_FIRSTCOL + j][i];
					if (c != null)
						c.paint(target);
				} else
					target.addSection(
						"label",
						(String) cells[CELL_FIRSTCOL + j][i]);
			}

			target.endTag("tr");
		}
		target.endTag("rows");

		// The select variable is only enabled if selectable
		if (selectable)
			target.addVariable(this, "selected", selectedKeys);

		// The cursors are only shown on pageable table
		if (first != 0 || getPageLength() > 0)
			target.addVariable(this, "firstvisible", first + 1);

		// Actions
		if (!actionSet.isEmpty()) {
			target.startTag("actions");
			target.addVariable(this, "action", "");
			for (Iterator i = actionSet.iterator(); i.hasNext();) {
				Action a = (Action) i.next();
				target.startTag("action");
				if (a.getCaption() != null)
					target.addAttribute("caption", a.getCaption());
				if (a.getIcon() != null)
					target.addAttribute("icon", a.getIcon());
				target.addAttribute("key", actionMapper.key(a));
				target.endTag("action");
			}
			target.endTag("actions");
		}
	}

	/** Get UIDL tag corresponding to component.
	 * @return UIDL tag as string.
	 */
	public String getTag() {
		return "table";
	}

	/** Return cached visible table contents */
	private Object[][] getVisibleCells() {

		// Return a buffered value if possible
		if (pageBuffer != null && isPageBufferingEnabled())
			return pageBuffer;

		// Stop listening the old properties and initialise the list
		if (listenedProperties == null)
			listenedProperties = new LinkedList();
		else
			for (Iterator i = listenedProperties.iterator(); i.hasNext();) {
				((Property.ValueChangeNotifier) i.next()).removeListener(this);
			}

		// Detach old visible component from the table
		if (visibleComponents == null)
			visibleComponents = new LinkedList();
		else
			for (Iterator i = visibleComponents.iterator(); i.hasNext();) {
				((Component) i.next()).setParent(null);
			}

		// Collect basic facts about the table page
		Object[] colids = getVisibleColumns();
		int cols = colids.length;
		int pagelen = getPageLength();
		int firstIndex = getCurrentPageFirstItemIndex();
		int rows = size();
		if (rows > 0 && firstIndex >=0)
			rows -= firstIndex;;
		if (pagelen > 0 && pagelen < rows)
			rows = pagelen;
		Object[][] cells = new Object[cols + CELL_FIRSTCOL][rows];
		if (rows == 0)
			return cells;
		Object id = getCurrentPageFirstItemId();
		int headmode = getRowHeaderMode();
		boolean[] iscomponent = new boolean[cols];
		for (int i = 0; i < cols; i++)
			iscomponent[i] =
				Component.class.isAssignableFrom(getType(colids[i]));

		// Create page contents
		int filledRows = 0;
		for (int i = 0; i < rows && id != null; i++) {
			cells[CELL_ITEMID][i] = id;
			cells[CELL_KEY][i] = itemIdMapper.key(id);
			if (headmode != ROW_HEADER_MODE_HIDDEN) {
				switch (headmode) {
					case ROW_HEADER_MODE_INDEX :
						cells[CELL_HEADER][i] =
							String.valueOf(i + firstIndex + 1);
						break;
					default :
						cells[CELL_HEADER][i] = getItemCaption(id);
				}
				cells[CELL_ICON][i] = getItemIcon(id);
			}
			if (cols > 0) {
				for (int j = 0; j < cols; j++) {
					Object value = null;
					Property p = getContainerProperty(id, colids[j]);
					if (p != null) {
						if (p instanceof Property.ValueChangeNotifier) {
							((Property.ValueChangeNotifier) p).addListener(
								this);
							listenedProperties.add(p);
						}
						if (iscomponent[j]) {
							value = p.getValue();
						} else if (p != null) {
							value = getPropertyValue(id, colids[j], p);
						} else {
							value = getPropertyValue(id, colids[j], null);
						}
					} else {
						value = "";
					}

					if (value instanceof Component) {
						((Component) value).setParent(this);
						visibleComponents.add((Component) value);
					}
					cells[CELL_FIRSTCOL + j][i] = value;

				}
			}
			id = ((Container.Ordered) items).nextItemId(id);

			filledRows++;
		}

		// Assure that all the rows of the cell-buffer are valid
		if (filledRows != cells[0].length) {
			Object[][] temp = new Object[cells.length][filledRows];
			for (int i = 0; i < cells.length; i++)
				for (int j = 0; j < filledRows; j++)
					temp[i][j] = cells[i][j];
			cells = temp;
		}

		// Save the results to internal buffer iff in buffering mode
		// to possible conserve memory from large non-buffered pages
		if (isPageBufferingEnabled())
			pageBuffer = cells;

		return cells;
	}

	/** Get value of property.
	 * 
	 * By default if the table is editable the fieldFactory is used to create
	 * editors for table cells. Otherwise formatPropertyValue is used to format
	 * the value representation.
	 *  
	 * @see #setFieldFactory(FieldFactory)
	 * @param rowId Id of the row (same as item Id)
	 * @param colId Id of the column
	 * @param property Property to be presented
	 * @return Object Either formatted value or Component for field.
	 */
	protected Object getPropertyValue(
		Object rowId,
		Object colId,
		Property property) {
		if (this.isEditable() && this.fieldFactory != null) {
			Field f =
				this.fieldFactory.createField(
					getContainerDataSource(),
					rowId,
					colId,
					this);
			f.setPropertyDataSource(property);
			return f;
		}

		return formatPropertyValue(rowId, colId, property);
	}

	/** Formats table cell property values.
	 *  By default the property.toString() and return a empty string for
	 *  null properties.
	 * 
	 * @param itemId
	 * @param property Property to be formatted
	 * @return String representation of property and its value.
	 * @since 3.1
	 */
	protected String formatPropertyValue(
		Object rowId,
		Object colId,
		Property property) {
		if (property == null) {
			return "";
		}
		return property.toString();
	}

	/* Action container *************************************************** */

	/**
	 * @see org.millstone.base.event.Action.Container#addActionHandler(Action.Handler)
	 */
	public void addActionHandler(Action.Handler actionHandler) {

		if (actionHandler != null) {

			if (actionHandlers == null) {
				actionHandlers = new LinkedList();
				actionMapper = new KeyMapper();
			}

			actionHandlers.add(actionHandler);

			requestRepaint();
		}
	}

	/**
	 * @see org.millstone.base.event.Action.Container#removeActionHandler(Action.Handler)
	 */
	public void removeActionHandler(Action.Handler actionHandler) {

		actionHandlers.remove(actionHandler);

		if (actionHandlers.isEmpty()) {
			actionHandlers = null;
			actionMapper = null;
		}

		requestRepaint();
	}

	/* Property value change listening support **************************** */

	/**
	 * @see org.millstone.base.data.Property.ValueChangeListener#valueChange(Property.ValueChangeEvent)
	 */
	public void valueChange(Property.ValueChangeEvent event) {
		super.valueChange(event);
		requestRepaint();
	}

	/**
	 * @see org.millstone.base.ui.Component#attach()
	 */
	public void attach() {
		super.attach();

		if (visibleComponents != null)
			for (Iterator i = visibleComponents.iterator(); i.hasNext();)
				 ((Component) i.next()).attach();
	}

	/**
	 * @see org.millstone.base.ui.Component#attach()
	 */
	public void detach() {
		super.detach();

		if (visibleComponents != null)
			for (Iterator i = visibleComponents.iterator(); i.hasNext();)
				 ((Component) i.next()).detach();
	}

	/**
	 * @see org.millstone.base.data.Container#removeAllItems()
	 */
	public boolean removeAllItems() {
		this.currentPageFirstItemId = null;
		this.currentPageFirstItemIndex = 0;
		return super.removeAllItems();
	}

	/**
	 * @see org.millstone.base.data.Container#removeItem(Object)
	 */
	public boolean removeItem(Object itemId) {
		Object nextItemId = ((Container.Ordered) items).nextItemId(itemId);
		boolean ret = super.removeItem(itemId);
		if (ret
			&& (itemId != null)
			&& (itemId.equals(this.currentPageFirstItemId))) {
			this.currentPageFirstItemId = nextItemId;
		}
		return ret;
	}

	/**
	 * @see org.millstone.base.data.Container#removeContainerProperty(Object)
	 */
	public boolean removeContainerProperty(Object propertyId)
		throws UnsupportedOperationException {

		// If a visible property is removed, remove the correcponding column
		if (propertyId != null) {
			int count = 0;
			for (int i = 0; i < visibleColumns.length; i++)
				if (propertyId.equals(visibleColumns[i]))
					count++;
			if (count > 0) {
				Object[] newVisCol = new Object[visibleColumns.length - count];
				int index = 0;
				for (int i = 0; i < visibleColumns.length; i++)
					if (!propertyId.equals(visibleColumns[i]))
						newVisCol[index++] = visibleColumns[i];
				setVisibleColumns(newVisCol);
			}
		}

		return super.removeContainerProperty(propertyId);
	}

	/** Adds a new property to the table and show it as a visible column.
	 * 
	 * @see org.millstone.base.data.Container#addContainerProperty(Object, Class, Object)
	 * 
	 * @param propertyId Id of the proprty
	 * @param type The class of the property
	 * @param defaultValue The default value given for all existing items
	 */
	public boolean addContainerProperty(
		Object propertyId,
		Class type,
		Object defaultValue)
		throws UnsupportedOperationException {
		if (!super.addContainerProperty(propertyId, type, defaultValue))
			return false;
		int lastcol = visibleColumns.length;
		Object[] newVisCol = new Object[lastcol + 1];
		for (int i = 0; i < visibleColumns.length; i++)
			newVisCol[i] = visibleColumns[i];
		newVisCol[lastcol] = propertyId;
		setVisibleColumns(newVisCol);
		return true;
	}

	/** Adds a new property to the table and show it as a visible column.
		 * 
		 * @see org.millstone.base.data.Container#addContainerProperty(Object, Class, Object)
		 * 
		 * @param propertyId Id of the proprty
		 * @param type The class of the property
		 * @param defaultValue The default value given for all existing items
		 * @param columnHeader Explicit header of the column. If explicit header is not needed, this should be set null.
		 * @param columnIcon Icon of the column. If icon is not needed, this should be set null.
		 * @param columnAlignment Alignment of the column. Null implies align left.
		 */
	public boolean addContainerProperty(
		Object propertyId,
		Class type,
		Object defaultValue,
		String columnHeader,
		Resource columnIcon,
		String columnAlignment)
		throws UnsupportedOperationException {
		if (!this.addContainerProperty(propertyId, type, defaultValue))
			return false;
		int lastcol = visibleColumns.length - 1;
		columnHeaders[lastcol] = columnHeader;
		columnIcons[lastcol] = columnIcon;
		String[] a = getColumnAlignments();
		a[lastcol] = columnAlignment;
		setColumnAlignments(a);
		return true;
	}

	/** Return list of items on the current page
	 * @see org.millstone.base.ui.Select#getVisibleItemIds()
	 */
	public Collection getVisibleItemIds() {

		LinkedList visible = new LinkedList();

		Object[][] cells = getVisibleCells();
		for (int i = 0; i < cells[CELL_ITEMID].length; i++)
			visible.add(cells[CELL_ITEMID][i]);

		return visible;
	}

	/** Container datasource item set change. Table must flush its buffers on change.
	 * @see org.millstone.base.data.Container.ItemSetChangeListener#containerItemSetChange(org.millstone.base.data.Container.ItemSetChangeEvent)
	 */
	public void containerItemSetChange(Container.ItemSetChangeEvent event) {
		pageBuffer = null;
		super.containerItemSetChange(event);
		setCurrentPageFirstItemIndex(this.getCurrentPageFirstItemIndex());
	}

	/** Container datasource property set change. Table must flush its buffers on change.
	 * @see org.millstone.base.data.Container.PropertySetChangeListener#containerPropertySetChange(org.millstone.base.data.Container.PropertySetChangeEvent)
	 */
	public void containerPropertySetChange(
		Container.PropertySetChangeEvent event) {
		pageBuffer = null;
		super.containerPropertySetChange(event);
	}

	/** Adding new items is not supported. 
	 * @see org.millstone.base.ui.Select#setNewItemsAllowed(boolean)
	 * @throws UnsupportedOperationException if set to true.
	 */
	public void setNewItemsAllowed(boolean allowNewOptions)
		throws UnsupportedOperationException {
		if (allowNewOptions)
			throw new UnsupportedOperationException();
	}

	/** Focusing to this component is not supported.
	 * @see org.millstone.base.ui.AbstractField#focus()
	 * @throws UnsupportedOperationException if invoked.
	 */
	public void focus() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.millstone.base.data.Container.Ordered#nextItemId(java.lang.Object)
	 */
	public Object nextItemId(Object itemId) {
		return ((Container.Ordered) items).nextItemId(itemId);
	}

	/**
	 * @see org.millstone.base.data.Container.Ordered#prevItemId(java.lang.Object)
	 */
	public Object prevItemId(Object itemId) {
		return ((Container.Ordered) items).prevItemId(itemId);
	}

	/**
	 * @see org.millstone.base.data.Container.Ordered#firstItemId()
	 */
	public Object firstItemId() {
		return ((Container.Ordered) items).firstItemId();
	}

	/**
	 * @see org.millstone.base.data.Container.Ordered#lastItemId()
	 */
	public Object lastItemId() {
		return ((Container.Ordered) items).lastItemId();
	}

	/**
	 * @see org.millstone.base.data.Container.Ordered#isFirstId(java.lang.Object)
	 */
	public boolean isFirstId(Object itemId) {
		return ((Container.Ordered) items).isFirstId(itemId);
	}

	/**
	 * @see org.millstone.base.data.Container.Ordered#isLastId(java.lang.Object)
	 */
	public boolean isLastId(Object itemId) {
		return ((Container.Ordered) items).isLastId(itemId);
	}

	/**
	 * @see org.millstone.base.data.Container.Ordered#addItemAfter(java.lang.Object)
	 */
	public Object addItemAfter(Object previousItemId)
		throws UnsupportedOperationException {
		return ((Container.Ordered) items).addItemAfter(previousItemId);
	}

	/**
	 * @see org.millstone.base.data.Container.Ordered#addItemAfter(java.lang.Object, java.lang.Object)
	 */
	public Item addItemAfter(Object previousItemId, Object newItemId)
		throws UnsupportedOperationException {
		return ((Container.Ordered) items).addItemAfter(
			previousItemId,
			newItemId);
	}

	/** Get the FieldFactory that is used to create editor for table
	 * cells.
	 * 
	 * The FieldFactory is only used if the Table is editable.
	 * 
	 * @see #isEditable
	 * @return FieldFactory used to create the Field instances.
	 */
	public FieldFactory getFieldFactory() {
		return fieldFactory;
	}

	/** Set the FieldFactory that is used to create editor for table
	 * cells.
	 * 
	 * The FieldFactory is only used if the Table is editable.
	 * By default the BaseFieldFactory is used.
	 * 
	 * @see #isEditable
	 * @see BaseFieldFactory
	 * @param fieldFactory The field factory to set
	 */
	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	/** Is table editable.
	 * 
	 * If table is editable a editor of type Field is
	 * created for each table cell. The assigned FieldFactory is
	 * used to create the instances.
	 * 
	 * To provide custom editors for table cells create a
	 * class implementins the FieldFactory interface, and assign
	 * it to table, and set the editable property to true.
	 * 
	 * @see Field
	 * @see FieldFactory
	 * @return true if table is editable, false oterwise.
	 */
	public boolean isEditable() {
		return editable;
	}

	/** Set the editable property.
	 * 
	 * If table is editable a editor of type Field is
	 * created for each table cell. The assigned FieldFactory is
	 * used to create the instances.
	 * 
	 * To provide custom editors for table cells create a
	 * class implementins the FieldFactory interface, and assign
	 * it to table, and set the editable property to true.
	 * 
	 * @see Field
	 * @see FieldFactory
	 * @param editable true if table should be editable by user.
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
