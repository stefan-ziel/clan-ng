/* $Id: LogCategory.java 47054 2015-04-16 13:17:37Z wur $ */

package ch.claninfo.common.logging;

import java.io.Serializable;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Diese Klasse dient als overlay für die Log4J Category Klasse.
 * <p>
 * Anwendungsbeispiel:
 * </p>
 * <p>
 * <b>private static LogCategory cat = new
 * LogCategory.(MyClassname.class.getName());</b>
 * </p>
 * <p>
 * cat.info("Init durchgelaufen");
 * </p>
 */

/**
 * @author clan informatik AG
 */
public class LogCategory implements Serializable {

	private static final String FQCN = Category.class.getName();
	//	private transient Logger logger;
	private final Logger LOGGER;
	private static LoggingEvent[] eventBuffer = new LoggingEvent[20];
	private static int bufferPointer;
	private static final long serialVersionUID = 1L;

	/**
	 * @param pClassName
	 * @deprecated diese Constructor soll man nicht mehr benützt werden. Man sollte den Name des Category und den Name des Klassen eingeben können
	 */
	public LogCategory(String pClassName) {
		this(PossibleLogCategory.DEFAULTCAT, pClassName);
	}

	/**
	 * @param pCategory  String
	 * @param pClassName String
	 */
	public LogCategory(String pCategory, String pClassName) {
		LOGGER = LogManager.getLogger(pCategory + '.' + pClassName);
	}

	/**
	 * Schaltet einen Log eventbuffer an. Damit weden logevents niedrigerer (z.B.
	 * INFO) Priorität gepuffert und im Fall eines Events höherer Priorität (z.B.
	 * ERROR) mit ausgegeben. Das ist praktisch für SQL logging.
	 *
	 * @param pSize
	 */
	public static void setEventBuffer(int pSize) {
		synchronized (FQCN) {
			if (pSize > 0) {
				eventBuffer = new LoggingEvent[pSize];
			} else {
				eventBuffer = null;
			}
		}
	}

	static void add(LoggingEvent pEvt) {
		synchronized (FQCN) {
			if (eventBuffer != null) {
				bufferPointer++;
				if (bufferPointer >= eventBuffer.length) {
					bufferPointer = 0;
				}
				eventBuffer[bufferPointer] = pEvt;
			}
		}
	}

	/**
	 * Logt eine Meldung auf Level DEBUG.
	 *
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void debug(Object pMessage) {
		doLog(Level.DEBUG, null, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level DEBUG.
	 *
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void debug(Object pMessage, Throwable pThrowable) {
		doLog(Level.DEBUG, null, pMessage, pThrowable);
	}

	/**
	 * Logt eine Meldung auf Level DEBUG.
	 *
	 * @param pUserId
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void debug(String pUserId, Object pMessage) {
		doLog(Level.DEBUG, pUserId, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level DEBUG.
	 *
	 * @param pUserId
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void debug(String pUserId, Object pMessage, Throwable pThrowable) {
		doLog(Level.DEBUG, pUserId, pMessage, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level DEBUG.
	 *
	 * @param pUserId    String
	 * @param pThrowable Throwable
	 */
	public void debug(String pUserId, Throwable pThrowable) {
		doLog(Level.DEBUG, pUserId, null, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level DEBUG.
	 *
	 * @param pThrowable Exception, die geloggt werden soll.
	 */
	public void debug(Throwable pThrowable) {
		doLog(Level.DEBUG, null, null, pThrowable);
	}

	/**
	 * Logt eine Meldung auf Level ERROR.
	 *
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void error(Object pMessage) {
		doLog(Level.ERROR, null, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level ERROR.
	 *
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void error(Object pMessage, Throwable pThrowable) {
		doLog(Level.ERROR, null, pMessage, pThrowable);
	}

	/**
	 * Logt eine Meldung auf Level ERROR.
	 *
	 * @param pUserId
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void error(String pUserId, Object pMessage) {
		doLog(Level.ERROR, pUserId, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level ERROR.
	 *
	 * @param pUserId
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void error(String pUserId, Object pMessage, Throwable pThrowable) {
		doLog(Level.ERROR, pUserId, pMessage, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level ERROR.
	 *
	 * @param pUserId
	 * @param pThrowable Exception, die geloggt werden soll.
	 */
	public void error(String pUserId, Throwable pThrowable) {
		doLog(Level.ERROR, pUserId, null, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level ERROR.
	 *
	 * @param pThrowable Exception, die geloggt werden soll.
	 */
	public void error(Throwable pThrowable) {
		doLog(Level.ERROR, null, null, pThrowable);
	}

	/**
	 * Logt eine Meldung auf Level FATAL.
	 *
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void fatal(Object pMessage) {
		doLog(Level.FATAL, null, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level FATAL.
	 *
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void fatal(Object pMessage, Throwable pThrowable) {
		doLog(Level.FATAL, null, pMessage, pThrowable);
	}

	/**
	 * Logt eine Meldung auf Level FATAL.
	 *
	 * @param pUserId
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void fatal(String pUserId, Object pMessage) {
		doLog(Level.FATAL, pUserId, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level FATAL.
	 *
	 * @param pUserId
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void fatal(String pUserId, Object pMessage, Throwable pThrowable) {
		doLog(Level.FATAL, pUserId, pMessage, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level FATAL.
	 *
	 * @param pUserId
	 * @param pThrowable Exception, die geloggt werden soll.
	 */
	public void fatal(String pUserId, Throwable pThrowable) {
		doLog(Level.FATAL, pUserId, null, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level FATAL.
	 *
	 * @param pThrowable Exception, die geloggt werden soll.
	 */
	public void fatal(Throwable pThrowable) {
		doLog(Level.FATAL, null, null, pThrowable);
	}

	/**
	 * Logt eine Meldung auf Level INFO.
	 *
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void info(Object pMessage) {
		doLog(Level.INFO, null, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level INFO.
	 *
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void info(Object pMessage, Throwable pThrowable) {
		doLog(Level.INFO, null, pMessage, pThrowable);
	}

	/**
	 * Logt eine Meldung auf Level INFO.
	 *
	 * @param pUserId  Benutzer für den gelogged wird
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void info(String pUserId, Object pMessage) {
		doLog(Level.INFO, pUserId, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level INFO.
	 *
	 * @param pUserId
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void info(String pUserId, Object pMessage, Throwable pThrowable) {
		doLog(Level.INFO, pUserId, pMessage, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level INFO.
	 *
	 * @param pUserId
	 * @param pThrowable Exception, die geloggt werden soll.
	 */
	public void info(String pUserId, Throwable pThrowable) {
		doLog(Level.INFO, pUserId, null, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level INFO.
	 *
	 * @param pThrowable Exception, die geloggt werden soll.
	 */
	public void info(Throwable pThrowable) {
		doLog(Level.INFO, null, null, pThrowable);
	}

	/**
	 * Checkt ob Logging auf Debug-Level aktiv ist.
	 *
	 * @return true, falls Debug aktiv ist, false wenn nicht..
	 */
	public boolean isDebugEnabled() {
		return LOGGER.isDebugEnabled();
	}

	/**
	 * Checkt ob Logging auf Info-Level aktiv ist.
	 *
	 * @return true, falls Debug aktiv ist, false wenn nicht..
	 */
	public boolean isInfoEnabled() {
		return LOGGER.isInfoEnabled();
	}

	/**
	 * Logt eine Meldung auf Level WARN.
	 *
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void warn(Object pMessage) {
		doLog(Level.WARN, null, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level WARN.
	 *
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void warn(Object pMessage, Throwable pThrowable) {
		doLog(Level.WARN, null, pMessage, pThrowable);
	}

	/**
	 * Logt eine Meldung auf Level WARN.
	 *
	 * @param pUserId
	 * @param pMessage Zusätzliche Fehlermeldung, die geloggt werden soll.
	 */
	public void warn(String pUserId, Object pMessage) {
		doLog(Level.WARN, pUserId, pMessage, null);
	}

	/**
	 * Logt eine Meldung auf Level WARN.
	 *
	 * @param pUserId
	 * @param pMessage   Zusätzliche Fehlermeldung, die geloggt werden soll.
	 * @param pThrowable Die zu loggende Exception
	 */
	public void warn(String pUserId, Object pMessage, Throwable pThrowable) {
		doLog(Level.WARN, pUserId, pMessage, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level WARN.
	 *
	 * @param pUserId
	 * @param pThrowable Exception, die geloggt werden soll.
	 */
	public void warn(String pUserId, Throwable pThrowable) {
		doLog(Level.WARN, pUserId, null, pThrowable);
	}

	/**
	 * Logt eine Exception auf Level WARN.
	 *
	 * @param pThrowable Exception, die geloggt werden soll.
	 */
	public void warn(Throwable pThrowable) {
		doLog(Level.WARN, null, null, pThrowable);
	}

	/**
	 * @param pUserId  String
	 * @param pMessage Object
	 */
	void doLog(Level pLevel, String pUserId, Object pMessage, Throwable pThrowable) {
		org.apache.logging.log4j.Level level = org.apache.logging.log4j.Level.getLevel(pLevel.toString());
		LOGGER.log(level, pMessage, pThrowable);
	}
}