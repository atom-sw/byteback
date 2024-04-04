package byteback.analysis.local.vimp.transformer.unit;

import byteback.analysis.local.common.transformer.unit.UnitTransformer;
import byteback.analysis.local.vimp.syntax.unit.SpecificationStmt;
import byteback.analysis.local.vimp.syntax.value.LogicConstant;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.*;

/**
 * Converts bytecode boolean expressions into logic boolean expressions. Unlike bytecode boolean expressions, logic
 * boolean expressions cannot evaluate to an integer. If the result of a boolean expression needs to be used in an int
 * context, this transformation will cast it explicitly.
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
        } else if (unit instanceof ReturnStmt) {
            throw new IllegalArgumentException("Unable to transform expressions in return statements");
        }
    }

    public void transformValueOfType(final Type expectedType, final ValueBox valueBox) {
        final Value value = valueBox.getValue();

        if (value instanceof BinopExpr binopExpr) {
            if (value instanceof AndExpr || value instanceof OrExpr || value instanceof XorExpr) {
                // Handles the case for boolean connectives (if the expected type is boolean).
                transformValueOfType(expectedType, binopExpr.getOp1Box());
                transformValueOfType(expectedType, binopExpr.getOp2Box());
            }
        } else if (value instanceof UnopExpr unopExpr) {
            transformValueOfType(expectedType, unopExpr.getOpBox());
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
