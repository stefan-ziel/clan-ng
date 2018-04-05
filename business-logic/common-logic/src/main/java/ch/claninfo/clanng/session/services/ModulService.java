/* $Id: ModulService.java 1235 2017-05-31 20:11:55Z lar $ */

package ch.claninfo.clanng.session.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ch.claninfo.clanng.session.entities.Modul;
import ch.claninfo.common.xml.StreamSerializer;
import ch.claninfo.common.xml.XMLProtocolConsts;

/**
 * Erzeugt die liste der Module
 */
@Service
public class ModulService {

	private static AtomicReference<ModulService> INSTANCE = new AtomicReference<>();

	// XML-Element
	private static final String CDATA = "CDATA"; //$NON-NLS-1$ ;

	private static final String MODUL_TAG = "Modul"; //$NON-NLS-1$ ;
	private static final String NAME_ATTR = "Name"; //$NON-NLS-1$ ;
	private static final String ROOT_TAG = "Root"; //$NON-NLS-1$ ;
	private static final String DBOWNER_ATTR = "DbOwner"; //$NON-NLS-1$ ;
	@Inject
	private EntityManager technicalEntityManager;
	List<Modul> modules;

	public ModulService() {
		final ModulService previous = INSTANCE.getAndSet(this);
		if (previous != null)
			throw new IllegalStateException("Second modul service " + this + " created after " + previous);
	}

	public static ModulService getInstance() {
		return INSTANCE.get();
	}

	/**
	 * Öffentlicher Service: Umrechnen von Modulnamen (z.B. 'common' ) in den
	 * zugehörigen Datenbankbenutzer (z.B. 'ALOW')
	 * 
	 * @param pModul Gewünschtes Modul
	 * @return Datenbankbenutzer zum Modul
	 */
	public static String modul2Owner(String pModul) {
		for (Modul modul : getInstance().getModules()) {
			if (modul.getProjekt().equals(pModul)) {
				return modul.getDbOwner();
			}
		}
		return pModul;
	}

	/**
	 * Öffentlicher Service: Umrechnen von Datenbankbenutzer (z.B. 'ALOW') in den
	 * zugehörigen Modulnamen (z.B. 'common' )
	 * 
	 * @param pOwner Gewünschter DB-Besitzer
	 * @return Name des Moduls
	 */
	public static String owner2Modul(String pOwner) {
		for (Modul modul : getInstance().getModules()) {
			if (modul.getDbOwner().equals(pOwner)) {
				return modul.getProjekt();
			}
		}
		return pOwner;
	}

	/**
	 * Modul-Definitionen ausliefern
	 * 
	 * @param pOut Ausgabe
	 * @throws IOException SQLFehler
	 * @throws SAXException
	 */
	public void copyMetadata(OutputStream pOut) throws IOException, SAXException {
		StreamSerializer serializer = new StreamSerializer(pOut, XMLProtocolConsts.ENCODING);
		serializer.startDocument();
		AttributesImpl atts = new AttributesImpl();
		serializer.startElement(null, ROOT_TAG, ROOT_TAG, atts);
		for (Modul modul : modules) {
			atts = new AttributesImpl();
			atts.addAttribute(null, NAME_ATTR, NAME_ATTR, CDATA, modul.getProjekt());
			atts.addAttribute(null, DBOWNER_ATTR, DBOWNER_ATTR, CDATA, modul.getDbOwner());
			serializer.startElement(null, MODUL_TAG, MODUL_TAG, atts);
			for (Modul detail : modul.getBaseModules()) {
				atts = new AttributesImpl();
				atts.addAttribute(null, NAME_ATTR, NAME_ATTR, CDATA, detail.getProjekt());
				serializer.startElement(null, MODUL_TAG, MODUL_TAG, atts);
				serializer.endElement(null, MODUL_TAG, MODUL_TAG);
			}
			serializer.endElement(null, MODUL_TAG, MODUL_TAG);
		}

		serializer.endElement(null, ROOT_TAG, ROOT_TAG);
		serializer.endDocument();
	}

	/**
	 * @return the modules
	 */
	public List<Modul> getModules() {
		if (modules == null) {
			modules = technicalEntityManager.createQuery("SELECT m FROM Modul m", Modul.class).getResultList(); //$NON-NLS-1$
		}
		return modules;
	}

}
