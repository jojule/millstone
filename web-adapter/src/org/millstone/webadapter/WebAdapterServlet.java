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

import org.millstone.base.Application.WindowAttachEvent;
import org.millstone.base.Application.WindowDetachEvent;
import org.millstone.base.service.FileTypeResolver;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.millstone.base.Application;
import org.millstone.base.ui.Window;
import org.millstone.base.terminal.Paintable;
import org.millstone.base.terminal.DownloadStream;
import org.millstone.base.terminal.ThemeResource;
import org.millstone.base.terminal.Paintable.RepaintRequestEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/** This servlet is the core of the MillStone Web Adapter, that adapts the
 * MillStone applications to Web standards. The web adapter can be used to
 * represent the most MillStone application using Web browsers and corresponding
 * technologies.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */

public class WebAdapterServlet
	extends HttpServlet
	implements
		Application.WindowAttachListener,
		Application.WindowDetachListener,
		Paintable.RepaintRequestListener {

	private static int DEFAULT_THEME_CACHETIME = 1000 * 60 * 60 * 24;
	private static int DEFAULT_BUFFER_SIZE = 32 * 1024;
	private static int DEFAULT_MAX_TRANSFORMERS = 1;
	private static int MAX_BUFFER_SIZE = 64 * 1024;
	private static String SESSION_ATTR_VARMAP = "varmap";
	private static String SESSION_ATTR_APPS = "apps";
	private static String SESSION_BINDING_LISTENER = "bindinglistener";
	private static String SESSION_DEFAULT_THEME = "default";
	private static String RESOURCE_URI = "/RES/";
	private static String THEME_JAR_PREFIX = "millstone-web-themes";
	private static String THEME_PATH = "/WEB-INF/lib/themes";
	private static String SERVER_COMMAND_PARAM = "SERVER_COMMANDS";
	private static int SERVER_COMMAND_STREAM_MAINTAIN_PERIOD = 15000;
	private static int SERVER_COMMAND_HEADER_PADDING = 2000;
	private Class applicationClass;
	private Properties applicationProperties;
	private UIDLTransformerFactory transformerFactory;
	private CollectionThemeSource themeSource;
	private String resourcePath = null;
	private boolean enableBrowserProbe = false;
	private boolean debugMode = false;
	private int maxConcurrentTransformers;
	private long transformerCacheTime;
	private long themeCacheTime;
	private WeakHashMap applicationToDirtyWindowSetMap = new WeakHashMap();
	private WeakHashMap applicationToServerCommandStreamLock =
		new WeakHashMap();
	private WeakHashMap applicationToLastRequestDate = new WeakHashMap();

	/** Called by the servlet container to indicate to a servlet that the
	 * servlet is being placed into service.
	 *
	 * @param servletConfig object containing the servlet's configuration and
	 *        initialization parameters
	 * @throws ServletException if an exception has occurred that interferes
	 *         with the servlet's normal operation.
	 */
	public void init(javax.servlet.ServletConfig servletConfig)
		throws javax.servlet.ServletException {
		super.init(servletConfig);

		// Get the application class name
		String applicationClassName =
			servletConfig.getInitParameter("application");
		if (applicationClassName == null) {
			Log.error("Application not specified in servlet parameters");
		}

		// Store the application parameters into Properties object
		this.applicationProperties = new Properties();
		for (Enumeration e = servletConfig.getInitParameterNames();
			e.hasMoreElements();
			) {
			String name = (String) e.nextElement();
			this.applicationProperties.setProperty(
				name,
				servletConfig.getInitParameter(name));
		}

		// Override with server.xml parameters		
		ServletContext context = servletConfig.getServletContext();
		for (Enumeration e = context.getInitParameterNames();
			e.hasMoreElements();
			) {
			String name = (String) e.nextElement();
			this.applicationProperties.setProperty(
				name,
				context.getInitParameter(name));
		}

		// Get the debug window parameter
		String debug =
			applicationProperties.getProperty(DebugWindow.WINDOW_NAME, "false");
		// Enable application specific debug
		this.debugMode = debug.equals("true");

		// Get the default browser parameter
		String defaultBrowser =
			applicationProperties.getProperty("browserprobe", "false");
		this.enableBrowserProbe = defaultBrowser.equals("true");

		// Get the maximum number of simultaneous transformers
		this.maxConcurrentTransformers =
			Integer.parseInt(
				applicationProperties.getProperty("maxtransformers", "-1"));
		if (this.maxConcurrentTransformers < 1)
			this.maxConcurrentTransformers = DEFAULT_MAX_TRANSFORMERS;
		;

		// Get cache time for transformers
		this.transformerCacheTime =
			Integer.parseInt(
				applicationProperties.getProperty(
					"transformercachetime",
					"-1"))
				* 1000;

		// Get cache time for theme resources
		this.themeCacheTime =
			Integer.parseInt(
				applicationProperties.getProperty("themecachetime", "-1"))
				* 1000;
		if (this.themeCacheTime < 0) {
			this.themeCacheTime = DEFAULT_THEME_CACHETIME;
		}

		// Get the theme sources
		this.themeSource = new CollectionThemeSource();
		String themeSources = applicationProperties.getProperty("themesource");
		if (themeSources != null) {
			StringTokenizer st = new StringTokenizer(themeSources, ";");
			while (st.hasMoreTokens()) {
				File f = new File(st.nextToken());
				try {
					if (f.isDirectory()) {
						this.themeSource.add(new DirectoryThemeSource(f, this));
					} else {
						this.themeSource.add(new JarThemeSource(f, this, ""));
					}
				} catch (java.io.FileNotFoundException de) {
					Log.except(
						"Failed to load the themes from '" + f + "'. Ignoring.",
						de);
				} catch (java.io.IOException je) {
					Log.except("Failed to load the themes from " + f, je);
				}
			}
		}

		// Initialize the default theme sources
		File f = findDefaultThemeJar();
		try {
			// Add themes.jar if exists							
			if (f != null && f.exists())
				this.themeSource.add(new JarThemeSource(f, this, ""));

		} catch (java.io.FileNotFoundException de) {
			Log.except("Failed to load themes from " + f, de);
		} catch (java.io.IOException je) {
			Log.except("Failed to load the themes from " + f, je);
		}
		try {
			// Add themes directory if exists
			f = new File(this.getServletContext().getRealPath(THEME_PATH));
			if (f.exists())
				this.themeSource.add(new DirectoryThemeSource(f, this));

		} catch (java.io.IOException je) {
			Log.except("Failed to load the themes from " + f, je);
		}

		// Check themes
		if (this.themeSource.getThemes().size() <= 0) {
			throw new ServletException("No themes found in specified themesources.");
		}

		// Initialize the transformer factory, if not initialized
		if (this.transformerFactory == null) {

			this.transformerFactory =
				new UIDLTransformerFactory(
					this.themeSource,
					this,
					this.maxConcurrentTransformers,
					this.transformerCacheTime);
		}

		// Load the application class using the same class loader
		// as the servlet itself
		ClassLoader loader = this.getClass().getClassLoader();
		try {
			this.applicationClass = loader.loadClass(applicationClassName);
		} catch (ClassNotFoundException e) {
			throw new ServletException(
				"Failed to load application class: " + applicationClassName);
		}
	}

	/** Receives standard HTTP requests from the public service method and
	 *  dispatches them.
	 *
	 * @param request object that contains the request the client
	 *        made of the servlet
	 * @param response object that contains the response the servlet
	 *        returns to the client
	 * @throws ServletException if an input or output error occurs while the
	 *         servlet is handling the TRACE request
	 * @throws IOException if the request for the TRACE cannot be handled
	 */
	protected void service(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		// Transformer and output stream for the result
		UIDLTransformer transformer = null;
		HttpVariableMap variableMap = null;
		OutputStream out = response.getOutputStream();
		try {

			// If the resource path is unassigned, initialize it
			if (resourcePath == null)
				resourcePath =
					request.getContextPath()
						+ request.getServletPath()
						+ RESOURCE_URI;
						
			// Handle resource requests
			if (handleResourceRequest(request, response))
				return;

			// Handle server commands
			if (handleServerCommands(request, response))
				return;

			// Get the application
			Application application = getApplication(request);

			// Create application if it doesn't exist
			if (application == null)
				application = createApplication(request);

			// Is this a download request from application
			DownloadStream download = null;

			// The rest of the process is synchronized with the application
			// in order to guarantee that no parallel variable handling is 
			// made
			synchronized (application) {

				// Set the last application request date
				applicationToLastRequestDate.put(application, new Date());

				// Get the variable map
				variableMap = getVariableMap(application, request);
				if (variableMap == null)
					return;

				// Change  all variables based on request parameters
				Map unhandledParameters = variableMap.handleVariables(request);

				// Check/handle client side feature checks
				WebBrowserProbe.handleProbeRequest(
					request,
					unhandledParameters);

				// Handle the URI if the application is still running
				if (application.isRunning())
					download = handleURI(application, request, response);

				// If this is not a download request
				if (download == null) {

					// Find the window within the application
					Window window = null;
					if (application.isRunning())
						window = getApplicationWindow(request, application);

					// Handle the unhandled parameters if the application is still running
					if (window != null
						&& unhandledParameters != null
						&& !unhandledParameters.isEmpty())
						window.handleParameters(unhandledParameters);

					// Remove application if it has stopped
					if (!application.isRunning()) {
						endApplication(request, response, application);
						return;
					}

					// Return if no window found
					if (window == null)
						return;

					// Get the terminal type for the window
					WebBrowser terminalType = (WebBrowser) window.getTerminal();

					// Set terminal type for the window, if not already set
					if (terminalType == null) {
						terminalType =
							WebBrowserProbe.getTerminalType(
								request.getSession());
					}

					// Find theme and initialize TransformerType
					UIDLTransformerType transformerType = null;
					if (window.getTheme() != null) {
						Theme activeTheme;
						if ((activeTheme =
							this.themeSource.getThemeByName(window.getTheme()))
							!= null) {
							transformerType =
								new UIDLTransformerType(
									terminalType,
									activeTheme);
						} else {
							Log.info(
								"Theme named '"
									+ window.getTheme()
									+ "' not found. Using system default theme.");
						}
					}

					// Use default theme if selected theme was not found.
					if (transformerType == null) {
						transformerType =
							new UIDLTransformerType(
								terminalType,
								this.themeSource.getThemeByName(
									WebAdapterServlet.SESSION_DEFAULT_THEME));
					}

					transformer =
						this.transformerFactory.getTransformer(transformerType);

					// Set the response type
					response.setContentType(terminalType.getContentType());

					// Create UIDL writer
					WebPaintTarget paintTarget =
						transformer.getPaintTarget(variableMap);

					// Assure that the correspoding debug window will be repainted property
					// by clearing it before the actual paint.
					DebugWindow debugWindow =
						(DebugWindow) application.getWindow(
							DebugWindow.WINDOW_NAME);
					if (debugWindow != null && debugWindow != window) {
						debugWindow.setWindowUIDL(window, "Painting...");
					}

					// Paint window		
					window.paint(paintTarget);
					paintTarget.close();

					// Window is now painted
					windowPainted(application, window);

					// Debug
					if (debugWindow != null && debugWindow != window) {
						debugWindow.setWindowUIDL(
							window,
							paintTarget.getUIDL());
					}

					// Set the function library state for this thread
					ThemeFunctionLibrary.setState(
						application,
						window,
						transformerType.getWebBrowser(),
						request.getSession(),
						this,
						transformerType.getTheme().getName());

				}
			}

			// For normal requests, transform the window
			if (download == null) {

				// Transform and output the result to browser
				// Note that the transform and transfer of the result is
				// not synchronized with the variable map. This allows
				// parallel transfers and transforms for better performance,
				// but requires that all calls from the XSL to java are 
				// thread-safe
				transformer.transform(out);
			}

			// For download request, transfer the downloaded data
			else {

				handleDownload(download, request, response);
			}

		} catch (UIDLTransformerException te) {
			// Write the error report to client
			BufferedWriter err =
				new BufferedWriter(new OutputStreamWriter(out));
			err.write(
				"<html><head><title>Application Internal Error</title></head><body>");
			err.write("<h1>" + te.getMessage() + "</h1>");
			err.write(te.getHTMLDescription());
			err.write("</body></html>");
			err.close();
		} catch (Throwable e) {
			// Re-throw other exceptions
			throw new ServletException(e);
		} finally {

			// Release transformer
			if (transformer != null)
				transformerFactory.releaseTransformer(transformer);

			// Clean the function library state for this thread 
			// for security reasons
			ThemeFunctionLibrary.cleanState();
		}
	}

	/** Handle the requested URI.
	 *  An application can add handlers to do special processing, when
	 *  a certain URI is requested. The handlers are invoked before
	 *  any windows URIs are processed and if a DownloadStream is
	 *  returned it is sent to the client.
	 *  @see org.millstone.base.terminal.URIHandler
	 *  
	 *  @param application Application owning the URI
	 *  @param request HTTP request instance
	 *  @param response HTTP response to write to.
	 *  @return boolean True if the request was handled and further processing
	 *           should be suppressed, false otherwise.
	 */
	private DownloadStream handleURI(
		Application application,
		HttpServletRequest request,
		HttpServletResponse response) {

		String uri = request.getPathInfo();

		// If no URI is available
		if (uri == null || uri.length() == 0 || uri.equals("/"))
			return null;

		// Remove the leading /	
		while (uri.startsWith("/") && uri.length() > 0)
			uri = uri.substring(1);

		// Handle the uri
		DownloadStream stream =
			application.handleURI(application.getURL(), uri);

		return stream;
	}

	/** Handle the requested URI.
	 *  An application can add handlers to do special processing, when
	 *  a certain URI is requested. The handlers are invoked before
	 *  any windows URIs are processed and if a DownloadStream is
	 *  returned it is sent to the client.
	 *  @see org.millstone.base.terminal.URIHandler
	 *  
	 *  @param application Application owning the URI
	 *  @param request HTTP request instance
	 *  @param response HTTP response to write to.
	 *  @return boolean True if the request was handled and further processing
	 *           should be suppressed, false otherwise.
	 */
	private void handleDownload(
		DownloadStream stream,
		HttpServletRequest request,
		HttpServletResponse response) {

		// Download from given stream
		InputStream data = stream.getStream();
		if (data != null) {

			// Set content type
			response.setContentType(stream.getContentType());

			// Set cache headers
			if (stream.getCacheTime() <= 0) {
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", 0);
			} else {
				response.setDateHeader(
					"Expires",
					System.currentTimeMillis() + stream.getCacheTime());
			}

			// Copy download stream parameters directly
			// to HTTP headers.
			Iterator i = stream.getParameterNames();
			if (i != null) {
				while (i.hasNext()) {
					String param = (String) i.next();
					response.setHeader(
						(String) param,
						stream.getParameter(param));
				}
			}

			int bufferSize = stream.getBufferSize();
			if (bufferSize <= 0 || bufferSize > MAX_BUFFER_SIZE)
				bufferSize = DEFAULT_BUFFER_SIZE;
			byte[] buffer = new byte[bufferSize];
			int bytesRead = 0;

			try {
				OutputStream out = response.getOutputStream();

				while ((bytesRead = data.read(buffer)) > 0) {
					out.write(buffer, 0, bytesRead);
					out.flush();
				}
				out.close();
			} catch (IOException ignored) {
			}

		}

	}

	/** Look for default theme JAR file.
	 * @return Jar file or null if not found.
	 */
	private File findDefaultThemeJar() {
		File lib =
			new File(this.getServletContext().getRealPath("/WEB-INF/lib"));
		String[] files = lib.list();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].toLowerCase().endsWith(".jar")
					&& files[i].startsWith(THEME_JAR_PREFIX)) {
					return new File(lib, files[i]);
				}
			}
		}
		return null;
	}

	/** Handle theme resource file requests.
	 *  Resources supplied with the themes are provided by the
	 *  WebAdapterServlet.
	 * 
	 *  @param request HTTP request 
	 *  @param response HTTP response 
	 *  @return boolean True if the request was handled and further processing
	 *           should be suppressed, false otherwise.
	 */
	private boolean handleResourceRequest(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException {

		String resourceId = request.getPathInfo();

		// Check if this really is a resource request
		if (resourceId == null || !resourceId.startsWith(RESOURCE_URI))
			return false;

		// Check the resource type
		resourceId = resourceId.substring(RESOURCE_URI.length());
		InputStream data = null;
		// Get theme resources
		try {
			data = themeSource.getResource(resourceId);
		} catch (ThemeSource.ThemeException e) {
			Log.info(e.getMessage());
			data = null;
		}

		// Write the response
		try {
			if (data != null) {
				response.setContentType(
					FileTypeResolver.getMIMEType(resourceId));

				// Use default cache time for theme resources
				if (this.themeCacheTime > 0) {
					response.setDateHeader(
						"Expires",
						System.currentTimeMillis() + this.themeCacheTime);
				}
				// Write the data to client 
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int bytesRead = 0;
				OutputStream out = response.getOutputStream();
				while ((bytesRead = data.read(buffer)) > 0) {
					out.write(buffer, 0, bytesRead);
				}
				out.close();
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (java.io.IOException e) {
			Log.info(
				"Resource transfer failed:  "
					+ request.getRequestURI()
					+ ". ("
					+ e.getMessage()
					+ ")");
		}

		return true;
	}

	/** Get the variable map for the session */
	private static synchronized HttpVariableMap getVariableMap(
		Application application,
		HttpServletRequest request) {

		HttpSession session = request.getSession();

		// Get the application to variablemap map
		HashMap varMapMap = (HashMap) session.getAttribute(SESSION_ATTR_VARMAP);
		if (varMapMap == null) {
			varMapMap = new HashMap();
			session.setAttribute(SESSION_ATTR_VARMAP, varMapMap);
		}

		// Create a variable map, if it does not exists.
		HttpVariableMap variableMap =
			(HttpVariableMap) varMapMap.get(application);
		if (variableMap == null) {
			variableMap = new HttpVariableMap();
			varMapMap.put(application, variableMap);
		}

		return variableMap;
	}

	/** Get the current application URL from request */
	private URL getApplicationUrl(HttpServletRequest request)
		throws MalformedURLException {

		URL applicationUrl;
		try {
			URL reqURL =
				new URL(
					(request.isSecure() ? "https://" : "http://")
						+ request.getServerName()
						+ ":"
						+ request.getServerPort()
						+ request.getRequestURI());
			String servletPath =
				request.getContextPath() + request.getServletPath();
			if (servletPath.length() == 0
				|| servletPath.charAt(servletPath.length() - 1) != '/')
				servletPath = servletPath + "/";
			applicationUrl = new URL(reqURL, servletPath);
		} catch (MalformedURLException e) {
			Log.error(
				"Error constructing application url "
					+ request.getRequestURI()
					+ " ("
					+ e
					+ ")");
			throw e;
		}

		return applicationUrl;
	}

	/** Get the existing application for given request.
	 *  Looks for application instance for given request
	 *  based on the requested URL.
	 *  @param request HTTP request 
	 *	@return Application instance, or null if the URL does not map to valid application.
	 */
	private Application getApplication(HttpServletRequest request)
		throws MalformedURLException {

		// Ensure that the session is still valid
		HttpSession session = request.getSession(false);
		if (session == null)
			return null;

		// Get the application url
		URL applicationUrl = getApplicationUrl(request);

		// Get application list for the session.
		LinkedList applications =
			(LinkedList) session.getAttribute(SESSION_ATTR_APPS);
		if (applications == null)
			return null;

		// Search for the application (using the application URI) from the list
		Application application = null;
		for (Iterator i = applications.iterator();
			i.hasNext() && application == null;
			) {
			Application a = (Application) i.next();
			String aPath = a.getURL().getPath();
			String servletPath =
				request.getContextPath() + request.getServletPath();
			if (servletPath.length() < aPath.length())
				servletPath += "/";
			if (servletPath.equals(aPath))
				application = a;
		}

		// Remove stopped applications from the list
		if (application != null && !application.isRunning()) {
			applications.remove(application);
			application = null;
		}

		return application;
	}

	/** Create a new application.
	 *	@return New application instance
	 */
	private Application createApplication(HttpServletRequest request)
		throws MalformedURLException, InstantiationException, IllegalAccessException {

		Application application = null;

		// Get the application url
		URL applicationUrl = getApplicationUrl(request);

		// Get application list.
		HttpSession session = request.getSession();
		if (session == null)
			return null;
		LinkedList applications =
			(LinkedList) session.getAttribute(SESSION_ATTR_APPS);
		if (applications == null) {
			applications = new LinkedList();
			session.setAttribute(SESSION_ATTR_APPS, applications);
			HttpSessionBindingListener sessionBindingListener =
				new SessionBindingListener(applications);
			session.setAttribute(
				SESSION_BINDING_LISTENER,
				sessionBindingListener);
		}

		// Create new application and start it
		try {
			application = (Application) this.applicationClass.newInstance();
			applications.add(application);
			application.addListener((Application.WindowAttachListener) this);
			application.addListener((Application.WindowDetachListener) this);
			application.setLocale(request.getLocale());
			application.start(applicationUrl, this.applicationProperties);

		} catch (IllegalAccessException e) {
			Log.error(
				"Illegal access to application class "
					+ this.applicationClass.getName());
			throw e;
		} catch (InstantiationException e) {
			Log.error(
				"Failed to instantiate application class: "
					+ this.applicationClass.getName());
			throw e;
		}

		return application;
	}

	/** End application */
	private void endApplication(
		HttpServletRequest request,
		HttpServletResponse response,
		Application application)
		throws IOException {

		String logoutUrl = application.getLogoutURL();
		if (logoutUrl == null)
			logoutUrl = application.getURL().toString();

		HttpSession session = request.getSession();
		if (session != null) {
			LinkedList applications =
				(LinkedList) session.getAttribute(SESSION_ATTR_APPS);
			if (applications != null)
				applications.remove(application);
		}

		response.sendRedirect(response.encodeRedirectURL(logoutUrl));
	}

	/** Get the existing application or create a new one.
	 *  Get a window within an application based on the requested URI.
	 *  @param request HTTP Request.
	 *  @param application Application to query for window.
	 *  @return Window mathing the given URI or null if not found.
	 */
	private Window getApplicationWindow(
		HttpServletRequest request,
		Application application)
		throws ServletException {

		Window window = null;

		// Find the window where the request is handled
		String path = request.getPathInfo();

		// Main window as the URI is empty
		if (path == null || path.length() == 0 || path.equals("/"))
			window = application.getMainWindow();

		// Try to search by window name
		else {
			String windowName = null;
			if (path.charAt(0) == '/')
				path = path.substring(1);
			int index = path.indexOf('/');
			if (index < 0) {
				windowName = path;
				path = "";
			} else {
				windowName = path.substring(0, index);
				path = path.substring(index + 1);
			}
			window = application.getWindow(windowName);

			// By default, we use main window
			if (window == null)
				window = application.getMainWindow();
		}

		// Create and open new debug window for application if requested
		if (this.debugMode
			&& application.getWindow(DebugWindow.WINDOW_NAME) == null)
			try {
				DebugWindow debugWindow =
					new DebugWindow(
						application,
						request.getSession(false),
						this);
				debugWindow.setWidth(370);
				debugWindow.setHeight(480);
				application.addWindow(debugWindow);
			} catch (Exception e) {
				throw new ServletException(
					"Failed to create debug window for application",
					e);
			}

		return window;
	}

	/** Get relative location of a theme resource.
	 *  @param theme Theme name
	 *  @param resource Theme resource
	 *  @return External URI specifying the resource
	 */
	public String getResourceLocation(String theme, ThemeResource resource) {

		if (resourcePath == null)
			return resource.getResourceId();
		return resourcePath + theme + "/" + resource.getResourceId();
	}

	/** Check if web adapter is in debug mode.
	 * 	Extra output is generated to log when debug mode is enabled.
	 *  @return Debug mode 
	 */
	public boolean isDebugMode() {
		return debugMode;
	}
	/** Returns the theme source.
	 * @return ThemeSource
	 */
	public ThemeSource getThemeSource() {
		return themeSource;
	}

	protected void addDirtyWindow(Application application, Window window) {
		synchronized (applicationToDirtyWindowSetMap) {
			HashSet dirtyWindows =
				(HashSet) applicationToDirtyWindowSetMap.get(application);
			if (dirtyWindows == null) {
				dirtyWindows = new HashSet();
				applicationToDirtyWindowSetMap.put(application, dirtyWindows);
			}
			dirtyWindows.add(window);
		}
	}

	protected void removeDirtyWindow(Application application, Window window) {
		synchronized (applicationToDirtyWindowSetMap) {
			HashSet dirtyWindows =
				(HashSet) applicationToDirtyWindowSetMap.get(application);
			if (dirtyWindows != null)
				dirtyWindows.remove(window);
		}
	}

	/**
	 * @see org.millstone.base.Application.WindowAttachListener#windowAttached(Application.WindowAttachEvent)
	 */
	public void windowAttached(WindowAttachEvent event) {
		event.getWindow().addListener((Paintable.RepaintRequestListener) this);

		// Add window to dirty window references if it is visible
		if (event.getWindow().isVisible())
			addDirtyWindow(event.getApplication(), event.getWindow());
	}

	/**
	 * @see org.millstone.base.Application.WindowDetachListener#windowDetached(Application.WindowDetachEvent)
	 */
	public void windowDetached(WindowDetachEvent event) {
		event.getWindow().removeListener(
			(Paintable.RepaintRequestListener) this);

		// Add dirty window reference for closing the window
		addDirtyWindow(event.getApplication(), event.getWindow());
	}

	/**
	 * @see org.millstone.base.terminal.Paintable.RepaintRequestListener#repaintRequested(Paintable.RepaintRequestEvent)
	 */
	public void repaintRequested(RepaintRequestEvent event) {

		Paintable p = event.getPaintable();
		Application app = null;
		if (p instanceof Window)
			app = ((Window) p).getApplication();

		if (app != null)
			addDirtyWindow(app, ((Window) p));

		Object lock = applicationToServerCommandStreamLock.get(app);
		if (lock != null)
			synchronized (lock) {
				lock.notifyAll();
			}
	}

	/** Get the list of dirty windows in application */
	protected Set getDirtyWindows(Application app) {
		HashSet dirtyWindows;
		synchronized (applicationToDirtyWindowSetMap) {
			dirtyWindows = (HashSet) applicationToDirtyWindowSetMap.get(app);
		}
		return dirtyWindows;
	}

	/** Remove a window from the list of dirty windows */
	private void windowPainted(Application app, Window window) {
		removeDirtyWindow(app, window);
	}

	/** Generate server commands stream. If the server commands are not requested, return false */
	private boolean handleServerCommands(
		HttpServletRequest request,
		HttpServletResponse response) {

		// Server commands are allways requested with certain parameter
		if (request.getParameter(SERVER_COMMAND_PARAM) == null)
			return false;

		// Get the application
		Application application;
		try {
			application = getApplication(request);
		} catch (MalformedURLException e) {
			return false;
		}
		if (application == null)
			return false;

		// Create continuous server commands stream
		try {

			// Writer for writing the stream
			PrintWriter w = new PrintWriter(response.getOutputStream());

			// Print necessary http page headers and padding
			w.println("<html><head></head><body>");
			for (int i = 0; i < SERVER_COMMAND_HEADER_PADDING; i++)
				w.print(' ');

			// Clock for synchronizing the stream
			Object lock = new Object();
			synchronized (applicationToServerCommandStreamLock) {
				Object oldlock =
					applicationToServerCommandStreamLock.get(application);
				if (oldlock != null)
					synchronized (oldlock) {
						oldlock.notifyAll();
					}
				applicationToServerCommandStreamLock.put(application, lock);
			}
			while (applicationToServerCommandStreamLock.get(application)
				== lock
				&& application.isRunning()) {
				synchronized (application) {

					// Session expiration
					Date lastRequest =
						(Date) applicationToLastRequestDate.get(application);
					if (lastRequest != null
						&& lastRequest.getTime()
							+ request.getSession().getMaxInactiveInterval()
								* 1000
							< System.currentTimeMillis()) {

						// Session expired, close application
						application.close();
					} else {

						// Application still alive - keep updating windows					
						Set dws = getDirtyWindows(application);
						if (dws != null && !dws.isEmpty()) {

							// For one of the dirty windows (in each application)
							// request redraw
							Window win = (Window) dws.iterator().next();
							String url = win.getURL().toString();
							w.println(
								"<script>\n"
									+ ThemeFunctionLibrary
										.getWindowRefreshScript(
										application,
										win,
										WebBrowserProbe.getTerminalType(
											request.getSession()))
									+ "</script>");

							removeDirtyWindow(application, win);

							// Windows that are closed immediately are "painted" now
							if (win.getApplication() == null
								|| !win.isVisible())
								win.requestRepaintRequests();
						}
					}
				}

				// Send the generated commands and newline immediately to browser
				w.println(" ");
				w.flush();
				response.flushBuffer();

				synchronized (lock) {
					try {
						lock.wait(SERVER_COMMAND_STREAM_MAINTAIN_PERIOD);
					} catch (InterruptedException ignored) {
					}
				}
			}
		} catch (IOException ignore) {

			// In case of an Exceptions the server command stream is
			// terminated
			synchronized (applicationToServerCommandStreamLock) {
				if (applicationToServerCommandStreamLock.get(application)
					== application)
					applicationToServerCommandStreamLock.remove(application);
			}
		}

		return true;
	}

	private class SessionBindingListener
		implements HttpSessionBindingListener {
		private LinkedList applications;
		protected SessionBindingListener(LinkedList applications) {
			this.applications = applications;
		}

		/**
		 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
		 */
		public void valueBound(HttpSessionBindingEvent arg0) {
			// We are not interested in bindings
		}

		/**
		 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
		 */
		public void valueUnbound(HttpSessionBindingEvent event) {

			// If the binding listener is unbound from the session, the
			// session must be closing
			if (event.getName().equals(SESSION_BINDING_LISTENER)) {

				// Close all applications
				Object[] apps = applications.toArray();
				for (int i = 0; i < apps.length; i++) {
					if (apps[i] != null) {

						// Close app
						 ((Application) apps[i]).close();

						// Stop application server commands stream
						Object lock =
							applicationToServerCommandStreamLock.get(apps[i]);
						if (lock != null)
							synchronized (lock) {
								lock.notifyAll();
							}
						applicationToServerCommandStreamLock.remove(apps[i]);

						// Remove application from applications list
						applications.remove(apps[i]);
					}
				}
			}
		}

	}
}
