package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.unit.SpecificationStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.LogicConstant;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
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
    public void transformUnit(final UnitContext unitContext) {
        final UnitBox unitBox = unitContext.getUnitBox();
        final Unit unit = unitBox.getUnit();

        if (unit instanceof final AssignStmt assignStmt) {
            final Type expectedType = assignStmt.getLeftOp().getType();
            final ValueBox rightValueBox = assignStmt.getRightOpBox();
            transformValueOfType(expectedType, rightValueBox);
        } else if (unit instanceof final IfStmt ifStmt) {
            final ValueBox conditionValueBox = ifStmt.getConditionBox();
            transformValueOfType(BooleanType.v(), conditionValueBox);
        } else if (unit instanceof final InvokeStmt invokeStmt) {
            transformValueOfType(VoidType.v(), invokeStmt.getInvokeExprBox());
        } else if (unit instanceof final SpecificationStmt specificationStmt) {
            final ValueBox conditionValueBox = specificationStmt.getConditionBox();
            transformValueOfType(BooleanType.v(), conditionValueBox);
        } else if (unit instanceof final ReturnStmt returnStmt) {
            final SootMethod sootMethod = unitContext.getBodyContext().getSootMethod();
            transformValueOfType(sootMethod.getReturnType(), returnStmt.getOpBox());
        }
    }

    public void transformValueOfType(final Type expectedType, final ValueBox valueBox) {
        final Value value = valueBox.getValue();

        if (value instanceof final BinopExpr binopExpr) {
            final Type op1Type = VimpTypeInterpreter.v().typeOf(binopExpr.getOp1());
            final Type op2Type = VimpTypeInterpreter.v().typeOf(binopExpr.getOp2());
            final Type type = VimpTypeInterpreter.v().join(op1Type, op2Type);
            transformValueOfType(type, binopExpr.getOp1Box());
            transformValueOfType(type, binopExpr.getOp2Box());
        } else if (value instanceof final InvokeExpr invokeExpr) {
            for (int i = 0; i < invokeExpr.getArgCount(); ++i) {
                final ValueBox argBox = invokeExpr.getArgBox(i);
                final Type argType = invokeExpr.getMethodRef().getParameterType(i);
                transformValueOfType(argType, argBox);
            }
        } else if (value instanceof final IntConstant intConstant
                && expectedType instanceof BooleanType) {
            valueBox.setValue(LogicConstant.v(intConstant.value > 0));
        }
    }

}
