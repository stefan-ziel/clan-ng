
package ch.claninfo.clanng.sqlparser;

public class NameConversionUtils {

	private NameConversionUtils() {}

	public static String camelCaseToClSnakeCase(String input) {
		int len = input.length();
		StringBuilder res = new StringBuilder(len + 1);
		res.append("p");
		for (int i = 0; i < len; i++) {
			char c = input.charAt(i);
			if (Character.isUpperCase(c)) {
				res.append('_');
				res.append(c);
			} else {
				res.append(Character.toUpperCase(c));
			}
		}
		return res.toString();
	}

	public static String snakeCaseToCamelCase(String input) {
		int len = input.length();
		StringBuilder res = new StringBuilder(len - 1);
		boolean upper = false;
		for (int i = 1; i < len; i++) {
			char c = input.charAt(i);
			if (c == '_') {
				upper = true;
			} else if (upper) {
				res.append(c);
				upper = false;
			} else {
				res.append(Character.toLowerCase(c));
			}
		}
		return res.toString();
	}
}