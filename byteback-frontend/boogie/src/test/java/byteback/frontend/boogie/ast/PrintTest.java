package byteback.frontend.boogie.ast;

import org.junit.Test;

public class PrintTest extends ASTTestFixture {

	@Test
	public void Print_GivenSimpleProgram_DoesNotThrowException() {
		final Program program = getProgram("Simple");
		final StringBuilder builder = new StringBuilder();
		program.print(builder);
	}

}
