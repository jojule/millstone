package org.millstone.webadapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
/**
 * @author Sami Ekblad
 *
 */
public class XSLReader implements XMLReader, ContentHandler {

	private Collection streams;
	private boolean startTagHandled = false;
	private String xslNamespace = "";
	private ContentHandler handler;
	private XMLReader reader;

	public XSLReader(XMLReader reader, Collection streams) {
		this.reader = reader;
		reader.setContentHandler(this);
		this.streams = streams;
	}
	
	/** Parse all streams given for constructor parameter.
	 *  The input parameter is ignored.
	 * @see org.xml.sax.XMLReader#parse(InputSource)
	 */
	public synchronized void parse(InputSource input)
		throws IOException, SAXException {

		startTagHandled = false;
		handler.startDocument();
		// Parse all files
		for (Iterator i = streams.iterator(); i.hasNext();) {
			InputStream in = (InputStream) i.next();
			reader.parse(new InputSource(in));

		}
		handler.endElement(xslNamespace, "stylesheet", "xsl:stylesheet");
		handler.endDocument();
	}
	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(String namespaceURI, String localName, String qName)
		throws SAXException {
		if (localName.equals("stylesheet")) {
			return; //Skip
		}
		handler.endElement(namespaceURI, localName, qName);
	}

	/**
	 * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
	 */
	public void processingInstruction(String target, String data)
		throws SAXException {
		handler.processingInstruction(target, data);
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(
		String namespaceURI,
		String localName,
		String qName,
		Attributes atts)
		throws SAXException {
		if (!startTagHandled) {
			if (localName.equals("stylesheet")) {
				startTagHandled = true;
				this.xslNamespace = namespaceURI;
			}
		} else if (localName.equals("stylesheet")) {
			return; //skip
		}
		handler.startElement(namespaceURI, localName, qName, atts);
	}
	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
		throws SAXException {
		handler.characters(ch, start, length);
	}

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		// Ignore document starts
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		//Ignore document ends
	}

	/**
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {
		handler.endPrefixMapping(prefix);
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int length)
		throws SAXException {
		handler.ignorableWhitespace(ch, start, length);
	}

	/**
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
	 */
	public void setDocumentLocator(Locator locator) {
		handler.setDocumentLocator(locator);
	}

	/**
	 * @see org.xml.sax.ContentHandler#skippedEntity(String)
	 */
	public void skippedEntity(String name) throws SAXException {
		handler.skippedEntity(name);
	}

	/**
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
	 */
	public void startPrefixMapping(String prefix, String uri)
		throws SAXException {
		handler.startPrefixMapping(prefix, uri);
	}

	/** Override the default content handler.
	 * @see org.xml.sax.XMLReader#getContentHandler()
	 */
	public ContentHandler getContentHandler() {
		return this.handler;
	}

	/** Override the default content handler.
	 * @see org.xml.sax.XMLReader#setContentHandler(ContentHandler)
	 */
	public void setContentHandler(ContentHandler handler) {
		this.handler = handler;
	}	
	/**
	 * @see org.xml.sax.XMLReader#getDTDHandler()
	 */
	public DTDHandler getDTDHandler() {
		return reader.getDTDHandler();
	}

	/**
	 * @see org.xml.sax.XMLReader#getEntityResolver()
	 */
	public EntityResolver getEntityResolver() {
		return reader.getEntityResolver();
	}

	/**
	 * @see org.xml.sax.XMLReader#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler() {
		return reader.getErrorHandler();
	}

	/**
	 * @see org.xml.sax.XMLReader#getFeature(String)
	 */
	public boolean getFeature(String name)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		return reader.getFeature(name);
	}

	/**
	 * @see org.xml.sax.XMLReader#getProperty(String)
	 */
	public Object getProperty(String name)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		return reader.getProperty(name);
	}

	/** Override the parse.
	 * @see org.xml.sax.XMLReader#parse(String)
	 */
	public void parse(String systemId) throws IOException, SAXException {
		this.parse((InputSource)null);
	}

	/**
	 * @see org.xml.sax.XMLReader#setDTDHandler(DTDHandler)
	 */
	public void setDTDHandler(DTDHandler handler) {
		reader.setDTDHandler(handler);
	}

	/**
	 * @see org.xml.sax.XMLReader#setEntityResolver(EntityResolver)
	 */
	public void setEntityResolver(EntityResolver resolver) {
		reader.setEntityResolver(resolver);
	}

	/**
	 * @see org.xml.sax.XMLReader#setErrorHandler(ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler handler) {
		reader.setErrorHandler(handler);
	}

	/**
	 * @see org.xml.sax.XMLReader#setFeature(String, boolean)
	 */
	public void setFeature(String name, boolean value)
		throws SAXNotRecognizedException, SAXNotSupportedException {
			reader.setFeature(name,value);
	}

	/**
	 * @see org.xml.sax.XMLReader#setProperty(String, Object)
	 */
	public void setProperty(String name, Object value)
		throws SAXNotRecognizedException, SAXNotSupportedException {
			reader.setProperty(name,value);
	}	
}
