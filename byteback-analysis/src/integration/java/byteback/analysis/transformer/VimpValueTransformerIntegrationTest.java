package byteback.analysis.transformer;

import byteback.analysis.body.jimple.transformer.VimpValueBodyTransformer;
import byteback.analysis.body.vimp.LogicConstant;
import byteback.analysis.body.vimp.Vimp;
import org.junit.Test;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

import static byteback.analysis.transformer.ValueTransformerFixture.assertEquiv;
import static org.junit.Assert.assertEquals;

public class VimpValueTransformerIntegrationTest {

    private static final VimpValueBodyTransformer.VimpValueTransformer transformer = new VimpValueBodyTransformer.VimpValueTransformer(UnknownType.v());

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
