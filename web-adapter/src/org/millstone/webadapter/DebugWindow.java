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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpSession;

import org.millstone.base.Application;
import org.millstone.base.data.Property;
import org.millstone.base.data.util.MethodProperty;
import org.millstone.base.terminal.FileResource;
import org.millstone.base.ui.*;

/**
 * This class provides a debugging window where one may view the UIDL of
 * the current window, or in a tabset the UIDL of an active frameset.
 * 
 * It is primarily inteded for creating and debugging themes.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class DebugWindow extends Window {

	TabSheet tabs = new TabSheet();
	Application application;
	WebAdapterServlet servlet;
	HttpSession session;
	Select themeSelector;
	HashMap rawUIDL = new HashMap();
	/**
	 * Constructor for DebugWindow.
	 * @param caption
	 * @param application
	 * @param uri
	 * @param layout
	 */
	protected DebugWindow(
		Application application,
		HttpSession session,
		WebAdapterServlet servlet) {
		super("Debug window");
		setName("debug");
		this.application = application;
		this.servlet = servlet;
		this.session = session;
		application.addWindow(this);

		// Create control buttons
		OrderedLayout controls =
			new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
		Label title =
			new Label("<b>Class:</b> " + application.getClass().getName());
		title.setContentMode(Label.CONTENT_XHTML);
		controls.addComponent(
			new Button("Restart Application", this, "restartApplication"));
		controls.addComponent(
			new Button("Clear Session", this, "clearSession"));
		Collection themes = servlet.getThemeSource().getThemes();
		Collection names = new LinkedList();
		for (Iterator i = themes.iterator(); i.hasNext();) {
			names.add(((Theme) i.next()).getName());
		}
		themeSelector = new Select("Application Theme", names);
		themeSelector.setPropertyDataSource(
			new MethodProperty(application, "theme"));
		themeSelector.setWriteThrough(false);

		// Create disable tab tab
		tabs.addTab(
			new Label("Select this tab to disable debug window updates"),
			"Disable",
			null);

		// Add all components
		addComponent(title);
		addComponent(controls);
		addComponent(themeSelector);
		addComponent(new Button("Change theme", this, "commitTheme"));
		addComponent(tabs);
		addComponent(new Button("Save UIDL", this, "saveUIDL"));
	}

	public void saveUIDL() {

		synchronized (rawUIDL) {

			String currentUIDL = (String) rawUIDL.get(tabs.getSelectedTab());

			if (currentUIDL == null)
				return;

			DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
			File file =
				new File(
					"/uidl-debug"
						+ df.format(new Date(System.currentTimeMillis()))
						+ ".xml");
			try {
				BufferedWriter out =
					new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file)));
				out.write(currentUIDL);
				out.close();

				//Open the UIDL also
				open(new FileResource(file, this.getApplication()));
				Log.info("UIDL written to file " + file);
			} catch (FileNotFoundException e) {
				Log.info("Failed to write debug to " + file + ": " + e);
			} catch (IOException e) {
				Log.info("Failed to write debug to " + file + ": " + e);
			}
		}
	}

	public void commitTheme() {
		themeSelector.commit();
	}

	public void clearSession() {
		session.invalidate();
	}

	public void restartApplication() {
		application.close();
	}

	public void setWindowUIDL(Window window, String uidl) {
		String caption = "UIDL:" + window.getName();
		synchronized (tabs) {
			for (Iterator i = tabs.getComponentIterator(); i.hasNext();) {
				Component c = (Component) i.next();
				if (tabs.getTabCaption(c).equals(caption)) {
					((Label) c).setValue(getHTMLFormattedUIDL(caption, uidl));
					((Label) c).setContentMode(Label.CONTENT_XHTML);
					rawUIDL.put(c, uidl);
					caption = null;
				}
			}

			// Add new tab
			if (caption != null) {
				Label l = new Label(getHTMLFormattedUIDL(caption, uidl));
				l.setContentMode(Label.CONTENT_XHTML);
				rawUIDL.put(l, uidl);
				tabs.addTab(l, caption, null);
			}
		}
	}

	public String getHTMLFormattedUIDL(String caption, String uidl) {
		StringBuffer sb = new StringBuffer();

		// Print formatted UIDL with errors embedded
		//Perl5Util util = new Perl5Util();

		int row = 0;
		int prev = 0;
		int index = 0;
		boolean lastLineWasEmpty = false;

		sb.append(
			"<TABLE WIDTH=\"100%\" STYLE=\"border-left: 1px solid black; "
				+ "border-right: 1px solid black; border-bottom: "
				+ "1px solid black; border-top: 1px solid black\""
				+ " cellpadding=\"0\" cellspacing=\"0\" BORDER=\"0\">");

		if (caption != null)
			sb.append(
				"<TR><TH BGCOLOR=\"#ddddff\" COLSPAN=\"2\">"
					+ "<FONT SIZE=\"+2\">"
					+ caption
					+ "</FONT></TH></TR>\n");

		boolean unfinished = true;
		while (unfinished) {
			row++;

			// Get individual line
			index = uidl.indexOf('\n', prev);
			String line;
			if (index < 0) {
				unfinished = false;
				line = uidl.substring(prev);
			} else {
				line = uidl.substring(prev, index);
				prev = index + 1;
			}

			// Escape the XML
			line = WebPaintTarget.escapeXML(line);

			// Code beautification : Comment lines
			line =
				replaceAll(
					line,
					"&lt;!--",
					"<SPAN STYLE = \"color: #00dd00\">&lt;!--");
			line = replaceAll(line, "--&gt;", "--&gt;</SPAN>");

			while (line.length() > 0 && line.charAt(0) == ' ') {
				line = line.substring(1);
			}
			boolean isEmpty = (line.length() == 0 || line.equals("\r"));
			line = " " + line;

			if (!(isEmpty && lastLineWasEmpty))
				sb.append(
					"<TR"
						+ ((row % 10) > 4 ? " BGCOLOR=\"#eeeeff\"" : "")
						+ ">"
						+ "<TD VALIGN=\"top\" ALIGN=\"rigth\" STYLE=\"border-right: 1px solid gray\"> "
						+ String.valueOf(row)
						+ " </TD><TD>"
						+ line
						+ "</TD></TR>\n");

			lastLineWasEmpty = isEmpty;

		}

		sb.append("</TABLE>\n");

		return sb.toString();
	}

	/**
	 * Replaces the characters in a substring of this <code>String</code>
	 * with characters in the specified <code>String</code>. The substring
	 * begins at the specified <code>start</code> and extends to the character
	 * at index <code>end - 1</code> or to the end of the
	 * <code>String</code> if no such character exists. First the
	 * characters in the substring are removed and then the specified
	 * <code>String</code> is inserted at <code>start</code>. (The
	 * <code>StringBuffer</code> will be lengthened to accommodate the
	 * specified String if necessary.)
	 * <p>
	 * NOTE: This operation is slow.
	 * </p>
	 * 
	 * @param      start    The beginning index, inclusive.
	 * @param      end      The ending index, exclusive.
	 * @param      str   String that will replace previous contents.
	 * @return     This string buffer.
	 * @exception  StringIndexOutOfBoundsException  if <code>start</code>
	 *             is negative, greater than <code>length()</code>, or
	 *		   greater than <code>end</code>.
	 */
	protected static String replace(
		String text,
		int start,
		int end,
		String str) {
		return new StringBuffer(text).replace(start, end, str).toString();
	}

	protected static String replaceAll(
		String text,
		String oldStr,
		String newStr) {
		StringBuffer sb = new StringBuffer(text);

		int newStrLen = newStr.length();
		int oldStrLen = oldStr.length();
		if (oldStrLen <= 0)
			return text;

		int lastIndex = text.length() - oldStr.length();
		char firstChar = oldStr.charAt(0);
		int i = 0;
		while (i <= sb.length() - oldStrLen) {
			if (sb.substring(i, i + oldStrLen).equals(oldStr)) {
				sb.replace(i, i + oldStrLen, newStr);
				i += newStrLen;
			} else {
				i++;
			}
		}
		return sb.toString();
	}

}
