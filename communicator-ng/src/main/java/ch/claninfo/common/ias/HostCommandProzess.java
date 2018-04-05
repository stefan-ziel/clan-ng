/* $Id: HostCommandProzess.java 1235 2017-05-31 20:11:55Z lar $ */

package ch.claninfo.common.ias;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.xml.sax.SAXException;

import ch.claninfo.clanng.session.services.SessionUtils;
import ch.claninfo.common.logging.LogCategory;
import ch.claninfo.common.logging.PossibleLogCategory;
import ch.claninfo.common.util.CommandExecuter;
import ch.claninfo.common.util.EnvReader;

/**
 * Ein Starterprozess f�r Oracle Reports.
 * <p>
 * Die per setParameter �bergebene Parameterwerte werden einfach in der Form
 * &gt;name&lt;=&gt;wert&lt; an den aufruf des Report-Runtime weitergegeben.
 * Ausnahme bilden hier folgende Schl�ssel:
 * </p>
 * <p>
 * -"EXE" definiert den Namen des ORACLE-Report-Runtime so wie er an das
 * Betriebssytem des Servers weitergegeben werden muss<br>
 * -"REPORT" gibt nur den namen der eigentlichen Reportdatei wieder und wird um
 * den in "BASEDIR" enthaltenen Pfad erweitert<br>
 * -"PARAMFORM=NO" und "BATCH=YES" werden immer gesetzt<br>
 * -Bei synchroner Ausf�hrung (Druckvorschau) werden die Schl�ssel
 * "DESTYPE=FILE" und "DESNAME" �berschrieben um die Ausgabe an den Client
 * zur�ckzuleiten
 * </p>
 * 
 * @author clan Inform�tica do Brasil
 */
public class HostCommandProzess extends AbstractProzess {

	// Parameter
	private static final String COMMAND_STRING = "COMMAND"; //$NON-NLS-1$
	private static final String WORKDIR_STRING = "WORKDIR"; //$NON-NLS-1$

	// Log
	static LogCategory myLogger;

	/**
	 * Erstellt ein neues Objektes.
	 */
	public HostCommandProzess() {
		super();
	}

	/**
	 * Gibt den Logger f�r diese Klasse zur�ck.
	 * 
	 * @return der Logger f�r diese Klasse
	 */
	static LogCategory getLogger() {

		if (myLogger == null) {
			myLogger = new LogCategory(PossibleLogCategory.DEFAULTCAT, HostCommandProzess.class.getName());
		}

		return myLogger;
	}

	/**
	 * Prozess effektiv ausfuhren (SAX) ContentHandler geschrieben
	 * 
	 * @see ch.claninfo.common.ias.Prozess#execute()
	 */
	@Override
	public void execute() throws SAXException {
		// Pruefung Parameter
		if (!hasParameter(COMMAND_STRING)) {
			throw new SAXException("Command missing"); //$NON-NLS-1$
		}

		try {
			String cmd = getCommand();
			getLogger().info(cmd.replaceAll(getConfig("hostcommand.pwd", "data"), "****")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			// Befehl ausfuehren

			String[] environment = getEnvironment();
			getLogger().info("Environment-Parameter:" + Arrays.asList(environment)); //$NON-NLS-1$
			CommandExecuter.exec(cmd, environment);
		}
		catch (IOException ioex) {
			myLogger.error(ioex);
			SAXException sqlex = new SAXException("Host command execution failed: " + ioex.getMessage()); //$NON-NLS-1$
			sqlex.setStackTrace(ioex.getStackTrace());
			throw sqlex;
		}
	}

	/**
	 * erzeugt einen String mit dem aufzurufenden Kommando
	 * 
	 * @return das Kommando
	 */
	private String getCommand() {
		StringBuilder cmd = new StringBuilder();
		boolean isWindows = System.getProperty("os.name").startsWith("Windows"); //$NON-NLS-1$//$NON-NLS-2$
		String tempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		String baseDir = getConfig("hostcommand.basedir", tempDir); //$NON-NLS-1$
		String outDir = getConfig("hostcommand.outdir", tempDir); //$NON-NLS-1$
		String user = getConfig("hostcommand.user", "clan"); //$NON-NLS-1$ //$NON-NLS-2$
		String pwd = getConfig("hostcommand.pwd", "data"); //$NON-NLS-1$ //$NON-NLS-2$
		String dbname = getConfig("hostcommand.dbname", "CITE"); //$NON-NLS-1$ //$NON-NLS-2$
		String workdir = getConfig("hostcommand.workdir", tempDir); //$NON-NLS-1$

		File fileBaseDir = new File(baseDir);
		if (isWindows) {
			cmd.append('"');
		}
		cmd.append(fileBaseDir.getAbsolutePath());
		cmd.append(File.separator);
		cmd.append(getParameter(COMMAND_STRING));
		if (isWindows) {
			cmd.append('"');
		}
		cmd.append(' ').append('"').append(SessionUtils.getSession().getSessionId()).append('"');
		cmd.append(' ').append('"').append(user).append('"');
		cmd.append(' ').append('"').append(pwd).append('"');
		cmd.append(' ').append('"').append(dbname).append('"');
		cmd.append(' ').append('"').append(outDir).append('"');
		// falls workdir nicht in den Properties ist, die aus der Config nehmen
		if (workdir == null || workdir.length() == 0) {
			workdir = getStringParameter(WORKDIR_STRING);
		}
		if (workdir != null && workdir.length() > 0) {
			cmd.append(' ').append('"').append(workdir).append('"');
		}
		return cmd.toString();

	}

	/**
	 * Methode holt sich die Umgebungsvariablen und f�gt die eigenen Parameter
	 * hinzu
	 * 
	 * @return ein Stringarray mit allen Umgebungsvariablen
	 * @throws IOException
	 * @throws SQLException falls etwas schiefging.
	 */
	private String[] getEnvironment() throws IOException {
		// environment parameter mit den Prozessparameter vereinen.
		String[] params;
		Properties env = EnvReader.getEnvVars();
		// zus�tzliche Parameter als Enviromnentvariablen hinzuf�gen
		for (Iterator<String> i = iterator(); i.hasNext();) {
			String key = i.next();
			if (!(COMMAND_STRING.equals(key))) {
				env.put(key, getParameter(key));
			}
		}
		// in ein array umwandeln
		params = new String[env.size()];
		int i = 0;
		Enumeration<Object> keys = env.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) env.get(key);
			params[i] = key + '=' + value;
			i++;
		}
		return params;
	}
}
