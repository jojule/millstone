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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementing connection between a web-browser and Millstone
 * Application.
 * 
 * (stage drafting and planning)
 * 
 * <h1>Paintable update format</h1>
 * 
 * <changes><change pid="271987">... UIDL XML FRAGMENT HERE ... </change>
 * <change pid="271987">... UIDL XML FRAGMENT HERE ... </change> <change
 * pid="271987">... UIDL XML FRAGMENT HERE ... </change> <change format="HTML"
 * paintableId="271987">. <![CDATA[... SERVER SIDE TRANSFORMED HTML HERE ...]]>
 * </change> </changes>
 * 
 * In the first phase, no server side-transforms are supported, so the format
 * attribute is omitted and only UIDL supported.
 * 
 * <h1>Request parameters</h1>
 * 
 * TBD
 * 
 * @author IT Mill Ltd, Joonas Lehtinen
 */
public class AjaxAdapterServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

    }

}
