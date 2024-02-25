package byteback.analysis.transformer;

import byteback.analysis.body.grimp.transformer.VimpUnitBodyTransformer;
import byteback.analysis.body.vimp.LogicConstant;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.common.namespace.BBLibNamespace;
import org.junit.Test;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StaticInvokeExpr;

import static byteback.analysis.transformer.UnitTransformerFixture.assertLogicUnitEquiv;

public class LogicUnitTransformerIntegrationTest {

    private static final VimpUnitBodyTransformer transformer = VimpUnitBodyTransformer.v();

    private static final LogicConstant trueConstant = LogicConstant.v(true);

    public static StaticInvokeExpr mockLogicStmtRef(final String methodName, final Value value) {
        return TransformerFixture.mockStaticInvokeExpr(BBLibNamespace.CONTRACT_CLASS_NAME, methodName, new Value[]{value});
    }

    @Test
    public void TransformUnit_GivenAssertionMethodRef_YieldsAssertionStmt() {
        final StaticInvokeExpr assertionRef = mockLogicStmtRef(BBLibNamespace.ASSERTION_NAME, trueConstant);
        final InvokeStmt invokeUnit = Jimple.v().newInvokeStmt(assertionRef);
        final UnitBox unitBox = Jimple.v().newStmtBox(invokeUnit);
        transformer.transformUnit(unitBox);
        final Unit transformedUnit = unitBox.getUnit();
        final Unit expectedunit = Vimp.v().newAssertionStmt(trueConstant);
        assertLogicUnitEquiv(expectedunit, transformedUnit);
    }

    @Test
    public void TransformUnit_GivenAssumptionMethodRef_YieldsAssumptionStmt() {
        final StaticInvokeExpr assumptionRef = mockLogicStmtRef(BBLibNamespace.ASSUMPTION_NAME, trueConstant);
        final InvokeStmt invokeUnit = Jimple.v().newInvokeStmt(assumptionRef);
        final UnitBox unitBox = Jimple.v().newStmtBox(invokeUnit);
        transformer.transformUnit(unitBox);
        final Unit transformedUnit = unitBox.getUnit();
        final Unit expectedunit = Vimp.v().newAssumptionStmt(trueConstant);
        assertLogicUnitEquiv(expectedunit, transformedUnit);
    }

    @Test
    public void TransformUnit_GivenInvariantMethodRef_YieldsInvariantStmt() {
        final StaticInvokeExpr invariantRef = mockLogicStmtRef(BBLibNamespace.INVARIANT_NAME, trueConstant);
        final InvokeStmt invokeUnit = Jimple.v().newInvokeStmt(invariantRef);
        final UnitBox unitBox = Jimple.v().newStmtBox(invokeUnit);
        transformer.transformUnit(unitBox);
        final Unit transformedUnit = unitBox.getUnit();
        final Unit expectedunit = Vimp.v().newInvariantStmt(trueConstant);
        assertLogicUnitEquiv(expectedunit, transformedUnit);
    }

}
