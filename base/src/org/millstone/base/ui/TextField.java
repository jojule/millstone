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

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.ErrorMessage;
import org.millstone.base.terminal.SystemError;
import org.millstone.base.terminal.PaintTarget;
import org.millstone.base.data.Property;

/** <p>A text editor component that can be bound to any bindable Property.
 * The text editor supports both multiline and single line modes, default
 * is one-line mode.</p>
 *
 * <p>Since <code>TextField</code> extends <code>AbstractField</code> it
 * implements the {@link org.millstone.base.data.Buffered} interface. A
 * <code>TextField</code> is in write-through mode by default, so
 * {@link org.millstone.base.ui.AbstractField#setWriteThrough(boolean)}
 * must be called to enable buffering.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class TextField extends AbstractField {

	/* Private members ************************************************* */

	/** Number of visible columns in the TextField. */
	private int columns = 0;

	/** Number of visible rows in a multiline TextField. Value 0 implies a
	 * single-line text-editor.
	 */
	private int rows = 0;

	/** Tells if word-wrapping should be used in multiline mode. */
	private boolean wordwrap = true;

	/** Tells if input is used to enter sensitive information that is
	 *  not echoed to display. Typically passwords. 
	 */
	private boolean secret = false;

	/* Constructors **************************************************** */

	/** Constructs an empty <code>TextField</code> with no caption. */
	public TextField() {
		setValue("");
	}

	/** Constructs an empty <code>TextField</code> with given caption. */
	public TextField(String caption) {
		setValue("");
		setCaption(caption);
	}

	/** Constructs a new <code>TextField</code> that's bound to the
	 * specified <code>Property</code> and has no caption.
	 * 
	 * @param dataSource the Property to be edited with this editor
	 */
	public TextField(Property dataSource) {
		setPropertyDataSource(dataSource);
	}

	/** Constructs a new <code>TextField</code> that's bound to the
	 * specified <code>Property</code> and has the given caption
	 * <code>String</code>.
	 * 
	 * @param caption caption <code>String</code> for the editor
	 * @param dataSource the Property to be edited with this editor
	 */
	public TextField(String caption, Property dataSource) {
		this(dataSource);
		setCaption(caption);
	}

	/** Constructs a new <code>TextField</code> with the given caption and
	 * initial text contents. The editor constructed this way will not be
	 * bound to a Property unless
	 * {@link org.millstone.base.data.Property.Viewer#setPropertyDataSource(Property)}
	 * is called to bind it.
	 * 
	 * @param caption caption <code>String</code> for the editor
	 * @param text initial text content of the editor
	 */
	public TextField(String caption, String value) {
		setValue(value);
		setCaption(caption);
	}

	/* Component basic features ********************************************* */

	/* Paint this component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		// Set secret attribute
		if (this.isSecret())
			target.addAttribute("secret", true);
			
		// Add the number of column and rows
		int c = getColumns();
		int r = getRows();
		if (c != 0)
			target.addAttribute("cols", String.valueOf(c));
		if (r != 0) {
			target.addAttribute("rows", String.valueOf(r));
			target.addAttribute("multiline", true);
			if (!wordwrap)
				target.addAttribute("wordwrap", false);
		}
		
		// Add content as variable
		target.addVariable(this, "text", (String) toString());
	}

	/* Gets the components UIDL tag string.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public String getTag() {
		return "textfield";
	}

	/* Invoked when a variable of the component changes.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void changeVariables(Object source, Map variables) {
		try {

			// Set the text
			if (variables.containsKey("text") && !isReadOnly()) {

				// Only do the setting if the string representation of the value
				// has been updated
				String newValue = (String) variables.get("text");
				String oldValue = toString();
				if (newValue != oldValue
					&& (newValue == null || !newValue.equals(oldValue)))
					setValue(newValue);

			}

		} catch (Throwable e) {
			if (e instanceof ErrorMessage)
				setComponentError((ErrorMessage) e);
			else
				setComponentError(new SystemError(e));
		}
	}

	/* Text field configuration ********************************************* */

	/** Gets the number of columns in the editor. If the number of columns
	 * is set 0, the actual number of displayed columns is determined
	 * implicitly by the adapter.
	 * 
	 * @param the number of columns for this editor
	 */
	public int getColumns() {
		return this.columns;
	}

	/** Sets the number of columns in the editor. If the number of columns
	 * is set 0, the actual number of displayed columns is determined
	 * implicitly by the adapter.
	 * 
	 * @return number of explicitly set columns
	 */
	public void setColumns(int columns) {
		if (columns < 0)
			columns = 0;
		this.columns = columns;
		requestRepaint();
	}

	/** Gets the number of rows in the editor. If the number of rows is set
	 * to 0, the actual number of displayed rows is determined implicitly by
	 * the adapter.
	 * 
	 * @return number of explicitly set rows
	 */
	public int getRows() {
		return this.rows;
	}

	/** Sets the number of rows in the editor. If the number of rows is set
	 * to 0, the actual number of displayed rows is determined implicitly by
	 * the adapter.
	 * 
	 * @param the number of rows for this editor
	 */
	public void setRows(int rows) {
		if (rows < 0)
			rows = 0;
		this.rows = rows;
		requestRepaint();
	}

	/** Tests if the editor is in word-wrap mode.
	 * 
	 * @return <code>true</code> if the component is in the word-wrap mode,
	 * <code>false</code> if not
	 */
	public boolean isWordwrap() {
		return this.wordwrap;
	}

	/** Sets the editor's word-wrap mode on or off. 
	 * 
	 * @param wordwrap boolean value specifying if the editor should be in
	 * word-wrap mode after the call or not.
	 */
	public void setWordwrap(boolean wordwrap) {
		this.wordwrap = wordwrap;
	}

	/* Property features **************************************************** */

	/* Gets the edited property's type.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Class getType() {
		return String.class;
	}
	/** Get the secret property on and off.
	 * If a field is used to enter secretinformation
	 * the information is not echoed to display.
	 * @return true if the field is used to enter secret information, false otherwise.
	 */
	public boolean isSecret() {
		return secret;
	}

	/** Set the secret property on and off.
	 * If a field is used to enter secretinformation
	 * the information is not echoed to display.
	 * @param secret value specifying if the field is used to enter secret information.
	 */
	public void setSecret(boolean secret) {
		this.secret = secret;
	}

}
