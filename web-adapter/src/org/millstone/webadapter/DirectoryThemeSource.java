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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.millstone.webadapter.ThemeSource.ThemeException;

/**
 * Theme source for reading themes from a directory on the Filesystem.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class DirectoryThemeSource implements ThemeSource {

	private File path;
	private Theme theme;
	private long descriptionFileModTime;
	private WebAdapterServlet webAdapterServlet;

	/** Collection of subdirectory entries */
	private Collection subdirs = new LinkedList();

	/** Creates a new instance of ThemeRepository by reading the themes
	 * from a local directory.
	 * @param path Path to the source directory .
	 * @param url External URL of the repository
	 * @throws FileNotFoundException if no theme files are found
	 */
	public DirectoryThemeSource(File path, WebAdapterServlet webAdapterServlet)
		throws FileNotFoundException, IOException {

		this.path = path;
		this.theme = null;
		this.webAdapterServlet = webAdapterServlet;

		if (!this.path.isDirectory())
			throw new java.io.FileNotFoundException(
				"Theme path must be a directory ('" + this.path + "')");

		// Load description file
		File description = new File(path, Theme.DESCRIPTIONFILE);
		if (description.exists()) {
			this.theme = new Theme(description);
			this.descriptionFileModTime = description.lastModified();
		} else {
			// There was no description file found. 
			// Handle subdirectories recursively		
			File[] files = this.path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					this.subdirs.add(
						new DirectoryThemeSource(files[i], webAdapterServlet));
				} else if (files[i].getName().toLowerCase().endsWith(".jar")) {
					this.subdirs.add(
						new JarThemeSource(files[i], webAdapterServlet, ""));
				}
			}

			if (this.subdirs.isEmpty()) {
				if (webAdapterServlet.isDebugMode()) {
					Log.info(
						"DirectoryThemeSource: Ignoring empty directory: "
							+ path);
				}
			}
		}
	}

	/**
	 * @see org.millstone.webadapter.ThemeSource#getXSLFiles(Theme, WebBrowser)
	 */
	public Collection getXSLStreams(Theme theme, WebBrowser type)
		throws ThemeException {
		Collection xslFiles = new LinkedList();

		// If this directory contains a theme 
		// return XSL from this theme	
		if (this.theme != null) {

			if (webAdapterServlet.isDebugMode()) {
				Log.info("Loading theme: " + theme);
			}

			// Get modification time of the description file
			long modTime =
				new File(this.path, Theme.DESCRIPTIONFILE).lastModified();

			// If desccription file was modified reload it
			if (modTime > this.descriptionFileModTime) {
				File description = new File(path, Theme.DESCRIPTIONFILE);
				if (description.exists()) {
					try {
						this.theme = new Theme(description);
						this.descriptionFileModTime =
							description.lastModified();
					} catch (IOException e) {
						throw new ThemeException(
							"Failed to reload theme description" + e);
					}
				}
			}

			Collection fileNames = theme.getFileNames(type);

			// Add all XSL file streams
			for (Iterator i = fileNames.iterator(); i.hasNext();) {
				File f = new File(this.path, (String) i.next());
				try {
					xslFiles.add(new FileInputStream(f));
				} catch (FileNotFoundException e) {
					throw new ThemeException("XSL File not found: " + f);
				}
			}

		} else {

			// Handle subdirectories: return the first match
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				if (source.getThemes().contains(theme))
					xslFiles.addAll(source.getXSLStreams(theme, type));
			}
		}

		// Return the concatenated stream
		return xslFiles;

	}

	/**
	 * @see org.millstone.webadapter.ThemeSource#getXSLStream(Theme,WebBrowser)
	 */
	public InputStream getXSLStream(Theme theme, WebBrowser type)
		throws ThemeException {

		Collection xslStreams = new LinkedList();

		// If this directory contains a theme 
		// return XSL from this theme	
		if (this.theme != null) {

			if (webAdapterServlet.isDebugMode()) {
				Log.info("Loading theme: " + theme);
			}

			// Get modification time of the description file
			long modTime =
				new File(this.path, Theme.DESCRIPTIONFILE).lastModified();

			// If desccription file was modified reload it
			if (modTime > this.descriptionFileModTime) {
				File description = new File(path, Theme.DESCRIPTIONFILE);
				if (description.exists()) {
					try {
						this.theme = new Theme(description);
						this.descriptionFileModTime =
							description.lastModified();
					} catch (IOException e) {
						throw new ThemeException(
							"Failed to reload theme description" + e);
					}
				}
			}

			Collection fileNames = theme.getFileNames(type);

			// Add all XSL file streams
			for (Iterator i = fileNames.iterator(); i.hasNext();) {
				File f = new File(this.path, (String) i.next());
				try {
					xslStreams.add(
						new XSLInputStream(
							new FileInputStream(f),
							webAdapterServlet,
							theme));

				} catch (java.io.FileNotFoundException e) {
					throw new ThemeException("XSL File not found: " + f);
				} catch (java.io.IOException e) {
					throw new ThemeException("Failed to read XSL file. " + f);
				}
			}

		} else {

			// Handle subdirectories: return the first match
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				if (source.getThemes().contains(theme))
					xslStreams.add(source.getXSLStream(theme, type));
			}
		}

		// Return the concatenated stream
		return new SequenceInputStream(
			java.util.Collections.enumeration(xslStreams));
	}

	/**
	 * @see org.millstone.webadapter.ThemeSource#getModificationTime()
	 */
	public long getModificationTime() {

		long modTime = 0;

		// If this directory contains a theme 
		// return XSL from this theme	
		if (this.theme != null) {

			// Get modification time of the description file
			modTime = new File(this.path, Theme.DESCRIPTIONFILE).lastModified();

			// Get modification time of the themes directory itself
			if (this.path.lastModified() > modTime) {
				modTime = this.path.lastModified();
			}

			// Check modification time for all files
			Collection fileNames = theme.getFileNames();
			for (Iterator i = fileNames.iterator(); i.hasNext();) {
				File f = new File(this.path, (String) i.next());
				if (f.lastModified() > modTime) {
					modTime = f.lastModified();
				}
			}
		} else {
			// Handle subdirectories
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				long t = source.getModificationTime();
				if (t > modTime)
					modTime = t;
			}
		}

		return modTime;

	}

	/**
	 * @see org.millstone.webadapter.ThemeSource#getResource(String)
	 */
	public InputStream getResource(String resourceId)
		throws ThemeSource.ThemeException {

		// If this directory contains a theme 
		// return XSL from this theme	
		if (this.theme != null) {

			try {
				return new FileInputStream(new File(this.path, resourceId));
			} catch (FileNotFoundException e) {
				throw new ThemeSource.ThemeException(
					"Resource " + resourceId + " not found.");
			}

		} else {
			int delim = resourceId.indexOf("/");
			String subResourceName = "";
			if (delim < resourceId.length() - 1)
				subResourceName = resourceId.substring(delim + 1);
			String subSourceName = resourceId.substring(0, delim);
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				if (source.getName().equals(subSourceName)) {
					return source.getResource(subResourceName);
				}
			}
		}

		throw new ThemeSource.ThemeException(
			"Resource " + resourceId + " not found.");

	}

	/**
	 * @see org.millstone.webadapter.ThemeSource#getThemes()
	 */
	public Collection getThemes() {
		Collection themes = new LinkedList();
		if (this.theme != null) {
			themes.add(this.theme);
		} else {
			for (Iterator i = this.subdirs.iterator(); i.hasNext();) {
				ThemeSource source = (ThemeSource) i.next();
				themes.addAll(source.getThemes());
			}

		}
		return themes;
	}

	/**
	 * @see org.millstone.webadapter.ThemeSource#getName()
	 */
	public String getName() {
		return this.path.getName();
	}

	/**
	 * @see org.millstone.webadapter.ThemeSource#getThemeByName(String)
	 */
	public Theme getThemeByName(String name) {
		Collection themes = this.getThemes();
		for (Iterator i = themes.iterator(); i.hasNext();) {
			Theme t = (Theme) i.next();
			if (name != null && name.equals(t.getName()))
				return t;
		}
		return null;
	}

}
