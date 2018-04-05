package ch.claninfo.clanng.domain.entities;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Pk implements Serializable {

	private static final Pattern pkPackagePattern = Pattern.compile("(?<package>.*\\.)[^.]+\\.(?<class>[^.]+)Pk$");

	public Class<? extends ClanEntity> getDtoClass() {
		Class<? extends Pk> currentClass = getClass();
		try {
			Matcher matcher = pkPackagePattern.matcher(currentClass.getName());

			if (matcher.find()) {
				return (Class<? extends ClanEntity>) Class.forName(matcher.group("package") + matcher.group("class"));
			}

			throw new RuntimeException("Wrong package or class name: " + getClass());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}