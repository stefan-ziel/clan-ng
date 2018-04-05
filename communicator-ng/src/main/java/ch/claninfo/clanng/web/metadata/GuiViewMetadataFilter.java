/* $Id: GuiViewMetadataFilter.java 1203 2017-05-18 20:54:22Z lar $ */

package ch.claninfo.clanng.web.metadata;

import ch.claninfo.common.util.security.PermissionInterface;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter für Gui Views für Metadatenfilter
 */
class GuiViewMetadataFilter extends XMLFilterImpl implements MetadataFilterInterface {

	private static final String PARAM_TAG = "Param";//$NON-NLS-1$
	private static final String GUI_VIEW_TAG = "GuiView";//$NON-NLS-1$
	private static final String GUI_METHOD_TAG = "GuiMethod";//$NON-NLS-1$
	private static final String NAME_ATTR = "Name";//$NON-NLS-1$
	private static final String KEY_ATTR = "Key";//$NON-NLS-1$
	private static final String GUI_DATA_TAG = "GuiData";//$NON-NLS-1$
	private static final String BO_ATTR = "Bo";//$NON-NLS-1$
	private static final String CDATA = "CDATA";//$NON-NLS-1$
	private static final String FALSE_VALUE = "false";//$NON-NLS-1$
	private static final String DELETE_ATTR = "Delete";//$NON-NLS-1$
	private static final String UPDATE_ATTR = "Update";//$NON-NLS-1$
	private static final String INSERT_ATTR = "Insert";//$NON-NLS-1$
	PermissionHelper myPermissions;
	String myViewName;
	boolean myIncludeMethod;
	boolean outsideGuiMethod = true;
	String myModul;

	/**
	 * @see org.xml.sax.helpers.XMLFilterImpl#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String pUri, String pLocalName, String pName) throws SAXException {
		if (GUI_METHOD_TAG.equals(pName)) {
			outsideGuiMethod = true;
			if (myIncludeMethod) {
				super.endElement(pUri, pLocalName, pName);
			}
		} else if (PARAM_TAG.equals(pName)) {
			if (myIncludeMethod || outsideGuiMethod) {
				super.endElement(pUri, pLocalName, pName);
			}
		} else {
			super.endElement(pUri, pLocalName, pName);
		}
	}

	/**
	 * @param pModul das aktuelle Modul
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
		myPermissions.startGroup(myModul, PermissionInterface.PERM_TYPE_GUIVIEW);
	}

	/**
	 * @see org.xml.sax.helpers.XMLFilterImpl#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String pUri, String pLocalName, String pName, Attributes pAtts) throws SAXException {
		if (GUI_VIEW_TAG.equals(pName)) {
			myViewName = pAtts.getValue(NAME_ATTR);
			super.startElement(pUri, pLocalName, pName, pAtts);
		} else if (GUI_DATA_TAG.equals(pName)) {
			AttributesImpl atts = new AttributesImpl(pAtts);
			String key = myViewName + '.' + pAtts.getValue(BO_ATTR);
			if (!myPermissions.hasPermission(key + '.' + INSERT_ATTR)) {
				setAttributeToFalse(atts, INSERT_ATTR);
			}
			if (!myPermissions.hasPermission(key + '.' + UPDATE_ATTR)) {
				setAttributeToFalse(atts, UPDATE_ATTR);
			}
			if (!myPermissions.hasPermission(key + '.' + DELETE_ATTR)) {
				setAttributeToFalse(atts, DELETE_ATTR);
			}
			super.startElement(pUri, pLocalName, pName, atts);
		} else if (GUI_METHOD_TAG.equals(pName)) {
			outsideGuiMethod = false;
			myIncludeMethod = myPermissions.hasPermission(myViewName + '.' + pAtts.getValue(KEY_ATTR));
			if (myIncludeMethod) {
				super.startElement(pUri, pLocalName, pName, pAtts);
			}
		} else if (PARAM_TAG.equals(pName)) {
			if (myIncludeMethod || outsideGuiMethod) {
				super.startElement(pUri, pLocalName, pName, pAtts);
			}
		} else {
			super.startElement(pUri, pLocalName, pName, pAtts);
		}
	}

	/**
	 * Attribut auf "false" setzen
	 * 
	 * @param pAtts
	 * @param pName
	 */
	private void setAttributeToFalse(AttributesImpl pAtts, String pName) {
		int index = pAtts.getIndex(pName);
		if (index < 0) {
			pAtts.addAttribute(null, pName, pName, CDATA, FALSE_VALUE);
		} else {
			pAtts.setValue(index, FALSE_VALUE);
		}
	}
}
