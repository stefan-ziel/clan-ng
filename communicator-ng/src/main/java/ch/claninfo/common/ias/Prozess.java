/* $Id: Prozess.java 1198 2017-05-17 19:57:40Z zis $ */

package ch.claninfo.common.ias;

import org.xml.sax.SAXException;

import ch.claninfo.common.connect.CommException;

/**
 * Prozessklassen interface für Listen & Statistiken
 * 
 * @author clan Informática do Brasil
 */
public interface Prozess {

	/**
	 * Prozess effektiv ausführen (SAX) Voraussetzung ist, dass ein entsprechender
	 * handler im gui implementiert is. momentan nur für XSL:FO und PDF Dokumente
	 * verfügbar
	 * 
	 * @throws SAXException
	 * @throws CommException
	 */
	void execute() throws SAXException, CommException;

	/**
	 * übergabe des Parameterwertes
	 * 
	 * @param pName
	 * @param pValue
	 */
	void param(String pName, Object pValue);

	/**
	 * Setzt die Aufruf Infos (SAX).
	 * 
	 * @param pProzessName name des Prozesses
	 * @param pCall Der Aufruf
	 * @param pTransparentData
	 * @throws CommException
	 */
	void setEnvironment(String pProzessName, String pTransparentData) throws CommException;

}
