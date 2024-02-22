package byteback.analysis.transformer;

import static byteback.analysis.transformer.ValueTransformerFixture.assertEquiv;

import byteback.analysis.common.namespace.BBLibNamespace;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.LogicForallExpr;
import byteback.analysis.body.vimp.transformer.QuantifierValueTransformer;
import org.junit.Test;
import soot.BooleanType;
import soot.Local;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;
import soot.jimple.StaticInvokeExpr;

public class QuantifierValueTransformerIntegrationTest {

	public static StaticInvokeExpr mockQuantifierExprRef(final String methodName, final Value argument) {
		return TransformerFixture.mockStaticInvokeExpr(BBLibNamespace.QUANTIFIER_CLASS_NAME, methodName,
				new Value[]{argument});
	}

	@Test
	public void TransformValue_GivenExistsMethodRef_YieldsLogicExistsExpr() {
		final Local local = Jimple.v().newLocal("a", BooleanType.v());
		final StaticInvokeExpr quantifierRef = mockQuantifierExprRef(BBLibNamespace.UNIVERSAL_QUANTIFIER_NAME, local);
		final ValueBox valueBox = Jimple.v().newRValueBox(quantifierRef);
		final LogicForallExpr expectedExpr = Vimp.v().newLogicForallExpr(local, quantifierRef);
		QuantifierValueTransformer.v().transformValue(valueBox);
		assertEquiv(expectedExpr, valueBox.getValue());
	}

	@Test
	public void TransformValue_GivenForallMethodRef_YieldsLogicExistsExpr() {
		final Local local = Jimple.v().newLocal("a", BooleanType.v());
		final StaticInvokeExpr quantifierRef = mockQuantifierExprRef(BBLibNamespace.EXISTENTIAL_QUANTIFIER_NAME, local);
		final ValueBox valueBox = Jimple.v().newRValueBox(quantifierRef);
		final LogicForallExpr expectedExpr = Vimp.v().newLogicForallExpr(local, quantifierRef);
		QuantifierValueTransformer.v().transformValue(valueBox);
		assertEquiv(expectedExpr, valueBox.getValue());
	}

}
