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

package org.millstone.webadapter;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import javax.servlet.http.HttpSession;

import org.millstone.base.service.ApplicationContext;

/** Web application context for Millstone applications.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.1
 */
public class WebApplicationContext implements ApplicationContext {

	HttpSession session;

	/** Create a new Web Application Context. */
	WebApplicationContext(HttpSession session) {
		this.session = session;
	}

	/* (non-Javadoc)
	 * @see org.millstone.base.service.ApplicationContext#getBaseDirectory()
	 */
	public File getBaseDirectory() {
		String realPath = session.getServletContext().getRealPath("/");
		return new File(realPath);
	}

	/** Get the http-session application is running in.
	 * 
	 * @return HttpSession this application context resides in
	 */
	public HttpSession getHttpSession() {
		return session;
	}

	/* (non-Javadoc)
	 * @see org.millstone.base.service.ApplicationContext#getApplications()
	 */
	public Collection getApplications() {
		LinkedList applications =
			(LinkedList) session.getAttribute(
				WebAdapterServlet.SESSION_ATTR_APPS);

		return Collections.unmodifiableCollection(
			applications == null ? (new LinkedList()) : applications);
	}
	
	/** Get application context for HttpSession.
	 * 
	 * @return application context for HttpSession.
	 */
	static public WebApplicationContext getApplicationContext(HttpSession session) {
		return new WebApplicationContext(session);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return session.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return session.hashCode();
	}

}
