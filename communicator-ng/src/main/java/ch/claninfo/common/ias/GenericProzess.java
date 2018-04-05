/* $Id: GenericProzess.java 1235 2017-05-31 20:11:55Z lar $ */

package ch.claninfo.common.ias;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import ch.claninfo.clanng.session.entities.ClanSession;
import ch.claninfo.clanng.session.services.SessionUtils;
import ch.claninfo.common.logging.LogCategory;
import ch.claninfo.common.logging.PossibleLogCategory;
import ch.claninfo.common.util.CommandExecuter;
import ch.claninfo.common.util.EncodingBase64InputStream;

/**
 * Prozessimplementierung für den Aufruf beliebeiger
 * Batchverarbeitungsprogramme.
 * <p>
 * Es wird ein im Parameter "CMD" ein vorformatierter Komandostring übergeben,
 * der eingeschlossen in geschweiften Klammern "{}", die Namen weiterer
 * Parameter enthält. Hierbei brauchen die eingebauten Parameternamen
 * "AUFTRAGSNR", "SESSIONID", "USER", "PASSWD", "DBNAME", "BASEDIR", "WORKDIR"
 * und "OUTDIR" nicht parametrisiert zu werden. Alle Anderen Parameter müssen in
 * der Prozessklasse der Prozessdefinition oder den Benutzerparametern enthalten
 * sein.
 * </p>
 * <p>
 * Wird ein der parameter "OUTPUT" übergeben und der Prozess synchron ausgeführt
 * wird der inhalt dieser Datei an den Client zurück geschickt. Wurde kein
 * "OUTPUT" angegeben wird der STDOUT an den Client geschickt.
 * </p>
 * Beispiel: <br>
 * CMD=dir {PATH}\{USER}. &gt; {OUTPUT}<br>
 * PATH=C:\TEMP
 *
 * @author clan informatik ag
 * @version $Revision: 1235 $
 */
public class GenericProzess extends AbstractProzess {

	private static final String AUFTRAGSNR = "AUFTRAGSNR"; //$NON-NLS-1$
	private static final String SESSIONID = "SESSIONID"; //$NON-NLS-1$
	private static final String USER = "USER"; //$NON-NLS-1$
	private static final String BIGDATA = "BIGDATA"; //$NON-NLS-1$
	private static final String BASE64_MAGIC = "6464"; //$NON-NLS-1$
	private static final String COMMAND = "CMD"; //$NON-NLS-1$
	private static final String OUT = "OUTPUT"; //$NON-NLS-1$
	private static final String PASSWD = "PASSWD"; //$NON-NLS-1$
	private static final String DBNAME = "DBNAME"; //$NON-NLS-1$
	private static final String BASEDIR = "BASEDIR"; //$NON-NLS-1$
	private static final String WORKDIR = "WORKDIR"; //$NON-NLS-1$
	private static final String OUTDIR = "OUTDIR"; //$NON-NLS-1$
	private static LogCategory myLogger;
	private File myOutFile;

	/**
	 * Creates a new Prozess object.
	 */
	public GenericProzess() {
		// does nothing
	}

	/**
	 * Gibt den Logger für diese Klasse zurück.
	 *
	 * @return der Logger für diese Klasse
	 */
	private static LogCategory getLogger() {

		if (myLogger == null) {
			myLogger = new LogCategory(PossibleLogCategory.DEFAULTCAT, GenericProzess.class.getName());
		}

		return myLogger;
	}

	/**
	 * Ausführen (SAX)
	 *
	 * @throws SAXException SQL Error
	 */
	@Override
	public void execute() throws SAXException {
		String tempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		String command = getStringParameter(COMMAND);
		if (command == null) {
			throw new SAXException("Generic Process: no " + COMMAND + " parameter specified."); //$NON-NLS-1$//$NON-NLS-2$
		}
		try {
			StringBuilder cmd = new StringBuilder();
			StringBuilder par = new StringBuilder();
			boolean inpar = false;
			File outfile = null;
			ClanSession session = SessionUtils.getSession();
			for (int i = 0; i < command.length(); i++) {
				char c = command.charAt(i);
				if (inpar) {
					if (c == '}') {
						String param = par.toString();
						if (hasParameter(param)) {
							String value = getStringParameter(param);
							if (OUT.equals(param)) {
								outfile = new File(value);
								if (outfile.exists()) {
									outfile.delete();
								}
							}
							cmd.append(value);
						} else if (USER.equals(param)) {
							String user = getConfig("hostcommand.user", "clan"); //$NON-NLS-1$ //$NON-NLS-2$
							if (user == null) {
								user = session.getUser().getUsername();
							}
							cmd.append(user);
						} else if (PASSWD.equals(param)) {
							cmd.append(getConfig("hostcommand.pwd", "data")); //$NON-NLS-1$ //$NON-NLS-2$
						} else if (DBNAME.equals(param)) {
							cmd.append(getConfig("hostcommand.dbname", "CITE")); //$NON-NLS-1$ //$NON-NLS-2$
						} else if (SESSIONID.equals(param)) {
							cmd.append(session.getSessionId());
						} else if (AUFTRAGSNR.equals(param)) {
							cmd.append(getAuftragsnummer());
						} else if (BASEDIR.equals(param)) {
							cmd.append(getConfig("hostcommand.basedir", tempDir)); //$NON-NLS-1$
						} else if (WORKDIR.equals(param)) {
							cmd.append(getConfig("hostcommand.workdir", tempDir)); //$NON-NLS-1$
						} else if (OUTDIR.equals(param)) {
							cmd.append(getConfig("hostcommand.outdir", tempDir)); //$NON-NLS-1$
						} else if (BIGDATA.equals(param)) {
							cmd.append(myOutFile.getAbsolutePath());
						} else if (OUT.equals(param)) {
							outfile = File.createTempFile("gen", ".tmp"); //$NON-NLS-1$//$NON-NLS-2$
							cmd.append(outfile.getAbsolutePath());
						} else {
							throw new SAXException("Invalid parameter " + param);//$NON-NLS-1$
						}
						inpar = false;
					} else {
						par.append(c);
					}
				} else {
					if (c == '{') {
						par.setLength(0);
						inpar = true;
					} else {
						cmd.append(c);
					}
				}
			}

			getLogger().info(cmd);

			Process proc = CommandExecuter.execAndForget(cmd.toString());
			byte[] buffer = new byte[8192];
			int errorlevel;
			InputStream inStream = new BufferedInputStream(proc.getInputStream());
			InputStream errStream = new BufferedInputStream(proc.getErrorStream());

			try {
				StringBuilder inbuffer = new StringBuilder();
				StringBuilder errbuffer = new StringBuilder();
				int incount = inStream.read(buffer);

				while (incount >= 0) {
					if (incount > 0) {
						inbuffer.append(new String(buffer, 0, incount));
						incount = inStream.read(buffer);
					}
				}

				int errcount = errStream.read(buffer);

				while (errcount >= 0) {
					if (errcount > 0) {
						errbuffer.append(new String(buffer, 0, errcount));
						errcount = errStream.read(buffer);
					}
				}

				errorlevel = proc.waitFor();

				if (errorlevel != 0) {
					throw new SAXException("Errorlevel " + errorlevel + " : " + errbuffer.toString());//$NON-NLS-1$//$NON-NLS-2$
				}

				if (!isAsync()) {
					ContentHandler out = startResult();
					try {
						startFile(null, null);
						try {
							// Wird Text oder Base64 im Knoten geschrieben?
							String temp;
							if (outfile == null) {
								temp = inbuffer.toString();
								out.characters(temp.toCharArray(), 0, temp.length());
							} else {
								inStream.close();
								inStream = new EncodingBase64InputStream(new FileInputStream(outfile));
								out.characters(BASE64_MAGIC.toCharArray(), 0, BASE64_MAGIC.length());
								incount = inStream.read(buffer);

								while (incount > 0) {
									temp = new String(buffer, 0, incount);
									out.characters(temp.toCharArray(), 0, temp.length());
									incount = inStream.read(buffer);
								}
							}
						}
						finally {
							endFile();
						}
					}
					finally {
						endResult();
					}
				}
			}
			finally {
				inStream.close();
			}
		}
		catch (InterruptedException | IOException e) {
			throw new SAXException("Execution failed", e);//$NON-NLS-1$
		}
	}
}
