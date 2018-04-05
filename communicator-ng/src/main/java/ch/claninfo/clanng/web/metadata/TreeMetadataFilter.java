/* $Id: TreeMetadataFilter.java 1187 2017-05-12 21:56:20Z zis $ */

package ch.claninfo.clanng.web.metadata;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import ch.claninfo.common.util.security.PermissionInterface;

/**
 * Basis für Metadatenfilter
 */
class TreeMetadataFilter extends XMLFilterImpl implements MetadataFilterInterface {

	PermissionHelper myPermissions;
	int myNoCopy = -1;
	String myCurrentTreeName;
	String myModul;

	/**
	 * @param pCh
	 * @param pStart
	 * @param pLength
	 * @throws SAXException
	 * @see org.xml.sax.helpers.XMLFilterImpl#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] pCh, int pStart, int pLength) throws SAXException {
		if (myNoCopy < 0) {
			super.characters(pCh, pStart, pLength);
		}
	}

	/**
	 * @param pUri
	 * @param pLocalName
	 * @param pName
	 * @throws SAXException
	 * @see org.xml.sax.helpers.XMLFilterImpl#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String pUri, String pLocalName, String pName) throws SAXException {
		if (myNoCopy < 0) {
			super.endElement(pUri, pLocalName, pName);
		}
		myNoCopy--;
	}

	/**
	 * @param pPermissions Berechtigungsklasse setzen
	 */
	@Override
	public void setPermissions(String pModul, PermissionHelper pPermissions) {
		myPermissions = pPermissions;
		myModul = pModul;
	}

	/**
	 * @see org.xml.sax.helpers.XMLFilterImpl#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		myPermissions.startGroup(myModul, PermissionInterface.PERM_TYPE_TREE);
	}

	/**
	 * @param pUri
	 * @param pLocalName
	 * @param pName
	 * @param pAtts
	 * @throws SAXException
	 * @see org.xml.sax.helpers.XMLFilterImpl#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String pUri, String pLocalName, String pName, Attributes pAtts) throws SAXException {
		if (myNoCopy < 0) {
			if ("ObjectTree".equals(pName)) { //$NON-NLS-1$
				myCurrentTreeName = pAtts.getValue("Name");//$NON-NLS-1$
			} else if ("ObjectItem".equals(pName)) { //$NON-NLS-1$
				String key = myCurrentTreeName + '.' + pAtts.getValue("View"); //$NON-NLS-1$
				if (!myPermissions.hasPermission(key)) {
					myNoCopy = 0;
				}
			}
		} else {
			myNoCopy++;
		}
		if (myNoCopy < 0) {
			super.startElement(pUri, pLocalName, pName, pAtts);
		}
	}
}
