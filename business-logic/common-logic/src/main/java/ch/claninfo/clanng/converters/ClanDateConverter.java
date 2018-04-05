package ch.claninfo.clanng.converters;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;

import ch.claninfo.common.util.Datum;
import ch.claninfo.common.util.DatumMonatJahr;
import ch.claninfo.common.util.DatumZeit;

public class ClanDateConverter {

	private static final DateTimeFormatter CLAN_DATE_TIME_FORMATTER;
	static final DateTimeFormatter CLAN_YEARMONTH_FORMATTER = DateTimeFormatter.ofPattern("MM.yyyy");
	static {
		CLAN_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
				.append(DateTimeFormatter.ofPattern("[dd.]MM.yyyy[ HH:mm:ss]"))
				.optionalStart()
				.appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
				.optionalEnd()
				.toFormatter();
	}

	private ClanDateConverter() {}

	/**
	 * Mimics the funcionality from {@link ch.claninfo.common.util.DatumZeitTS#datumParse(java.lang.String)}.
	 * Simply parses the string to a given Temporal class t. Knowing that a 'NOW' will return the current time with the give class t.
	 * @param parsable Parses the Clan formatted date time string.
	 * @param t The temporal the string should be parsed to.
	 * @return The parsed temporal.
	 * @throws java.time.format.DateTimeParseException - if the text cannot be parsed
	 */
	public static <T extends Temporal> T parseClanDateToTemporal(String parsable, Class<T> t) {
		if ("NOW".equalsIgnoreCase(parsable)) {
			try {
				return (T) t.getDeclaredMethod("now").invoke(null);
			}
			catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException("Could not create temporal " + t + ".", e);
			}
		}

		if (LocalDate.class.isAssignableFrom(t)) {
			return (T) LocalDate.parse(parsable, CLAN_DATE_TIME_FORMATTER);

		} else if (LocalDateTime.class.isAssignableFrom(t)) {
			return (T) LocalDateTime.parse(parsable, CLAN_DATE_TIME_FORMATTER);

		} else if (Instant.class.isAssignableFrom(t)) {
			return (T) LocalDateTime.parse(parsable, CLAN_DATE_TIME_FORMATTER).atZone(ZoneId.systemDefault()).toInstant();

		} else if (YearMonth.class.isAssignableFrom(t)) {
			return (T) YearMonth.parse(parsable, CLAN_YEARMONTH_FORMATTER);
		}

		throw new IllegalArgumentException("Cannot convert to '" + t + "'");
	}

	public static Temporal clanDateTimeToJava(Date d) {
		if (d == null) {
			return null;
		}

		LocalDateTime dateTime = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		Class<? extends Date> dClass = d.getClass();
		if (DatumZeit.class.equals(dClass)) {
			return dateTime.truncatedTo(ChronoUnit.SECONDS);

		} else if (Datum.class.equals(dClass)) {
			return dateTime.toLocalDate();

		} else if (DatumMonatJahr.class.equals(dClass)) {
			return YearMonth.from(dateTime);
		}

		return dateTime;
	}

	/**
	 * Formats the given temporal according to clan standards.
	 * @param temporal formattable temporal.
	 * @return Formatted string.
	 */
	public static String clanDateFormat(Temporal temporal) {
		if (temporal == null) {
			return "";
		}

		return CLAN_DATE_TIME_FORMATTER.format(temporal);
	}
}