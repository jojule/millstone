/*
 * Created on Mar 26, 2005 by IT Mill Ltd, Joonas Lehtinen
 */
package org.millstone.ajaxadapter.browser;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.millstone.base.ui.Window;

/**
 * @author IT Mill Ltd, Joonas Lehtinen
 */
public class UnsupportedBrowser extends AbstractBrowser {

    String userAgentHeader;

    public UnsupportedBrowser(String userAgentHeader) {
        this.userAgentHeader = userAgentHeader;
    }

    public void createAjaxClient(HttpServletRequest request,
            HttpServletResponse response, Window window) throws IOException {

        Writer w = response.getWriter();
        w
                .write("<html><head><title>"
                        + window.getCaption()
                        + "</title></head><body><h1>Unsupported browser agent</h1>"
                        + "<p>Your browser agent '"
                        + userAgentHeader
                        + "' is not supported Millstone Ajax "
                        + "Adapter. Please use one of the following browsers: <ul><li>Firefox</li>"
                        + "<li>Internet Explorer</li><li>Safari</li></ul>"
                        + "</body></html>");
        w.close();
    }
}
