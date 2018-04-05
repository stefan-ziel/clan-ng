
package ch.claninfo.clanng.web.connect;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Configurable;

import ch.claninfo.clanng.converters.ClanDateConverter;
import ch.claninfo.clanng.domain.entities.ClanEntity;
import ch.claninfo.clanng.domain.entities.SplittableBo;
import ch.claninfo.clanng.domain.services.JpaBoFactory;
import ch.claninfo.clanng.session.services.SessionUtils;
import ch.claninfo.clanng.sqlparser.NameConversionUtils;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.connect.JPASendInterface;
import ch.claninfo.common.connect.Method;
import ch.claninfo.common.connect.MsgTarget;
import ch.claninfo.common.connect.ReceiveFactoryInterface;
import ch.claninfo.common.connect.ReceiveInterface;
import ch.claninfo.common.dao.BaseBo;
import ch.claninfo.common.util.TypeMap;

@Configurable(preConstruction = true)
public class BoEntitySend implements JPASendInterface {

	private final static Logger LOGGER = LogManager.getLogger();

	static {
		TypeMap.addConverter(new TypeMap.Converter() {
			@Override
			public <T> T convert(Class<T> pTargetClass, String pValue) {
				if (Temporal.class.isAssignableFrom(pTargetClass)) {
					return (T) ClanDateConverter.parseClanDateToTemporal(pValue, (Class<? extends Temporal>) pTargetClass);
				}
				return null;
			}
		});
	}

	private Map<String, Object> parameterMap;
	private Method currentMethod;
	private String currentModul;
	private String currentBoName;
	private EntityManager em;
	@Inject
	private KieContainer kieContainer;
	private KieSession kieSession;
	private ReceiveInterface receiveListener;
	private JpaBoFactory boFactory;

	public BoEntitySend(EntityManager pEm) {
		em = pEm;
		boFactory = new JpaBoFactory(pEm);
		kieSession = kieContainer.newKieSession();
		kieSession.setGlobal("logger", LogManager.getLogger("ch.claninfo.clanng.drools-logger"));
		kieSession.setGlobal("em", em);
		kieSession.setGlobal("session", SessionUtils.getSession());
	}

	@Override
	public void abortTransaction() {
		em.getTransaction().rollback();
	}

	@Override
	public void endParlist() throws CommException {
		LOGGER.debug("processing '{}' on Bo '{}.{}'", currentMethod, currentModul, currentBoName);

		try {
			ClanEntity.setThreadDrools(kieSession);

			SplittableBo bo = boFactory.newTransferObject(currentModul, currentBoName);
			boFactory.fillProperties(bo, parameterMap);
			kieSession.insert(bo);

			kieSession.fireAllRules();

			receiveListener.startResult();
			if (bo.isDirty()) {
				for (String changed : bo.getChangedProperties()) {
					try {
						changed = changed.substring(0, 1).toLowerCase() + changed.substring(1);
						String name = NameConversionUtils.camelCaseToClSnakeCase(changed);
						Object value = PropertyUtils.getProperty(bo, changed);
						receiveListener.setParamValue(name, convertJPAToClan(value));
					}
					catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						throw new CommException(e);
					}
				}
			}
			receiveListener.endResult();

			LOGGER.debug("finished '{}' on Bo '{}.{}'", currentMethod, currentModul, currentBoName);
		}
		finally {
			ClanEntity.setThreadDrools(null);
		}
	}

	@Override
	public void endTransaction() throws CommException {
		em.getTransaction().commit();
	}

	@Override
	public void fetch(MsgTarget msgTarget, String procnr, String txBez, String sql, List<Object> parameters, int fetchIndex, int fetchSize, int maxRowsToFetch, ReceiveFactoryInterface listener) throws CommException {
		// Dynamic SQL is no longer supported.
		throw new UnsupportedOperationException();
	}

	@Override
	public void fetch(MsgTarget msgTarget, String procnr, String txBez, String boModule, String boName, String what, String where, String orderBy, List<Object> parameters, int fetchIndex, int fetchSize, int maxRowsToFetch, ReceiveFactoryInterface listener) throws CommException {
		Class<? extends SplittableBo> boClass = boFactory.getBoClass(boModule, boName);
		List<? extends SplittableBo> results = boFactory.nativeQuery(boClass, where, orderBy);
		sendResult(results, fetchIndex, fetchSize, listener);
	}

	@Override
	public void fetch(String procnr, String txBez, String boModule, String boName, List<String> what, String where, String orderBy, List<Object> parameters, int fetchIndex, int fetchSize, int maxRowsToFetch, ReceiveFactoryInterface listener) throws CommException {
		Class<? extends SplittableBo> boClass = boFactory.getBoClass(boModule, boName);
		List<? extends SplittableBo> results = boFactory.asList(boClass, where, orderBy);
		sendResult(results, fetchIndex, fetchSize, listener);
	}

	@Override
	public void param(String name, Object value, boolean isKey, boolean isOutPut) throws CommException {
		if (value instanceof Date) {
			value = ClanDateConverter.clanDateTimeToJava((Date) value);
		}
		parameterMap.put(NameConversionUtils.snakeCaseToCamelCase(name), value);
	}

	@Override
	public void param(String name, String boundTo) throws CommException {
		// We do not support mass processing
		throw new UnsupportedOperationException();
	}

	@Override
	public void startParlist(String boModule, String boName, Method method, ReceiveInterface listener) throws CommException {
		currentModul = boModule;
		currentBoName = boName;
		currentMethod = method;
		receiveListener = listener;
		parameterMap = new HashMap<>();
	}

	@Override
	public void startParlist(String boModul, String boName, Method method, String selModul, String selBo, String selWhere, boolean parallel) throws CommException {
		// We do not support mass processing
		throw new UnsupportedOperationException();
	}

	@Override
	public void startTransaction(MsgTarget msgTarget, String procnr, boolean sync, String txBez, boolean txRollback, String asyncExecutionDateTime) throws CommException {
		if (!sync || txRollback || asyncExecutionDateTime != null) {
			throw new UnsupportedOperationException();
		}

		em.getTransaction().begin();
	}

	/**
	 * Sets results in the listener interface
	 *
	 * @param results  results to send
	 * @param listener The interface component that will effectively receive the results.
	 */
	private void sendResult(List<? extends SplittableBo> results, int startIndex, int resultCount, ReceiveFactoryInterface listener) throws CommException {
		listener.startReceive(results.size());
		try {
			if (results.isEmpty()) {
				return;
			}

			List<PropertyDescriptor> props = JpaBoFactory.getPropertyDescriptors(results.get(0).getClass());
			for (int row = startIndex; row < startIndex + resultCount && row < results.size(); row++) {
				BaseBo bo = results.get(row - 1);
				ReceiveInterface receiver = listener.getReceiver(row);
				receiver.startResult();

				for (PropertyDescriptor prop : props) {
					String name = NameConversionUtils.camelCaseToClSnakeCase(prop.getName());
					java.lang.reflect.Method readMethod = prop.getReadMethod();

					if (readMethod != null) {
						Object value = readMethod.invoke(bo);
						receiver.setParamValue(name, convertJPAToClan(value));
					}
				}
				receiver.endResult();
			}
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new CommException(e);
		}
		listener.endReceive();
	}

	private String convertJPAToClan(Object pValue) {
		if (pValue == null) {
			return "";
		}

		if (pValue instanceof Temporal) {
			return ClanDateConverter.clanDateFormat((Temporal) pValue);
		}

		return pValue.toString();
	}
}