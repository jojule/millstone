/*******************************************************************************
 * 
 * Millstone Open Sourced User Interface Library for Internet Development with
 * Java
 * 
 * Millstone is a registered trademark of IT Mill Ltd Copyright 2000-2005 IT
 * Mill Ltd
 * 
 * ************************************************************************
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * ************************************************************************
 * 
 * For more information, contact:
 * 
 * IT Mill Ltd phone: +358 2 4802 7180 Ruukinkatu 2-4 fax: +358 2 4802 7181
 * 20540, Turku email: info@itmill.com Finland company www: www.itmill.com
 * 
 * Primary source for MillStone information and releases: www.millstone.org
 */
package org.millstone.ajaxadapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.millstone.base.Application;
import org.millstone.base.Application.WindowAttachEvent;
import org.millstone.base.Application.WindowDetachEvent;
import org.millstone.base.terminal.Paintable;
import org.millstone.base.terminal.Paintable.RepaintRequestEvent;

public class PaintListener implements Paintable.RepaintRequestListener,
        Application.WindowAttachListener, Application.WindowDetachListener {

    private Set dirtyPaintabletSet = new HashSet();

    private Set paintables = new HashSet();

    public synchronized String add(Paintable paintable) {

        if (!paintables.contains(paintable)) {
            // Listen visual changes
            paintable.addListener((Paintable.RepaintRequestListener) this);
            paintables.add(paintable);
        }
        return calculateCacheId(paintable);
    }

    public synchronized void remove(Paintable paintable) {

        // Stop listening visual changes
        paintable.removeListener((Paintable.RepaintRequestListener) this);
        paintables.remove(paintable);

    }

    public String getCacheId(Paintable paintable) {
        if (paintables.contains(paintable)) {
            return calculateCacheId(paintable);
        }
        return null;
    }

    private String calculateCacheId(Paintable paintable) {
        return "" + paintable.hashCode();
    }

    public synchronized Set getDirtyComponents() {
        return Collections.unmodifiableSet(dirtyPaintabletSet);

    }

    public synchronized void clearDirtyComponents() {
        dirtyPaintabletSet.clear();
    }

    public void repaintRequested(RepaintRequestEvent event) {
        dirtyPaintabletSet.add(event.getPaintable());
    }

    public void windowAttached(WindowAttachEvent event) {
        add(event.getWindow());
        dirtyPaintabletSet.add(event.getWindow());
    }

    public void windowDetached(WindowDetachEvent event) {
        remove(event.getWindow());
    }

    public void paintablePainted(Paintable p) {
        dirtyPaintabletSet.remove(p);
        p.requestRepaintRequests();
    }

    public boolean isDirty(Paintable paintable) {
        return (dirtyPaintabletSet.contains(paintable));
    }

}
