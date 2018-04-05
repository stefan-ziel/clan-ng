/* $Id: MenuMetadataFilter.java 1187 2017-05-12 21:56:20Z zis $ */

package ch.claninfo.clanng.web.metadata;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import ch.claninfo.common.util.security.PermissionInterface;

/**
 * Basis für Metadatenfilter
 */
class MenuMetadataFilter extends XMLFilterImpl implements MetadataFilterInterface {

	private static final String FAVORITE_TREE_NAME = "treemenufavorites.xml"; //$NON-NLS-1$
	private static final String FAVORITE_TOOL_NAME = "toolbarfavoriten.xml"; //$NON-NLS-1$

	PermissionHelper myPermissions;
	ElementInfo myCurrent;
	int myNoCopy = -1;
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
		myCurrent.end();
		myCurrent = myCurrent.myParent;
		myNoCopy--;
	}

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
		myPermissions.startGroup(myModul, PermissionInterface.PERM_TYPE_MENU);
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
		myCurrent = new ElementInfo(myCurrent, pName, pAtts);
		if (myNoCopy < 0) {
			if ("MenuItem".equals(pName)) { //$NON-NLS-1$
				String key = pAtts.getValue("Permission"); //$NON-NLS-1$
				if (key == null) {
					key = pAtts.getValue("Command"); //$NON-NLS-1$
				}
				if (key == null) {
					if (pAtts.getValue("Text") == null) { //$NON-NLS-1$
						// MenuItem mit Aktionsklasse
						myCurrent.start();
					}
				} else if (myPermissions.hasPermission(key)) {
					myCurrent.start();
				} else {
					myNoCopy = 0;
				}
			} else if (isFavoritenTab(pAtts) || !"GuiMenu".equals(pName)) { //$NON-NLS-1$
				// GuiMenus nur öffnen wenn Menupunkte enthalten
				myCurrent.start();
			}
		} else {
			myNoCopy++;
		}
	}

	/**
	 * Für berechtigung, wenn es keine "Menu Items" gibt dann auch keine tab
	 * mehr..ausser bei favoriten, die sind immer da.
	 *
	 * @param pAtts Attributes XML Element Attributes
	 * @return boolean True falls Favoriten menu tree tab.
	 */
	private boolean isFavoritenTab(Attributes pAtts) {
		String value = pAtts.getValue("Name"); //$NON-NLS-1$
		return FAVORITE_TREE_NAME.equalsIgnoreCase(value) || FAVORITE_TOOL_NAME.equalsIgnoreCase(value);
	}

	class ElementInfo {

		String myName;
		Attributes myAtts;
		boolean myWritten = false;
		ElementInfo myParent;

		ElementInfo(ElementInfo pParent, String pName, Attributes pAtts) {
			myName = pName;
			myAtts = new AttributesImpl(pAtts);
			myParent = pParent;
		}

		void end() throws SAXException {
			if (myWritten) {
				getContentHandler().endElement(null, myName, myName);
			}
		}

		void start() throws SAXException {
			if (!myWritten) {
				if (myParent != null) {
					myParent.start();
				}
				getContentHandler().startElement(null, myName, myName, myAtts);
				myWritten = true;
			}
		}

	}

}
