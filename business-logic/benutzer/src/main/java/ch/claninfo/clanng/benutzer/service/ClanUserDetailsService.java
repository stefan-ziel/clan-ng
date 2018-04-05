/* $Id: ClanUserDetailsService.java 1246 2017-06-22 20:04:51Z lar $ */

package ch.claninfo.clanng.benutzer.service;

import java.time.LocalDate;
import java.time.Period;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import ch.claninfo.clanng.benutzer.entities.Benutzer;
import ch.claninfo.clanng.business.logic.exceptions.BrException;

/**
 * Userdetails from DB
 */
@Component
public class ClanUserDetailsService implements UserDetailsService {

	// TODO read configuration
	private long passwordValidity = 90;
	private String passwordFormat = ".*"; //$NON-NLS-1$

	@Inject
	EntityManager technicalEntityManager;

	public void changePassword(String pUsername, String pOldPassword, String pNewPassword) throws BrException {
		EntityTransaction trans = technicalEntityManager.getTransaction();
		trans.begin();
		try {
			Benutzer user = findBenutzer(pUsername);
			if (!user.getPassword().equals(pOldPassword)) {
				BrException.raise(97003, pUsername);
			}
			if (pNewPassword.equals(pOldPassword)) {
				BrException.raise(97311, pUsername);
			}
			for (String prevPwd : user.getPasswordHist()) {
				if (pNewPassword.equals(prevPwd)) {
					BrException.raise(97311, pUsername);
				}
			}
			user.setPassword(pNewPassword);
			user.setExpireDate(LocalDate.now().plusDays(passwordValidity));
			trans.commit();
		}
		catch (Throwable th) {
			trans.rollback();
			throw th;
		}
	}

	public Benutzer findBenutzer(String pUsername) {
		Benutzer user = technicalEntityManager.find(Benutzer.class, pUsername);
		if (user == null) {
			throw new UsernameNotFoundException(pUsername + " not found"); //$NON-NLS-1$
		}
		return user;
	}

	public int getPasswordDaysLeft(String pUsername) {
		Benutzer user = findBenutzer(pUsername);
		return Period.between(LocalDate.now(), user.getExpireDate()).getDays();

	}

	public String getPasswordFormat() {
		return passwordFormat;
	}

	/**
	 * @return the passwordValidity
	 */
	public long getPasswordValidity() {
		return passwordValidity;
	}

	@Override
	public UserDetails loadUserByUsername(String pUsername) throws UsernameNotFoundException {
		return findBenutzer(pUsername);
	}

	public void loginFailed(String pUsername) {
		try {
			Benutzer user = findBenutzer(pUsername);
			user.setLoginFailures(user.getLoginFailures() + 1);
		}
		catch (UsernameNotFoundException f) {
			// nothing to do
		}

	}

	/**
	 * @param pPasswordFormat the passwordFormat to set
	 */
	public void setPasswordFormat(String pPasswordFormat) {
		passwordFormat = pPasswordFormat;
	}

	/**
	 * @param pPasswordValidity the passwordValidity to set
	 */
	public void setPasswordValidity(long pPasswordValidity) {
		passwordValidity = pPasswordValidity;
	}
}
