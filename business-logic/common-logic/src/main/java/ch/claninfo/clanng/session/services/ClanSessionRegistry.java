/* $Id: ClanSessionRegistry.java 1249 2017-07-13 20:38:41Z lar $ */

package ch.claninfo.clanng.session.services;

import java.sql.Date;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import ch.claninfo.clanng.session.entities.BoSession;
import ch.claninfo.clanng.session.entities.ClanSession;
import ch.claninfo.common.connect.SessionInterface;
import ch.claninfo.common.util.Language;

/**
 * Clans session registry
 */
@Component
public class ClanSessionRegistry implements SessionRegistry, ApplicationListener<SessionDestroyedEvent> {

	private static final Log LOGGER = LogFactory.getLog(ClanSessionRegistry.class);

	@Inject
	private UserDetailsService userDetailsService;

	@Inject
	private EntityManager technicalEntityManager;

	String defaultCompany;
	String defaultModul;
	Language defaultLanguage;
	String defaultDrucker;

	public void alterSession(String pSessionId, Map<String, Object> pNewProperties) {
		EntityTransaction trans = technicalEntityManager.getTransaction();
		trans.begin();
		try {
			ClanSession session = getSession(pSessionId);
			if (session != null) {
				if (pNewProperties.containsKey(SessionInterface.SESSION_PROPERTY_COMPANY)) {
					session.setCompany((String) pNewProperties.get(SessionInterface.SESSION_PROPERTY_COMPANY));
				}
				if (pNewProperties.containsKey(SessionInterface.SESSION_PROPERTY_MODUL)) {
					session.setModul((String) pNewProperties.get(SessionInterface.SESSION_PROPERTY_MODUL));
				}
				if (pNewProperties.containsKey(SessionInterface.SESSION_PROPERTY_PRINTER)) {
					session.setDrucker((String) pNewProperties.get(SessionInterface.SESSION_PROPERTY_PRINTER));
				}
				if (pNewProperties.containsKey(SessionInterface.SESSION_PROPERTY_SPRCD)) {
					session.setSprcd((Integer) pNewProperties.get(SessionInterface.SESSION_PROPERTY_SPRCD));
				}
			}
			trans.commit();
		}
		catch (Throwable th) {
			trans.rollback();
			throw th;
		}
	}

	@Override
	public List<Object> getAllPrincipals() {
		ArrayList<Object> res = new ArrayList<>();
		for (String uid : technicalEntityManager.createQuery("SELECT DISTINCT s.userid FROM BoSession s", String.class).getResultList()) { //$NON-NLS-1$
			res.add(userDetailsService.loadUserByUsername(uid));
		}
		return res;
	}

	@Override
	public List<SessionInformation> getAllSessions(Object pPrincipal, boolean pIncludeExpiredSessions) {
		Assert.notNull(pPrincipal, "Principal required as per interface contract"); //$NON-NLS-1$

		List<BoSession> sessions = technicalEntityManager.createQuery("SELECT s FROM BoSession s WHERE s.userid=?1", BoSession.class).setParameter(1, pPrincipal).getResultList(); //$NON-NLS-1$
		List<SessionInformation> res = new ArrayList<>(sessions.size());
		for (BoSession session : sessions) {
			if (pIncludeExpiredSessions || !session.getSessionInformation().isExpired()) {
				if (session.getUser() == null && session.getUserid() != null) {
					session.setUser(userDetailsService.loadUserByUsername(session.getUserid()));
				}
				res.add(session.getSessionInformation());
			}
		}
		return res;
	}

	public String getDefaultCompany() {
		if (defaultCompany == null) {
			defaultCompany = (String) getJni("ch.claninfo.ias.default.company", "CLA"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return defaultCompany;
	}

	public String getDefaultDrucker() {
		if (defaultDrucker == null) {
			defaultDrucker = (String) getJni("ch.claninfo.ias.default.drucker", "common"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return defaultDrucker;
	}

	public Language getDefaultLanguage() {
		if (defaultLanguage == null) {
			defaultLanguage = Language.parseString((String) getJni("ch.claninfo.ias.default.sprcd", "1")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return defaultLanguage;
	}

	public String getDefaultModul() {
		if (defaultModul == null) {
			defaultModul = (String) getJni("ch.claninfo.ias.default.modul", "common"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return defaultModul;
	}

	public ClanSession getSession(String pSessionId) {
		BoSession session = technicalEntityManager.find(BoSession.class, pSessionId);
		if (session != null && session.getUser() == null && session.getUserid() != null) {
			session.setUser(userDetailsService.loadUserByUsername(session.getUserid()));
		}
		return session;
	}

	@Override
	public SessionInformation getSessionInformation(String pSessionId) {
		Assert.hasText(pSessionId, "SessionId required as per interface contract"); //$NON-NLS-1$
		ClanSession session = getSession(pSessionId);
		return session == null ? null : new SessionInformation(session.getPrincipal(), session.getSessionId(), Date.from(session.getLastRequest().atZone(ZoneId.systemDefault()).toInstant()));
	}

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		String sessionId = event.getId();
		removeSessionInformation(sessionId);
	}

	@Override
	public void refreshLastRequest(String pSessionId) {
		EntityTransaction trans = technicalEntityManager.getTransaction();
		trans.begin();
		try {
			Assert.hasText(pSessionId, "SessionId required as per interface contract"); //$NON-NLS-1$
			getSessionInformation(pSessionId).refreshLastRequest();
			trans.commit();
		}
		catch (Throwable th) {
			trans.rollback();
			throw th;
		}
	}

	@Override
	public void registerNewSession(String pSessionId, Object pPrincipal) {
		Assert.hasText(pSessionId, "SessionId required as per interface contract"); //$NON-NLS-1$
		Assert.notNull(pPrincipal, "Principal required as per interface contract"); //$NON-NLS-1$
		EntityTransaction trans = technicalEntityManager.getTransaction();
		trans.begin();
		try {

			BoSession session = new BoSession();
			UserDetails benutzer = (UserDetails) pPrincipal;
			session.setSessionId(pSessionId);
			session.setUser(benutzer);
			session.setUserid(benutzer.getUsername());
			session.setCompany(getDefaultCompany());
			session.setModul(getDefaultModul());
			session.setSprcd(getDefaultLanguage().getSprcd());
			session.setDrucker(getDefaultDrucker());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Registering session " + session); //$NON-NLS-1$
			}
			technicalEntityManager.persist(session);
			trans.commit();
		}
		catch (Throwable th) {
			trans.rollback();
			throw th;
		}
	}

	@Override
	public void removeSessionInformation(String pSessionId) {
		ClanSession session = getSession(pSessionId);
		if (session != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Removing session " + session); //$NON-NLS-1$
			}
			EntityTransaction trans = technicalEntityManager.getTransaction();
			trans.begin();
			try {
				technicalEntityManager.remove(session);
				trans.commit();
			}
			catch (Throwable th) {
				trans.rollback();
				throw th;
			}
		}

	}

	public static Object getJni(String pKey, Object pDefault) {
		Object val;
		try {
			Context initContext = (Context) new InitialContext().lookup("java:comp/env"); //$NON-NLS-1$ ;
			val = initContext.lookup(pKey);
		}
		catch (NamingException e) {
			val = null;
		}
		return val == null ? pDefault : val;
	}

}
