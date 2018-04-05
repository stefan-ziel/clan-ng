
package ch.claninfo.clanng.web.metadata;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import ch.claninfo.common.saxconnect.FactoryMetaData;
import ch.claninfo.common.xml.AbstractFactory;
import ch.claninfo.common.xml.DOMBuilderHandler;

@Component
public class BoDef extends FactoryMetaData {

	@Inject
	MetadataLoaderInterface loader;

	@PostConstruct
	void init() {
		AbstractFactory.setLoader(new Loader() {

			@Override
			public void clear() {
				// NOP
			}

			@Override
			public Element load(String pModul, String pWhat) throws ClassNotFoundException {
				try {
					DOMBuilderHandler handler = new DOMBuilderHandler();
					loader.getMetadata(pModul, pWhat.substring(0, pWhat.length() - 7).toLowerCase(), handler);
					return ((Document) handler.getRoot()).getDocumentElement();
				}
				catch (SAXException e) {
					throw new ClassNotFoundException("unable to load definition " + pModul + '.' + pWhat, e); //$NON-NLS-1$
				}
			}
		});
	}
}
