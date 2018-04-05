/* $Id: CodeMetaService.java 1279 2017-10-25 19:49:21Z zis $ */

package ch.claninfo.clanng.web.metadata;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ch.claninfo.clanng.domain.services.CodeList;
import ch.claninfo.clanng.domain.services.CodeService;
import ch.claninfo.clanng.domain.services.CodeValue;
import ch.claninfo.common.util.Language;
import ch.claninfo.common.xml.StreamSerializer;
import ch.claninfo.common.xml.XMLProtocolConsts;

/**
 * Die Codetabelle aus der Datenbank laden und cachen / Aktualität des cache
 * prüfen
 */
@Component
public class CodeMetaService {

	// XML-Element
	private static final String MODUL_TAG = "Modul"; //$NON-NLS-1$ ;
	// private static final String NAME_ATTR = "Name"; //$NON-NLS-1$;
	private static final String SHORT_ATTR = "Short"; //$NON-NLS-1$
	private static final String TIMESTMP_ATTR = "TimeStmp"; //$NON-NLS-1$
	private static final String VALUE_ATTR = "Value"; //$NON-NLS-1$
	private static final String CODEVALUE_TAG = "CodeValue"; //$NON-NLS-1$
	private static final String LANGUAGE_ATTR = "Language"; //$NON-NLS-1$
	private static final String LANGUAGETEXT_TAG = "LanguageText"; //$NON-NLS-1$
	private static final String LONG_ATTR = "Long"; //$NON-NLS-1$
	private static final String CODE_TAG = "Code"; //$NON-NLS-1$
	private static final String CDATA = "CDATA"; //$NON-NLS-1$

	@Inject
	private CodeService codes;

	/**
	 * Code-Definitionsdatei ausliefern
	 * 
	 * @param pModul Modul
	 * @param pOut Ausgabe
	 * @throws SAXException
	 */
	public void copyMetadata(String pModul, OutputStream pOut) throws SAXException {
		StreamSerializer out = new StreamSerializer(pOut);
		AttributesImpl atts = new AttributesImpl();
		out.startDocument();
		atts.addAttribute(null, XMLProtocolConsts.ATTR_NAME, XMLProtocolConsts.ATTR_NAME, CDATA, pModul);
		LocalDateTime stamp = getMetadataTimeStamp(pModul);
		long millies = stamp.toEpochSecond(ZoneOffset.UTC) * 1000;
		atts.addAttribute(null, TIMESTMP_ATTR, TIMESTMP_ATTR, CDATA, Long.toString(millies));
		out.startElement(null, XMLProtocolConsts.MODUL_TAG, XMLProtocolConsts.MODUL_TAG, atts);
		for (CodeList code : codes.getCodes(pModul)) {
			atts = new AttributesImpl();
			atts.addAttribute(null, XMLProtocolConsts.ATTR_NAME, XMLProtocolConsts.ATTR_NAME, CDATA, code.getName());
			out.startElement(null, CODE_TAG, CODE_TAG, atts);
			for (CodeValue cdwert : code.getValues()) {
				atts = new AttributesImpl();
				atts.addAttribute(null, VALUE_ATTR, VALUE_ATTR, CDATA, cdwert.getValue().toString());
				out.startElement(null, CODEVALUE_TAG, CODEVALUE_TAG, atts);
				for (Language lang : Language.values()) {
					String s = cdwert.getShortText(lang.getLocale());
					String l = cdwert.getLongText(lang.getLocale());
					if (s != null || l != null) {
						atts = new AttributesImpl();
						atts.addAttribute(null, LANGUAGE_ATTR, LANGUAGE_ATTR, CDATA, lang.getIsoLanguage());
						if (l != null) {
							atts.addAttribute(null, LONG_ATTR, LONG_ATTR, CDATA, l);
						}
						if (s != null) {
							atts.addAttribute(null, SHORT_ATTR, SHORT_ATTR, CDATA, s);
						}
						out.startElement(null, LANGUAGETEXT_TAG, LANGUAGETEXT_TAG, atts);
						out.endElement(null, LANGUAGETEXT_TAG, LANGUAGETEXT_TAG);
					}
				}
				out.endElement(null, CODEVALUE_TAG, CODEVALUE_TAG);
			}
			out.endElement(null, CODE_TAG, CODE_TAG);
		}
		out.endElement(null, MODUL_TAG, MODUL_TAG);
		out.endDocument();
	}

	/**
	 * Nachsehen wann das ruleset zuletzt geändert wurde
	 * 
	 * @param pModul Modul
	 * @return Datum der letzten Änderung
	 */
	public LocalDateTime getMetadataTimeStamp(String pModul) {
		LocalDateTime timestamp = LocalDateTime.MIN;
		for (CodeList code : codes.getCodes(pModul)) {
			LocalDateTime lu = code.getLastUpdate();
			if (lu.compareTo(timestamp) > 0) {
				timestamp = lu;
			}
		}
		return timestamp;
	}
}
