/* $Id: FileLoadProzess.java 1249 2017-07-13 20:38:41Z lar $ */

package ch.claninfo.common.ias;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import ch.claninfo.common.logging.LogCategory;
import ch.claninfo.common.logging.PossibleLogCategory;
import ch.claninfo.common.xml.SAXUtils;
import ch.claninfo.common.xml.StreamSerializer;

/**
 * <p>
 * Liest ein oder mehrere files und schickt sie als ein File an den client.
 * </p>
 * Parameter:<br>
 * pBASEDIR := Sourceverzeichnis der zu ladenden Files<br>
 * pUSESFLAGFILE := Wird ein FlagFile-geprueft bevor Files gelesen werden
 * (default: false)<br>
 * pFLAGFILENAME := Name des FlagFiles (Nur gebraucht wenn
 * pUSESFLAGFILE=true)<br>
 * pFILEPATTERN := Namensmuster der zu lesenden Files (wilchar = *)<br>
 * pWRITELOG := soll eine Log-Datei geschrieben werden beim Aufruf. (default:
 * false)<br>
 * pBACKUPFILES := sollen ein Backup der gelesenen files erstellt werden.
 * (default: false)<br>
 * pDELFLAGFILE := soll FlagFile geloescht werden (default: true)<br>
 * pROOTTAGNAME := Tagname des Roottags (nur bei XML-Input wichtig) <br>
 * pBACKUPFOLDERNAME := Name des BackupFolders (Default ist Datum_Zeit) <br>
 * pNOENVELOPE := Sollen die Dateien in eine &gt;files>&gt;file> struktur
 * verpackt werden (Default ist true) <br>
 * pMERGEFILES := Sollen einzelne Inputfiles vor der verarbeitung gemerged
 * werden (default: true)<br>
 * Beispiel: <br>
 * pBASEDIR=C:\\Temp<br>
 * pUSESFLAGFILE=true<br>
 * pFLAGFILENAME=semaphore<br>
 * pFILEPATTERN=imp*.csv<br>
 * pWRITELOG=true<br>
 * pBACKUPFILES=true<br>
 * pDELFLAGFILE=true<br>
 * pFIXCSVQUOTES:= Ersetzt " innerhalb von Strings durch ' in CSV Files<br>
 * 
 * @author clan informatik AG
 */
public class FileLoadProzess extends AbstractProzess {

	private static final String PAR_BASEDIR = "pBASEDIR"; //$NON-NLS-1$
	private static final String PAR_FLAGFILENAME = "pFLAGFILENAME"; //$NON-NLS-1$
	private static final String PAR_USESFLAGFILE = "pUSESFLAGFILE"; //$NON-NLS-1$
	private static final String PAR_FILEPATTERN = "pFILEPATTERN"; //$NON-NLS-1$
	private static final String PAR_WRITELOG = "pWRITELOG"; //$NON-NLS-1$
	private static final String PAR_BACKUPFILES = "pBACKUPFILES"; //$NON-NLS-1$
	private static final String PAR_DELFLAGFILE = "pDELFLAGFILE"; //$NON-NLS-1$
	private static final String PAR_FIXCSVQUOTES = "pFIXCSVQUOTES"; //$NON-NLS-1$
	private static final String PAR_BACKUPFOLDERNAME = "pBACKUPFOLDERNAME"; //$NON-NLS-1$
	private static final String PAR_MERGEFILES = "pMERGEFILES"; //$NON-NLS-1$
	private static final String PAR_NOENVELOPE = "pNOENVELOPE"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$

	static LogCategory mySQL = new LogCategory(PossibleLogCategory.SQLCAT, FileLoadProzess.class.getName());
	private String baseDir = EMPTY;
	private String filePattern = EMPTY;
	private String flagFileName = EMPTY;
	private String backupFolderName = EMPTY;
	private boolean usesFlagFile = false;
	private boolean writeLog = false;
	private boolean backupFiles = false;
	private boolean delFlagFile = true;
	private boolean fixCSVQuotes = true;
	private boolean mergeFiles = true;
	private DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");//$NON-NLS-1$
	private File flagFile = null;
	private FLPLogger flplogger = null;
	private boolean isXMLFile = false;
	private boolean inQuote = false;
	private boolean noEnvelope = false;

	/**
	 * Creates a new Prozess object.
	 */
	public FileLoadProzess() {
		super();
	}

	/**
	 * @see ch.claninfo.common.ias.Prozess#execute()
	 */
	@Override
	public void execute() throws SAXException {
		ContentHandler out = startResult();
		try {
			initParams();
			if (!noEnvelope) {
				startFile(null, null);
			}
			try {
				flplogger.start();
				File basedir = new File(baseDir);
				File backupFolder = null;
				if (basedir.exists() && basedir.isDirectory()) {
					File[] files = basedir.listFiles(new FLPFileFilter(filePattern));
					isXMLFile = filePattern.substring(filePattern.lastIndexOf(".") + 1, filePattern.length()).equals("xml");//$NON-NLS-1$//$NON-NLS-2$
					// Check Flagfile
					if (isFlagfileOk()) {
						// Backupverzeichnis erstellen
						if (backupFiles) {
							backupFolder = new File(baseDir + File.separator + backupFolderName);
							if (!backupFolder.exists()) {
								backupFolder.mkdir();
							}
							flplogger.log("Create backup directory");//$NON-NLS-1$
						}
						if (isXMLFile) {
							loopXMLFiles(out, backupFolder, files);
						} else {
							loopCSVFiles(out, backupFolder, files);
						}
						// Delete Flagfile
						if (!deleteFlagfile()) {
							flplogger.log("Flagfile could not be deleted (" + flagFileName + ')');//$NON-NLS-1$
							throw new SAXException("Flagfile could not be deleted (" + flagFileName + ')');//$NON-NLS-1$
						}
					} else {
						flplogger.log("No Flagfile found (" + flagFileName + ')');//$NON-NLS-1$
						mySQL.debug("No Flagfile found (" + flagFileName + ')');//$NON-NLS-1$
					}
				}
			}
			finally {
				flplogger.end();
				if (!noEnvelope) {
					endFile();
				}
			}
		}
		catch (IOException e) {
			throw new SAXException(e.getMessage());
		}
		finally {
			endResult();
		}
	}

	/**
	 * @param quotedString
	 * @return String
	 */
	protected String eliminateDoubleQuotes(String quotedString) {
		String input = quotedString;
		StringBuilder out = new StringBuilder();
		char semikolon = ";".charAt(0);//$NON-NLS-1$
		if (input.contains("\"")) {//$NON-NLS-1$
			while ((input.contains("\""))) {//$NON-NLS-1$
				int pos = input.indexOf("\"");//$NON-NLS-1$
				if (inQuote) {
					if (((input.length() >= pos + 2) && (input.charAt(pos + 1) == semikolon)) || (input.length() == pos + 1)) {
						out.append(input.substring(0, pos + 1));
						input = input.substring(pos + 1);
						inQuote = false;
					} else {
						// out.append(input.substring(0,pos));
						input = input.replaceFirst("\"", "\'");//$NON-NLS-1$//$NON-NLS-2$
					}
				} else {
					if (((pos > 0) && (input.charAt(pos - 1) == semikolon)) || (pos == 0)) {
						out.append(input.substring(0, pos + 1));
						input = input.substring(pos + 1);
						inQuote = true;
					} else {
						// out.append(input.substring(0,pos));
						input = input.replaceFirst("\"", "\'");//$NON-NLS-1$//$NON-NLS-2$
					}
				}
			}
			out.append(input);
		} else {
			out.append(input);
		}
		return out.toString();
	}

	/**
	 * Loescht das Flagfile
	 * 
	 * @return true falls das Flagfile geloescht wurde
	 */
	private boolean deleteFlagfile() {
		if ((usesFlagFile) && (delFlagFile)) {
			return flagFile.delete();
		}
		return true;
	}

	/**
	 * initialisiert die Parameter
	 */
	private void initParams() {
		if (hasParameter(PAR_BASEDIR)) {
			baseDir = getStringParameter(PAR_BASEDIR);
		}
		if (hasParameter(PAR_FILEPATTERN)) {
			filePattern = getStringParameter(PAR_FILEPATTERN);
		}
		if (hasParameter(PAR_FLAGFILENAME)) {
			flagFileName = getStringParameter(PAR_FLAGFILENAME);
		}
		if (hasParameter(PAR_USESFLAGFILE)) {
			usesFlagFile = getBooleanParameter(PAR_USESFLAGFILE);
		}
		if (hasParameter(PAR_USESFLAGFILE)) {
			usesFlagFile = getBooleanParameter(PAR_USESFLAGFILE);
		}
		if (hasParameter(PAR_WRITELOG)) {
			writeLog = getBooleanParameter(PAR_WRITELOG);
		}
		if (hasParameter(PAR_BACKUPFILES)) {
			backupFiles = getBooleanParameter(PAR_BACKUPFILES);
		}
		if (hasParameter(PAR_DELFLAGFILE)) {
			delFlagFile = getBooleanParameter(PAR_DELFLAGFILE);
		}
		if (hasParameter(PAR_FIXCSVQUOTES)) {
			fixCSVQuotes = getBooleanParameter(PAR_FIXCSVQUOTES);
		}
		if (hasParameter(PAR_BACKUPFOLDERNAME)) {
			backupFolderName = getStringParameter(PAR_BACKUPFOLDERNAME);
		} else {
			backupFolderName = dateformat.format(LocalDateTime.now());
		}
		if (hasParameter(PAR_MERGEFILES)) {
			mergeFiles = getBooleanParameter(PAR_MERGEFILES);
		}
		if (hasParameter(PAR_NOENVELOPE)) {
			noEnvelope = getBooleanParameter(PAR_NOENVELOPE);
		}
		// Logger initialisieren
		flplogger = new FLPLogger(baseDir + File.separator + getProzessName() + ".log", writeLog);//$NON-NLS-1$

		// correct basePath if needed
		while (baseDir.endsWith(File.separator)) {
			baseDir = baseDir.substring(0, baseDir.length() - 1);
		}
	}

	/**
	 * Testet ob das FlagFile vorhanden ist. Wird kein Flagfile verwendet
	 * (default), wird immer true returniert
	 * 
	 * @return true oder false
	 */
	private boolean isFlagfileOk() {
		if (usesFlagFile) {
			flplogger.log("TestfFlag file (" + flagFileName + ')');//$NON-NLS-1$
			flagFile = new File(baseDir + File.separator + flagFileName);
			return flagFile.exists();
		}
		return true;
	}

	/**
	 * Loopt über einzelne csv Files und fügt diese zusammen.
	 * 
	 * @param out
	 * @param backupFolder
	 * @param files
	 * @throws SAXException
	 * @throws SQLException
	 * @throws IOException
	 */
	private void loopCSVFiles(ContentHandler out, File backupFolder, File[] files) throws SAXException, IOException {
		flplogger.log("Reading the data");//$NON-NLS-1$
		// Loop Files
		for (int i = 0; i < files.length; i++) {
			// read file
			File backupFile;
			Writer writer = null;

			try {
				// Backupcopy erstellen
				if (backupFiles) {
					flplogger.log(files[i].getName());
					backupFile = new File(backupFolder.getPath() + File.separator + files[i].getName());
					writer = new OutputStreamWriter(new FileOutputStream(backupFile));
				}
				String temp;
				InputStream inStream = new FileInputStream(files[i]);
				BufferedReader breader = new BufferedReader(new InputStreamReader(inStream));
				temp = breader.readLine();

				// Write backup and response
				while (temp != null) {

					if (fixCSVQuotes) {
						temp = eliminateDoubleQuotes(temp);
					}
					temp = temp + "\n";//$NON-NLS-1$
					out.characters(temp.toCharArray(), 0, temp.length());
					if (backupFiles) {
						writer.write(temp.toCharArray());
					}
					temp = breader.readLine();
				}

				// Delete InputFile
				inStream.close();
				files[i].delete();
			}
			catch (IOException e) {
				flplogger.log("Error while reading the file (" + files[i].getName() + ')');//$NON-NLS-1$
				throw new SAXException("Error while reading the file (" + files[i].getName() + ')');//$NON-NLS-1$
			}
			finally {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
				if (!mergeFiles) {
					break;
				}
			}
		}
	}

	/**
	 * Loopt durch einzelne XML-Files und fügt diese zusammen.
	 * 
	 * @param out
	 * @param backupFolder
	 * @param files
	 * @throws SAXException
	 * @throws SQLException
	 * @throws IOException
	 */
	private void loopXMLFiles(ContentHandler out, File backupFolder, File[] files) throws SAXException {
		flplogger.log("Reading the data");//$NON-NLS-1$
		XmlFilter filter = new XmlFilter();
		StringWriter stringWriter = new StringWriter();
		StreamSerializer serializer = new StreamSerializer(stringWriter);
		serializer.startDocument();
		filter.setContentHandler(serializer);

		for (int i = 0; i < files.length; i++) {
			// read file
			File backupFile;
			try {
				SAXUtils.parse(files[i].getAbsolutePath(), filter);
				// Backupcopy erstellen
				if (backupFiles) {
					flplogger.log(files[i].getName());
					backupFile = new File(backupFolder.getPath() + File.separator + files[i].getName());
					files[i].renameTo(backupFile);
				}
			}
			finally {
				if (!mergeFiles) {
					break;
				}
			}
		}
		filter.end();
		serializer.endDocument();
		String text = stringWriter.toString();
		out.characters(text.toCharArray(), 0, text.length());
	}
}

/**
 * Filterklasse
 * 
 * @author clan informatik AG
 */
class FLPFileFilter implements FileFilter {

	private String pattern = ""; //$NON-NLS-1$

	/**
	 * @param pPattern
	 */
	public FLPFileFilter(String pPattern) {
		super();
		this.pattern = pPattern;
	}

	/**
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File pfile) {
		String filename = pfile.getName();
		return accept(filename);
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return pattern;
	}

	/**
	 * @param filename
	 * @return true or false
	 */
	protected boolean accept(String filename) {
		StringTokenizer tokenizer = new StringTokenizer(pattern, "*"); //$NON-NLS-1$
		int counter = 0;
		String token; //$NON-NLS-1$
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			int index = filename.indexOf(token);
			if ((index > -1) && (index >= counter)) {
				counter = index;
			} else {
				return false;
			}
		}
		return true;
	}

}

/**
 * FLPLogger Logger für FileLoadProzess
 * 
 * @author clan informatik AG
 */
class FLPLogger {

	private boolean logenabled = false;
	private File logFile;
	private PrintWriter writer = null;
	private DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SS: ");//$NON-NLS-1$

	/**
	 * @param logfilename
	 * @param enabled
	 */
	public FLPLogger(String logfilename, boolean enabled) {
		logFile = new File(logfilename);
		logenabled = enabled;
		try {
			writer = new PrintWriter(new FileWriter(logFile, true));
		}
		catch (IOException e) {
			logenabled = false;
		}
	}

	/**
	 * Ende des Aufrufs
	 */
	public void end() {
		if (logenabled) {
			log("End");//$NON-NLS-1$
			writer.flush();
			writer.close();
		}
	}

	/**
	 * loggt die message mit DatumZeit-Tag
	 * 
	 * @param message
	 */
	public void log(String message) {
		if (logenabled) {
			String temp = dateformat.format(LocalDateTime.now());
			writer.println(temp + "\t" + message);//$NON-NLS-1$
		}
	}

	/**
	 * Start für neuen Aufruf
	 */
	public void start() {
		if (logenabled) {
			writer.println("-----------------------------------------------------------");//$NON-NLS-1$
			log("Start");//$NON-NLS-1$
		}
	}
}

class XmlFilter extends XMLFilterImpl {

	String localName = null;
	String uri = null;
	String qualifiedName = null;
	private int level = 0;

	public void end() throws SAXException {
		if (qualifiedName != null) {
			super.endElement(uri, localName, qualifiedName);
		}
	}

	@Override
	public void endDocument() {
		// NOP
	}

	@Override
	public void endElement(String pNamespaceUri, String pLocalName, String pQualifiedName) throws SAXException {
		level--;
		if (level > 0) {
			super.endElement(pNamespaceUri, pLocalName, pQualifiedName);
		}
	}

	@Override
	public void startDocument() {
		// NOP
	}

	@Override
	public void startElement(String pNamespaceURI, String pLocalName, String pQualifiedName, Attributes pAttrs) throws SAXException {
		if (level > 0) {
			super.startElement(pNamespaceURI, pLocalName, pQualifiedName, pAttrs);
		} else {
			if (qualifiedName == null) {
				localName = pLocalName;
				qualifiedName = pQualifiedName;
				uri = pNamespaceURI;
				super.startElement(pNamespaceURI, pLocalName, pQualifiedName, pAttrs);
			} else {
				if (!pQualifiedName.equals(qualifiedName)) {
					end();
					throw new SAXException("XML files has diferent root elements! Expected: " + qualifiedName + ", found: " + pQualifiedName); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		level++;
	}
}
