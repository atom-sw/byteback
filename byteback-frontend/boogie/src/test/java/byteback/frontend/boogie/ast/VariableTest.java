package byteback.frontend.boogie.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import org.junit.Test;

public class VariableTest extends ASTTestFixture {

	@Test
	public void Variables_OnSimpleProgram_ReturnsSingleElementTable() {
		final Program program = getProgram("Simple");
		assertEquals(1, program.variables().size());
	}

	@Test
	public void Variables_OnSimpleIdentityFunction_ReturnsSingleElementTable() {
		final Function function = getFunction("Simple", "identity");
		assertEquals(1, function.variables().size());
	}

	@Test
	public void Variables_OnArithmeticAdditionFunction_Returns2ElementsTable() {
		final Function function = getFunction("Arithmetic", "addition");
		assertEquals(2, function.variables().size());
	}

	@Test
	public void Variables_OnArithmeticSumProcedure_Returns2ElementsTable() {
		final Procedure procedure = getProcedure("Arithmetic", "sum");
		assertEquals(2, procedure.variables().size());
	}

	@Test
	public void Variables_OnArithmeticIdentityProcedure_Returns2ElementsTable() {
		final Procedure procedure = getProcedure("Simple", "identity");
		assertEquals(2, procedure.variables().size());
	}

	@Test
	public void Variables_OnArithmeticIdentityProcedureBody_Returns2ElementsTable() {
		final Procedure procedure = getProcedure("Simple", "identity");
		assertEquals(1, procedure.getBody().variables().size());
	}

	@Test
	public void Variables_OnSimplePrototypeImplementation_Returns2ElementsTable() {
		final Collection<Implementation> implementations = getImplementations("Simple", "prototype");

		for (Implementation implementation : implementations) {
			assertEquals(2, implementation.variables().size());
		}
	}

	@Test
	public void GetConstantDeclaration_OnSimpleUnitVariable_ReturnsConstantDeclaration() {
		final Variable variable = getVariable("Simple", "unit");
		assertTrue(variable.getConstantDeclaration().isPresent());
	}

	@Test
	public void References_OnSimpleUnitVariable_ReturnsListOfTwo() {
		final Variable variable = getVariable("Simple", "unit");
		assertEquals(2, variable.references().size());
	}

	@Test
	public void References_OnSimpleIdentityProcedureVariable0_ReturnsListOfOne() {
		final Variable variable = getProcedure("Simple", "identity").variables().get(0);
		assertEquals(1, variable.references().size());
	}

	@Test
	public void References_OnArithmeticAdditionParameters_ReturnsListOfOne() {
		final Function function = getFunction("Arithmetic", "addition");

		for (Variable variable : function.variables()) {
			assertEquals(1, variable.references().size());
		}
	}

}
