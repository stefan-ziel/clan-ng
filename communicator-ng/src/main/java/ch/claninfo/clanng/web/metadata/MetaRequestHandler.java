/* $Id: MetaRequestHandler.java 1279 2017-10-25 19:49:21Z zis $ */

package ch.claninfo.clanng.web.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xalan.lib.sql.ConnectionPool;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import ch.claninfo.clanng.business.logic.exceptions.InvalidRequestException;
import ch.claninfo.clanng.session.entities.ClanSession;
import ch.claninfo.clanng.session.services.ClanSessionRegistry;
import ch.claninfo.clanng.session.services.ModulService;
import ch.claninfo.common.logging.LogCategory;
import ch.claninfo.common.logging.PossibleLogCategory;
import ch.claninfo.common.util.relinfo.ReleaseModule;
import ch.claninfo.common.xml.DOMUtils;
import ch.claninfo.common.xml.StreamSerializer;

/**
 * Behandlung von Metadatenrequests. Diese werden durch Zugriffe auf das
 * BOV_RULESET realisiert Die URL muss dazu folgende Form haben:
 * <p>
 * .../Communicator?meta=&lt;typ&gt;&amp;modul=&lt;modul&gt;&amp;session=&lt;
 * sessionid&gt;
 * </p>
 * <p>
 * wobei
 * <ul>
 * <li><em>typ</em>: Gewünschter Metadatentyp ('bo', 'servlet', 'view', 'code',
 * 'menu', 'guitype', 'header')</li>
 * <li><em>modul</em>: Modul für das die Metadaten geholt werden sollen
 * ('common', 'pvclan', ... )</li>
 * <li><em>sessionid</em>: Ein gültiger Sitzungsschlüssel</li>
 * </ul>
 * </p>
 * 
 * @version $Revision: 1279 $
 */
@Component
public class MetaRequestHandler {

	private static final LogCategory CAT = new LogCategory(PossibleLogCategory.DEFAULTCAT, MetaRequestHandler.class.getName());
	private static final LogCategory PERMCAT = new LogCategory(PossibleLogCategory.PERMISSIONCAT, MetaRequestHandler.class.getName());

	// XML-Element
	private static final String MODUL_TAG = "Modul"; //$NON-NLS-1$ ;
	private static final String NAME_ATTR = "Name"; //$NON-NLS-1$ ;
	private static final String VERSIONS_TAG = "Versions"; //$NON-NLS-1$ ;
	private static final String VERSION_TAG = "Version"; //$NON-NLS-1$ ;
	private static final String VERSION_ATTR = "Version"; //$NON-NLS-1$ ;
	private static final String REVISION_ATTR = "Revision"; //$NON-NLS-1$ ;
	private static final String RELEASEDATE_ATTR = "ReleaseDate"; //$NON-NLS-1$ ;
	private static final String MODUL_ATTR = "Modul"; //$NON-NLS-1$ ;
	// private static final String DBOWNER_ATTR = "DbOwner"; //$NON-NLS-1$;

	// misc
	private static final String CONTENT_TYPE_XML = "application/xml; charset=UTF-8"; //$NON-NLS-1$ ;
	private static final String CONTENT_TYPE_ZIP = "application/zip"; //$NON-NLS-1$ ;

	// url query parameter
	private static final String MODUL_PARAM = "modul"; //$NON-NLS-1$ ;
	private static final String SESSION_PARAM = "session"; //$NON-NLS-1$ ;
	private static final String META_PARAM = "meta"; //$NON-NLS-1$ ;
	private static final String ID_PARAM = "id"; //$NON-NLS-1$ ;
	private static final String COMPRESS_PARAM = "compress"; //$NON-NLS-1$ ;

	// Metatypen
	private static final String META_MODUL = "modul"; //$NON-NLS-1$ ;
	private static final String META_CODE = "code"; //$NON-NLS-1$ ;
	private static final String META_VERSION = "version"; //$NON-NLS-1$ ;
	private static final String META_STAMPS = "stamps"; //$NON-NLS-1$ ;
	private static final String META_MENU = "menu"; //$NON-NLS-1$ ;
	private static final String META_VIEW = "view"; //$NON-NLS-1$ ;
	private static final String META_TREE = "tree"; //$NON-NLS-1$ ;
	private static final String META_WF = "wf"; //$NON-NLS-1$ ;

	private static final String META_TAG = "Meta"; //$NON-NLS-1$ ;
	private static final String CDATA = "CDATA"; //$NON-NLS-1$ ;
	private static final String RELEASE_INFO_FILE = "releaseinfo.properties"; //$NON-NLS-1$ ;

	@Inject
	private MetadataLoaderInterface metadataLoader;

	@Inject
	private ClanSessionRegistry sessionRegistry;

	@Inject
	private CodeMetaService codeService;

	/**
	 * Einen Request behandeln erster Schritt: URL entziffern
	 * 
	 * @param pRequest Anfrage
	 * @param pResponse Antwort
	 * @throws IOException Antwort kann nicht geschrieben werden
	 */
	public void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException, InvalidRequestException {
		OutputStream out = getOutputStream(pRequest, pResponse);

		String type = pRequest.getParameter(META_PARAM);
		if (type == null) {
			throw new IOException("'meta' parameter missing."); //$NON-NLS-1$
		}
		try {

			if (META_VERSION.equals(type)) {
				ContentHandler outhandler = new StreamSerializer(out);
				listReleaseInfos(outhandler);
			} else {

				String sessionId = pRequest.getParameter(SESSION_PARAM);
				if (sessionId == null) {
					throw new InvalidRequestException("'session' parameter missing."); //$NON-NLS-1$
				}

				ClanSession sessionObject = sessionRegistry.getSession(sessionId);
				if (sessionObject == null) {
					throw new IOException("no valid session found."); //$NON-NLS-1$
				}
				SecurityContextHolder.getContext().setAuthentication(sessionObject);

				if (META_MODUL.equals(type)) {
					ModulService.getInstance().copyMetadata(out);
				} else if (META_WF.equals(type)) {
					String id = pRequest.getParameter(ID_PARAM);

					if (id == null) {
						throw new IOException("No workflow id informed"); //$NON-NLS-1$
					}
					// ContentHandler outhandler = new StreamSerializer(out);
					// TODO
					// WorkflowFactory.getInstance().getEngine(Integer.parseInt(id),
					// conn).getBluePrint(outhandler);
					throw new IOException("Not yet implemented"); //$NON-NLS-1$
				} else {
					String modul = pRequest.getParameter(MODUL_PARAM);

					if (modul == null) {
						throw new IOException("No modul informed", null); //$NON-NLS-1$
					}

					if (META_STAMPS.equals(type)) {
						listMetadataTimeStamps(modul, out);

					} else if (META_CODE.equals(type)) {
						codeService.copyMetadata(modul, out);

					} else {
						if (hasFilter(type)) {
							MetadataFilterInterface filter = getFilter(type);
							PERMCAT.debug(type + "Metadaten werden gemaess Rollenkonfiguration aufbereitet"); //$NON-NLS-1$
							// Wenn gefiltert werden muss dann filtern
							ContentHandler outhandler = new StreamSerializer(out);
							filter.setPermissions(modul, new PermissionHelper());
							filter.setContentHandler(outhandler);
							metadataLoader.getMetadata(modul, type, filter);
						} else {
							// sonst kopieren
							PERMCAT.debug(type + " Metadaten werden unverändert zum Client kopiert"); //$NON-NLS-1$
							ContentHandler outhandler = new StreamSerializer(out);
							metadataLoader.getMetadata(modul, type, outhandler);
						}
					}
				}
				out.close();
			}
		}
		catch (SAXException ex) {
			CAT.info("Cound not load metadata for : " + ex.getMessage()); //$NON-NLS-1$
			CAT.info(ex);
			pResponse.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getLocalizedMessage());
		}
	}

	/**
	 * Metadaten werden hochgeladen
	 * 
	 * @param pRequest Anfrage
	 * @param pPool Datenbankverbindungspool
	 * @throws IOException Antwort kann nicht geschrieben werden
	 */
	public void doPut(HttpServletRequest pRequest, ConnectionPool pPool) throws IOException {
		if (metadataLoader instanceof ResourceMetadataLoader) {
			// only in develpmentmode we do this
			try {
				Document doc = DOMUtils.parse(pRequest.getInputStream());
				Element modul = doc.getDocumentElement();
				String name = modul.getAttribute(NAME_ATTR);
				CAT.debug("Indexing metadata for " + name); //$NON-NLS-1$
				((ResourceMetadataLoader) metadataLoader).doIndex(name, modul);
			}
			catch (SAXException | ClassNotFoundException e) {
				throw new IOException(e);
			}
		} else {
			CAT.debug("Put ignored."); //$NON-NLS-1$
		}
	}

	/**
	 * Nachsehen wann das ruleset zuletzt geändert wurde
	 * 
	 * @param pModul Modul
	 * @param pTyp Rulesettyp
	 * @return Datum der letzten Änderung
	 * @throws SAXException Lesefehler
	 */
	public long getMetadataTimeStamp(String pModul, String pTyp) throws SAXException {
		LocalDateTime metaDataTs = metadataLoader.getMetadataTimeStamp(pModul, pTyp);
		// MetaRequest Schlossimplementierung
		if (hasFilter(pTyp)) {
			PermissionHelper perm = new PermissionHelper();
			return Objects.hash(metaDataTs, perm.getPermissionStamp());
		}
		return metaDataTs.hashCode();
	}

	/* @see hasFilter */
	private MetadataFilterInterface getFilter(String pTyp) {
		if (META_VIEW.equals(pTyp)) {
			return new GuiViewMetadataFilter();
		} else if (META_MENU.equals(pTyp)) {
			return new MenuMetadataFilter();
		} else if (META_TREE.equals(pTyp)) {
			return new TreeMetadataFilter();
		}
		return null;
	}

	private OutputStream getOutputStream(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException {
		OutputStream out;
		if ("true".equals(pRequest.getParameter(COMPRESS_PARAM))) {//$NON-NLS-1$
			pResponse.setContentType(CONTENT_TYPE_ZIP);
			out = new ZipOutputStream(pResponse.getOutputStream());
			((ZipOutputStream) out).putNextEntry(new ZipEntry("content.xml")); //$NON-NLS-1$

		} else {
			pResponse.setContentType(CONTENT_TYPE_XML);
			out = pResponse.getOutputStream();
		}
		return out;
	}

	/* @see getFilter */
	private boolean hasFilter(String pTyp) {
		return META_MENU.equals(pTyp) || META_VIEW.equals(pTyp) || META_TREE.equals(pTyp);
	}

	private void listMetadataTimeStamp(String pType, StreamSerializer serializer, LocalDateTime... stamps) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute(null, NAME_ATTR, NAME_ATTR, CDATA, pType);
		serializer.startElement(null, META_TAG, META_TAG, atts);
		long value = 0;
		for (LocalDateTime stamp : stamps) {
			long st = stamp.toEpochSecond(ZoneOffset.UTC) * 1000;
			value = value + (st >> (stamps.length - 1));
		}
		String text = Long.toString(value);
		PERMCAT.debug("stamp for " + pType + ':' + text); //$NON-NLS-1$
		serializer.characters(text.toCharArray(), 0, text.length());
		serializer.endElement(null, META_TAG, META_TAG);
	}

	private void listMetadataTimeStamps(String pModul, OutputStream pOut) throws IOException {
		try {
			StreamSerializer serializer = new StreamSerializer(pOut);
			AttributesImpl atts = new AttributesImpl();
			PermissionHelper perm = new PermissionHelper();
			serializer.startDocument();
			serializer.startElement(null, MODUL_TAG, MODUL_TAG, atts);
			listMetadataTimeStamp(META_CODE, serializer, codeService.getMetadataTimeStamp(pModul));
			listMetadataTimeStamp(MetadataLoaderInterface.META_TEXT, serializer, metadataLoader.getMetadataTimeStamp(pModul, MetadataLoaderInterface.META_TEXT));
			listMetadataTimeStamp(MetadataLoaderInterface.META_BO, serializer, metadataLoader.getMetadataTimeStamp(pModul, MetadataLoaderInterface.META_BO));
			listMetadataTimeStamp(MetadataLoaderInterface.META_GUITYPE, serializer, metadataLoader.getMetadataTimeStamp(pModul, MetadataLoaderInterface.META_GUITYPE));
			listMetadataTimeStamp(MetadataLoaderInterface.META_HEADER, serializer, metadataLoader.getMetadataTimeStamp(pModul, MetadataLoaderInterface.META_HEADER));
			listMetadataTimeStamp(MetadataLoaderInterface.META_MENU, serializer, metadataLoader.getMetadataTimeStamp(pModul, MetadataLoaderInterface.META_MENU), perm.getPermissionStamp());
			listMetadataTimeStamp(MetadataLoaderInterface.META_METHOD, serializer, metadataLoader.getMetadataTimeStamp(pModul, MetadataLoaderInterface.META_METHOD));
			listMetadataTimeStamp(MetadataLoaderInterface.META_TREE, serializer, metadataLoader.getMetadataTimeStamp(pModul, MetadataLoaderInterface.META_TREE), perm.getPermissionStamp());
			listMetadataTimeStamp(MetadataLoaderInterface.META_VIEW, serializer, metadataLoader.getMetadataTimeStamp(pModul, MetadataLoaderInterface.META_VIEW), perm.getPermissionStamp());
			serializer.endElement(null, MODUL_TAG, MODUL_TAG);
			serializer.endDocument();
		}
		catch (SAXException se) {
			throw new IOException(se.getMessage(), se);
		}
	}

	private void listReleaseInfos(ContentHandler pOut) throws SAXException, IOException {
		AttributesImpl atts = new AttributesImpl();
		pOut.startDocument();
		pOut.startElement(null, VERSIONS_TAG, VERSIONS_TAG, atts);
		atts = new AttributesImpl();
		atts.addAttribute(null, MODUL_ATTR, MODUL_ATTR, CDATA, ReleaseModule.COMMUNICATOR_WEBAPP.getModuleName());
		InputStream is = getClass().getResourceAsStream('/' + ReleaseModule.COMMUNICATOR_WEBAPP.getReleaseModule() + RELEASE_INFO_FILE);
		if (is != null) {
			Properties props = new Properties();
			try {
				props.load(is);
				atts.addAttribute(null, NAME_ATTR, NAME_ATTR, CDATA, props.getProperty("name")); //$NON-NLS-1$
				atts.addAttribute(null, VERSION_ATTR, VERSION_ATTR, CDATA, props.getProperty("version")); //$NON-NLS-1$
				atts.addAttribute(null, REVISION_ATTR, REVISION_ATTR, CDATA, props.getProperty("revision")); //$NON-NLS-1$
				atts.addAttribute(null, RELEASEDATE_ATTR, RELEASEDATE_ATTR, CDATA, props.getProperty("releasedate")); //$NON-NLS-1$
			}
			finally {
				is.close();
			}
		}
		pOut.startElement(null, VERSION_TAG, VERSION_TAG, atts);
		for (ReleaseModule mod : ReleaseModule.values()) {
			if (!mod.equals(ReleaseModule.COMMUNICATOR_WEBAPP)) {
				is = getClass().getResourceAsStream('/' + mod.getReleaseModule() + RELEASE_INFO_FILE);
				if (is != null) {
					Properties props = new Properties();
					try {
						props.load(is);
						atts = new AttributesImpl();
						atts.addAttribute(null, MODUL_ATTR, MODUL_ATTR, CDATA, mod.getModuleName());
						atts.addAttribute(null, NAME_ATTR, NAME_ATTR, CDATA, props.getProperty("name")); //$NON-NLS-1$
						atts.addAttribute(null, VERSION_ATTR, VERSION_ATTR, CDATA, props.getProperty("version")); //$NON-NLS-1$
						atts.addAttribute(null, REVISION_ATTR, REVISION_ATTR, CDATA, props.getProperty("revision")); //$NON-NLS-1$
						atts.addAttribute(null, RELEASEDATE_ATTR, RELEASEDATE_ATTR, CDATA, props.getProperty("releasedate")); //$NON-NLS-1$
						pOut.startElement(null, MODUL_TAG, MODUL_TAG, atts);
						pOut.endElement(null, MODUL_TAG, MODUL_TAG);
					}
					finally {
						is.close();
					}
				}
			}
		}
		pOut.endDocument();
	}

	static class NoDocumentFilter extends XMLFilterImpl {

		NoDocumentFilter(ContentHandler pInner) {
			setContentHandler(pInner);
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
