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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;

import org.millstone.base.terminal.*;
import org.millstone.base.terminal.PaintTarget;
import org.millstone.base.terminal.KeyMapper;
import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.VariableOwner;

/** Tabsheet component. 
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class TabSheet extends AbstractComponentContainer {

	/** Linked list of component tabs */
	private LinkedList tabs = new LinkedList();

	/** Tab -> caption mapping */
	private Hashtable tabCaptions = new Hashtable();

	/** Tab -> icon mapping */
	private Hashtable tabIcons = new Hashtable();

	/** Selected tab */
	private Component selected = null;
	private KeyMapper keyMapper = new KeyMapper();

	/** Holds value of property tabsHIdden. */
	private boolean tabsHidden;

	/** Get component container iterator for going trough all the components in the container.
	 * @return Iterator of the components inside the container.
	 */
	public Iterator getComponentIterator() {
		return java.util.Collections.unmodifiableList(tabs).iterator();
	}

	/** Remove a component from this container.
	 * @param c The component to be removed.
	 */
	public void removeComponent(Component c) {
		if (c != null && tabs.contains(c)) {
			super.removeComponent(c);
			tabs.remove(c);
			tabCaptions.remove(c);
			if (c.equals(selected))
				selected = (Component) tabs.getFirst();
			requestRepaint();
		}
	}

	/** Add a component into this container.
	 * The component is added as a tab where its default tab-caption is 
	 * the caption of the component.
	 * @param c The component to be added.
	 */
	public void addComponent(Component c) {
		addTab(c, c.getCaption(), getIcon());
	}

	/** Add a new tab into TabSheet.
	 * @param c The component to be added onto tab.
	 * @param caption The caption of the tab.
	 * @param icon Set the icon of the tab.
	 */
	public void addTab(Component c, String caption, Resource icon) {
		if (c != null) {
			tabs.addLast(c);
			tabCaptions.put(c, caption != null ? caption : "");
			if (icon != null)
				tabIcons.put(c, icon);
			if (selected == null)
				selected = c;
			super.addComponent(c);
			requestRepaint();
		}
	}

	/** Get component UIDL tag.
	 * @return Component UIDL tag as string.
	 */
	public String getTag() {
		return "tabsheet";
	}

	/** Move all components from another container to this container.
	 * The components are removed from the other container.
	 * @param source The container components are removed from.
	 */
	public void moveComponentsFrom(ComponentContainer source) {
		for (Iterator i = source.getComponentIterator(); i.hasNext();) {
			Component c = (Component) i.next();
			String caption = null;
			Resource icon = null;
			if (TabSheet.class.isAssignableFrom(source.getClass())) {
				caption = ((TabSheet) source).getTabCaption(c);
				icon = ((TabSheet) source).getTabIcon(c);
			}
			source.removeComponent(c);
			addTab(c, caption, icon);

		}
	}

	/** Paint the content of this component.
	 * @param event PaintEvent.
	 * @throws PaintException The paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		if (areTabsHidden())
			target.addAttribute("hidetabs", true);

		target.startTag("tabs");

		for (Iterator i = getComponentIterator(); i.hasNext();) {
			Component c = (Component) i.next();
			target.startTag("tab");
			Resource icon = getTabIcon(c);
			if (icon != null)
				target.addAttribute("icon", icon);
			String caption = getTabCaption(c);
			if (caption != null && caption.length() > 0)
				target.addAttribute("caption", caption);
			target.addAttribute("key", keyMapper.key(c));
			if (c.equals(selected)) {
				target.addAttribute("selected", true);
				c.paint(target);
			}
			target.endTag("tab");
		}

		target.endTag("tabs");

		if (selected != null)
			target.addVariable(this, "selected", keyMapper.key(selected));
	}

	/** Are tabs hidden.
	 * @return Property visibility
	 */
	public boolean areTabsHidden() {
		return this.tabsHidden;
	}

	/** Setter for property tabsHidden.
	 * @param tabsHidden True if the tabs should be hidden.
	 */
	public void hideTabs(boolean tabsHidden) {
		this.tabsHidden = tabsHidden;
		requestRepaint();
	}

	/** Get the caption for a component */
	public String getTabCaption(Component c) {
		String caption = (String) tabCaptions.get(c);
		if (caption == null)
			caption = "";
		return caption;
	}

	/** Set the caption for a component */
	public void setTabCaption(Component c, String caption) {
		tabCaptions.put(c, caption);
		requestRepaint();
	}

	/** Get the icon for a component */
	public Resource getTabIcon(Component c) {
		return (Resource) tabIcons.get(c);
	}

	/** Set the icon for a component */
	public void setTabIcon(Component c, Resource icon) {
		if (icon == null)
			tabIcons.remove(c);
		else
			tabIcons.put(c, icon);
		requestRepaint();
	}

	/** Set the selected tab */
	public void setSelectedTab(Component c) {
		if (c != null && tabs.contains(c) && !selected.equals(c)) {
			selected = c;
			requestRepaint();
		}
	}

	/** Get the selected tab */
	public Component getSelectedTab() {
		return selected;
	}

	/** Invoked when the value of a variable has changed.
	 * @param event Variable change event containing the information about
	 * the changed variable.
	 */
	public void changeVariables(Object source, Map variables) {
		try {
			if (variables.containsKey("selected"))
				setSelectedTab(
					(Component) keyMapper.get(
						(String) variables.get("selected")));
		} catch (Throwable e) {
			setComponentError(new SystemError(e));
		}
	}

	/* Documented in superclass */
	public void replaceComponent(
		Component oldComponent,
		Component newComponent) {

		// Get the captions
		String oldCaption = getTabCaption(oldComponent);
		Resource oldIcon = getTabIcon(oldComponent);
		String newCaption = getTabCaption(newComponent);
		Resource newIcon = getTabIcon(newComponent);
	
		// Get the locations			
		int oldLocation = -1;
		int newLocation  = -1;
		int location = 0;
		for (Iterator i=tabs.iterator(); i.hasNext();) {
			Component component = (Component) i.next();

			if (component == oldComponent) oldLocation = location;
			if (component == newComponent) newLocation = location;

			location++;
		}	

		if (oldLocation == -1)
			addComponent(newComponent);
		else if (newLocation == -1) {
			removeComponent(oldComponent);
			addComponent(newComponent);
			tabs.remove(newComponent);
			tabs.add(oldLocation,newComponent);
			setTabCaption(newComponent,oldCaption);
			setTabIcon(newComponent,oldIcon);
		} else {
			if (oldLocation > newLocation) {
				tabs.remove(oldComponent);
				tabs.add(newLocation,oldComponent);
				tabs.remove(newComponent);
				tabs.add(oldLocation,newComponent);	
			} else {
				tabs.remove(newComponent);
				tabs.add(oldLocation,newComponent);	
				tabs.remove(oldComponent);
				tabs.add(newLocation,oldComponent);
			}
			setTabCaption(newComponent,oldCaption);
			setTabIcon(newComponent,oldIcon);
			setTabCaption(oldComponent,newCaption);
			setTabIcon(oldComponent,newIcon);
			
			requestRepaint();
		}
	}
}
