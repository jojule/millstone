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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The WebBrowserProbe uses JavaScript to determine the capabilities
 * of the client browser.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class WebBrowserProbe {

	private static final String CLIENT_TYPE = "clientcheck";
	private static final String CLIENT_CHECK_PAGE =
		" <HTML> "
			+ "\n    <HEAD>      "
			+ "\n      <META http-equiv=\"Cache-Control\" content=\"no-cache\" />"
			+ "\n      <META http-equiv=\"Pragma\" content=\"no-cache\" />"
			+ "\n      <META http-equiv=\"Expires\" content=\"0\" />"
			+ "\n      <META http-equiv=\"Refresh\" content=\"600;\" />"
			+ "\n      <TITLE></TITLE>"
			+ "\n      "
			+ "\n		<SCRIPT LANGUAGE=\"javascript1.1\">"
			+ "\n			<!--"
			+ "\n			var ver11 = \"JavaScript 1.1\";"
			+ "\n		    // -->"
			+ "\n    	</SCRIPT>"
			+ "\n		<SCRIPT LANGUAGE=\"javascript1.2\">"
			+ "\n			<!--"
			+ "\n			var ver12 = \"JavaScript 1.2\";"
			+ "\n		    // -->"
			+ "\n    	</SCRIPT>	"
			+ "\n		<SCRIPT LANGUAGE=\"javascript1.3\">"
			+ "\n			<!--"
			+ "\n			var ver13 = \"JavaScript 1.3\";"
			+ "\n		    // -->"
			+ "\n  	</SCRIPT>	"
			+ "\n		<SCRIPT LANGUAGE=\"javascript1.4\">"
			+ "\n			<!--"
			+ "\n			var ver14 = \"JavaScript 1.4\";"
			+ "\n		    // -->"
			+ "\n    	</SCRIPT>	"
			+ "\n		<SCRIPT LANGUAGE=\"javascript1.5\">"
			+ "\n			<!--"
			+ "\n			var ver15 = \"JavaScript 1.5\";"
			+ "\n		    // -->"
			+ "\n    	</SCRIPT>	"
			+ "\n		 <SCRIPT LANGUAGE=\"javascript\">"
			+ "\n			<!--"
			+ "\n			var ver11;"
			+ "\n			var ver12;"
			+ "\n			var ver13;"
			+ "\n			var ver14;"
			+ "\n			var ver15;"
			+ "\n			var ver = ver11 ? ver11 : 'none';"
			+ "\n			ver = ver12 ? ver12 : ver;"
			+ "\n			ver = ver13 ? ver13 : ver;"
			+ "\n			ver = ver14 ? ver14 : ver;"
			+ "\n			ver = ver15 ? ver15 : ver;"
			+ "\n			"
			+ "\n			function checkCSS() {"
			+ "\n			    //checktype = "
			+ "\n			    //   document.layers ? document.checkcss : checkcss;"
			+ "\n			    ret = (document.getElementById(\"checkcss\") != null) ? true : false;"
			+ "\n			    return ret;"
			+ "\n			}"
			+ "\n			"
			+ "\n			function checkFrames() {"
			+ "\n			    return true;"
			+ "\n			}"
			+ "\n			"
			+ "\n			function checkVersions() {"
			+ "\n			    var  div = document.getElementById(\"nojs\");"
			+ "\n			    if (div != null) {"
			+ "\n                  div.style.display = \"none\";"
			+ "\n			       document.clientcheck.jsversion.value = ver;"
			+ "\n			       document.clientcheck.screenwidth.value = window.screen.width;"
			+ "\n			       document.clientcheck.screenheight.value = window.screen.height;"
			+ "\n			       document.clientcheck.frames.value = checkFrames();"
			+ "\n			       document.clientcheck.css.value = checkCSS();"
			+ "\n			       document.clientcheck.javaenabled.value = navigator.javaEnabled();"
			+ "\n			       document.clientcheck.submit();"
			+ "\n			    }"
			+ "\n			}			"
			+ "\n			// -->"
			+ "\n    	 </SCRIPT>"
			+ "\n    </HEAD>"
			+ "\n    <BODY ONLOAD=\"checkVersions()\">"
			+ "\n        <SPAN ID=\"checkcss\" STYLE=\"position:absolute; color: #000066; font-family: Arial, Helvetica; font-size: 12px\">Please wait...</SPAN>"
			+ "\n   		<FORM NAME=\"clientcheck\" METHOD=\"post\">"
			+ "\n   			<INPUT NAME=\"clientcheck\" TYPE=\"hidden\" VALUE=\"true\"/>"
			+ "\n   			<INPUT NAME=\"jsversion\" TYPE=\"hidden\" VALUE=\"JavaScript none\"/>"
			+ "\n   			<INPUT NAME=\"screenwidth\" TYPE=\"hidden\" VALUE=\"640\"/>"
			+ "\n   			<INPUT NAME=\"screenheight\" TYPE=\"hidden\" VALUE=\"480\"/>"
			+ "\n   			<INPUT NAME=\"css\" TYPE=\"hidden\" VALUE=\"false\"/>"
			+ "\n   			<INPUT NAME=\"frames\" TYPE=\"hidden\" VALUE=\"false\"/>"
			+ "\n   			<INPUT NAME=\"javaenabled\" TYPE=\"hidden\" VALUE=\"false\"/>"
			+ "\n   			<DIV ID=\"nojs\">"
			+ "\n   			Javascript not rcognized.<BR>"
			+ "\n   			<INPUT NAME=\"button\" TYPE=\"submit\" VALUE=\"Continue\" />"
			+ "\n   			</DIV>"
			+ "\n   		</FORM>    "
			+ "\n    </BODY>"
			+ "\n  </HTML>";

	/** Return the terminal type from the given session. 
	 *  @return WebBrowser instance for the given session. 
	 */
	public static WebBrowser getTerminalType(HttpSession session) {
		return (WebBrowser) session.getAttribute(CLIENT_TYPE);
	}

	/** Set the terminal type for the given session. 
	 *  @return WebBrowser instance for the given session. 
	 */
	public static void setTerminalType(
		HttpSession session,
		WebBrowser terminal) {
		session.setAttribute(CLIENT_TYPE, terminal);
	}

	/** Handle client checking. 
	 *  @param request The HTTP request to process.
	 *  @param response HTTP response to write to.
	 *  @return true is request was handled, false otherwise.
	 **/
	public static boolean handleProbeRequest(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException {

		HttpSession s = request.getSession();
		WebBrowser type = (WebBrowser) s.getAttribute(CLIENT_TYPE);
		if (type != null) {

			// If client is checked, allow further request processing
			if (type.isClientSideChecked())
				return false;

			// Create new type based on client parameters
			type = probe(request);
			type.setClientSideChecked(true);
			s.setAttribute(CLIENT_TYPE, type);

			return false;
		} else {
			// Create default type from request
			type = probe(request);
			s.setAttribute(CLIENT_TYPE, type);

			// If this client allows checking, do it
			if (type.performClientCheck()) {
				printBrowserProbe(response);
				return true;
			} else {
				// Mark as checked to disable checking
				type.setClientSideChecked(true);
			}
		}
		return false;
	}

	/** Generate a client check page. 
	 *  Prints the web page performing the client-side tests.
	 *  @param response Response to write the page to.
	 */
	private static void printBrowserProbe(HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			out.print(CLIENT_CHECK_PAGE);
			out.close();

		} catch (IOException e) {
			Log.except("Failed to generate client check page.", e);
		}
	}

	/** Determine HTML version based on user agent. 
	 *  @param agent HTTP User-Agent request header.
	 *  @return new WebBrowser instance initialized based on agent features.
	 */
	public static WebBrowser probe(String agent) {
		WebBrowser res = new WebBrowser();
		if (agent == null)
			return res;

		// Set the agent string
		res.setBrowserApplication(agent);

		// Konqueror
		if (agent.indexOf("Konqueror") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_5_6);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} 
		
		// Opera
		else if (
			(agent.indexOf("Opera 6.") >= 0)
				|| (agent.indexOf("Opera 5.") >= 0)
				|| (agent.indexOf("Opera 4.") >= 0)) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("Opera 3.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(false);
			res.setFrameSupport(true);
		} 
		
		// OmniWeb
		else if (agent.indexOf("OmniWeb") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		}

		// Mosaic
		else if (agent.indexOf("Mosaic") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		}

		// Lynx
		else if (agent.indexOf("Lynx") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_NONE);
			res.setJavaEnabled(false);
			res.setFrameSupport(true);
		}

		// Microsoft Browsers
		// See Microsoft documentation for details:
		// http://msdn.microsoft.com/library/default.asp?url=/library/
		//        en-us/script56/html/js56jsoriversioninformation.asp
		else if (agent.indexOf("MSIE 6.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_5_6);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("MSIE 5.5") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_5_5);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("MSIE 5.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_5_0);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("MSIE 4.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_3_0);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("MSIE 3.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JSCRIPT_1_0);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("MSIE 2.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_NONE);
			res.setJavaEnabled(false);
			if (agent.indexOf("Mac") >= 0) {
				res.setFrameSupport(true);
			} else {
				res.setFrameSupport(false);
			}
		}

		// Netscape browsers
		else if (agent.indexOf("Netscape6") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_5);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (
			(agent.indexOf("Mozilla/4.06") >= 0)
				|| (agent.indexOf("Mozilla/4.7") >= 0)) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_3);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("Mozilla/4.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_2);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("Mozilla/3.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_1);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		} else if (agent.indexOf("Mozilla/2.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_0);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		}

		// Mozilla Open-Source Browsers		
		else if (agent.indexOf("Mozilla/5.") >= 0) {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_1_5);
			res.setJavaEnabled(true);
			res.setFrameSupport(true);
		}

		// Unknown browser
		else {
			res.setJavaScriptVersion(WebBrowser.JAVASCRIPT_UNCHECKED);
			res.setJavaEnabled(false);
			res.setMarkupVersion(WebBrowser.MARKUP_UNKNOWN);
			res.setFrameSupport(false);
		}

		return res;
	}

	/** Create new instance of WebBrowser by initializing the values
	 *  based on user request.
	 *  @param request Request to be used as defaults.
	 *  @return new WebBrowser instance initialized based on request parameters.
	 */
	public static WebBrowser probe(HttpServletRequest request) {

		// Initialize defaults based on client features
		WebBrowser res = probe(request.getHeader("User-Agent"));

		// Client locales
		Collection locales = res.getLocales();
		for (Enumeration e = request.getLocales(); e.hasMoreElements();) {
			locales.add(e.nextElement());
		}

		//Javascript version
		String val = request.getParameter("jsversion");
		if (val != null) {
			res.setJavaScriptVersion(WebBrowser.parseJavaScriptVersion(val));
		}

		//Java support
		val = request.getParameter("javaenabled");
		if (val != null) {
			res.setJavaEnabled(Boolean.valueOf(val).booleanValue());
		}

		//Screen width
		val = request.getParameter("screenwidth");
		if (val != null) {
			res.setScreenWidth(Integer.parseInt(val));
		}

		//Screen height
		val = request.getParameter("screenheight");
		if (val != null) {
			res.setScreenHeight(Integer.parseInt(val));
		}

		//Frame support
		val = request.getParameter("frames");
		if (val != null) {
			res.setFrameSupport(Boolean.valueOf(val).booleanValue());
		}

		return res;
	}
}