/* **********************************************************************

 Millstone 
 Open Sourced User Interface Library for
 Internet Development with Java

 Millstone is a registered trademark of IT Mill Ltd
 Copyright 2000-2005 IT Mill Ltd

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

package org.millstone.ajaxadapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;

import org.millstone.base.terminal.SystemError;
import org.millstone.base.terminal.UploadStream;
import org.millstone.base.terminal.VariableOwner;

/**
 * @author IT Mill Ltd, Joonas Lehtinen
 */
public class VariableMap {

    // Id <-> (Owner,Name) mapping
    private Map idToNameMap = new HashMap();

    private Map idToTypeMap = new HashMap();

    private Map idToOwnerMap = new HashMap();

    private Map idToValueMap = new HashMap();

    private Map ownerToNameToIdMap = new WeakHashMap();

    private Object mapLock = new Object();

    // Id generator
    private long lastId = 0;

    /** Convert the string to a supported class */
    private static Object convert(Class type, String value)
            throws java.lang.ClassCastException {
        try {

            // Boolean typed variables
            if (type.equals(Boolean.class))
                return new Boolean(!(value.equals("") || value.equals("false")));

            // Integer typed variables
            if (type.equals(Integer.class))
                return new Integer(value.trim());

            // String typed variables
            if (type.equals(String.class))
                return value;

            throw new ClassCastException("Unsupported type: " + type.getName());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Register a new variable.
     * 
     * @return id to assigned for this variable.
     */
    public String registerVariable(String name, Class type, Object value,
            VariableOwner owner) {

        // Check that the type of the class is supported
        if (!(type.equals(Boolean.class) || type.equals(Integer.class)
                || type.equals(String.class) || type.equals(String[].class) || type
                .equals(UploadStream.class)))
            throw new SystemError("Unsupported variable type: "
                    + type.getClass());

        synchronized (mapLock) {

            // Check if the variable is already mapped
            HashMap nameToIdMap = (HashMap) ownerToNameToIdMap.get(owner);
            if (nameToIdMap == null) {
                nameToIdMap = new HashMap();
                ownerToNameToIdMap.put(owner, nameToIdMap);
            }
            String id = (String) nameToIdMap.get(name);

            if (id == null) {
                // Generate new id and register it
                id = "v" + String.valueOf(++lastId);
                nameToIdMap.put(name, id);
                idToOwnerMap.put(id, new WeakReference(owner));
                idToNameMap.put(id, name);
                idToTypeMap.put(id, type);
            }

            idToValueMap.put(id, value);

            return id;
        }
    }

    /** Unregisters a variable. */
    public void unregisterVariable(String name, VariableOwner owner) {

        synchronized (mapLock) {

            // Get the id
            HashMap nameToIdMap = (HashMap) ownerToNameToIdMap.get(owner);
            if (nameToIdMap == null)
                return;
            String id = (String) nameToIdMap.get(name);
            if (id != null)
                return;

            // Remove all the mappings
            nameToIdMap.remove(name);
            if (nameToIdMap.isEmpty())
                ownerToNameToIdMap.remove(owner);
            idToNameMap.remove(id);
            idToTypeMap.remove(id);
            idToValueMap.remove(id);
            idToOwnerMap.remove(id);

        }
    }

    /**
     * Handle all variable changes in this request.
     * 
     * @param req
     *            Http request to handle
     * @param listeners
     *            If the list is non null, only the listed listeners are served.
     *            Otherwise all the listeners are served.
     * @return Name to Value[] mapping of unhandled variables
     */
    public Map handleVariables(HttpServletRequest req) throws IOException {

        // TODO Variable handling is not implemented

        return new HashMap();
    }

}
