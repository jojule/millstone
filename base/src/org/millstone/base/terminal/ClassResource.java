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

package org.millstone.base.terminal;

import java.io.InputStream;
import java.net.URL;

import org.millstone.base.Application;
import org.millstone.base.service.FileTypeResolver;
import sun.rmi.log.ReliableLog;

/** Class resource is a named resource accessed with the class loader.
 *  
 *  This can be used to access resources such as icons, files, etc.
 *  @see java.lang.Class#getResource(java.lang.String)
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class ClassResource implements ApplicationResource {

	private Class associatedClass;
	private String resourceName;
	private Application application;

	/** Create new application resource instance. 
	 * The resource id is relative to the location of the application class.
	 * 
	 * @param resourceName Unique identifier of the resource within the application.
	 * @param application The application this resource will be added to.
	 * */
	public ClassResource(String resourceName, Application application) {
		this.associatedClass = application.getClass();
		this.resourceName = resourceName;
		this.application = application;
		if (resourceName == null)
			throw new NullPointerException();
		application.addResource(this);
	}

	/** Create new application resource instance. 
	 * 
	 * @param associatedClass The class of the which the resource is associated.
	 * @param resourceName Unique identifier of the resource within the application.
	 * @param application The application this resource will be added to.
	 * */
	public ClassResource(
		Class associatedClass,
		String resourceName,
		Application application) {
		this.associatedClass = associatedClass;
		this.resourceName = resourceName;
		this.application = application;
		if (resourceName == null || associatedClass == null)
			throw new NullPointerException();
		application.addResource(this);
	}

	public String getMIMEType() {
		return FileTypeResolver.getMIMEType(this.resourceName);
	}

	public Application getApplication() {
		return application;
	}

	public String getFilename() {
		int index = 0;
		int next = 0;
		while ((next = resourceName.indexOf('/', index)) > 0
			&& next + 1 < resourceName.length())
			index = next + 1;
		return resourceName.substring(index);
	}

	public DownloadStream getStream() {
		return new DownloadStream(
			associatedClass.getResourceAsStream(resourceName),
			getMIMEType(),
			getFilename());
	}

	/** Use default buffer size.
	 * @return Always returns 0.
	 */
	public int getBufferSize() {
		return 0;
	}

}
