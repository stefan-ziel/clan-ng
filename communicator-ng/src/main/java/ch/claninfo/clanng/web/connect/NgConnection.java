
package ch.claninfo.clanng.web.connect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Configurable;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import ch.claninfo.common.connect.AbstractSession;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.connect.MetaDataInterface;
import ch.claninfo.common.connect.PermissionRec;
import ch.claninfo.common.connect.SendInterface;

/**
 * just creating sender with Drools
 */
@Configurable
public class NgConnection extends AbstractSession {

	private ConcurrentMap<Object, SendInterface> keyToSender = new ConcurrentHashMap<>();

	@Inject
	EntityManagerFactory emf;

	@Inject
	MetaDataInterface boDef;

	@Override
	public void changePassword(String pUid, String pOldPassword, String pNewPassword) throws CommException {
		throw new UnsupportedOperationException();
	}

	@Override
	public MetaDataInterface getMetaData() throws CommException {
		// TODO Metadata type remapping
		return boDef;
	}

	@Override
	public int getPasswordDaysLeft() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPasswordFormat() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node getPermissions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SendInterface getSender(Object key) {
		// TODO JDBC Session Context
		return keyToSender.computeIfAbsent(key, o -> new BoEntitySend(emf.createEntityManager()));
	}

	@Override
	public boolean isPasswordExpired() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendMetaRequest(String pQuery, ContentHandler pResult) throws CommException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendPermissionRequest(PermissionRec[] pPermissions) throws CommException {
		throw new UnsupportedOperationException();
	}
}
