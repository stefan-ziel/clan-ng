/* $Id: MetadataFilterInterface.java 1187 2017-05-12 21:56:20Z zis $ */

package ch.claninfo.clanng.web.metadata;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLFilter;

/**
 * XML-Filter um Metadaten zu berechtigen
 */
public interface MetadataFilterInterface extends ContentHandler, XMLFilter {

	/**
	 * @param pModul das aktuelle Modul
	 * @param pPermissions Berechtigungsklasse setzen
	 */
	void setPermissions(String pModul, PermissionHelper pPermissions);
}
