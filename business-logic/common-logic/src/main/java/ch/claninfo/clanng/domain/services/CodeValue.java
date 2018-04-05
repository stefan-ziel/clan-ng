/* $Id: CodeValue.java 1279 2017-10-25 19:49:21Z zis $ */

package ch.claninfo.clanng.domain.services;

import java.util.Locale;

/**
 * 
 */
public interface CodeValue {

	String getLongText(Locale pLocale);

	String getShortText(Locale pLocale);

	Object getValue();

	boolean isValid(Object pValue);
}
