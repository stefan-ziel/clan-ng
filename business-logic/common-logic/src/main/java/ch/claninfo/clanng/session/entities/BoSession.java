
package ch.claninfo.clanng.session.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.UserDetails;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ch.claninfo.clanng.domain.entities.HistEntity;
import ch.claninfo.clanng.domain.types.XMLDocument;
import ch.claninfo.clanng.domain.types.XMLTypeProxy;

/**
 * The persistent class for the BO_SESSION database table.
 */
@Entity
@Table(schema = "ALOW", name = "BO_SESSION")
@TypeDefs({@TypeDef(name = "XMLType", typeClass = XMLTypeProxy.class)})
public class BoSession extends HistEntity implements Serializable, ClanSession {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SESSION_ID")
	private String sessionId;

	@Column(name = "BENUTZER_OBJEKT")
	@Type(type = "XMLType")
	private XMLDocument benutzerObjekt;

	private String company;
	private String drucker;
	private String loglevel;
	private String modul;
	private int sprcd;
	private String userid;
	private transient ClanSessionInformation sessionInformation;
	private transient UserDetails user;
	private transient long auftragnr;
	private transient long procnr;

	/**
	 * @return the auftragnr
	 */
	@Override
	public long getAuftragnr() {
		return auftragnr;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getUser().getAuthorities();
	}

	public XMLDocument getBenutzerObjekt() {
		return this.benutzerObjekt;
	}

	@Override
	public String getCompany() {
		return this.company;
	}

	@Override
	public Object getCredentials() {
		return getUser().getPassword();
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public String getDrucker() {
		return this.drucker;
	}

	@Override
	public LocalDateTime getLastRequest() {
		return getLastUpdate();
	}

	@Override
	public String getLoglevel() {
		return this.loglevel;
	}

	@Override
	public String getModul() {
		return this.modul;
	}

	@Override
	public String getName() {
		return getSessionId();
	}

	@Override
	public Object getPrincipal() {
		return getUser();
	}

	/**
	 * @return the procnr
	 */
	@Override
	public long getProcnr() {
		return procnr;
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public SessionInformation getSessionInformation() {
		if (sessionInformation == null) {
			sessionInformation = new ClanSessionInformation(user, sessionId, getLastRequest());
		}
		return sessionInformation;
	}

	@Override
	public int getSprcd() {
		return this.sprcd;
	}

	@Override
	public UserDetails getUser() {
		return user;
	}

	public String getUserid() {
		return this.userid;
	}

	@Override
	public boolean isAuthenticated() {
		return sessionId != null;
	}

	/**
	 * @param pAuftragnr the auftragnr to set
	 */
	@Override
	public void setAuftragnr(long pAuftragnr) {
		auftragnr = pAuftragnr;
	}

	@Override
	public void setAuthenticated(boolean pIsAuthenticated) {
		// NOP
	}

	public void setBenutzerObjekt(XMLDocument benutzerObjekt) {
		this.benutzerObjekt = benutzerObjekt;
	}

	@Override
	public void setCompany(String company) {
		this.company = company;
		saveXML();
	}

	@Override
	public void setDrucker(String drucker) {
		this.drucker = drucker;
		saveXML();
	}

	@Override
	public void setLoglevel(String loglevel) {
		this.loglevel = loglevel;
	}

	@Override
	public void setModul(String modul) {
		this.modul = modul;
		saveXML();
	}

	/**
	 * @param pProcnr the procnr to set
	 */
	@Override
	public void setProcnr(long pProcnr) {
		procnr = pProcnr;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public void setSprcd(int sprcd) {
		this.sprcd = sprcd;
		saveXML();
	}

	/**
	 * @param pUser the user to set
	 */
	public void setUser(UserDetails pUser) {
		user = pUser;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "BoSession [sessionId=" + sessionId + ", userid=" + userid + ", company=" + company + ", modul=" + modul + ", drucker=" + drucker + ", loglevel=" + loglevel + ", sprcd=" + sprcd + ", lastRequest=" + getLastRequest() + "]";
	}

	void saveXML() {
		try {
			XMLDocument doc = new XMLDocument();
			ContentHandler handler = doc.getBuilder();
			AttributesImpl atts = new AttributesImpl();
			handler.startDocument();
			handler.startElement(null, null, "SessionUserObject", atts); //$NON-NLS-1$
			write(handler, "Modul", modul); //$NON-NLS-1$
			write(handler, "Firma", company); //$NON-NLS-1$
			write(handler, "Sprache", Integer.toString(sprcd)); //$NON-NLS-1$
			write(handler, "Drucker", drucker); //$NON-NLS-1$
			handler.endElement(null, null, "SessionUserObject"); //$NON-NLS-1$
			handler.endDocument();
			setBenutzerObjekt(doc);
		}
		catch (SAXException e) {
			// OOPS
		}
	}

	private void write(ContentHandler pHandler, String pTag, String pValue) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		pHandler.startElement(null, null, pTag, atts);
		if (pValue != null && pValue.length() > 0) {
			pHandler.characters(pValue.toCharArray(), 0, pValue.length());
		}
		pHandler.endElement(null, null, pTag);
	}

	class ClanSessionInformation extends SessionInformation {

		public ClanSessionInformation(Object pPrincipal, String pSessionId, LocalDateTime pLastRequest) {
			super(pPrincipal, pSessionId, Date.from(pLastRequest.atZone(ZoneId.systemDefault()).toInstant()));
		}

		@Override
		public void refreshLastRequest() {
			super.refreshLastRequest();
			setDm(LocalDateTime.now());
		}

	}
}
