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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.millstone.base.terminal.Terminal;
import org.millstone.base.ui.Window;

/**
 * General terminal implementation for all browsers supported by the Millstone
 * Ajax Adapter.
 * 
 * (stage drafting and planning)
 * 
 * @author IT Mill Ltd, Joonas Lehtinen
 */
public abstract class WebBrowser implements Terminal {

    /*
     * (non-Javadoc)
     * 
     * @see org.millstone.base.terminal.Terminal#getDefaultTheme()
     */
    public String getDefaultTheme() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.millstone.base.terminal.Terminal#getScreenWidth()
     */
    public int getScreenWidth() {
        // TODO Auto-generated method stub
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.millstone.base.terminal.Terminal#getScreenHeight()
     */
    public int getScreenHeight() {
        // TODO Auto-generated method stub
        return -1;
    }

    public static WebBrowser getBrowser(String userAgentHeader) {

        if (userAgentHeader.matches(".*Safari.*")) 
            return new Safari();
        if (userAgentHeader.matches(".*Internet Explorer.*")) 
            return new InternetExplorer();
        if (userAgentHeader.matches(".*Firefox.*")) 
            return new Firefox();
        
        return new UnsupportedBrowser(userAgentHeader);
    }

    public abstract void createAjaxClient(HttpServletRequest request,
            HttpServletResponse response, Window window) throws IOException;
}
