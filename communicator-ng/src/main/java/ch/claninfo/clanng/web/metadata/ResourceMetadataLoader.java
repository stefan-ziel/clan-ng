/* $Id: ResourceMetadataLoader.java 1253 2017-07-19 13:46:06Z lar $ */

package ch.claninfo.clanng.web.metadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ch.claninfo.clanng.session.services.ModulService;
import ch.claninfo.common.logging.LogCategory;
import ch.claninfo.common.logging.PossibleLogCategory;
import ch.claninfo.common.util.CSQLException;
import ch.claninfo.common.xml.AbstractFactory;
import ch.claninfo.common.xml.DOMUtils;
import ch.claninfo.common.xml.SAXUtils;

/**
 * Lesen der Metadaten aus den Resourcen
 */
public class ResourceMetadataLoader extends AbstractFactory implements MetadataLoaderInterface {

	private static final String DEF_FILENAME = "BoDef.xml"; //$NON-NLS-1$
	private static final String MODUL_TAG = "Modul"; //$NON-NLS-1$
	private final static LogCategory CAT = new LogCategory(PossibleLogCategory.DEFAULTCAT, ResourceMetadataLoader.class.getName());
	private static final Pattern keyPattern = Pattern.compile("(?<modul>.*?)\\.(?<tag>.*?)\\.(?<name>.*)");
	private LocalDateTime boTimeStamp;

	@Override
	public void doIndex(String pModul, Node pParent) throws ClassNotFoundException {
		boTimeStamp = LocalDateTime.now();
		super.doIndex(pModul, pParent);
	}

	@Override
	public void getMetadata(String pModul, String pTyp, ContentHandler pHandler) throws SAXException {
		try {
			if (META_BO.equals(pTyp)) {
				// pseudo load:
				getConfigOrNull(pModul, DEF_FILENAME, pTyp, pTyp);
				AttributesImpl atts = new AttributesImpl();
				pHandler.startDocument();
				atts.addAttribute(null, "Name", "Name", "CDATA", pModul); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				atts.addAttribute(null, "DbOwner", "DbOwner", "CDATA", ModulService.modul2Owner(pModul)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				pHandler.startElement(null, MODUL_TAG, MODUL_TAG, atts);

				for (String key : (Iterable<String>) this::keys) {
					Matcher matcher = keyPattern.matcher(key);

					if (!matcher.find() || !Objects.equals(pModul, matcher.group("modul"))) {
						continue;
					}

					String tag = matcher.group("tag");

					if ("Domain".equals(tag) || "BusinessRule".equals(tag) || "Bo".equals(tag)) {
						DOMUtils.serialize(getConfig(pModul, DEF_FILENAME, tag, matcher.group("name")), pHandler);
					}
				}

				pHandler.endElement(null, MODUL_TAG, MODUL_TAG);
				pHandler.endDocument();
			} else {
				URL path = getPath(pModul, pTyp);
				if (path == null) {
					throw new SAXException("Resource path " + pModul + "/" + pTyp + " not found.");//$NON-NLS-1$
				}
				try (InputStream is = path.openStream()) {
					SAXUtils.parse(is, pHandler);
				}
			}
		}
		catch (ClassNotFoundException | IOException e) {
			throw new SAXException(e);
		}
	}

	/**
	 * die Resource untersuchen und deren Dateidatum zurückgeben
	 */
	@Override
	public LocalDateTime getMetadataTimeStamp(String pModul, String pTyp) throws SAXException {
		try {
			if (META_BO.equals(pTyp)) {
				if (LocalDateTime.MIN.equals(boTimeStamp)) {
					boTimeStamp = LocalDateTime.now();
				}
				return boTimeStamp;
			}

			URL loc = getPath(pModul, pTyp);
			long modifiedMilli = 0;
			if (loc != null) {
				String proto = loc.getProtocol();
				if ("file".equals(proto)) { //$NON-NLS-1$
					File file = new File(new URI(loc.toExternalForm()));
					modifiedMilli = file.lastModified();

				} else if ("jar".equals(proto) || "zip".equals(proto)) { //$NON-NLS-1$//$NON-NLS-2$
					String part = loc.toExternalForm();
					URI uri = new URI(part.substring(4, part.indexOf('!')));
					proto = uri.getScheme();
					if ("file".equals(proto)) { //$NON-NLS-1$
						File file = new File(uri);
						modifiedMilli = file.lastModified();
					} else {
						modifiedMilli = loc.openConnection().getLastModified();
					}
				} else {
					modifiedMilli = loc.openConnection().getLastModified();
				}
			}
			return Instant.ofEpochMilli(modifiedMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
		}
		catch (URISyntaxException | IOException use) {
			throw new SAXException(use);
		}
	}

	/**
	 * Url zur Resource
	 * 
	 * @param pModul
	 * @param pTyp
	 * @return URL
	 * @throws CSQLException
	 */
	private URL getPath(String pModul, String pTyp) {
		String modul = ModulService.owner2Modul(pModul);
		String prefix = pTyp.substring(0, 1).toUpperCase() + pTyp.substring(1);
		URL res = getClass().getResource("/ch/claninfo/" + modul + "/gui/resources/" + prefix + "Def.xml");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		if (res != null) {
			CAT.info("Loading metadata from: " + res.toExternalForm()); //$NON-NLS-1$
		}
		return res;
	}

}
