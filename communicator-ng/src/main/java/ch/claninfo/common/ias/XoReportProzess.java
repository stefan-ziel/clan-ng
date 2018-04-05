/* $Id: XoReportProzess.java 1235 2017-05-31 20:11:55Z lar $ */

package ch.claninfo.common.ias;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import ch.claninfo.clanng.session.services.SessionUtils;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.logging.LogCategory;
import ch.claninfo.common.logging.PossibleLogCategory;
import ch.claninfo.common.xml.SAXUtils;

/**
 * L&S Prozess für XO-Reports
 */
public class XoReportProzess extends AbstractProzess {

	/**
	 * Wert für interne JDBC-Verbindung
	 */
	public static final String INTERNAL_VALUE = "internal"; //$NON-NLS-1$

	/**
	 * Konfigurationsparameter für JDBC-Passwort. Wird ignoriert wenn JDBC_URL
	 * leer oder "internal".
	 */
	public static final String JDBC_PASSWORD = "JDBC_PASSWORD"; //$NON-NLS-1$

	/**
	 * Konfigurationsparameter für JDBC-Verbindungs URL. Wenn nicht vorhanden wird
	 * das BO-Protokoll verwendet, wenn "internal" wird eine Connection aus dem
	 * Pool verwendet
	 */
	public static final String JDBC_URL = "JDBC_URL"; //$NON-NLS-1$

	/**
	 * Konfigurationsparameter für XO-Server URL.
	 */
	public static final String XO_URL = "XO_URL"; //$NON-NLS-1$

	/**
	 * Verwendete URL-Encoding für den XO-Server
	 */
	public static final String XO_ENCODING = "XO_ENCODING"; //$NON-NLS-1$

	/**
	 * Konfigurationsparameter für JDBC-Anmeldebenutzer. Wird ignoriert wenn
	 * JDBC_URL leer oder "internal".
	 */
	public static final String JDBC_USER = "JDBC_USER"; //$NON-NLS-1$

	/**
	 * Konfigurationsparameter ob eine Session aufgebaut werden soll. Wird
	 * ignoriert wenn JDBC_URL leer oder "internal".
	 */
	public static final String JDBC_SESSION = "JDBC_SESSION"; //$NON-NLS-1$

	/**
	 * Konfigurationsparameter für den Reportnamen mit Pfad, falls dieser nicht in
	 * der Reportdefinition abgelegt wurde
	 */
	public static final String REPORT_NAME = "REPORTNAME"; //$NON-NLS-1$

	/**
	 * Konfigurationsparameter für den Reportnamen ohne Pfad, falls dieser nicht
	 * in der Reportdefinition abgelegt wurde
	 */
	public static final String REPORT = "REPORT"; //$NON-NLS-1$

	private static final String REPORT_PARAM = "report"; //$NON-NLS-1$
	private static final String AUFTRAGNR_PARAM = "AUFTRAGSNR"; //$NON-NLS-1$
	private static final String CONNECTION_PARAM = "connection"; //$NON-NLS-1$
	private static final String POOL_PARAM = "pool"; //$NON-NLS-1$
	private static final String SESSIONID_PARAM = "sessionid"; //$NON-NLS-1$
	private static final String MODE_PARAM = "mode"; //$NON-NLS-1$
	private static final String MODE_SYNC = "sync"; //$NON-NLS-1$
	private static final String MODE_ASYNC = "async"; //$NON-NLS-1$

	private static final LogCategory LOGGER = new LogCategory(PossibleLogCategory.DEFAULTCAT, XoReportProzess.class.getName());
	private File reportFile;

	/**
	 * @see ch.claninfo.common.ias.Prozess#execute
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void execute() throws SAXException {
		StringBuilder url = new StringBuilder();
		String encoding;

		if (hasParameter(XO_ENCODING)) {
			encoding = getStringParameter(XO_ENCODING);
		} else {
			encoding = getConfig("xoserver.encoding", "ISO-8859-1"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (hasParameter(XO_URL)) {
			url.append(getParameter(XO_URL));
		} else {
			url.append(getConfig("xoserver", null)); //$NON-NLS-1$
		}

		url.append('?').append(MODE_PARAM).append('=').append(isAsync() ? MODE_ASYNC : MODE_SYNC);

		for (Iterator i = iterator(); i.hasNext();) {
			String paramName = (String) i.next();
			addParameter(url, paramName, getStringParameter(paramName), encoding);
		}

		String jdbc = getStringParameter(JDBC_URL);
		boolean hasSession;
		if ((jdbc == null) || INTERNAL_VALUE.equals(jdbc)) {
			hasSession = !"false".equalsIgnoreCase(getStringParameter(JDBC_SESSION)); //$NON-NLS-1$
		} else if (jdbc.startsWith("pool:")) { //$NON-NLS-1$
			addParameter(url, POOL_PARAM, jdbc.substring(5), encoding);
			hasSession = !"false".equalsIgnoreCase(getStringParameter(JDBC_SESSION)); //$NON-NLS-1$
		} else {
			addParameter(url, CONNECTION_PARAM, getStringParameter(JDBC_USER) + '/' + getParameter(JDBC_PASSWORD) + '@' + jdbc, encoding);
			hasSession = getBooleanParameter(JDBC_SESSION);
		}

		if (hasSession) {
			addParameter(url, SESSIONID_PARAM, SessionUtils.getSession().getSessionId(), encoding);
		}

		if (isAsync()) {
			addParameter(url, AUFTRAGNR_PARAM, getAuftragsnummer().toString(), encoding);
		}

		String report;

		if (reportFile != null) {
			report = reportFile.getAbsolutePath();
		} else {
			report = getStringParameter(REPORT_NAME);
			if (report == null) {
				report = getStringParameter(REPORT);
				if (report == null) {
					throw new SAXException("No report definition found."); //$NON-NLS-1$
				}
				report = getConfig("oracle.report.basedir", "") + report; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		addParameter(url, REPORT_PARAM, report, encoding);

		HttpURLConnection myLink;
		try {
			LOGGER.debug(url);
			myLink = (HttpURLConnection) new URL(url.toString()).openConnection();
			try {
				myLink.setRequestMethod("GET"); //$NON-NLS-1$
				myLink.setUseCaches(false);
				myLink.setDoInput(true);
				if (myLink.getResponseCode() == 200) {
					LOGGER.debug("Call OK."); //$NON-NLS-1$
					if (!isAsync()) {
						ContentHandler result = startResult();
						SAXUtils.parse(myLink.getInputStream(), new NoDocumentFilter(result));
						endResult();
					}
				} else {
					throw new SAXException("Error " + myLink.getResponseCode() + ' ' + myLink.getResponseMessage()); //$NON-NLS-1$
				}
			}
			finally {
				myLink.disconnect();
			}
		}
		catch (IOException e) {
			throw new SAXException(e);
		}
	}

	@Override
	public void setEnvironment(String pProzessName, String pTransparentData) throws CommException {
		super.setEnvironment(pProzessName, pTransparentData);
		if (pTransparentData == null) {
			reportFile = null;
		} else {
			String baseDir = getConfig("oracle.report.basedir", ""); //$NON-NLS-1$ //$NON-NLS-2$
			String company = SessionUtils.getSession().getCompany();
			reportFile = new File(baseDir, company + '_' + pProzessName + ".xorep"); //$NON-NLS-1$
			if (!reportFile.exists() || System.currentTimeMillis() - reportFile.lastModified() > 1000 * 15) {
				try {
					try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(reportFile))) {
						writer.write(pTransparentData);
					}
				}
				catch (IOException e) {
					throw new CommException(e);
				}
			}
		}
	}

	private void addParameter(StringBuilder pUrl, String pName, String pValue, String pEncoding) throws SAXException {
		if (pName.equals(JDBC_PASSWORD)) {
			return;
		}
		if (pName.equals(JDBC_URL)) {
			return;
		}
		if (pName.equals(JDBC_USER)) {
			return;
		}
		if (pName.equals(JDBC_SESSION)) {
			return;
		}
		if (pName.equals(REPORT_NAME)) {
			return;
		}
		if (pName.equals(REPORT)) {
			return;
		}
		if (pName.equals(XO_URL)) {
			return;
		}
		if (pName.equals(XO_ENCODING)) {
			return;
		}
		try {
			pUrl.append('&').append(URLEncoder.encode(pName, pEncoding)).append('=').append(URLEncoder.encode(pValue, pEncoding));
		}
		catch (UnsupportedEncodingException e) {
			throw new SAXException(e);
		}
	}

	class NoDocumentFilter extends XMLFilterImpl {

		/**
		 * @param pChild innerer handler
		 */
		protected NoDocumentFilter(ContentHandler pChild) {
			super();
			setContentHandler(pChild);
		}

		@Override
		public void endDocument() throws SAXException {
			// NOP
		}

		@Override
		public void startDocument() throws SAXException {
			// NOP
		}

	}
}
