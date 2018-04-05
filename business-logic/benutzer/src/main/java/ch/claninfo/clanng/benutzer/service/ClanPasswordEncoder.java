/* $Id: ClanPasswordEncoder.java 1198 2017-05-17 19:57:40Z zis $ */

package ch.claninfo.clanng.benutzer.service;

import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ch.claninfo.common.util.security.PasswordEncrypter;

/**
 * Wrapper for PasswordEncrypter
 */
@Component
public class ClanPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence pRawPassword) {
		try {
			return PasswordEncrypter.getEncryptedPassword(pRawPassword.toString());
		}
		catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public boolean matches(CharSequence pRawPassword, String pEncodedPassword) {
		return pEncodedPassword == null || pRawPassword.toString().equals(pEncodedPassword) || encode(pRawPassword).equals(pEncodedPassword);
	}

}
