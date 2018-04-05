
package ch.claninfo.clanng.benutzer.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import ch.claninfo.clanng.converters.ClanDateConverter;
import ch.claninfo.clanng.domain.entities.HistEntity;
import ch.claninfo.clanng.domain.types.XMLDocument;
import ch.claninfo.clanng.domain.types.XMLTypeProxy;
import ch.claninfo.common.xml.DOMUtils;

/**
 * The persistent class for the BENUTZER database table.
 */
@Entity
@Table(schema = "ALOW", name = "BENUTZER")
@TypeDefs({@TypeDef(name = "XMLType", typeClass = XMLTypeProxy.class)})
public class Benutzer extends HistEntity implements Serializable, UserDetails {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(updatable = false, unique = true, nullable = false, length = 10)
	private String userid;

	@Column(name = "BENUTZER_XML")
	@Type(type = "XMLType")
	private XMLDocument benutzerXml;

	// uni-directional many-to-many association to Rolle
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "ALOW", name = "BENUTZER_ROLLE_MAP", joinColumns = @JoinColumn(name = "USERID", referencedColumnName = "USERID", nullable = false, insertable = false, updatable = false), inverseJoinColumns = {@JoinColumn(name = "COMPANY", referencedColumnName = "COMPANY", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "ROLLE_ID", referencedColumnName = "ROLLE_ID", nullable = false, insertable = false, updatable = false)})
	private Set<Rolle> rolles;

	transient String passwordHash;
	transient String[] passwordHist;
	transient LocalDate expireDate;
	transient String defaultCompany;
	transient int xsVs;
	transient int xsMand;
	transient boolean locked;
	transient int loginFailures;
	transient HashSet<GrantedAuthority> authorities;
	transient LocalDateTime authorityStamp;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (authorities == null) {
			authorityStamp = LocalDateTime.MIN;
			authorities = new HashSet<>();
			for (Rolle rolle : getRollen()) {
				authorities.add(rolle);
				LocalDateTime stamp = rolle.getLastUpdate();
				if (stamp.isAfter(authorityStamp)) {
					authorityStamp = stamp;
				}
				for (Berechtigung ber : rolle.getBerechtigungen()) {
					authorities.add(ber);
					stamp = ber.getLastUpdate();
					if (stamp.isAfter(authorityStamp)) {
						authorityStamp = stamp;
					}
				}
			}
		}
		return authorities;
	}

	public LocalDateTime getAuthorityStamp() {
		if (authorities == null) {
			getAuthorities();
		}
		return authorityStamp;
	}

	public XMLDocument getBenutzerXml() {
		return this.benutzerXml;
	}

	/**
	 * @return the defaultCompany
	 */
	public String getDefaultCompany() {
		loadXML();
		return defaultCompany;
	}

	/**
	 * @return the expireDate
	 */
	public LocalDate getExpireDate() {
		loadXML();
		return expireDate;
	}

	/**
	 * @return the loginFailures
	 */
	public int getLoginFailures() {
		loadXML();
		return loginFailures;
	}

	@Override
	public String getPassword() {
		loadXML();
		return passwordHash;
	}

	public String[] getPasswordHist() {
		return passwordHist;
	}

	public Set<Rolle> getRollen() {
		return this.rolles;
	}

	public String getUserid() {
		return this.userid;
	}

	@Override
	public String getUsername() {
		return getUserid();
	}

	/**
	 * @return the xsMand
	 */
	public int getXsMand() {
		loadXML();
		return xsMand;
	}

	/**
	 * @return the xsVs
	 */
	public int getXsVs() {
		loadXML();
		return xsVs;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		loadXML();
		return !locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return getExpireDate().isAfter(LocalDate.now());
	}

	@Override
	public boolean isEnabled() {
		return isAccountNonLocked();
	}

	public void setBenutzerXml(XMLDocument benutzerXml) {
		this.benutzerXml = benutzerXml;
	}

	/**
	 * @param pDefaultCompany the defaultCompany to set
	 */
	public void setDefaultCompany(String pDefaultCompany) {
		defaultCompany = pDefaultCompany;
		saveXML();
	}

	/**
	 * @param pExpireDate the expireDate to set
	 */
	public void setExpireDate(LocalDate pExpireDate) {
		expireDate = pExpireDate;
		saveXML();
	}

	/**
	 * @param pLocked the locked to set
	 */
	public void setLocked(boolean pLocked) {
		locked = pLocked;
		saveXML();
	}

	/**
	 * @param pLoginFailures the loginFailures to set
	 */
	public void setLoginFailures(int pLoginFailures) {
		loginFailures = pLoginFailures;
		saveXML();
	}

	public void setPassword(String pPasswordHash) {
		passwordHist[4] = passwordHist[3];
		passwordHist[3] = passwordHist[2];
		passwordHist[2] = passwordHist[1];
		passwordHist[1] = passwordHist[0];
		passwordHist[0] = passwordHash;
		passwordHash = pPasswordHash;
		saveXML();
	}

	public void setPasswordHist(String[] pPasswordHist) {
		passwordHist = pPasswordHist;
		saveXML();
	}

	public void setRollen(Set<Rolle> rolles) {
		this.rolles = rolles;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @param pXsMand the xsMand to set
	 */
	public void setXsMand(int pXsMand) {
		xsMand = pXsMand;
		saveXML();
	}

	/**
	 * @param pXsVs the xsVs to set
	 */
	public void setXsVs(int pXsVs) {
		xsVs = pXsVs;
		saveXML();
	}

	void loadXML() {
		if (passwordHist == null) {
			try {
				passwordHist = new String[5];
				if (getBenutzerXml() != null) {
					DOMUtils.serialize(getBenutzerXml().getDocument(), new UserReader());
				}
			}
			catch (SAXException e) {
				throw new IllegalArgumentException();
			}
		}
	}

	void saveXML() {
		try {
			XMLDocument doc = new XMLDocument();
			ContentHandler handler = doc.getBuilder();
			AttributesImpl atts = new AttributesImpl();
			handler.startDocument();
			handler.startElement(null, null, "ROWSET", atts); //$NON-NLS-1$
			handler.startElement(null, null, "ROW", atts); //$NON-NLS-1$
			write(handler, "PXSVERS", Integer.toString(xsVs)); //$NON-NLS-1$
			write(handler, "PXSMNDT", Integer.toString(xsMand)); //$NON-NLS-1$
			write(handler, "PPASSWD", passwordHash); //$NON-NLS-1$
			write(handler, "PPASSWDEXP", expireDate.toString()); //$NON-NLS-1$
			write(handler, "PSTATUS", locked ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			write(handler, "PLOGINFAILURE", Integer.toString(loginFailures)); //$NON-NLS-1$
			write(handler, "PPASSWDHIST1", passwordHist[0]); //$NON-NLS-1$
			write(handler, "PPASSWDHIST2", passwordHist[1]); //$NON-NLS-1$
			write(handler, "PPASSWDHIST3", passwordHist[2]); //$NON-NLS-1$
			write(handler, "PPASSWDHIST4", passwordHist[3]); //$NON-NLS-1$
			write(handler, "PPASSWDHIST5", passwordHist[4]); //$NON-NLS-1$
			write(handler, "PPREFCOMP", defaultCompany); //$NON-NLS-1$
			handler.endElement(null, null, "ROW"); //$NON-NLS-1$
			handler.endElement(null, null, "ROWSET"); //$NON-NLS-1$
			handler.endDocument();
			setBenutzerXml(doc);
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

	class UserReader extends DefaultHandler {

		StringBuilder val;

		@Override
		public void characters(char[] pCh, int pStart, int pLength) throws SAXException {
			if (val != null) {
				val.append(pCh, pStart, pLength);
			}
		}

		@Override
		public void endElement(String pUri, String pLocalName, String pQName) throws SAXException {
			if ("PXSVERS".equals(pQName)) { //$NON-NLS-1$
				xsVs = Integer.parseInt(val.toString());
			} else if ("PXSMNDT".equals(pQName)) { //$NON-NLS-1$
				xsMand = Integer.parseInt(val.toString());
			} else if ("PPASSWD".equals(pQName)) { //$NON-NLS-1$
				passwordHash = val.toString();
			} else if ("PPASSWDEXP".equals(pQName)) { //$NON-NLS-1$
				expireDate = ClanDateConverter.parseClanDateToTemporal(val.toString(), LocalDate.class);
			} else if ("PSTATUS".equals(pQName)) { //$NON-NLS-1$
				locked = '1' == val.charAt(0);
			} else if ("PLOGINFAILURE".equals(pQName)) { //$NON-NLS-1$
				loginFailures = Integer.parseInt(val.toString());
			} else if ("PPASSWDHIST1".equals(pQName)) { //$NON-NLS-1$
				passwordHist[0] = val.toString();
			} else if ("PPASSWDHIST2".equals(pQName)) { //$NON-NLS-1$
				passwordHist[1] = val.toString();
			} else if ("PPASSWDHIST3".equals(pQName)) { //$NON-NLS-1$
				passwordHist[2] = val.toString();
			} else if ("PPASSWDHIST4".equals(pQName)) { //$NON-NLS-1$
				passwordHist[3] = val.toString();
			} else if ("PPASSWDHIST5".equals(pQName)) { //$NON-NLS-1$
				passwordHist[4] = val.toString();
			} else if ("PPREFCOMP".equals(pQName)) { //$NON-NLS-1$
				defaultCompany = val.toString();
			}
		}

		@Override
		public void startElement(String pUri, String pLocalName, String pQName, Attributes pAttributes) throws SAXException {
			if (pQName.charAt(0) == 'P') {
				val = new StringBuilder();
			}
		}
	}

}
