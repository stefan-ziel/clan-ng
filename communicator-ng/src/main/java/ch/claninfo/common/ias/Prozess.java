/* $Id: Prozess.java 1198 2017-05-17 19:57:40Z zis $ */

package ch.claninfo.common.ias;

import org.xml.sax.SAXException;

import ch.claninfo.common.connect.CommException;

/**
 * Prozessklassen interface f�r Listen & Statistiken
 * 
 * @author clan Inform�tica do Brasil
 */
public interface Prozess {

	/**
	 * Prozess effektiv ausf�hren (SAX) Voraussetzung ist, dass ein entsprechender
	 * handler im gui implementiert is. momentan nur f�r XSL:FO und PDF Dokumente
	 * verf�gbar
	 * 
	 * @throws SAXException
	 * @throws CommException
	 */
	void execute() throws SAXException, CommException;

	/**
	 * �bergabe des Parameterwertes
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
