
package ch.claninfo.clanng.codes.validation;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;

import ch.claninfo.clanng.codes.entities.Cdwbez;
import ch.claninfo.clanng.codes.entities.Cdwert;
import ch.claninfo.clanng.codes.entities.Code;
import ch.claninfo.clanng.domain.services.CodeList;
import ch.claninfo.clanng.domain.services.CodeService;
import ch.claninfo.clanng.domain.services.CodeValue;
import ch.claninfo.clanng.session.services.ModulService;
import ch.claninfo.common.util.Language;

@Component
public class CodeServiceImpl implements CodeService {

	@Inject
	private EntityManager technicalEntityManager;

	private transient Map<String, Map<String, CodeList>> codes = new HashMap<>();

	@Override
	public CodeList getCode(String pModul, String pCodeName) {
		return getCodesMap(pModul).get(pCodeName);
	}

	@Override
	public Collection<CodeList> getCodes(String modul) {
		return getCodesMap(modul).values();
	}

	@Override
	public boolean isValid(String modul, String codeName, Object value) {
		CodeList code = getCode(modul, codeName);
		return code != null && code.getValue(value) != null;
	}

	private Map<String, CodeList> getCodesMap(String modul) {
		Map<String, CodeList> res = codes.get(modul);
		if (res == null) {
			String owner = ModulService.modul2Owner(modul);
			res = new HashMap<>();
			for (Code code : technicalEntityManager.createNamedQuery("Code.findAll", Code.class).getResultList()) { //$NON-NLS-1$
				CodeListImpl cl = new CodeListImpl();
				boolean hasValue = false;
				cl.name = code.getCdnam();
				cl.lastUpdate = code.getLastUpdate();
				for (Cdwert cdwert : code.getCdwerts()) {
					if (owner.equals(cdwert.getModul())) {
						CodeValueImpl cv = new CodeValueImpl();
						LocalDateTime lu = cdwert.getLastUpdate();
						cv.value = cdwert.getCdwert();
						if (lu.compareTo(cl.lastUpdate) > 0) {
							cl.lastUpdate = lu;
						}
						for (Cdwbez cdwbez : cdwert.getCdwbezs()) {
							Text text = new Text();
							text.shortText = cdwbez.getCdwbezk();
							text.longText = cdwbez.getCdwbezl();
							cv.texts.put(Language.valueOf(cdwbez.getSprcd()).getLocale(), text);
							lu = cdwbez.getLastUpdate();
							if (lu.compareTo(cl.lastUpdate) > 0) {
								cl.lastUpdate = lu;
							}
						}
						cl.values.put(cv.value, cv);
						hasValue = true;
					}
				}
				if (hasValue) {
					res.put(cl.name, cl);
				}
			}
			codes.put(modul, res);
		}
		return res;
	}

	static class CodeListImpl implements CodeList {

		String name;
		LocalDateTime lastUpdate;
		Map<String, CodeValue> values = new HashMap<>();

		@Override
		public LocalDateTime getLastUpdate() {
			return lastUpdate;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public CodeValue getValue(Object pKey) {
			return values.get(pKey.toString());
		}

		@Override
		public Collection<CodeValue> getValues() {
			return values.values();
		}
	}

	static class CodeValueImpl implements CodeValue {

		String value;
		HashMap<Locale, Text> texts = new HashMap<Locale, Text>();

		@Override
		public String getLongText(Locale pLocale) {
			Text text = texts.get(pLocale);
			return text == null ? null : text.longText;
		}

		@Override
		public String getShortText(Locale pLocale) {
			Text text = texts.get(pLocale);
			return text == null ? null : text.shortText;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public boolean isValid(Object pValue) {
			return pValue != null && pValue.toString().equals(value);
		}

	}

	static class Text {

		String shortText;
		String longText;
	}

}
