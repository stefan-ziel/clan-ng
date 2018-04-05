/* $Id: PermissionHelper.java 1249 2017-07-13 20:38:41Z lar $ */

package ch.claninfo.clanng.web.metadata;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import ch.claninfo.clanng.benutzer.entities.Benutzer;
import ch.claninfo.clanng.benutzer.entities.Berechtigung;
import ch.claninfo.clanng.session.services.SessionUtils;

/**
 * Serverseitige Implementierung der Berechtigung
 */
public class PermissionHelper {

	private static final String ADMINISTRATOR_NAME = "clanadmin"; //$NON-NLS-1$
	private static final String ADMINISTRATOR_ROLE = "Administrator"; //$NON-NLS-1$

	HashSet<String> authorities = new HashSet<>();
	boolean isAdmin;

	public LocalDateTime getPermissionStamp() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof Benutzer) {
				return ((Benutzer) principal).getAuthorityStamp();
			}
		}
		return LocalDateTime.MIN;
	}

	public boolean hasPermission(String pName) {
		return isAdmin || authorities.contains(pName);
	}

	public void startGroup(String pModul, int pType) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		Object principal = authentication.getPrincipal();
		authorities.clear();
		isAdmin = false;
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails) principal;
			isAdmin = ADMINISTRATOR_NAME.equals(user.getUsername());
			if (!isAdmin) {
				String company = SessionUtils.getSession().getCompany();
				String prefix = Berechtigung.buildAuthority(company, pModul, pType, ""); //$NON-NLS-1$
				int prefixLen = prefix.length();
				String adminRole = company + '/' + ADMINISTRATOR_ROLE;
				for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
					String authority = grantedAuthority.getAuthority();
					if (adminRole.equals(authority)) {
						isAdmin = true;
						break;
					}
					if (authority.startsWith(prefix)) {
						authorities.add(authority.substring(prefixLen));
					}
				}
			}
		}

	}
}
