/* $Id: XMLDocument.java 1203 2017-05-18 20:54:22Z lar $ */

package ch.claninfo.clanng.domain.types;

import java.io.StringReader;

import ch.claninfo.common.xml.DOMBuilderHandler;
import ch.claninfo.common.xml.DOMUtils;
import ch.claninfo.common.xml.SAXUtils;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 */
public class XMLDocument {

	String serializedData;
	Document document;

	public XMLDocument() {
		// Empty NULL
	}

	public XMLDocument(String pSerializedData) {
		serializedData = pSerializedData;
	}

	XMLDocument(Document pDocument) {
		document = pDocument;
	}

	@Override
	public XMLDocument clone() {
		XMLDocument clone = new XMLDocument();
		if (document != null) {
			clone.document = (Document) document.cloneNode(true);
		}
		clone.serializedData = serializedData;
		return clone;
	}

	@Override
	public boolean equals(Object pOther) {
		if (this == pOther)
			return true;
		if (pOther == null)
			return false;
		if (getClass() != pOther.getClass())
			return false;
		XMLDocument other = (XMLDocument) pOther;
		if (isEmpty())
			return other.isEmpty();
		try {
			return getString().equals(other.getString());
		}
		catch (SAXException e) {
			return false;
		}
	}

	public ContentHandler getBuilder() {
		document = new oracle.xml.parser.v2.XMLDocument();
		return new DOMBuilderHandler(document);
	}

	public Document getDocument() throws SAXException {
		if (document == null && serializedData != null) {
			SAXUtils.parse(new InputSource(new StringReader(serializedData)), getBuilder());
		}
		return document;
	}

	public String getString() throws SAXException {
		if (serializedData == null && document != null) {
			serializedData = DOMUtils.serialize(document);
		}
		return serializedData;
	}

	public boolean hasDocument() {
		return document != null;
	}

	@Override
	public int hashCode() {
		try {
			String s = getString();
			return s == null ? 0 : s.hashCode();
		}
		catch (SAXException e) {
			return super.hashCode();
		}
	}

	public boolean isEmpty() {
		return serializedData == null && document == null;
	}

	public void serialize(ContentHandler pHandler) throws SAXException {
		if (document != null) {
			DOMUtils.serialize(document, pHandler);
		} else if (serializedData != null) {
			SAXUtils.parse(new StringReader(serializedData), pHandler);
		}
	}
}
