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

import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Hashtable;
import org.millstone.base.terminal.PaintTarget;
import org.millstone.base.terminal.PaintException;

/** <p>A container that consists of components with certain coordinates on a
 * grid. The grid has an initial width and height which are automatically
 * extended if components are added outside the grid's initial size.</p>
 * 
 * <p>Each component in a <code>GridLayout</code> uses a certain
 * {@link GridLayout.Area area} (x1,y1,x2,y2) from the grid. One should not
 * add components that would overlap with the existing components because in
 * such case an {@link OverlapsException} is thrown.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class GridLayout extends AbstractComponentContainer implements Layout {

	/** Initial grid x size */
	private int width = 0;

	/** Initial grid y size */
	private int height = 0;

	/** Minimum (leftmost) x coordinate at the grid */
	private int minX = 0;

	/** Minimum (upper) y coordinate at the grid */
	private int minY = 0;

	/** Maximum (rightmost) x coordinate at the grid. This can grow. */
	private int maxX = 0;

	/** Maximum (bottom) y coordinate at the grid. This can grow. */
	private int maxY = 0;

	/** Next y coordinate at the grid, when adding components without position. */
	private int nextY = 0;

	/** Contains all items that are placed on the grid.
	 * These are components with grid area definition.
	 *
	 * (In the future this could use the TreeMap instead of the LinkedList)
	 */
	private LinkedList areas = new LinkedList();

	/** Contains all component that are placed on the grid.
	 * 
	 */
	private LinkedList components = new LinkedList();

	/** Constructor for grid of given size.
	 * Note that grid's final size depends on the items that are added into the grid.
	 * Grid grows if you add components outside the grid's area.
	 * @param width Width of the grid.
	 * @param height Height of the grid.
	 */
	public GridLayout(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	/** Constructs an empty grid layout that is extended as needed. */
	public GridLayout() {
		this(0, 0);
	}

	/** <p>Adds a component with a specified area to the grid. The area the
	 * new component should take is defined by specifying the upper left
	 * corner (x1, y1) and the lower right corner (x2, y2) of the area.</p>
	 * 
	 * <p>If the new component overlaps with any of the existing components
	 * already present in the grid the operation will fail and an 
	 * {@link OverlapsException} is thrown.</p>
	 * 
	 * @param c The component to be added.
	 * @param x1 The X-coordinate of the upper left corner of the area
	 * <code>c</code> is supposed to occupy
	 * @param y1 The Y-coordinate of the upper left corner of the area
	 * <code>c</code> is supposed to occupy
	 * @param x2 The X-coordinate of the lower right corner of the area
	 * <code>c</code> is supposed to occupy
	 * @param y2 The Y-coordinate of the lower right corner of the area
	 * <code>c</code> is supposed to occupy
	 * @throws OverlapsException if the new component overlaps with any
	 * of the components already in the grid
	 */
	public void addComponent(Component c, int x1, int y1, int x2, int y2)
		throws OverlapsException {

		Area area = new Area(x1, y1, x2, y2);
		Item newItem = new Item(c, area);
		c.setParent(this);

		// Add newItem to grid
		// Check that newItem does not overlap with existing items
		checkExistingOverlaps(newItem);

		// Insert newItem to right place at the list
		// Respect top-down, left-right ordering
		Iterator i = areas.iterator();
		int correctIndex = 0;
		boolean found = false;

		while (!found && i.hasNext()) {
			Item existingItem = (Item) i.next();
			correctIndex = areas.indexOf(existingItem);
			if (newItem.getArea().getY1() > existingItem.getArea().getY1()) {
				// Continue
			} else if (
				newItem.getArea().getY1() == existingItem.getArea().getY1()) {
				// On the same row
				if (newItem.getArea().getX1()
					> existingItem.getArea().getX1()) {
					// But greater not found
				} else {
					// Stop, greater found
					found = true;
					continue;
				}
			} else {
				// Stop, greater found
				found = true;
				continue;
			}
		}

		if (!found) {
			this.areas.add(newItem);
			this.components.add(c);
			this.nextY = newItem.getArea().getY1();
		} else {
			this.areas.add(correctIndex, newItem);
			this.components.add(c);
			this.nextY = newItem.getArea().getY1();
		}

		//
		// Update used grid area coordinates.
		// Handles automatic grid resizing and crop (optional)
		//
		if (this.minX > area.getX1())
			this.minX = area.getX1();
		if (this.minY > area.getY1())
			this.minY = area.getY1();
		if (this.maxX < area.getX2())
			this.maxX = area.getX2();
		if (this.maxY < area.getY2())
			this.maxY = area.getY2();
			
		fireComponentAttachEvent(c);
		requestRepaint();
	}

	/** Tests if the given item overlaps with any of the items already on
	 * the grid.
	 * 
	 * @param newItem Item to be checked for overlapping
	 * @throws OverlapsException if <code>newItem</code> overlaps with
	 * any existing item
	 */
	private void checkExistingOverlaps(Item newItem) throws OverlapsException {
		for (Iterator i = areas.iterator(); i.hasNext();) {
			Item existingItem = (Item) i.next();
			if ((existingItem.getArea().overlaps(newItem.getArea()))) {
				// Component not added, overlaps with existing component
				throw new OverlapsException(existingItem);
			}
		}
	}

	/** Add component into this container to coordinates x1,y1 (NortWest corner of the area.)
	 * End coordinates (SouthEast corner of the area) are the same as x1,y1. Component width
	 * and height is 1.
	 * @param c The component to be added.
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 */
	public void addComponent(Component c, int x, int y) {
		this.addComponent(c, x, y, x, y);
	}

	/** Force the next component to be added to the beginning of the next line.
	 *  By calling this function user can ensure that no more components are
	 *  added to the right of the previous component.
	 * @see #addComponent(Component)
	 * @param c The component to be added.
	 */
	public void newLine() {
		this.nextY = (this.nextY <= this.minY) ? this.minY + 1 : this.nextY + 1;
	}

	/** Add a component into this container to first available place on the grid.
	 * Suitable place is searched as left to right and top to down order.
	 * Primarily add to right side region if there is space, otherwise add to bottom region.
	 * If grid has all cells reserved, then grid height is
	 * enlarged by one and component is added to bottom of the grid.
	 * Component's width and height is 1.
	 * @param c The component to be added.
	 */
	public void addComponent(Component c) {

		// Find first available place from the grid

		// Iterate every row
		int beginY = this.nextY > this.minY ? this.nextY : this.minY;
		for (int cury = beginY; cury <= maxY; cury++) {
			// Iterate every acolumn
			for (int curx = this.minX; curx <= maxX; curx++) {
				// Check if added component overlaps with existing items
				boolean overlaps = false;
				for (Iterator i = areas.iterator(); i.hasNext();) {
					Item existingItem = (Item) i.next();
					Area area = new Area(curx, cury, curx, cury);
					Item newItem = new Item(c, area);
					if ((existingItem.getArea().overlaps(newItem.getArea()))) {
						overlaps = true;
						continue;
					}
				}
				if (!overlaps) {
					this.addComponent(c, curx, cury, curx, cury);
					return;
				}
			}
		}

		// All places from the grid iterated, no space found

		// Add component to bottom in the grid,
		// enlarges the grid by one.
		this.addComponent(c, 0, this.maxY + 1, 0, this.maxY + 1);
	}

	/** Removes the first occurence of a given component from this
	 * container.
	 * 
	 * @param c The component to be removed.
	 */
	public void removeComponent(Component c) {
		for (Iterator i = areas.iterator(); i.hasNext();) {
			Item item = (Item) i.next();
			if (item.getComponent() == c) {
				this.areas.remove(item);
				this.components.remove(item);
				fireComponentDetachEvent(c);
				requestRepaint();
				return;
			}
		}
	}

	/** Removes a component specified with it's top-left corner coordinates
	 * from this grid.
	 * 
	 * @param x Component's top-left corner's X-coordinate
	 * @param y Component's top-left corner's Y-coordinate
	 */
	public void removeComponent(int x, int y) {
		for (Iterator i = areas.iterator(); i.hasNext();) {
			Item item = (Item) i.next();
			if ((item.getArea().getX1() == x)
				&& (item.getArea().getY1() == y)) {
				this.areas.remove(item);
				this.components.remove(item);
				fireComponentDetachEvent(item.getComponent());
				requestRepaint();
				return;
			}
		}
	}

	/** Removes all components from the grid and resets it to it's initial
	 * width and height.
	 */
	public void removeAllComponents() {

		// Remove components
		super.removeAllComponents();

		// Reset grid width and height
		this.minX = 0;
		this.minY = 0;
		this.maxX = this.width > 0 ? this.width - 1 : 0;
		this.maxY = this.height > 0 ? this.height - 1 : 0;
	}

	/** Gets an Iterator to the component container contents. Using the
	 * Iterator it's possible to step through the contents of the container.
	 * 
	 * @return Iterator of the components inside the container.
	 */
	public Iterator getComponentIterator() {
		return components.iterator();
	}

	/** Paints the contents of this component.
	 * 
	 * @param event PaintEvent.
	 * @throws PaintException The paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		//
		// Construct grid
		//

		target.addAttribute("h", maxY - minY + 1);
		target.addAttribute("w", maxX - minX + 1);

		// Current item to be processed (fetch first item)
		Iterator i = areas.iterator();
		// Item contains Component and Area
		Item item = null;
		if (i.hasNext()) {
			item = (Item) i.next();
		}

		// Collect rowspan related information here
		// Fix!!! use linkedList e.g.
		Hashtable cellUsed = new Hashtable();

		// Empty cell collector
		int emptyCells = 0;

		// Iterate every applicable row
		for (int cury = this.minY; cury <= maxY; cury++) {
			target.startTag("gr");

			// Iterate every applicable column
			for (int curx = this.minX; curx <= maxX; curx++) {
				// Check if current item is located at curx,cury
				if (item != null
					&& (item.area.getY1() == cury)
					&& (item.area.getX1() == curx)) {
					// Write current item at current x,y position

					// But first check if empty cell needs to be rendered
					if (emptyCells > 0) {
						target.startTag("gc");
						if (emptyCells > 1) {
							target.addAttribute("w", emptyCells);
						}
						target.endTag("gc");
						emptyCells = 0;
					}

					// Now proceed rendering current item
					int cols = (item.area.getX2() - item.area.getX1()) + 1;
					int rows = (item.area.getY2() - item.area.getY1()) + 1;
					target.startTag("gc");

					if (cols > 1) {
						target.addAttribute("w", cols);
					}
					if (rows > 1) {
						target.addAttribute("h", rows);
					}
					item.getComponent().paint(target);

					target.endTag("gc");

					// Update cellUsed if rowspan needed
					if (rows > 1) {
						int spannedx = curx;
						for (int j = 1; j <= cols; j++) {
							cellUsed.put(
								new Integer(spannedx),
								new Integer(cury + rows - 1));
							spannedx++;
						}
					}

					// Skip current item's spanned columns
					if (cols > 1) {
						curx += cols - 1;
					}

				} else {
					// Check against cellUsed, render space or ignore cell
					if (cellUsed.containsKey(new Integer(curx))) {
						// Current column contains already an item,
						// check if rowspan affects at current x,y position
						int rowspanDepth =
							((Integer) cellUsed.get(new Integer(curx)))
								.intValue();

						if (rowspanDepth >= cury) {
							// ignore cell
							// Check if empty cell needs to be rendered
							if (emptyCells > 0) {
								target.startTag("gc");
								if (emptyCells > 1) {
									target.addAttribute("w", emptyCells);
								}
								target.endTag("gc");

								emptyCells = 0;
							}
						} else {
							// empty cell is needed
							emptyCells++;
							// Remove cellUsed key as it has become obsolete
							cellUsed.remove(new Integer(curx));
						}
					} else {
						// empty cell is needed
						emptyCells++;
					}
				}

				// Fetch next item
				if (i.hasNext()) {
					item = (Item) i.next();
				} else {
					item = null;
				}

			} // iterate every column

			// Last column handled of current row

			// Check if empty cell needs to be rendered
			if (emptyCells > 0) {
				target.startTag("gc");
				if (emptyCells > 1) {
					target.addAttribute("w", emptyCells);
				}
				target.endTag("gc");

				emptyCells = 0;
			}

			target.endTag("gr");
		} // iterate every row

		// Last row handled
	}

	/** Gets the components UIDL tag.
	 * 
	 * @return Component UIDL tag as string.
	 * @see org.millstone.base.ui.AbstractComponent#getTag()
	 */
	public String getTag() {
		return "gridlayout";
	}

	/** This class defines an area on a grid. An Area is defined by the
	 * coordinates of its upper left corner (x1,y1) and lower right corner
	 * (x2,y2)
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class Area {

		/** X-coordinate of the upper left corner of the area */
		private int x1;

		/** Y-coordinate of the upper left corner of the area */
		private int y1;

		/** X-coordinate of the lower right corner of the area */
		private int x2;

		/** Y-coordinate of the lower right corner of the area */
		private int y2;

		/** <p>Construct a new area on a grid.
		 * 
		 * @param x1 The X-coordinate of the upper left corner of the area
		 * <code>c</code> is supposed to occupy
		 * @param y1 The Y-coordinate of the upper left corner of the area
		 * <code>c</code> is supposed to occupy
		 * @param x2 The X-coordinate of the lower right corner of the area
		 * <code>c</code> is supposed to occupy
		 * @param y2 The Y-coordinate of the lower right corner of the area
		 * <code>c</code> is supposed to occupy
		 * @throws OverlapsException if the new component overlaps with any
		 * of the components already in the grid
		 */
		public Area(int x1, int y1, int x2, int y2) {
			this.setX1(x1);
			this.setY1(y1);
			this.setX2(x2);
			this.setY2(y2);
		}

		/** Constructs a new Area at a specified location with width and
		 * height of 1.
		 * 
		 * @param x1 X-coordinate of the upper left corner of the new Area
		 * @param y1 Y-coordinate of the upper left corner of the new Area
		 */
		public Area(int x1, int y1) {
			this(x1, y1, x1, y1);
		}

		/** Tests if the given Area overlaps with an another Area.
		 * 
		 * @param other Another Area that's to be tested for overlap with
		 * this area
		 * @return <code>true</code> if <code>other</code> overlaps with
		 * this area, <code>false</code> if it doesn't
		 */
		public boolean overlaps(Area other) {
			if ((this.getX1() <= other.getX2())
				&& (other.getX1() <= this.getX2())
				&& (other.getY1() <= this.getY2())
				&& (this.getY1() <= other.getY2())) {
				return true;
			} else {
				return false;
			}
		}

		/** Gets the X-coordinate of the upper left corner of the Area.
		 * 
		 * @return X-coordinate of the upper left corner of the Area
		 */
		public int getX1() {
			return this.x1;
		}

		/** Gets the Y-coordinate of the upper left corner of the Area.
		 * 
		 * @return Y-coordinate of the upper left corner of the Area
		 */
		public int getY1() {
			return this.y1;
		}

		/** Gets the X-coordinate of the lower right corner of the Area.
		 * 
		 * @return X-coordinate of the lower right corner of the Area.
		 */
		public int getX2() {
			return this.x2;
		}

		/** Gets the Y-coordinate of the lower right corner of the Area.
		 * 
		 * @return Y-coordinate of the lower right corner of the Area.
		 */
		public int getY2() {
			return this.y2;
		}

		/** Method setX1.
		 * @param x1
		 */
		private void setX1(int x1) {
			this.x1 = x1;
		}

		/** Method setY1.
		 * @param y1
		 */
		private void setY1(int y1) {
			this.y1 = y1;
		}

		/** Method setX2.
		 * @param x2
		 */
		private void setX2(int x2) {
			this.x2 = x2;
		}

		/** Method setY2.
		 * @param y2
		 */
		private void setY2(int y2) {
			this.y2 = y2;
		}

	}

	/** An UI component wrapper that is placed on in an Area inside a grid.
	 * 
	 * @see org.millstone.base.ui.Component Component
	 * @see org.millstone.base.ui.GridLayout.Area Area
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class Item {
		private Area area;
		private Component component;

		/** Constructs a new Item that can be added into a grid.
		 * 
		 * @param component Component to be added as an Item
		 * @param area Area on the grid
		 */
		public Item(Component component, Area area) {
			this.component = component;
			this.area = area;
		}

		/** Gets the Area this Item belongs to.
		 * 
		 * @return the Area this item belongs to
		 */
		public Area getArea() {
			return this.area;
		}

		/** Gets the component this Item wraps.
		 * 
		 * @return the component this Item wraps
		 */
		public Component getComponent() {
			return this.component;
		}
	}

	/** An <code>Exception</code> object which is thrown when two Items
	 * occupy the same space on a grid
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class OverlapsException extends java.lang.RuntimeException {

		private Exception originalException = null;
		private Item existingItem = null;

		/** Constructs an <code>OverlapsException</code> with the specified
		 * detail message.
		 * 
		 * @param msg the detail message.
		 */
		public OverlapsException(Item existingItem) {
			super();
			this.existingItem = existingItem;
		}

		/** Returns the Item that is the cause of the exception
		 * 
		 * @return the overlapping Item
		 */
		public Item getOverlappingItem() {
			return this.existingItem;
		}
	}

	/** Set the width of the grid.
	 * @param width - The amount of cells across the grid.
	 */
	public void setWidth(int width) {
		this.width = width;
		this.maxX = width > 0 ? width - 1 : 0;
	}

	/** Get the width of the grids.
	 * @return int - how many cells across.
	 */
	public int getWidth() {
		return this.width;
	}

	/** Set the height of the grid.
	 * @param height - The amount of cells from top to bottom in the grid.
	 */
	public void setHeight(int height) {
		this.height = height;
		this.maxY = height > 0 ? height - 1 : 0;
	}

	/** Get the height of the grid.
	 * @return int - how many cells high the grid is
	 */
	public int getHeight() {
		return this.height;
	}
}
