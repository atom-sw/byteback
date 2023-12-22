package byteback.frontend.boogie.ast;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ImplementationTest extends ASTTestFixture {

	@Test
	public void Implementations_OnSimpleProgram_ReturnsOneElementTable() {
		final Program program = getProgram("Simple");
		assertTrue(program.implementations().size() == 1);
	}

	@Test
	public void Implementations_OnArithmeticProgram_ReturnsZeroElementTable() {
		final Program program = getProgram("Arithmetic");
		assertTrue(program.implementations().size() == 0);
	}

}
