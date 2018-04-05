/* $Id: AbstractProzess.java 1248 2017-07-11 19:28:25Z lar $ */

package ch.claninfo.common.ias;

import java.util.Hashtable;
import java.util.Iterator;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ch.claninfo.clanng.domain.types.XMLDocument;
import ch.claninfo.clanng.session.services.ClanSessionRegistry;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.xml.XMLProtocolConsts;

/**
 * Abstrakte Prozessklasse
 * <p>
 * Speichert die gesetzten Parameter in einer Map zwischen.
 * </p>
 * 
 * @author clan Informática do Brasil
 */
public abstract class AbstractProzess implements Prozess {

	/**
	 * Parameter für pseudo Auftragsnummer wenn so getan werden soll als ob es
	 * asynchron ist
	 */
	public static final String FAKE_ASYNC = "FAKE_ASYNC"; //$NON-NLS-1$

	private static final String CDATA = "CDATA"; //$NON-NLS-1$
	private Hashtable<String, Object> myValues = new Hashtable<>();
	private ContentHandler myResponse;
	private XMLDocument myResult;
	private boolean myFilesOpen;
	private String myProzessName;

	/**
	 * @return the prozessName
	 */
	public String getProzessName() {
		return myProzessName;
	}

	@Override
	public void param(String pName, Object pValue) {
		myValues.put(pName, pValue);
	}

	@Override
	public void setEnvironment(String pProzessName, String pTransparentData) throws CommException {
		myProzessName = pProzessName;
	}

	/**
	 * File tag für das Resultat beenden
	 * 
	 * @throws SAXException
	 */
	protected void endFile() throws SAXException {
		myResponse.endElement(null, XMLProtocolConsts.FILE_TAG, XMLProtocolConsts.FILE_TAG);
	}

	/**
	 * Result-parameter beenden
	 * 
	 * @throws SAXException
	 */
	protected void endResult() throws SAXException {
		if (myFilesOpen) {
			myResponse.endElement(null, XMLProtocolConsts.FILES_TAG, XMLProtocolConsts.FILES_TAG);
		}
		myResponse.endElement(null, XMLProtocolConsts.PAR_TAG, XMLProtocolConsts.PAR_TAG);
	}

	/**
	 * Auftragsnummer weitergeben
	 * 
	 * @return Auftragsnummer
	 */
	protected Object getAuftragsnummer() {
		// schaun ob wir asynchron spielen ...
		Object anummer = myValues.get(FAKE_ASYNC);
		if (anummer == null) {
			// wenn nicht dann ob wir asynchron sind.
			// TODO async
			anummer = "missing feature";
		}
		return anummer;
	}

	/**
	 * Liest einen Parameter Aus
	 * 
	 * @param pName Name des Parameters
	 * @return Wert des parameters
	 */
	protected boolean getBooleanParameter(String pName) {
		Object res = getParameter(pName);
		if (res instanceof Boolean) {
			return (Boolean) res;
		}
		if (res instanceof Number) {
			return ((Number) res).intValue() != 0;
		}
		return res != null && Boolean.parseBoolean(res.toString());
	}

	/**
	 * read configuration
	 * 
	 * @param pName Key
	 * @param pDefault Default value if not defined
	 * @return value for the key
	 */
	protected String getConfig(String pName, String pDefault) {
		Object val = ClanSessionRegistry.getJni("ch.claninfo.ias." + pName, pDefault);
		return val == null ? pDefault : val.toString();
	}

	/**
	 * Liest einen Parameter Aus
	 * 
	 * @param pName Name des Parameters
	 * @return Wert des parameters
	 */
	protected Object getParameter(String pName) {
		return myValues.get(pName);
	}

	/**
	 * Liest einen Parameter Aus
	 * 
	 * @param pName Name des Parameters
	 * @return Wert des parameters
	 */
	protected String getStringParameter(String pName) {
		Object res = getParameter(pName);
		return res == null ? null : res.toString();
	}

	/**
	 * Testet die Existenz eines Parameters
	 * 
	 * @param pName Name des Parameters
	 * @return Existenz des Parameters
	 */
	protected boolean hasParameter(String pName) {
		return myValues.containsKey(pName);
	}

	/**
	 * Wird der Prozess asynchron oder synchron ausgefuhrt
	 * 
	 * @return asynchron = true, synchron = false
	 */
	protected boolean isAsync() {
		return getAuftragsnummer() != null;
	}

	/**
	 * Erzeugt einen Iterator für die Keys
	 * 
	 * @return Iterator für die Keys
	 */
	protected Iterator<String> iterator() {
		return myValues.keySet().iterator();
	}

	/**
	 * File tag für das Resultat beginnen
	 * 
	 * @param pFileName Dateiname - kann auch null sein
	 * @param pContentType Mime Typ - kann auch null sein
	 * @throws SAXException
	 */
	protected void startFile(String pFileName, String pContentType) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		if (!myFilesOpen) {
			myResponse.startElement(null, XMLProtocolConsts.FILES_TAG, XMLProtocolConsts.FILES_TAG, atts);
			myFilesOpen = true;
		}

		if (pFileName != null) {
			atts.addAttribute(null, XMLProtocolConsts.ATTR_FILE_NAME, XMLProtocolConsts.ATTR_FILE_NAME, CDATA, pFileName);
		}
		if (pContentType != null) {
			atts.addAttribute(null, XMLProtocolConsts.ATTR_FILE_CONTENTTYPE, XMLProtocolConsts.ATTR_FILE_CONTENTTYPE, CDATA, pContentType);
		}
		myResponse.startElement(null, XMLProtocolConsts.FILE_TAG, XMLProtocolConsts.FILE_TAG, atts);
	}

	/**
	 * Result-parameter beginnen
	 * 
	 * @return hierein schreiben
	 * @throws SAXException
	 */
	protected ContentHandler startResult() throws SAXException {
		myResult = new XMLDocument();
		// TODO return it to sender
		myResponse = myResult.getBuilder();
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute(null, XMLProtocolConsts.ATTR_PAR_NAME, XMLProtocolConsts.ATTR_PAR_NAME, CDATA, "pRESULT"); //$NON-NLS-1$
		myResponse.startElement(null, XMLProtocolConsts.PAR_TAG, XMLProtocolConsts.PAR_TAG, atts);
		return myResponse;
	}
}
