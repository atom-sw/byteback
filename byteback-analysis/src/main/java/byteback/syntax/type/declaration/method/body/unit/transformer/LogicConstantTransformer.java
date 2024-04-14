package byteback.syntax.type.declaration.method.body.unit.transformer;

import byteback.syntax.type.declaration.method.body.unit.context.UnitContext;
import byteback.syntax.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.type.declaration.method.body.unit.SpecificationStmt;
import byteback.syntax.value.LogicConstant;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.*;

/**
 * Introduces logic constants (true, false) wherever appropriate.
 *
 * @author paganma
 */
public class LogicConstantTransformer extends UnitTransformer {

    private static final Lazy<LogicConstantTransformer> INSTANCE = Lazy.from(LogicConstantTransformer::new);

    private LogicConstantTransformer() {
    }

    public static LogicConstantTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void walkUnit(final UnitContext unitContext) {
        final UnitBox unitBox = unitContext.getUnitBox();
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
