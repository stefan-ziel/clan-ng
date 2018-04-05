
package ch.claninfo.clanng.sqlparser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.TerminalNode;

import ch.claninfo.clanng.sqlparser.grammar.SqlWhereBaseListener;
import ch.claninfo.clanng.sqlparser.grammar.SqlWhereLexer;
import ch.claninfo.clanng.sqlparser.grammar.SqlWhereParser;

public class JpqlWhereBuilder {

	private final SqlWhereParser sqlWhereParser;
	private TokenStreamRewriter streamRewriter;

	public JpqlWhereBuilder(String sqlWhere) {
		ANTLRInputStream antlrInputStream = new ANTLRInputStream(sqlWhere);
		SqlWhereLexer lexer = new SqlWhereLexer(antlrInputStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		streamRewriter = new TokenStreamRewriter(tokenStream);
		sqlWhereParser = new SqlWhereParser(tokenStream);

		sqlWhereParser.addParseListener(new SqlWhereBaseListener() {

			@Override
			public void exitCondition(SqlWhereParser.ConditionContext ctx) {
				TerminalNode columnNode = ctx.COLUMN();
				if (columnNode != null) {
					Token symbol = columnNode.getSymbol();
					streamRewriter.replace(symbol, NameConversionUtils.snakeCaseToCamelCase(symbol.getText()));
				}
			}
		});
	}

	public String getResultingString() {
		sqlWhereParser.where();
		return streamRewriter.getText();
	}
}
