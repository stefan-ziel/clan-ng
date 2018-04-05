
package ch.claninfo.clanng.web.connect;

import java.util.Map;
import javax.inject.Inject;

import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.connect.SessionInterface;
import ch.claninfo.common.connect.batch.BatchProcessingSessionFilter;
import ch.claninfo.common.saxconnect.SessionFactory;
import ch.claninfo.common.util.batch.queue.JobQueueInterface;
import org.springframework.stereotype.Component;

@Component
public class NgSessionFactory implements SessionFactory {

	@Inject
	JobQueueInterface queue;

	@Override
	public SessionInterface getSession(String sessionId) throws CommException {
		SessionInterface session = createSession();
		session.reconnect(null, sessionId);
		return session;
	}

	@Override
	public SessionInterface newSession(Map<String, Object> pSessionProperties) throws CommException {
		String sessionId = (String) pSessionProperties.get(SessionInterface.SESSION_PROPERTY_ID);
		SessionInterface defaultSession;
		if (sessionId != null) {
			defaultSession = getSession(sessionId);
			defaultSession.sendAlterSession(pSessionProperties);
		} else {
			defaultSession = createSession();
			defaultSession.getSessionProperties().putAll(pSessionProperties);
		}

		return defaultSession;
	}

	@Override
	public SessionInterface newSession(String userName, String password) throws CommException {
		SessionInterface defaultSession = createSession();
		defaultSession.connect(null, userName, password);

		return defaultSession;
	}

	private SessionInterface createSession() {
		BatchProcessingSessionFilter batchFilter = new BatchProcessingSessionFilter(new NgConnection());
		batchFilter.setQueue(queue);
		return new AuthenticationFilter(new SessionFilter(batchFilter));
	}
}
