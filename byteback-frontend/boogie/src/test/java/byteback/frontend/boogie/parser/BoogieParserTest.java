package byteback.frontend.boogie.parser;

import beaver.Scanner;
import byteback.frontend.boogie.ResourcesUtil;
import byteback.frontend.boogie.scanner.BoogieLexer;
import org.junit.Test;

public class BoogieParserTest {

	@Test
	public void Parse_GivenSimpleProgram_DoesNotThrowExceptions() throws Exception {
		final BoogieParser parser = new BoogieParser();
		final Scanner scanner = new BoogieLexer(ResourcesUtil.getBoogieReader("Simple"));
		parser.parse(scanner);
	}

	@Test
	public void Parse_GivenArithmeticProgram_DoesNotThrowExceptions() throws Exception {
		final BoogieParser parser = new BoogieParser();
		final Scanner scanner = new BoogieLexer(ResourcesUtil.getBoogieReader("Arithmetic"));
		parser.parse(scanner);
	}

}
