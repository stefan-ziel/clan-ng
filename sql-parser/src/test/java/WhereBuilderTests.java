import java.io.IOException;

import ch.claninfo.clanng.sqlparser.JpqlWhereBuilder;

public class WhereBuilderTests {
	public static void main(String[] args) throws IOException {
		String s = "   pVSNUM='154463'";
		JpqlWhereBuilder whereBuilder = new JpqlWhereBuilder(s);

		System.out.println(whereBuilder.getResultingString());
	}
}