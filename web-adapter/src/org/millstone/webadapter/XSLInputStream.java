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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;

import org.millstone.base.terminal.ThemeResource;

/** Stream for parsing XSL files.
 *  Functionality:
 *  <li>Remove xml declaration tags from stream</li>
 *  <li>Convert resource references to URLs</li>
 *  Web adapter is used to convert external resource references
 *  (such as references to images, stylesheets, etc.) into URLs.
 *  This way the XSL stream may include local references which are them
 *  converted into URLs by the web adapter.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class XSLInputStream extends InputStream {

	private BufferedReader data;
	private char[] buffer;
	private int bufferIndex;
	private WebAdapterServlet webAdapterServlet;
	private Theme theme;

	/** Creates a new instance of XSLInputStream */
	public XSLInputStream(
		InputStream input,
		WebAdapterServlet webAdapterServlet,
		Theme theme)
		throws IOException {
		this.webAdapterServlet = webAdapterServlet;
		this.data =
			new java.io.BufferedReader(new java.io.InputStreamReader(input),1024*64);
		this.theme = theme;
	}

	public int read() throws java.io.IOException {
		int c;
		// If there is data in buffer return that
		if ((this.buffer != null) && (this.buffer.length > this.bufferIndex)) {
			return buffer[bufferIndex++];
		} else if ((c = data.read()) >= 0) {
			if (c == '<') {
				if (peek("xsl:stylesheet", data)) {
					skipUntilChar('>', data);
					return data.read(); // return the next character
				} else if (peek("/xsl:stylesheet", data)) {
					skipUntilChar('>', data);
					return data.read(); // return the next character
				} else if (peek("?xml", data)) {
					skipUntilChar('>', data);
					return data.read(); // return the next character
				} else {
					return c;
				}
			} else if ((c == 'r') && peek("es(", data)) {
				skipUntilChar('(', data);
				String resource = skipUntilChar(')', data);

				String themeName = this.theme.getName();
				int pos = -1;
				if ((pos = resource.indexOf(',')) >= 0) {
					themeName = resource.substring(0, pos);
					resource = resource.substring(pos + 1);
				}

				this.buffer =
					(webAdapterServlet
						.getResourceLocation(
							themeName,
							new ThemeResource(resource)))
						.toCharArray();
				this.bufferIndex = 1;
				return buffer[0];
				// return the current character                   
			}
		}
		return c;
	}

	private boolean peek(String item, java.io.Reader stream)
		throws java.io.IOException {
		stream.mark(item.length() + 1); // Mark the current position

		char[] buf = new char[item.length()];
		boolean val = false;
		if (stream.read(buf) == item.length()) {
			val = new String(buf).equals(item);
		}
		stream.reset(); // relocate to marked position
		return val;
	}

	private String skipUntilChar(char chr, java.io.Reader stream)
		throws java.io.IOException {
		String res = "";
		int c;
		while (((c = stream.read()) >= 0) && (((char) c) != chr)) {
			res += (char) c;
		}
		return res;
	}
}
