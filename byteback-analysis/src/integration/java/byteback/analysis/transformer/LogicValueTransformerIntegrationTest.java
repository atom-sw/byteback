package byteback.analysis.transformer;

import static byteback.analysis.transformer.ValueTransformerFixture.assertEquiv;
import static org.junit.Assert.assertEquals;

import byteback.analysis.Vimp;
import byteback.analysis.vimp.LogicConstant;
import org.junit.Test;
import soot.BooleanType;
import soot.Local;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

public class LogicValueTransformerIntegrationTest {

	private static final LogicValueTransformer transformer = new LogicValueTransformer(null);

	@Test
	public void TransformValue_GivenFalseIntConstantBox_YieldsFalse() {
		final IntConstant intConstant = IntConstant.v(0);
		final ValueBox valueBox = Jimple.v().newImmediateBox(intConstant);
		transformer.transformValue(valueBox);
		assertEquals(LogicConstant.v(false), valueBox.getValue());
	}

	@Test
	public void TransformValue_GivenTrueIntConstantBox_YieldsFalse() {
		final IntConstant intConstant = IntConstant.v(1);
		final ValueBox valueBox = Jimple.v().newImmediateBox(intConstant);
		transformer.transformValue(valueBox);
		assertEquals(LogicConstant.v(true), valueBox.getValue());
	}

	@Test
	public void TransformValue_GivenBooleanAndBox_YieldsLogicAnd() {
		final Value booleanAnd = Jimple.v().newAndExpr(LogicConstant.v(true), LogicConstant.v(false));
		final Value logicAnd = Vimp.v().newLogicAndExpr(LogicConstant.v(true), LogicConstant.v(false));
		final ValueBox valueBox = Jimple.v().newRValueBox(booleanAnd);
		transformer.transformValue(valueBox);
		assertEquiv(logicAnd, valueBox.getValue());
	}

	@Test
	public void TransformStatement_GivenIntConstantAssignStmt_YieldsLogicConstantAssignStmt() {
		final Local local = Jimple.v().newLocal("l", BooleanType.v());
		final AssignStmt transformed = Jimple.v().newAssignStmt(local, IntConstant.v(1));
		final AssignStmt expected = Jimple.v().newAssignStmt(local, LogicConstant.v(true));
		transformer.transformUnit(transformed);
		assertEquiv(expected.getRightOp(), transformed.getRightOp());
	}

}
