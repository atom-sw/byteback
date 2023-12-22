package byteback.frontend.boogie.util;

import beaver.Parser;
import byteback.frontend.boogie.ast.Program;
import byteback.frontend.boogie.parser.BoogieParser;
import byteback.frontend.boogie.scanner.BoogieLexer;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;

public class ParserUtil {

	public static Program parseBoogieProgram(final Path path) throws IOException, Parser.Exception {
		final Reader reader = new FileReader(path.toFile());

		return parseBoogieProgram(reader);
	}

	public static Program parseBoogieProgram(final Reader reader) throws IOException, Parser.Exception {
		final BoogieLexer lexer = new BoogieLexer(reader);
		final BoogieParser parser = new BoogieParser();
		final Program program = (Program) parser.parse(lexer);

		return program;
	}

	public static Program parseBoogieProgram(final String string) throws IOException, Parser.Exception {
		final Reader reader = new StringReader(string);

		return parseBoogieProgram(reader);
	}

}
