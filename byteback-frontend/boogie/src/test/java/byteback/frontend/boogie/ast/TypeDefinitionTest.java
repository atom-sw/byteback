package byteback.frontend.boogie.ast;

import static org.junit.Assert.assertEquals;

import byteback.frontend.boogie.TestUtil;
import org.junit.Test;

public class TypeDefinitionTest extends ASTTestFixture {

	@Test
	public void TypeDefinitions_OnSimpleProgram_ReturnsOneElementTable() {
		final Program program = getProgram("Simple");
		assertEquals(program.typeDefinitions().size(), 1);
	}

	@Test
	public void TypeDefinitions_OnArithmeticProgram_ReturnsZeroElementTable() {
		final Program program = getProgram("Arithmetic");
		assertEquals(program.typeDefinitions().size(), 0);
	}

	@Test
	public void getDefinedType_OnSimpleUnitType_DoesNotThrowException() {
		final TypeDefinition typeDefinition = getTypeDefinition("Simple", "Unit");
		typeDefinition.getDefinedType();
	}

	@Test
	public void makeTypeAccess_OnSimpleUnitType_ReturnsExpectedTypeAccess() {
		final TypeDefinition typeDefinition = getTypeDefinition("Simple", "Unit");
		final TypeAccess actual = typeDefinition.getDefinedType().makeTypeAccess();
		final TypeAccess expected = new UnknownTypeAccess(typeDefinition.getDefinedType().makeAccessor(), new List<>());
		TestUtil.assertAstEquals(expected, actual);
	}

}
