
package ch.claninfo.clanng.domain.services;

import java.util.Collection;

public interface CodeService {

	CodeList getCode(String modul, String codeName);

	Collection<CodeList> getCodes(String modul);

	boolean isValid(String modul, String codeName, Object value);

}
