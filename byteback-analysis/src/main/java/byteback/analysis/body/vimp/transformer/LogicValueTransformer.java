package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.syntax.*;
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
public class LogicValueTransformer extends BodyTransformer {

    private static final Lazy<LogicValueTransformer> instance = Lazy.from(LogicValueTransformer::new);

    private LogicValueTransformer() {
    }

    public static LogicValueTransformer v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        for (final Unit unit : body.getUnits()) {
            if (unit instanceof AssignStmt assignStmt) {
                final Type type = assignStmt.getLeftOp().getType();
                final ValueBox rightValueBox = assignStmt.getRightOpBox();
                transformValueOfType(type, rightValueBox);
            } else if (unit instanceof IfStmt ifStmt) {
                final ValueBox conditionValueBox = ifStmt.getConditionBox();
                transformValueOfType(BooleanType.v(), conditionValueBox);
            } else if (unit instanceof ReturnStmt returnStmt) {
                final ValueBox returnValueBox = returnStmt.getOpBox();
                transformValueOfType(body.getMethod().getReturnType(), returnValueBox);
            } else if (unit instanceof SpecificationStmt specificationStmt) {
                final ValueBox conditionValueBox = specificationStmt.getConditionBox();
                transformValueOfType(BooleanType.v(), conditionValueBox);
            }
        }
    }

    public void transformValueOfType(final Type type, final ValueBox valueBox) {
        final Value value = valueBox.getValue();
        if (value instanceof BinopExpr binopExpr) {
            if (value instanceof AndExpr || value instanceof OrExpr || value instanceof XorExpr) {
                transformValueOfType(type, binopExpr.getOp1Box());
                transformValueOfType(type, binopExpr.getOp2Box());
            }
        } else if (value instanceof UnopExpr unopExpr) {
            transformValueOfType(type, unopExpr.getOpBox());
        } else if (value instanceof InvokeExpr invokeExpr) {
            for (int i = 0; i < invokeExpr.getArgCount(); ++i) {
                final ValueBox argBox = invokeExpr.getArgBox(i);
                final Type argType = invokeExpr.getMethodRef().getParameterType(i);
                transformValueOfType(argType, argBox);
            }
        } else if (value instanceof IntConstant intConstant && type instanceof BooleanType) {
            valueBox.setValue(LogicConstant.v(intConstant.value > 0));
        }
    }
}
