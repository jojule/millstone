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

package org.millstone.ajaxadapter.browser;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.millstone.base.ui.Window;

/** Mozilla specific terminal implementation of terminal interface.
 * 
 * (stage drafting and planning)
 * 
 * @author IT Mill Ltd, Joonas Lehtinen
 */
public class Firefox extends WebBrowser {

    public void createAjaxClient(HttpServletRequest request,
            HttpServletResponse response, Window window) throws IOException {

        Writer w = response.getWriter();

        // TODO Unimplemented
        w.write("<html><head><title>" + window.getCaption()
                + "</title></head><body><h1>Firefox support is not yet done...</h1></body></html>");
        w.close();
    }}
