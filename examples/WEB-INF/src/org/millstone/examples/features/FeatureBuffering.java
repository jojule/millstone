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

package org.millstone.examples.features;

public class FeatureBuffering extends Feature {
	protected String getExampleSrc() {
		return super.getExampleSrc();
	}

	protected String getTitle() {
		return "Buffering";
	}
	/**
	 * @see org.millstone.examples.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "<p>Millstone data model provides interface for implementing "
			+ "buffering in data components. The basic idea is that a component "
			+ "reading their state from data source can implement "
			+ "Buffered-interface, for storing the value internally. "
			+ "Buffering provides transactional access "
			+ "for setting data: data can be put to components buffer and "
			+ "afterwards committed to or discarded from component data source. "
			+ "The buffering can be used both for creating interactive interfaces "
			+ "as well as caching the data for performance reasons.</p>"
			+ "<p>Buffered interface contains methods for committing and discarding "
			+ "changes to an object and support for controlling buffering mode "
			+ "with read-through and write-through modes. "
			+ "Read-through mode means that the value read from the buffered "
			+ "object is constantly up to date with the data source. "
			+ "Write-through mode means that all changes to the object are "
			+ "immediately updated to the data source.</p>";
	}

	/**
	 * @see org.millstone.examples.features.Feature#getImage()
	 */
	protected String getImage() {
		return "buffering.jpg";
	}

}
