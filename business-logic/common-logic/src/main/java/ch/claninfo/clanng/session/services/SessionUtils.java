/* $Id: SessionUtils.java 1235 2017-05-31 20:11:55Z lar $ */

package ch.claninfo.clanng.session.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import ch.claninfo.clanng.session.entities.ClanSession;

/**
 * 
 */
public class SessionUtils {

	private SessionUtils() {
		super();
	}

	public static ClanSession getSession() {
		return (ClanSession) SecurityContextHolder.getContext().getAuthentication();
	}

	public static String getUserName() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		}
		return principal.toString();
	}
}
