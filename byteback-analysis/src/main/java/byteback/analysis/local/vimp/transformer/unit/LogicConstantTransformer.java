package byteback.analysis.local.vimp.transformer.unit;

import byteback.analysis.local.common.transformer.unit.UnitTransformer;
import byteback.analysis.local.vimp.analyzer.value.VimpTypeInterpreter;
import byteback.analysis.local.vimp.syntax.unit.SpecificationStmt;
import byteback.analysis.local.vimp.syntax.value.LogicConstant;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JReturnVoidStmt;

/**
 * Introduces logic constants (true, false) wherever appropriate.
 *
 * @author paganma
 */
public class LogicConstantTransformer extends UnitTransformer {

    private static final Lazy<LogicConstantTransformer> instance = Lazy.from(LogicConstantTransformer::new);

    private LogicConstantTransformer() {
    }

    public static LogicConstantTransformer v() {
        return instance.get();
    }

    @Override
    public void transformUnit(final UnitBox unitBox) {
        final Unit unit = unitBox.getUnit();

        if (unit instanceof AssignStmt assignStmt) {
            final Type expectedType = assignStmt.getLeftOp().getType();
            final ValueBox rightValueBox = assignStmt.getRightOpBox();
            transformValueOfType(expectedType, rightValueBox);
        } else if (unit instanceof IfStmt ifStmt) {
            final ValueBox conditionValueBox = ifStmt.getConditionBox();
            transformValueOfType(BooleanType.v(), conditionValueBox);
        } else if (unit instanceof InvokeStmt invokeStmt) {
            transformValueOfType(VoidType.v(), invokeStmt.getInvokeExprBox());
        } else if (unit instanceof SpecificationStmt specificationStmt) {
            final ValueBox conditionValueBox = specificationStmt.getConditionBox();
            transformValueOfType(BooleanType.v(), conditionValueBox);
        }
    }

    public void transformValueOfType(final Type expectedType, final ValueBox valueBox) {
        final Value value = valueBox.getValue();

        if (value instanceof BinopExpr binopExpr) {
            final Type type1 = VimpTypeInterpreter.v().typeOf(binopExpr.getOp1());
            final Type type2 = VimpTypeInterpreter.v().typeOf(binopExpr.getOp2());
            final Type type = VimpTypeInterpreter.v().join(type1, type2);

            transformValueOfType(type, binopExpr.getOp1Box());
            transformValueOfType(type, binopExpr.getOp2Box());
        } else if (value instanceof InvokeExpr invokeExpr) {
            for (int i = 0; i < invokeExpr.getArgCount(); ++i) {
                final ValueBox argBox = invokeExpr.getArgBox(i);
                final Type argType = invokeExpr.getMethodRef().getParameterType(i);
                transformValueOfType(argType, argBox);
            }
        } else if (value instanceof IntConstant intConstant && expectedType instanceof BooleanType) {
            valueBox.setValue(LogicConstant.v(intConstant.value > 0));
        }
    }

}
