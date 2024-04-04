package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.analyzer.value.VimpTypeInterpreter;
import byteback.analysis.local.vimp.syntax.value.NestedExprConstructor;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

public class VimpAssignmentCaster extends BodyTransformer {

    public void convertIfNeeded(final NestedExprConstructor exprConstructor, final ValueBox valueBox,
                                final Type expectedType) {
        final Value value = valueBox.getValue();
        final Type actualType = VimpTypeInterpreter.v().typeOf(value);

        if (actualType != expectedType) {
            final Value newValue =
                    exprConstructor.apply(Jimple.v().newCastExpr(exprConstructor.apply(value), expectedType));
            valueBox.setValue(newValue);
        }
    }

    @Override
    public void transformBody(final Body body) {
        final var exprConstructor = new NestedExprConstructor(body);

        for (final Unit unit : body.getUnits()) {
            if (unit instanceof AssignStmt assignStmt) {
                final ValueBox rightOpBox = assignStmt.getRightOpBox();
                final Type leftType = VimpTypeInterpreter.v().typeOf(assignStmt.getLeftOp());
                convertIfNeeded(exprConstructor, rightOpBox, leftType);
            }
        }
    }

}
