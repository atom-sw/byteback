package byteback.analysis.vimp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import soot.BooleanType;
import soot.Local;
import soot.NormalUnitPrinter;
import soot.Type;
import soot.UnitPrinter;
import soot.jimple.Jimple;
import soot.util.Chain;
import soot.util.HashChain;

public class QuantifierExprIntegrationTest {

	public static Chain<Local> makeLocals(final int n, final Type t) {
		final Chain<Local> locals = new HashChain<>();

		for (int i = 0; i < n; ++i) {
			locals.add(Jimple.v().newLocal("v" + i, t));
		}

		return locals;
	}

	@Test
	public void ToString_OnBasicForallExpr_PrintsCorrectly() {
		final Chain<Local> locals = makeLocals(1, BooleanType.v());
		final LogicForallExpr value = new LogicForallExpr(locals, locals.getFirst());
		final UnitPrinter printer = new NormalUnitPrinter(Jimple.v().newBody());
		printer.setIndent("");
		value.toString(printer);
		assertEquals("(∀ boolean v0 :: v0)", printer.output().toString());
	}

	@Test
	public void ToString_OnBasicExistsExpr_PrintsCorrectly() {
		final Chain<Local> locals = makeLocals(1, BooleanType.v());
		final LogicExistsExpr value = new LogicExistsExpr(locals, locals.getFirst());
		final UnitPrinter printer = new NormalUnitPrinter(Jimple.v().newBody());
		printer.setIndent("");
		value.toString(printer);
		assertEquals("(∃ boolean v0 :: v0)", printer.output().toString());
	}

	@Test
	public void ToString_OnMultiParamExistsExpr_PrintsCorrectly() {
		final Chain<Local> locals = makeLocals(2, BooleanType.v());
		final LogicExistsExpr value = new LogicExistsExpr(locals, locals.getFirst());
		final UnitPrinter printer = new NormalUnitPrinter(Jimple.v().newBody());
		printer.setIndent("");
		value.toString(printer);
		assertEquals("(∃ boolean v0, boolean v1 :: v0)", printer.output().toString());
	}

}
