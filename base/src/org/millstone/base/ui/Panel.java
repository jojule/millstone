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
import java.util.Map;

import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.PaintTarget;
import org.millstone.base.terminal.Sizeable;
import org.millstone.base.terminal.Scrollable;

/** Panel - a simple single component container.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Panel extends AbstractComponentContainer implements Sizeable, Scrollable {

    /* Predefined styles *********************************************** */

    /** Predefined style for panel with light emphasis.
     */
    public static final String STYLE_LIGHT = "light";

    /** Predefined style for panel with strong emphasis.
     */
    public static final String STYLE_STRONG = "strong";

    /** Layout of the panel */
    private Layout layout;
    
    /** Width of the panel or -1 if unspecified */
    private int width = -1;
    
    /** Height of the panel or -1 if unspecified */
    private int height = -1;
    
    /** Width unit */
    private int widthUnit = Sizeable.UNITS_PIXELS;

    /** Height unit */
    private int heightUnit = Sizeable.UNITS_PIXELS;

	/** Scroll X position */
	private int scrollOffsetX = 0;

	/** Scroll Y position */
	private int scrollOffsetY = 0;
	
	/** Scrolling mode */
	private boolean scrollable = false;

    /** Create new empty panel.
     *  Ordered layout is used.
     */
    public Panel() {
        this(new OrderedLayout());
    }
    
    /** Create new empty panel with given layout.
     * Layout must be non-null.
     *
     * @param layout The layout used in the panel.
     */
    public Panel(Layout layout) {
        setLayout(layout);
    }
    
    /** Create new empty panel with caption.
     * Ordered layout is used.
     *
     * @param caption The caption used in the panel.
     */
    public Panel(String caption) {
        this(caption,new OrderedLayout());
    }
    
    /** Create new empty panel with caption.
     *
     * @param caption The caption of the panel.
     * @param layout The layout used in the panel.
     */
    public Panel(String caption, Layout layout) {
        this(layout);
        setCaption(caption);
    }
    
    /** Get the current layout of the panel.
     * @return Current layout of the panel.
     */
    public Layout getLayout() {
        return this.layout;
    }
    
    /** Set the layout of the panel.
     * All the components are moved to new layout.
     *
     * @param layout New layout of the panel.
     */
    public void setLayout(Layout layout) {
        
        // Only allow non-null layouts
        if (layout == null)
            layout = new OrderedLayout();
        
        // Set the panel to be parent for the layout
        layout.setParent(this);
        dependsOn(layout);
        
        // If panel already contains a layout, move the contents to new one
        // and detach old layout from the panel
        if (this.layout != null) {
            layout.moveComponentsFrom(this.layout);
            removeDirectDependency(this.layout);
            this.layout.setParent(null);
        }
        
        // Set the new layout
        this.layout = layout;
    }
    
    /** Paint the content of this component.
     * @param event PaintEvent.
     * @throws PaintException The paint operation failed.
     */
    public void paintContent(PaintTarget target) throws PaintException {
        layout.paint(target);
        target.addVariable(this, "height", getHeight());
        target.addVariable(this, "width", getWidth());
        if (isScrollable())
	        target.addVariable(this, "scrollleft", getScrollOffsetX());
    	    target.addVariable(this, "scrolldown", getScrollOffsetY());
    }
    
    /** Get component UIDL tag.
     * @return Component UIDL tag as string.
     */
    public String getTag() {
        return "panel";
    }
    
    /** Add a component into this container.
     * @param c The component to be added.
     */
    public void addComponent(Component c) {
        layout.addComponent(c);
        fireComponentAttachEvent(c);
        // No repaint request is made as we except the underlaying container to 
        // request repaints
    }
    
    /** Remove a component from this container.
     * @param c The component to be added.
     */
    public void removeComponent(Component c) {
        layout.removeComponent(c);
        fireComponentDetachEvent(c);
        // No repaint request is made as we except the underlaying container to 
        // request repaints
    }
    
    /** Get component container iterator for going trough all the components in the container.
     * @return Iterator of the components inside the container.
     */
    public Iterator getComponentIterator() {
        return layout.getComponentIterator();
    }
    
	/**
	 * Returns the height.
	 * @return int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the width.
	 * @return int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the height.
	 * @param height The height to set
	 */
	public void setHeight(int height) {
		this.height = height;
		requestRepaint();
	}

	/**
	 * Sets the width.
	 * @param width The width to set
	 */
	public void setWidth(int width) {
		this.width = width;
		requestRepaint();
	}

	/**
	 * @see org.millstone.base.terminal.VariableOwner#changeVariables(Object, Map)
	 */
	public void changeVariables(Object source, Map variables) {
		super.changeVariables(source, variables);
	
		// Get new size	
		Integer newWidth = (Integer) variables.get("width");
		Integer newHeight = (Integer) variables.get("height");
		if (newWidth != null && newWidth.intValue() != getWidth())
			setWidth(newWidth.intValue());
		if (newHeight != null && newHeight.intValue() != getHeight())
			setHeight(newHeight.intValue());
			
		// Scrolling
		Integer newScrollX = (Integer) variables.get("scrollleft");
		Integer newScrollY = (Integer) variables.get("scrolldown");
		if (newScrollX != null && newScrollX.intValue() != getScrollOffsetX())
			setScrollOffsetX(newScrollX.intValue());
		if (newScrollY != null && newScrollY.intValue() != getScrollOffsetY())
			setScrollOffsetY(newScrollY.intValue());
	}

	/**
	 * @see org.millstone.base.terminal.Sizeable#getHeightUnits()
	 */
	public int getHeightUnits() {
		return heightUnit;
	}

	/**
	 * @see org.millstone.base.terminal.Sizeable#getWidthUnits()
	 */
	public int getWidthUnits() {
		return widthUnit;
	}

	/**
	 * @see org.millstone.base.terminal.Sizeable#setHeightUnits(int)
	 */
	public void setHeightUnits(int units) {
		if (heightUnit != units) {
			heightUnit = units;
			requestRepaint();
		}
	}

	/**
	 * @see org.millstone.base.terminal.Sizeable#setWidthUnits(int)
	 */
	public void setWidthUnits(int units) {
		if (widthUnit != units) {
			widthUnit = units;
			requestRepaint();
		}
	}

	/* Scrolling functionality */

	/* Documented in interface */
	public int getScrollOffsetX() {
		return scrollOffsetX;
	}

	/* Documented in interface */
	public int getScrollOffsetY() {
		return scrollOffsetY;
	}

	/* Documented in interface */
	public boolean isScrollable() {
		return scrollable;
	}

	/* Documented in interface */
	public void setScrollable(boolean isScrollingEnabled) {
		if (scrollable != isScrollingEnabled) {
			scrollable = isScrollingEnabled;
			requestRepaint();	
		}
	}

	/* Documented in interface */
	public void setScrollOffsetX(int pixelsScrolledLeft) {
		if (pixelsScrolledLeft < 0)
			throw new IllegalArgumentException("Scroll offset must be at least 0");
		if (this.scrollOffsetX != pixelsScrolledLeft) {
			scrollOffsetX = pixelsScrolledLeft;	
			requestRepaint();
		}
	}

	/* Documented in interface */
	public void setScrollOffsetY(int pixelsScrolledDown) {
		if (pixelsScrolledDown < 0)
			throw new IllegalArgumentException("Scroll offset must be at least 0");
		if (this.scrollOffsetY != pixelsScrolledDown) {
			scrollOffsetY = pixelsScrolledDown;	
			requestRepaint();
		}
	}

}
