
package ch.claninfo.clanng.business.logic.exceptions;

import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.util.BrExceptionInterface;
import ch.claninfo.common.util.Language;
import ch.claninfo.common.util.Message;
import ch.claninfo.common.util.TextID;
import ch.claninfo.common.xml.TextFactory;

public class BrException extends CommException implements BrExceptionInterface {

	private static final TextFactory textFactory = new TextFactory();
	Message message;

	public BrException(int brId, Object... parameters) {
		this(new Message(brId, parameters));
	}

	public BrException(Message pMessage) {
		super(pMessage.getText());
		message = pMessage;
	}

	public BrException(String modul, int brId, Language language, Object... parameters) {
		this(new Message(modul, language.getLocale(), new TextID(brId), parameters));
	}

	public static void raise(int id, Object... parameters) throws BrException {
		throw new BrException("common", id, Language.DE, parameters);
	}

	public static void raise(String modul, int id, Language language, Object... parameters) throws BrException {
		throw new BrException(modul, id, language, parameters);
	}

	@Override
	public int getBrNumber() {
		return message.getNumber();
	}

	@Override
	public Object[] getParameters() {
		return message.getParameters();
	}

	@Override
	public Message getReason() {
		return message;
	}
}
