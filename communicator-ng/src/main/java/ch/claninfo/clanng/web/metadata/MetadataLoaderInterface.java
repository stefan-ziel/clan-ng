/* $Id: MetadataLoaderInterface.java 1249 2017-07-13 20:38:41Z lar $ */

package ch.claninfo.clanng.web.metadata;

import java.time.LocalDateTime;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * 
 */
public interface MetadataLoaderInterface {

	/**
	 * der Ruleset Key f�r BO-Definitionen
	 */
	String META_TEXT = "text"; //$NON-NLS-1$ ;
	/**
	 * der Ruleset Key f�r BO-Definitionen
	 */
	String META_BO = "bo"; //$NON-NLS-1$ ;
	/**
	 * der Ruleset Key f�r Gui-Typ-Definitionen
	 */
	String META_GUITYPE = "guitype"; //$NON-NLS-1$ ;
	/**
	 * der Ruleset Key f�r Header-Definitionen
	 */
	String META_HEADER = "header"; //$NON-NLS-1$ ;
	/**
	 * der Ruleset Key f�r Men�-Definitionen
	 */
	String META_MENU = "menu"; //$NON-NLS-1$ ;
	/**
	 * der Ruleset Key f�r Methoden-Definitionen
	 */
	String META_METHOD = "method"; //$NON-NLS-1$ ;
	/**
	 * der Ruleset Key f�r Baum-Definitionen
	 */
	String META_TREE = "tree"; //$NON-NLS-1$ ;
	/**
	 * der Ruleset Key f�r View-Definitionen
	 */
	String META_VIEW = "view"; //$NON-NLS-1$ ;

	/**
	 * Nummerierung der Metadatentypen (entspricht Rulesetnr)
	 */
	String[] META_KEYS = {null, META_TEXT, META_BO, META_VIEW, null, META_MENU, META_GUITYPE, META_HEADER, META_TREE, META_METHOD};

	/**
	 * Metadaten als Stream
	 * 
	 * @param pModul
	 * @param pTyp
	 * @param pOut
	 * @throws SAXException
	 */
	void getMetadata(String pModul, String pTyp, ContentHandler pOut) throws SAXException;

	/**
	 * Nachsehen wann das ruleset zuletzt ge�ndert wurde
	 * 
	 * @param pModul Modul
	 * @param pTyp Rulesettyp
	 * @param pConn DB-Verbindung
	 * @return Datum der letzten �nderung
	 * @throws SAXException Lesefehler
	 */
	LocalDateTime getMetadataTimeStamp(String pModul, String pTyp) throws SAXException;
}
