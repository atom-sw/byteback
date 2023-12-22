package byteback.analysis.transformer;

import static byteback.analysis.transformer.ValueTransformerFixture.assertEquiv;

import byteback.analysis.Namespace;
import byteback.analysis.Vimp;
import byteback.analysis.vimp.LogicForallExpr;
import org.junit.Test;
import soot.BooleanType;
import soot.Local;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;
import soot.jimple.StaticInvokeExpr;

public class QuantifierValueTransformerIntegrationTest {

	public static StaticInvokeExpr mockQuantifierExprRef(final String methodName, final Value argument) {
		return TransformerFixture.mockStaticInvokeExpr(Namespace.QUANTIFIER_CLASS_NAME, methodName,
				new Value[]{argument});
	}

	@Test
	public void TransformValue_GivenExistsMethodRef_YieldsLogicExistsExpr() {
		final Local local = Jimple.v().newLocal("a", BooleanType.v());
		final StaticInvokeExpr quantifierRef = mockQuantifierExprRef(Namespace.UNIVERSAL_QUANTIFIER_NAME, local);
		final ValueBox valueBox = Jimple.v().newRValueBox(quantifierRef);
		final LogicForallExpr expectedExpr = Vimp.v().newLogicForallExpr(local, quantifierRef);
		QuantifierValueTransformer.v().transformValue(valueBox);
		assertEquiv(expectedExpr, valueBox.getValue());
	}

	@Test
	public void TransformValue_GivenForallMethodRef_YieldsLogicExistsExpr() {
		final Local local = Jimple.v().newLocal("a", BooleanType.v());
		final StaticInvokeExpr quantifierRef = mockQuantifierExprRef(Namespace.EXISTENTIAL_QUANTIFIER_NAME, local);
		final ValueBox valueBox = Jimple.v().newRValueBox(quantifierRef);
		final LogicForallExpr expectedExpr = Vimp.v().newLogicForallExpr(local, quantifierRef);
		QuantifierValueTransformer.v().transformValue(valueBox);
		assertEquiv(expectedExpr, valueBox.getValue());
	}

}
