package ch.claninfo.clanng.versicherte.brs;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VerspendBr {

	private final static Logger LOGGER = LogManager.getLogger();

	public static boolean hasPendencies(EntityManager em, BigDecimal vsnum, LocalDate berdat) {
		LOGGER.debug("Checking if vsnum '{}' and berdat '{}' has pendencies.", vsnum, berdat);
		TypedQuery<Long> query = em.createNamedQuery("Verspend.pendenciesCount", Long.class);
		query.setParameter("vsnum", vsnum);
		query.setParameter("berdat", berdat);

		Long singleResult = query.getSingleResult();
		return singleResult > 0;
	}
}