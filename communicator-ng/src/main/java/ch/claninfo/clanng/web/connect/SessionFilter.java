
package ch.claninfo.clanng.web.connect;

import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.context.SecurityContextHolder;

import ch.claninfo.clanng.session.entities.ClanSession;
import ch.claninfo.clanng.session.services.ClanSessionRegistry;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.connect.ConnectionInterface;
import ch.claninfo.common.connect.DefaultSessionFilter;

/**
 * Sitzungsumgebung verwalten
 */
@Configurable
public class SessionFilter extends DefaultSessionFilter {

	private static final char[] DIGITS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '7', '8', '9', '0', '_', '-'};
	private static final Random RANDOM = new Random();
	@Inject
	private ClanSessionRegistry sessionRegistry;

	/**
	 * @param pInner
	 */
	public SessionFilter(ConnectionInterface pInner) {
		super(pInner);
	}

	@Override
	public void connect(String pDispatcher, String pUser, String pPassword) throws CommException {
		super.connect(pDispatcher, pUser, pPassword);
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String sessionId = newSessionId();
		setSessionId(sessionId);
		sessionRegistry.registerNewSession(sessionId, user);
		ClanSession session = sessionRegistry.getSession(sessionId);
		SecurityContextHolder.getContext().setAuthentication(session);
	}

	@Override
	public void disconnect() throws CommException {
		sessionRegistry.removeSessionInformation(getSessionId());
		super.disconnect();
	}

	@Override
	public void reconnect(String pDispatcher, String pSessionId) throws CommException {
		super.reconnect(pDispatcher, pSessionId);
		sessionRegistry.refreshLastRequest(pSessionId);
		ClanSession session = sessionRegistry.getSession(pSessionId);
		if (session == null) {
			throw new CommException("Session not found.");
		}
		SecurityContextHolder.getContext().setAuthentication(session);
		setUser(session.getUser().getUsername());
		getSessionProperties().put(SESSION_PROPERTY_COMPANY, session.getCompany());
		getSessionProperties().put(SESSION_PROPERTY_MODUL, session.getModul());
		getSessionProperties().put(SESSION_PROPERTY_PRINTER, session.getDrucker());
		getSessionProperties().put(SESSION_PROPERTY_SPRCD, Integer.valueOf(session.getSprcd()));
	}

	/**
	 * @param pNewProperties
	 * @throws CommException
	 * @see ch.claninfo.common.connect.DefaultSessionFilter#sendAlterSession(java.util.Map)
	 */
	@Override
	public void sendAlterSession(Map<String, Object> pNewProperties) throws CommException {
		sessionRegistry.alterSession(getSessionId(), pNewProperties);
		super.sendAlterSession(pNewProperties);
	}

	String newSessionId() {
		char[] value = new char[32];
		for (int i = 0; i < value.length; i++) {
			int r = RANDOM.nextInt(0x40);
			value[i] = DIGITS[r];
		}
		return new String(value, 0, value.length);
	}
}
