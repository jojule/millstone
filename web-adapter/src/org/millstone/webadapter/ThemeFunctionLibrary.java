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

import org.millstone.base.Application;
import org.millstone.base.terminal.ThemeResource;
import org.millstone.base.ui.FrameWindow;
import org.millstone.base.ui.Window;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpSession;

/** This a function library that can be used from the theme XSL-files. It provides
 * easy access to current application, window, theme, webbrowser and session. The
 * internal threadlocal state must be maintained by the webadapter in order go guarantee
 * that it works.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */

public class ThemeFunctionLibrary {

	static private final int APPLICATION = 0;
	static private final int WINDOW = 1;
	static private final int WEBBROWSER = 2;
	static private final int SESSION = 3;
	static private final int WEBADAPTERSERVLET = 4;
	static private final int THEME = 5;

	static private ThreadLocal state = new ThreadLocal();

	static protected void setState(
		Application application,
		Window window,
		WebBrowser webBrowser,
		HttpSession session,
		WebAdapterServlet webAdapterServlet,
		String theme) {
		state.set(
			new Object[] {
				application,
				window,
				webBrowser,
				session,
				webAdapterServlet,
				theme });
	}

	static protected void cleanState() {
		state.set(null);
	}

	/** Returns a reference to the application object associated
	 * with the session that the call came from.
	 */
	static public Application application() {
		try {
			return (Application) ((Object[]) state.get())[APPLICATION];
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Returns a reference to the current window object associated
	 * with the session that the call came from.
	 */
	static public Window window() {
		try {
			return (Window) ((Object[]) state.get())[WINDOW];
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Returns a reference to the browser object associated
	 * with the session that the call came from.
	 */
	static public WebBrowser browser() {
		try {
			return (WebBrowser) ((Object[]) state.get())[WEBBROWSER];
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Returns a reference to the current servlet http session object
	 * that is associated with the session that the call came from.
	 */
	static public HttpSession session() {
		try {
			return (HttpSession) ((Object[]) state.get())[SESSION];
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Return a reference to the current theme object that is
	 * associated with the session that the call came from.
	 */
	static public String theme() {
		try {
			return (String) ((Object[]) state.get())[THEME];
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Return an URI to the named resource from the named theme.
	 */
	static public String resource(String resource, String theme) {
		try {
			return (
				(WebAdapterServlet)
					(
						(Object[]) state
							.get())[WEBADAPTERSERVLET])
							.getResourceLocation(
				theme,
				new ThemeResource(resource));
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Return an URI to the named resource.
	 */
	static public String resource(String resource) {
		try {
			return (
				(WebAdapterServlet)
					(
						(Object[]) state
							.get())[WEBADAPTERSERVLET])
							.getResourceLocation(
				theme(),
				new ThemeResource(resource));
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Generate JavaScript for page header that handles 
	 * window refreshing, opening and closing.
	 * 
	 * Generates script that:
	 * <ul> 
	 *  <li>Requests that all windows that need repaint be reloaded</li>
	 *  <li>Sets the window name</li>
	 *  <li>Closes window if it is set to be closed </li>
	 * <ul>
	 * 
	 */
	static public String windowScript() {
		return generateWindowScript(window());
	}

	static private String generateWindowScript(Window window) {

		StringBuffer script = new StringBuffer();
		Application app = window.getApplication();
		LinkedList update = new LinkedList();

		// Add all the windows needto update list
		WebAdapterServlet wa =
			(WebAdapterServlet) ((Object[]) state.get())[WEBADAPTERSERVLET];
		Set dirtyWindows = wa != null ? wa.getDirtyWindows(app) : null;
		if (dirtyWindows != null)
			for (Iterator i = dirtyWindows.iterator(); i.hasNext();) {
				Window w = (Window) i.next();
				if (w != window) {
					if (w instanceof FrameWindow)
						update.addFirst(w);
					else
						update.addLast(w);
				}
			}

		// Remove all windows that are in frames, of such frame windows that
		// will be updated anyway
		Object[] u = update.toArray();
		if (u.length > 0 && (window instanceof FrameWindow))
			u[u.length - 1] = window;
		for (int i = 0; i < u.length; i++) {
			try {
				FrameWindow w = (FrameWindow) u[i];
				LinkedList framesets = new LinkedList();
				framesets.add(w.getFrameset());
				while (!framesets.isEmpty()) {
					FrameWindow.Frameset fs =
						(FrameWindow.Frameset) framesets.removeFirst();
					for (Iterator j = fs.getFrames().iterator();
						j.hasNext();
						) {
						FrameWindow.Frame f = (FrameWindow.Frame) j.next();
						if (f instanceof FrameWindow.Frameset)
							framesets.add(f);
						else if (f.getWindow() != null) {
							update.remove(f.getWindow());
							wa.removeDirtyWindow(app, f.getWindow());
						}
					}
				}
			} catch (ClassCastException ignored) {
			}
		}

		// Set window name
		script.append("window.name = \"" + window.getName() + "\";\n");

		// Generate window updatescript
		for (Iterator i = update.iterator(); i.hasNext();) {
			Window w = (Window) i.next();
			script.append(getWindowRefreshScript(w));

			wa.removeDirtyWindow(app, w);

			// Windows that are closed immediately are "painted" now
			if (w.getApplication() == null || !w.isVisible())
				w.requestRepaintRequests();
		}

		// Close current window if it is not visible
		if (!window.isVisible())
			script.append("window.close();\n");

		return script.toString();
	}

	/* Static mapping for 0 to be sunday. */
	private static int[] weekdays =
		new int[] {
			Calendar.SUNDAY,
			Calendar.MONDAY,
			Calendar.TUESDAY,
			Calendar.WEDNESDAY,
			Calendar.THURSDAY,
			Calendar.FRIDAY,
			Calendar.SATURDAY };

	/** Get name for week day.
	 * @param Number of week day. 0 first day of week.
	 * @return Name of week day in applications current locale.
	 */
	static public int getFirstDayOfWeek() {
		try {
			Application app =
				(Application) ((Object[]) state.get())[APPLICATION];
			Calendar cal = new GregorianCalendar(app.getLocale());
			int first = cal.getFirstDayOfWeek();
			for (int i = 0; i < 7; i++) {
				if (first == weekdays[i])
					return i;
			}
			return 0; // default to sunday
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Get name for week day.
	 * @param Number of week day. 0 sunday, 1 monday, ...
	 * @return Name of week day in applications current locale.
	 */
	static public String getShortWeekday(int dayOfWeek) {
		try {
			Application app =
				(Application) ((Object[]) state.get())[APPLICATION];
			DateFormatSymbols df = new DateFormatSymbols(app.getLocale());
			return df.getShortWeekdays()[weekdays[dayOfWeek]];
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Get short name for month.
	 * @param Number of month. 0 is January, 1 is February, and so on.
	 * @return Name of month in applications current locale.
	 */
	static public String getShortMonth(int month) {
		try {
			Application app =
				(Application) ((Object[]) state.get())[APPLICATION];
			DateFormatSymbols df = new DateFormatSymbols(app.getLocale());
			String monthName = df.getShortMonths()[month];
			return monthName;
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Get name for month.
	 * @param Number of month. 0 is January, 1 is February, and so on.
	 * @return Name of month in applications current locale.
	 */
	static public String getMonth(int month) {
		try {
			Application app =
				(Application) ((Object[]) state.get())[APPLICATION];
			DateFormatSymbols df = new DateFormatSymbols(app.getLocale());
			String monthName = df.getMonths()[month];
			return monthName;
		} catch (NullPointerException e) {
			throw new IllegalStateException();
		}
	}

	/** Generate JavaScript for updating given window */
	static protected String getWindowRefreshScript(Window window) {

		if (window == null)
			return "";

		String name = window.getName();

		if (name == null)
			return "";

		// If window is closed or hidden
		if (window.getApplication() == null || !window.isVisible())
			return "win = window.open(\"\",\""
				+ name
				+ "\");\n  "
				+ "if (win != null) { win.close(); }\n";

		String url = window.getURL().toString();

		String features = "";
		int width = window.getWidth();
		int height = window.getHeight();
		if (width >= 0)
			features += "width=" + width;
		if (height >= 0)
			features += ((features.length() > 0) ? "," : "")
				+ "height="
				+ height;
		switch (window.getBorder()) {
			case Window.BORDER_NONE :
				features += ((features.length() > 0) ? "," : "")
					+ "toolbar=0,location=0,menubar=0,status=0,resizable=1,scrollbars="
					+ (window.isScrollable() ? "1" : "0");
				break;
			case Window.BORDER_MINIMAL :
				features += ((features.length() > 0) ? "," : "")
					+ "toolbar=1,location=0,menubar=0,status=1,resizable=1,scrollbars="
					+ (window.isScrollable() ? "1" : "0");
				break;
			case Window.BORDER_DEFAULT :
				features += ((features.length() > 0) ? "," : "")
					+ "toolbar=1,location=1,menubar=1,status=1,resizable=1,scrollbars="
					+ (window.isScrollable() ? "1" : "0");
				break;
		}

		return "win = window.open(\"\",\""
			+ name
			+ "\",\""
			+ features
			+ "\");\n"
			+ "if (win != null) {\n"
			+ "if (win.location.href == \"about:blank\") {\n"
			+ "win.location.href = \""
			+ url
			+ "\";}\n"
			+ "else {\n"
			+ "doc = win.document;\n"
			+ "if (doc == null) {\n"
			+ "win.navigate(\""
			+ url
			+ "\"); }\n"
			+ "else { form = doc.forms[\"millstone\"];\n"
			+ " //alert(\"form[0] = \" + form[0] + \"forms = \" + doc.forms);\n"
			+ "if (form == null) {doc.location.reload();}"
			+ "else {form.submit();}"
			+ "}}}\n";
	}
}
