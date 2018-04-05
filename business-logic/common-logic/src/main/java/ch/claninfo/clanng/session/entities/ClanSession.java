/* $Id: ClanSession.java 1249 2017-07-13 20:38:41Z lar $ */

package ch.claninfo.clanng.session.entities;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 */
public interface ClanSession extends Authentication {

	long getAuftragnr();

	String getCompany();

	@Override
	Object getDetails();

	String getDrucker();

	LocalDateTime getLastRequest();

	String getLoglevel();

	String getModul();

	long getProcnr();

	String getSessionId();

	SessionInformation getSessionInformation();

	int getSprcd();

	UserDetails getUser();

	void setAuftragnr(long auftragnr);

	void setCompany(String company);

	void setDrucker(String drucker);

	void setLoglevel(String loglevel);

	void setModul(String modul);

	void setProcnr(long procnr);

	void setSprcd(int sprcd);

}
