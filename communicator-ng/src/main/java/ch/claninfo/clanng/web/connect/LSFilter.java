/* $Id: LSFilter.java 1235 2017-05-31 20:11:55Z lar $ */

package ch.claninfo.clanng.web.connect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Configurable;
import org.xml.sax.SAXException;

import ch.claninfo.clanng.session.services.SessionUtils;
import ch.claninfo.clanng.web.model.ProzDef;
import ch.claninfo.clanng.web.model.ProzKlasse;
import ch.claninfo.clanng.web.model.ProzParamZuord;
import ch.claninfo.clanng.web.model.ProzParamZuordPk;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.connect.DefaultSenderFilter;
import ch.claninfo.common.connect.DefaultSessionFilter;
import ch.claninfo.common.connect.Method;
import ch.claninfo.common.connect.ReceiveInterface;
import ch.claninfo.common.connect.SendInterface;
import ch.claninfo.common.ias.Prozess;

/**
 * Redirects Listen & Statistiken Calls
 */
@Configurable
public class LSFilter extends DefaultSessionFilter {

	@Inject
	private EntityManager technicalEntityManager;

	@Override
	public SendInterface getSender(Object pKey) {
		return new LSSender(getInnerConnection().getSender(pKey));
	}

	class LSSender extends DefaultSenderFilter {

		Prozess prozess;
		ProzParamZuordPk id = new ProzParamZuordPk();

		public LSSender(SendInterface pInner) {
			super(pInner);
		}

		@Override
		public void endParlist() throws CommException {
			if (prozess == null) {
				super.endParlist();
			} else {
				try {
					prozess.execute();
				}
				catch (SAXException e) {
					throw new CommException(e);
				}
			}
		}

		@Override
		public void param(String pName, Object pValue, boolean pIsKey, boolean pIsOutPut) throws CommException {
			if (prozess == null) {
				super.param(pName, pValue, pIsKey, pIsOutPut);
			} else {
				id.setProzParamName(pName);
				ProzParamZuord paramZuord = technicalEntityManager.find(ProzParamZuord.class, id);
				String name = paramZuord.getAlias();
				if (name == null) {
					name = paramZuord.getProzParamName();
				}
				prozess.param(name, pValue);
			}
		}

		/**
		 * @param pBoModul
		 * @param pBoName
		 * @param pMethod
		 * @param pListener
		 * @throws CommException
		 * @see ch.claninfo.common.connect.DefaultSenderFilter#startParlist(java.lang.String,
		 * java.lang.String, ch.claninfo.common.connect.Method,
		 * ch.claninfo.common.connect.ReceiveInterface)
		 */
		@Override
		public void startParlist(String pBoModul, String pBoName, Method pMethod, ReceiveInterface pListener) throws CommException {
			if ("common".equals(pBoModul) && pBoName.startsWith("LS")) { //$NON-NLS-1$ //$NON-NLS-2$
				try {
					String prozDefName = pBoName.substring(2);
					ProzDef prozDef = technicalEntityManager.find(ProzDef.class, prozDefName);
					ProzKlasse prozKlasse = prozDef.getProzKlasse();
					Class<?> prozessClass = Class.forName(prozKlasse.getClassRef());

					prozess = (Prozess) prozessClass.newInstance();

					if (prozKlasse.getConfig() != null) {
						// Parameter aus der Prozessklasse übergeben
						Properties props = new Properties();
						props.load(new ByteArrayInputStream(prozKlasse.getConfig().getBytes()));
						for (String paramName : props.stringPropertyNames()) {
							prozess.param(paramName, props.getProperty(paramName));
						}
					}

					prozess.setEnvironment(prozDefName, prozDef.getProzessDef());

					id.setCompany(SessionUtils.getSession().getCompany());
					id.setModul(SessionUtils.getSession().getModul());
					id.setProzDefName(prozDefName);
				}
				catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
					throw new CommException(e);
				}
			} else {
				super.startParlist(pBoModul, pBoName, pMethod, pListener);
			}
		}
	}
}
