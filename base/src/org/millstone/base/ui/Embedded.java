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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.millstone.base.service.FileTypeResolver;
import org.millstone.base.terminal.DownloadStream;
import org.millstone.base.terminal.ExternalResource;
import org.millstone.base.terminal.PaintTarget;
import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.Resource;
import org.millstone.base.terminal.Sizeable;

/** Component for embedding external objects.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Embedded extends AbstractComponent implements Sizeable {

	/** General object type */
	public static final int TYPE_OBJECT = 0;

	/** Image types */
	public static final int TYPE_IMAGE = 1;

	/** Type of the object */
	private int type = TYPE_OBJECT;

	/** Source of the embedded object */
	private Resource source = new ExternalResource("");

	/** Dimensions of the object. */
	private int width = -1;
	private int height = -1;
	private int widthUnits = Sizeable.UNITS_PIXELS;
	private int heightUnits = Sizeable.UNITS_PIXELS;

	/** Generic object attributes */
	private String mimeType = null;
	private String standby = null;

	/** Hash of object parameteres.  */
	private Hashtable parameters = new Hashtable();

	/** Applet or other client side runnable properties. */
	private String codebase = null;
	private String codetype = null;
	private String classId = null;
	private String archive = null;
	private String usemap = null;

	/** Creates a new empty Embedded object.
	 */
	public Embedded() {
	}

	/** Creates a new empty Embedded object with caption.
	 */
	public Embedded(String caption) {
		setCaption(caption);
	}

	/** Creates a new Embedded object whose contents is loaded from given resource. 
	 * The dimensions are assumed if possible. The type is guessed from resource.
	 */
	public Embedded(String caption, Resource source) {
		setCaption(caption);
		setSource(source);
	}

	/** Get component UIDL tag.
	 * @return Component UIDL tag as string.
	 */
	public String getTag() {
		return "embedded";
	}

	/** Invoked when the component state should be painted  */
	public void paintContent(PaintTarget target) throws PaintException {

		if (type == TYPE_IMAGE) {
			target.addAttribute("type", "image");
		}
		
		target.addAttribute("src", source);
		// Dimensions
		if (width > 0)
			target.addAttribute(
				"width",
				"" + width + Sizeable.UNIT_SYMBOLS[this.widthUnits]);
		if (height > 0)
			target.addAttribute(
				"height",
				"" + height + Sizeable.UNIT_SYMBOLS[this.heightUnits]);
		if (mimeType != null && !"".equals(mimeType))
			target.addAttribute("mimetype", mimeType);
		if (classId != null && !"".equals(classId))
			target.addAttribute("classid", classId);
		if (codebase != null && !"".equals(codebase))
			target.addAttribute("codebase", codebase);
		if (codetype != null && !"".equals(codetype))
			target.addAttribute("codetype", codetype);
		if (standby != null && !"".equals(standby))
			target.addAttribute("standby", standby);
		if (archive != null && !"".equals(archive))
			target.addAttribute("archive", archive);
		if (usemap != null && !"".equals(usemap))
			target.addAttribute("usemap", usemap);

		// Params
		for (Iterator i = this.getParameterNames(); i.hasNext();) {
			target.startTag("embeddedparam");
			String key = (String) i.next();
			target.addAttribute("name", key);
			target.addAttribute("value", (String) getParameter(key));
			target.endTag("embeddedparam");
		}
	}

	/** Set an object parameter.
	 *  Parameters are optional information, and they are passed to the 
	 *  instantiated object. Parameters are are stored as name value pairs.
	 *  This overrides the previous value assigned to this parameter.
	 * @param name - The name of the parameter.
	 * @param value - The value of the parameter.
	 */
	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}

	/** Get the value of an object parameter.
	 *  Parameters are optional information, and they are passed to the 
	 *  instantiated object. Parameters are are stored as name value pairs.
	 * @return Value of parameter or null if not found.
	 */
	public String getParameter(String name) {
		return (String) parameters.get(name);
	}

	/** Remove an object parameter from the list.
	 * @param name - The name of the parameter to remove.
	 */
	public void removeParameter(String name) {
		parameters.remove(name);
	}

	/** Get embedded object parameter names.
	 * @return Iterator of parameters names.
	 */
	public Iterator getParameterNames() {
		return parameters.keySet().iterator();
	}

	/**
	 * Returns the codebase, the root of all relative path mentions.
	 * @return String
	 */
	public String getCodebase() {
		return codebase;
	}

	/**
	 * Returns the MIME-Type of the code.
	 * @return String
	 */
	public String getCodetype() {
		return codetype;
	}

	/**
	 * Returns the MIME-Type of the object
	 * @return String
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns the standby text displayed when
	 * the object is loading.
	 * @return String
	 */
	public String getStandby() {
		return standby;
	}

	/**
	 * Sets the codebase, the root used in all relative path mentions.
	 * @param codebase The codebase to set
	 */
	public void setCodebase(String codebase) {
		this.codebase = codebase;
	}

	/**
	 * Sets the codetype, the MIME-Type of the code.
	 * @param codetype The codetype to set
	 */
	public void setCodetype(String codetype) {
		this.codetype = codetype;
	}

	/**
	 * Sets the mimeType, the MIME-Type of the object.
	 * @param mimeType The mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Sets the standby, the text to display while loading the object.
	 * @param standby The standby to set
	 */
	public void setStandby(String standby) {
		this.standby = standby;
	}

	/**
	 * Returns the visual height of the object.
	 * Default height is -1, which is interpreted as "unspecified".
	 * @return The height in units specified by heightUnits property.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the visual width of the object.
	 * Default width is -1, which is interpreted as "unspecified".
	 * @return The width in units specified by widthUnits property.
	 */
	public int getWidth() {
		return width;
	}

	/** Sets the visual height of the object.
	 *  Default height is -1, which is interpreted as "unspecified".
	 * @param height The height in units specified by heightUnits property.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/** Sets the visual width of the object.
	 *  Default width is -1, which is interpreted as "unspecified".
	 * @param width The width in units specified by widthUnits property.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns the classId attribute.
	 * @return String
	 */
	public String getClassId() {
		return classId;
	}

	/**
	 * Sets the classId attribute.
	 * @param classId The classId to set
	 */
	public void setClassId(String classId) {
		this.classId = classId;
	}

	/** Get the resource contained in the embedded object.
	 * @return Resource
	 */
	public Resource getSource() {
		return source;
	}

	/** Get the type of the embedded object.
	 * <p>This can be one of the following:<ul>
	 * <li>TYPE_OBJECT <i>(This is the default)</i>
	 * <li>TYPE_IMAGE
	 * </ul>
	 * </p>
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/** Set the object source resource.
	 * The dimensions are assumed if possible. 
	 * The type is guessed from resource.
	 * @param source The source to set
	 */
	public void setSource(Resource source) {
		this.source = source;

		this.source = source;

		if (source != null) {
			String mt = source.getMIMEType();
			if ((mt.substring(0, mt.indexOf("/")).equalsIgnoreCase("image"))) {
				type = TYPE_IMAGE;
			} else {
				type = TYPE_OBJECT;
			}
		}
	}

	/** Sets the object type.
	 * <p>This can be one of the following:<ul>
	 * <li>TYPE_OBJECT <i>(This is the default)</i>
	 * <li>TYPE_IMAGE
	 * </ul>
	 * </p>
	 * @param type The type to set
	 */
	public void setType(int type) {
		if (type != TYPE_OBJECT && type != TYPE_IMAGE)
			throw new IllegalArgumentException("Unsupported type");
		this.type = type;
	}

	/**
	 * Returns the archive attribute.
	 * @return String
	 */
	public String getArchive() {
		return archive;
	}

	/**
	 * Sets the archive attribute.
	 * @param archive The archive string to set
	 */
	public void setArchive(String archive) {
		this.archive = archive;
	}

	/**
	 * Returns an URL to the used imagemap.
	 * @return String
	 */
	public String getUsemap() {
		return usemap;
	}

	/**
	 * Sets an URL to an imagemap to use.
	 * @param usemap The imagemap URL
	 */
	public void setUsemap(String usemap) {
		this.usemap = usemap;
	}

	/**Get height property units.
	 * Default units are <code>Sizeable.UNITS_PIXELS</code>.
	 * @see org.millstone.base.terminal.Sizeable#getHeightUnits()
	 */
	public int getHeightUnits() {
		return this.heightUnits;
	}

	/**Get width property units.
	 * Default units are <code>Sizeable.UNITS_PIXELS</code>.
	 * @see org.millstone.base.terminal.Sizeable#getWidthUnits()
	 */
	public int getWidthUnits() {
		return this.widthUnits;
	}

	/**Set height property units.
	 * @see org.millstone.base.terminal.Sizeable#setHeightUnits(int)
	 */
	public void setHeightUnits(int units) {
		if (units <= Sizeable.UNITS_PERCENTAGE) {
			this.heightUnits = units;
		}
	}

	/**Set width property units.
	 * @see org.millstone.base.terminal.Sizeable#setWidthUnits(int)
	 */
	public void setWidthUnits(int units) {
		if (units <= Sizeable.UNITS_PERCENTAGE) {
			this.widthUnits = units;
		}
	}

}
