/* $Id: DbMetadataLoader.java 1249 2017-07-13 20:38:41Z lar $ */

package ch.claninfo.clanng.web.metadata;

import java.time.LocalDateTime;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ch.claninfo.clanng.session.services.ModulService;
import ch.claninfo.clanng.session.services.SessionUtils;
import ch.claninfo.clanng.web.model.Ruleset;

/**
 * Lesen der Metadaten aus der Ruleset Tabelle
 */
public class DbMetadataLoader implements MetadataLoaderInterface {

	@Inject
	private EntityManager technicalEntityManager;

	@Override
	public void getMetadata(String pModul, String pTyp, ContentHandler pHandler) throws SAXException {
		List<Ruleset> ruleset = getCurrent(pModul, pTyp);
		if (ruleset.size() == 1) {
			ruleset.get(0).getRulesetxml().serialize(pHandler);
		} else {
			pHandler.startDocument();
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute(null, null, "Name", "CDATA", pModul); //$NON-NLS-1$ //$NON-NLS-2$
			pHandler.startElement(null, "Modul", "Modul", atts); //$NON-NLS-1$ //$NON-NLS-2$
			pHandler.endElement(null, "Modul", "Modul"); //$NON-NLS-1$ //$NON-NLS-2$
			pHandler.endDocument();
		}
	}

	@Override
	public LocalDateTime getMetadataTimeStamp(String pModul, String pTyp) throws SAXException {
		List<Ruleset> ruleset = getCurrent(pModul, pTyp);
		if (ruleset.size() == 1) {
			return ruleset.get(0).getGdat();
		}
		return LocalDateTime.MIN;
	}

	/**
	 * @param pType Als String
	 * @return Als Integer
	 */
	protected Integer getType(String pType) {
		int i = 0;
		while (i < META_KEYS.length) {
			if (pType.equals(META_KEYS[i])) {
				return i;
			}
			i++;
		}
		return null;
	}

	List<Ruleset> getCurrent(String pModul, String pTyp) {
		String owner = ModulService.modul2Owner(pModul);
		return technicalEntityManager.createQuery("SELECT r FROM Ruleset r "
		                                          + "WHERE r.company = :Company AND r.modul = :Modul AND r.rulesettyp = 7 AND r.rulesetnr = :Nummer "
		                                          + "AND r.procnr=0 AND r.gdat = (SELECT max(x.gdat) FROM  Ruleset x WHERE x.company = :Company AND x.gdat < :Gdat "
		                                          + "AND x.modul = :Modul AND x.rulesettyp = 7 AND x.rulesetnr = :Nummer AND x.procnr=0)", Ruleset.class)// //$NON-NLS-1$
		                             .setParameter("Company", SessionUtils.getSession().getCompany())// //$NON-NLS-1$
		                             .setParameter("Gdat", LocalDateTime.now())// //$NON-NLS-1$
		                             .setParameter("Modul", owner)// //$NON-NLS-1$
		                             .setParameter("Nummer", getType(pTyp))// //$NON-NLS-1$
		                             .getResultList();
	}
}