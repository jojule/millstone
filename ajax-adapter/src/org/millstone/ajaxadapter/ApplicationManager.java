/* **********************************************************************

 Millstone 
 Open Sourced User Interface Library for
 Internet Development with Java

 Millstone is a registered trademark of IT Mill Ltd
 Copyright 2000-2005 IT Mill Ltd

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

package org.millstone.ajaxadapter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.millstone.ajaxadapter.browser.Firefox;
import org.millstone.base.Application;
import org.millstone.base.Application.WindowAttachEvent;
import org.millstone.base.Application.WindowDetachEvent;
import org.millstone.base.terminal.DownloadStream;
import org.millstone.base.terminal.Paintable;
import org.millstone.base.terminal.Terminal;
import org.millstone.base.terminal.Paintable.RepaintRequestEvent;
import org.millstone.base.ui.Window;

/**
 * @author IT Mill Ltd, Joonas Lehtinen
 */
public class ApplicationManager implements Paintable.RepaintRequestListener,
Application.WindowAttachListener, Application.WindowDetachListener {

    private static String GET_PARAM_VARIABLE_CHANGES = "changeVariables";

    private static String GET_PARAM_REPAINT_ALL = "repaintAll";

    private static String GET_PARAM_UI_CHANGES_FORMAT = "format";

        private WeakHashMap applicationToVariableMapMap = new WeakHashMap();

    private HashSet dirtyPaintabletSet = new HashSet();

    private WeakHashMap paintableIdMap = new WeakHashMap();
    
    private int idSequence = 0;

    private Application application;

    public ApplicationManager(Application application) {
        this.application = application;
    }

    private VariableMap getVariableMap() {
        VariableMap vm = (VariableMap) applicationToVariableMapMap
                .get(application);
        if (vm == null) {
            vm = new VariableMap();
            applicationToVariableMapMap.put(application, vm);
        }
        return vm;
    }
    public void takeControl() {
        application.addListener((Application.WindowAttachListener) this);
        application.addListener((Application.WindowDetachListener) this);

    }

    public void releaseControl() {
        application.removeListener((Application.WindowAttachListener) this);
        application.removeListener((Application.WindowDetachListener) this);
    }

    public void handleXmlHttpRequest(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String uiChangesFormat = request
                .getParameter(GET_PARAM_UI_CHANGES_FORMAT);
        String variableChanges = request
                .getParameter(GET_PARAM_VARIABLE_CHANGES);
        boolean repaintAll = request.getParameter(GET_PARAM_REPAINT_ALL) != null;

        OutputStream out = response.getOutputStream();
        try {

            // Is this a download request from application
            DownloadStream download = null;

            // The rest of the process is synchronized with the application
            // in order to guarantee that no parallel variable handling is
            // made
            synchronized (application) {

                // Change all variables based on request parameters
                Map unhandledParameters = getVariableMap().handleVariables(
                        request);

                // Handle the URI if the application is still running
                // TODO No URI handling supported as of yet
                // if (application.isRunning())
                // 	download = handleURI(application, request, response);

                // If this is not a download request
                if (download == null) {

                    // Find the window within the application
                    Window window = null;
                    if (application.isRunning())
                        window = getApplicationWindow(request, application);

                    // Handle the unhandled parameters if the application is
                    // still running
                    if (window != null && unhandledParameters != null
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
                    Terminal terminalType = window.getTerminal();

                    // Set terminal type for the window, if not already set
                    if (terminalType == null) {

                        // TODO Terminal should be constructed in some
                        // meaningful way
                        terminalType = new Firefox();
                    }

                    // Set the response type
                    // TODO Response type should be set
                    // response.setContentType(terminalType.getContentType());

                    // Create UIDL writer
                    UIDLPaintTarget phoneTerminal = new UIDLPaintTarget(
                            getVariableMap(), this, out);

                    // Paint components
                    Set paintables;
                    if (request.getParameter(GET_PARAM_REPAINT_ALL) != null) {
                        paintables = new LinkedHashSet();
                        paintables.add(window);
                    } else
                        paintables = getDirtyComponents();
                    if (paintables != null) {
                        for (Iterator i = (new ArrayList(paintables))
                                .iterator(); i.hasNext();) {
                            Paintable p = (Paintable) i.next();
                            phoneTerminal.startTag("change");
                            phoneTerminal.addAttribute("format", "uidl");
                            phoneTerminal.addAttribute("pid",
                                    getPaintableId(p));
                            p.paint(phoneTerminal);
                            phoneTerminal.endTag("change");
                            paintablePainted(p);
                        }
                        phoneTerminal.close();
                    }
                    out.flush();
                }
            }

            // For normal requests, transform the window
            // TODO Downloads are not implemented
            // if (download != null) {
            //	handleDownload(download, request, response);
            //}

            out.flush();
            out.close();

        } catch (Throwable e) {
            // Write the error report to client
            OutputStreamWriter w = new OutputStreamWriter(out);
            PrintWriter err = new PrintWriter(w);
            err
                    .write("<html><head><title>Application Internal Error</title></head><body>");
            err.write("<h1>" + e.toString() + "</h1><pre>\n");
            e.printStackTrace(new PrintWriter(err));
            err.write("\n</pre></body></html>");
            err.close();
        } finally {

        }

    }

    /**
     * Get the existing application or create a new one. Get a window within an
     * application based on the requested URI.
     * 
     * @param request
     *            HTTP Request.
     * @param application
     *            Application to query for window.
     * @return Window mathing the given URI or null if not found.
     */
    private Window getApplicationWindow(HttpServletRequest request,
            Application application) throws ServletException {

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

        return window;
    }

    /** End application */
    private void endApplication(HttpServletRequest request,
            HttpServletResponse response, Application application)
            throws IOException {

        String logoutUrl = application.getLogoutURL();
        if (logoutUrl == null)
            logoutUrl = application.getURL().toString();

        AjaxApplicationContext context = (AjaxApplicationContext) application
                .getContext();
        if (context != null)
            context.removeApplication(application);

        response.sendRedirect(response.encodeRedirectURL(logoutUrl));
    }

    /**
     * @param window
     * @return
     */
    public synchronized String getPaintableId(Paintable paintable) {

        String id = (String) paintableIdMap.get(paintable);
        if (id == null) {
            id = "PID-" + Integer.toString(idSequence++);
            paintableIdMap.put(paintable,id);
        }
        
        return id;
    }
    
    public synchronized Set getDirtyComponents() {
        return Collections.unmodifiableSet(dirtyPaintabletSet);

    }

    public synchronized void clearDirtyComponents() {
        dirtyPaintabletSet.clear();
    }

    public void repaintRequested(RepaintRequestEvent event) {
        dirtyPaintabletSet.add(event.getPaintable());
    }

    public void paintablePainted(Paintable p) {
        dirtyPaintabletSet.remove(p);
        p.requestRepaintRequests();
    }

    public boolean isDirty(Paintable paintable) {
        return (dirtyPaintabletSet.contains(paintable));
    }

    public void windowAttached(WindowAttachEvent event) {
        event.getWindow().addListener(this);
        dirtyPaintabletSet.add(event.getWindow());
    }

    public void windowDetached(WindowDetachEvent event) {
        event.getWindow().removeListener(this);
    }

}
