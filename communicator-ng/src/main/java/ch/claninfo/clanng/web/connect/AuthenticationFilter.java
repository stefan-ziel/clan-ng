/* $Id: AuthenticationFilter.java 1248 2017-07-11 19:28:25Z lar $ */

package ch.claninfo.clanng.web.connect;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import ch.claninfo.clanng.benutzer.entities.Berechtigung;
import ch.claninfo.clanng.benutzer.entities.Rolle;
import ch.claninfo.clanng.benutzer.service.ClanUserDetailsService;
import ch.claninfo.clanng.business.logic.exceptions.BrException;
import ch.claninfo.clanng.session.entities.Firma;
import ch.claninfo.clanng.session.entities.Modul;
import ch.claninfo.clanng.session.services.ModulService;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.connect.ConnectionInterface;
import ch.claninfo.common.connect.DefaultSessionFilter;
import ch.claninfo.common.connect.PermissionRec;
import ch.claninfo.common.xml.DOMUtils;
import ch.claninfo.common.xml.XMLProtocolConsts;

/**
 * Benutzer Anmeldung und Berechtigungen verwalten
 */
@Configurable
public class AuthenticationFilter extends DefaultSessionFilter {

	@Inject
	private AuthenticationManager authenticationManager;

	@Inject
	private ClanUserDetailsService userDetailService;

	@Inject
	private EntityManager technicalEntityManager;

	/**
	 * @param pInner
	 */
	public AuthenticationFilter(ConnectionInterface pInner) {
		super(pInner);
	}

	@Override
	public void changePassword(String pUid, String pOldPassword, String pNewPassword) throws CommException {
		userDetailService.changePassword(pUid, pOldPassword, pNewPassword);
	}

	@Override
	public void connect(String pDispatcher, String pUser, String pPassword) throws CommException {
		try {
			Authentication token = new UsernamePasswordAuthenticationToken(pUser, pPassword);
			token = authenticationManager.authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(token);
		}
		catch (BadCredentialsException e) {
			userDetailService.loginFailed(pUser);
			BrException.raise(97001, pUser);
		}
		catch (CredentialsExpiredException e) {
			BrException.raise(97002, pUser);
		}
		catch (LockedException e) {
			BrException.raise(97004, pUser);
		}
		catch (DisabledException e) {
			BrException.raise(97005, pUser);
		}
		super.connect(pDispatcher, pUser, pPassword);
	}

	@Override
	public int getPasswordDaysLeft() {
		return userDetailService.getPasswordDaysLeft(getUser());
	}

	@Override
	public String getPasswordFormat() {
		return userDetailService.getPasswordFormat();
	}

	@Override
	public Node getPermissions() {
		// übler Komaptibilitäts code wahrscheinlich längst überflüssig
		Document doc = DOMUtils.newDocument();
		Element modulNode = doc.createElement(XMLProtocolConsts.MODUL_TAG);
		for (Firma company : technicalEntityManager.createQuery("SELECT f FROM Firma f", Firma.class).getResultList()) { //$NON-NLS-1$
			Text text = doc.createTextNode(company.getBez());
			Element textNode = doc.createElement(XMLProtocolConsts.TEXT_TAG);
			textNode.setAttribute(XMLProtocolConsts.ATTR_SPRCD, "1"); //$NON-NLS-1$
			textNode.appendChild(text);
			Element companyNode = doc.createElement(XMLProtocolConsts.COMPANY_TAG);
			companyNode.setAttribute(XMLProtocolConsts.ATTR_NAME, company.getCompany());
			companyNode.appendChild(textNode);
			modulNode.appendChild(companyNode);
		}

		Element permissions = doc.createElement(XMLProtocolConsts.PERMISSIONS_TAG);
		permissions.setAttribute(XMLProtocolConsts.ATTR_ACCESS, XMLProtocolConsts.VALUE_ALLOW);

		for (Modul modul : ModulService.getInstance().getModules()) {
			if (!"common".equals(modul.getProjekt())) { //$NON-NLS-1$
				Element amNode = (Element) doc.importNode(modulNode, true);
				amNode.setAttribute(XMLProtocolConsts.ATTR_NAME, modul.getProjekt());
				permissions.appendChild(amNode);
			}
		}

		doc.appendChild(permissions);
		return permissions;
	}

	@Override
	public boolean isPasswordExpired() {
		return !userDetailService.loadUserByUsername(getUserId()).isCredentialsNonExpired();
	}

	@Override
	public void sendPermissionRequest(PermissionRec[] pPermissions) throws CommException {
		String company = (String) getSessionProperties().get(SESSION_PROPERTY_COMPANY);
		String modul = (String) getSessionProperties().get(SESSION_PROPERTY_MODUL);
		UserDetails user = userDetailService.loadUserByUsername(getUserId());

		for (PermissionRec perm : pPermissions) {
			perm.setAllowed(false);
			for (GrantedAuthority ga : user.getAuthorities()) {
				if (ga instanceof Rolle) {
					Rolle r = (Rolle) ga;
					if (r.getCompany().equals(company)) {
						for (Berechtigung ber : r.getBerechtigungen()) {
							if (ber.getModul().equals(modul) && ber.getPermissionTyp() == perm.getType() && ber.getPermissionName().equals(perm.getKey())) {
								perm.setAllowed(true);
								break;
							}
						}
					}
				}
			}
		}
	}

}
