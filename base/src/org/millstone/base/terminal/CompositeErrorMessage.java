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

/** Class for combining multiple error messages together.
 *
 * @author  IT Mill Ltd
 * @version @VERSION@
 * @since 3.0
 */
public class CompositeErrorMessage implements ErrorMessage {

	/** Array of all the errors */
	private ErrorMessage[] errors;

	/** Level of the error */
	private int level;

	/** Constructor for CompositeErrorMessage.
	 * 
	 * @param errorMessages Array of error messages that are listed togeter. 
	 * Nulls are ignored, but at least one message is required.
	 */
	public CompositeErrorMessage(ErrorMessage[] errorMessages) {
		int nonNullCount = 0;
		for (int i = 0; i < errorMessages.length; i++)
			if (errorMessages[i] != null)
				nonNullCount++;
		if (nonNullCount == 0)
			throw new IllegalArgumentException("Composite error message must have at least one error");

		level = Integer.MIN_VALUE;
		errors = new ErrorMessage[nonNullCount];
		int index = 0;
		for (int i = 0; i < errorMessages.length; i++)
			if (errorMessages[i] != null) {
				errors[index++] = errorMessages[i];
				int l = errorMessages[i].getErrorLevel();
				if (l > level)
					level = l;
			}
	}

	/** The error level is the largest error level in 
	 * @see org.millstone.base.terminal.ErrorMessage#getErrorLevel()
	 */
	public final int getErrorLevel() {
		return level;
	}

	public void paint(PaintTarget target) throws PaintException {

		if (errors.length == 1)
			errors[0].paint(target);
		else {
			target.startTag("error");

			if (level > 0 && level <= ErrorMessage.INFORMATION)
				target.addAttribute("level", "info");
			else if (level <= ErrorMessage.WARNING)
				target.addAttribute("level", "warning");
			else if (level <= ErrorMessage.ERROR)
				target.addAttribute("level", "error");
			else if (level <= ErrorMessage.CRITICAL)
				target.addAttribute("level", "critical");
			else
				target.addAttribute("level", "system");

			// Paint all the exceptions
			for (int i = 0; i < errors.length; i++)
				errors[i].paint(target);

			target.endTag("error");
		}
	}

	/* Documented in super interface */
	public void addListener(RepaintRequestListener listener) {
	}

	/* Documented in super interface */
	public void removeListener(RepaintRequestListener listener) {
	}

	/* Documented in super interface */
	public void requestRepaint() {
	}

	/* Documented in super interface */
	public void requestRepaintRequests() {
	}

}
