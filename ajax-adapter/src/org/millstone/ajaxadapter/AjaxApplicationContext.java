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

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.WeakHashMap;

import javax.servlet.ServletContext;

import org.millstone.base.Application;
import org.millstone.base.service.ApplicationContext;

/**
 * @author IT Mill Ltd, Joonas Lehtinen
 */
public class AjaxApplicationContext implements ApplicationContext {

    private LinkedList applications = new LinkedList();

    private ServletContext servletContext;

    private LinkedList transactionListeners = new LinkedList();

    private WeakHashMap applicationToManagerMap = new WeakHashMap();

    AjaxApplicationContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    ApplicationManager getApplicationManager(Application application) {
        ApplicationManager vm = (ApplicationManager) applicationToManagerMap
                .get(application);
        if (vm == null) {
            vm = new ApplicationManager(application);
            applicationToManagerMap.put(application, vm);
        }
        return vm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.millstone.base.service.ApplicationContext#getBaseDirectory()
     */
    public File getBaseDirectory() {

        String path = servletContext.getRealPath("/");
        if (path == null)
            return null;
        return new File(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.millstone.base.service.ApplicationContext#getApplications()
     */
    public Collection getApplications() {
        return Collections.unmodifiableCollection(applications);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.millstone.base.service.ApplicationContext#addTransactionListener(org.millstone.base.service.ApplicationContext.TransactionListener)
     */
    public void addTransactionListener(TransactionListener listener) {
        transactionListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.millstone.base.service.ApplicationContext#removeTransactionListener(org.millstone.base.service.ApplicationContext.TransactionListener)
     */
    public void removeTransactionListener(TransactionListener listener) {
        transactionListeners.remove(listener);
    }

    /**
     * Create a new application.
     * 
     * @return New application instance
     */
    Application createApplication(Class applicationClass, URL applicationUrl,
            Locale locale, Properties applicationStartProperties)
            throws InstantiationException, IllegalAccessException {

        Application application = null;

        // Create new application and start it
        try {
            application = (Application) applicationClass.newInstance();
            applications.add(application);
            application.setLocale(locale);

            getApplicationManager(application).takeControl();

            application.start(applicationUrl, applicationStartProperties, this);

        } catch (IllegalAccessException e) {
            Log.error("Illegal access to application class "
                    + applicationClass.getName());
            throw e;
        } catch (InstantiationException e) {
            Log.error("Failed to instantiate application class: "
                    + applicationClass.getName());
            throw e;
        }

        return application;
    }

    void removeApplication(Application application) {
        applications.remove(application);
    }

    Application getApplication(URL applicationUrl, String servletPath) {
        // Search for the application (using the application URI) from the list
        Application application = null;
        for (Iterator i = applications.iterator(); i.hasNext()
                && application == null;) {
            Application a = (Application) i.next();
            String aPath = a.getURL().getPath();
            if (servletPath.length() < aPath.length())
                servletPath += "/";
            if (servletPath.equals(aPath))
                application = a;
        }

        // Remove stopped application from the list
        if (application != null && !application.isRunning()) {
            applications.remove(application);
            application = null;
        }

        return application;
    }
}
