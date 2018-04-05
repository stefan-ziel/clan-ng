/* $Id: CodeList.java 1279 2017-10-25 19:49:21Z zis $ */

package ch.claninfo.clanng.domain.services;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 
 */
public interface CodeList {

	LocalDateTime getLastUpdate();

	String getName();

	CodeValue getValue(Object key);

	Collection<CodeValue> getValues();
}
