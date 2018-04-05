package ch.claninfo.clanng.versicherte.brs;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.claninfo.clanng.versicherte.entities.Vsstamm;

public class VsstammBr {
	private final static Logger LOGGER = LogManager.getLogger();

	public static boolean isActive(EntityManager em, BigDecimal vsnum) {
		LOGGER.debug("Checking if vsnum '{}' is active.", vsnum);

		TypedQuery<Vsstamm> query = em.createNamedQuery("Vsstam.findByVsnum", Vsstamm.class);
		query.setParameter("vsnum", vsnum);
		Vsstamm vsstamm = query.getSingleResult();

		return vsstamm.isAktiv();
	}

	public static boolean isValidWageDate(EntityManager em, BigDecimal vsnum, LocalDate gdat) {
		LOGGER.debug("Checking for vsnum '{}' if gdat '{}' is a valid wage date.", vsnum, gdat);

		TypedQuery<Vsstamm> query = em.createNamedQuery("Vsstam.findByVsnum", Vsstamm.class);
		query.setParameter("vsnum", vsnum);

		Vsstamm vsstamm = query.getSingleResult();
		return vsstamm.getEinfid().isBefore(gdat);
	}
}