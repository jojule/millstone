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

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.PaintTarget;
import org.millstone.base.data.Property;
import org.millstone.base.terminal.PaintException;
import org.millstone.base.terminal.ErrorMessage;
import org.millstone.base.terminal.SystemError;
import org.millstone.base.terminal.PaintTarget;

/** <p>A date editor component that can be bound to any bindable Property.
 * that is compatible with java.util.Date.
 *
 * <p>Since <code>DateField</code> extends <code>AbstractField</code> it
 * implements the {@link org.millstone.base.data.Buffered} interface. A
 * <code>DateField</code> is in write-through mode by default, so
 * {@link org.millstone.base.ui.AbstractField#setWriteThrough(boolean)}
 * must be called to enable buffering.</p>
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class DateField extends AbstractField {

	/* Private members ************************************************* */

	/** Resolution identifier: milliseconds */
	public static final int RESOLUTION_MSEC = 0;

	/** Resolution identifier: seconds. */
	public static final int RESOLUTION_SEC = 1;

	/** Resolution identifier: minutes. */
	public static final int RESOLUTION_MIN = 2;

	/** Resolution identifier: hours. */
	public static final int RESOLUTION_HOUR = 3;

	/** Resolution identifier: days. */
	public static final int RESOLUTION_DAY = 4;

	/** Resolution identifier: months. */
	public static final int RESOLUTION_MONTH = 5;

	/** Resolution identifier: years. */
	public static final int RESOLUTION_YEAR = 6;

	/** Specified smallest modifiable unit */
	private int resolution = RESOLUTION_MSEC;

	/** Specified largest modifiable unit */
	private static final int largestModifiable = RESOLUTION_YEAR;

	/* Constructors **************************************************** */

	/** Constructs an empty <code>DateField</code> with no caption. */
	public DateField() {
	}

	/** Constructs an empty <code>DateField</code> with caption.
	 * 
	 * @param caption The caption of the datefield.
	 */
	public DateField(String caption) {
		setCaption(caption);
	}

	/** Constructs a new <code>DateField</code> that's bound to the
	 * specified <code>Property</code> and has the given caption
	 * <code>String</code>.
	 * 
	 * @param caption caption <code>String</code> for the editor
	 * @param dataSource the Property to be edited with this editor
	 */
	public DateField(String caption, Property dataSource) {
		this(dataSource);
		setCaption(caption);
	}

	/** Constructs a new <code>DateField</code> that's bound to the
	 * specified <code>Property</code> and has no caption.
	 * 
	 * @param dataSource the Property to be edited with this editor
	 */
	public DateField(Property dataSource) throws IllegalArgumentException {
		if (!Date.class.isAssignableFrom(dataSource.getType()))
			throw new IllegalArgumentException(
				"Can't use "
					+ dataSource.getType().getName()
					+ " typed property as datasource");

		setPropertyDataSource(dataSource);
	}

	/** Constructs a new <code>DateField</code> with the given caption and
	 * initial text contents. The editor constructed this way will not be
	 * bound to a Property unless
	 * {@link org.millstone.base.data.Property.Viewer#setPropertyDataSource(Property)}
	 * is called to bind it.
	 * 
	 * @param caption caption <code>String</code> for the editor
	 * @param text initial text content of the editor
	 */
	public DateField(String caption, Date value) {
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

		// Print the value in gregorian calendar
		Calendar calendar = new GregorianCalendar();
		Date currentDate = (Date) getValue();
		if (currentDate != null)
			calendar.setTime(currentDate);
		for (int r = resolution; r <= largestModifiable; r++)
			switch (r) {
				case RESOLUTION_MSEC :
					target.addVariable(
						this,
						"msec",
						currentDate != null
							? calendar.get(Calendar.MILLISECOND)
							: -1);
					break;
				case RESOLUTION_SEC :
					target.addVariable(
						this,
						"sec",
						currentDate != null
							? calendar.get(Calendar.SECOND)
							: -1);
					break;
				case RESOLUTION_MIN :
					target.addVariable(
						this,
						"min",
						currentDate != null
							? calendar.get(Calendar.MINUTE)
							: -1);
					break;
				case RESOLUTION_HOUR :
					target.addVariable(
						this,
						"hour",
						currentDate != null
							? calendar.get(Calendar.HOUR_OF_DAY)
							: -1);
					break;
				case RESOLUTION_DAY :
					target.addVariable(
						this,
						"day",
						currentDate != null
							? calendar.get(Calendar.DAY_OF_MONTH)
							: -1);
					break;
				case RESOLUTION_MONTH :
					target.addVariable(
						this,
						"month",
						currentDate != null
							? calendar.get(Calendar.MONTH) + 1
							: -1);
					break;
				case RESOLUTION_YEAR :
					target.addVariable(
						this,
						"year",
						currentDate != null ? calendar.get(Calendar.YEAR) : -1);
					break;
			}
	}

	/* Gets the components UIDL tag string.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public String getTag() {
		return "datefield";
	}

	/* Invoked when a variable of the component changes.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void changeVariables(Object source, Map variables) {

		try {

			if (!isReadOnly()
				&& (variables.containsKey("year")
					|| variables.containsKey("month")
					|| variables.containsKey("day")
					|| variables.containsKey("hour")
					|| variables.containsKey("min")
					|| variables.containsKey("sec")
					|| variables.containsKey("msec"))) {

				// Old and new dates
				Date oldDate = (Date) getValue();
				Date newDate = null;

				// Get the new date in parts
				// Null values are converted to negative values.
				int year =
					variables.containsKey("year")
						? (variables.get("year") == null
							? -1
							: ((Integer) variables.get("year")).intValue())
						: 0;
				int month =
					variables.containsKey("month")
						? (variables.get("month") == null
							? -1
							: ((Integer) variables.get("month")).intValue() - 1)
						: 0;
				int day =
					variables.containsKey("day")
						? (variables.get("day") == null
							? -1
							: ((Integer) variables.get("day")).intValue())
						: 1;
				int hour =
					variables.containsKey("hour")
						? (variables.get("hour") == null
							? -1
							: ((Integer) variables.get("hour")).intValue())
						: 0;
				int min =
					variables.containsKey("min")
						? (variables.get("min") == null
							? -1
							: ((Integer) variables.get("min")).intValue())
						: 0;
				int sec =
					variables.containsKey("sec")
						? (variables.get("sec") == null
							? -1
							: ((Integer) variables.get("sec")).intValue())
						: 0;
				int msec =
					variables.containsKey("msec")
						? (variables.get("msec") == null
							? -1
							: ((Integer) variables.get("msec")).intValue())
						: 0;

				// If any of the components is < 0, the date should be null
				if (year < 0
					|| month < 0
					|| day < 0
					|| hour < 0
					|| min < 0
					|| sec < 0
					|| msec < 0)
					newDate = null;
				else {
					newDate =
						new GregorianCalendar(year, month, day, hour, min, sec)
							.getTime();
					if (msec > 0)
						newDate = new Date(newDate.getTime() + msec);
				}

				if (newDate != oldDate
					&& (newDate == null || !newDate.equals(oldDate)))
					setValue(newDate);
			}

		} catch (Throwable e) {
			if (e instanceof ErrorMessage)
				setComponentError((ErrorMessage) e);
			else
				setComponentError(new SystemError(e));
		}
	}

	/* Property features **************************************************** */

	/* Gets the edited property's type.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Class getType() {
		return Date.class;
	}

	/* Return the value of the property in human readable textual format.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public String toString() {
		Date value = (Date) getValue();
		return value.toString();
	}

	/* Set the value of the property.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void setValue(Object newValue)
		throws Property.ReadOnlyException, Property.ConversionException {

		// Allow setting dates directly
		if (newValue == null || newValue instanceof Date)
			super.setValue(newValue);
		else {

			// Try to parse as string
			try {
				SimpleDateFormat parser = new SimpleDateFormat();
				Date val = parser.parse(newValue.toString());
				super.setValue(val);
			} catch (ParseException e) {
				throw new Property.ConversionException(e.getMessage());
			}
		}
	}

	/** Set DateField datasource.
	 * Datasource type must assignable to Date.
	 * 
	 * @see org.millstone.base.data.Property.Viewer#setPropertyDataSource(Property)
	 */
	public void setPropertyDataSource(Property newDataSource) {
		if (Date.class.isAssignableFrom(newDataSource.getType()))
			super.setPropertyDataSource(newDataSource);
		else
			throw new IllegalArgumentException("DateField only supports Date properties");
	}

	/**
	 * Returns the resolution.
	 * @return int
	 */
	public int getResolution() {
		return resolution;
	}

	/** Sets the resolution of the DateField
	 * @param resolution The resolution to set
	 */
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
}
