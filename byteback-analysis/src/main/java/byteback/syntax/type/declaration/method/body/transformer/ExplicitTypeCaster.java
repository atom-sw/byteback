package byteback.syntax.type.declaration.method.body.transformer;

import byteback.syntax.type.declaration.method.body.context.BodyContext;
import byteback.syntax.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.value.NestedExprConstructor;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.*;

public class ExplicitTypeCaster extends BodyTransformer {

    private static final Lazy<ExplicitTypeCaster> INSTANCE = Lazy.from(ExplicitTypeCaster::new);

    public static ExplicitTypeCaster v() {
        return INSTANCE.get();
    }

    private ExplicitTypeCaster() {
    }

    public void castIfNeeded(final NestedExprConstructor exprConstructor, final ValueBox valueBox,
                             final Type expectedType) {
        final Value value = valueBox.getValue();

        // Propagate checks to subexpressions.
        if (value instanceof final BinopExpr binopExpr) {
            if (binopExpr instanceof AndExpr
                    || binopExpr instanceof OrExpr
                    || binopExpr instanceof XorExpr
                    || binopExpr instanceof AddExpr
                    || binopExpr instanceof SubExpr
                    || binopExpr instanceof MulExpr
                    || binopExpr instanceof DivExpr
                    || binopExpr instanceof EqExpr
                    || binopExpr instanceof LtExpr
                    || binopExpr instanceof LeExpr
                    || binopExpr instanceof GtExpr
                    || binopExpr instanceof GeExpr
            ) {
                final Type type1 = VimpTypeInterpreter.v().typeOf(binopExpr.getOp1());
                final Type type2 = VimpTypeInterpreter.v().typeOf(binopExpr.getOp2());
                final Type joinedType = VimpTypeInterpreter.v().join(type1, type2);

                castIfNeeded(exprConstructor, binopExpr.getOp1Box(), joinedType);
                castIfNeeded(exprConstructor, binopExpr.getOp2Box(), joinedType);
            }
        } else if (value instanceof final UnopExpr unopExpr) {
            if (unopExpr instanceof LengthExpr) {
                if (unopExpr.getOp().getType() instanceof ArrayType) {
                    return;
                }
            } else {
                castIfNeeded(exprConstructor, unopExpr.getOpBox(), expectedType);
            }
        }

        // If the current expression's type differs from the expected type, we cast the expression to that expected
        // type.
        final Type actualType = VimpTypeInterpreter.v().typeOf(value);

        if (actualType != expectedType) {
            final Value newValue =
                    exprConstructor.apply(Jimple.v().newCastExpr(exprConstructor.apply(value), expectedType));
            valueBox.setValue(newValue);
        }
    }

    @Override
    public void walkBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final var exprConstructor = new NestedExprConstructor(body);

        for (final Unit unit : body.getUnits()) {
            if (unit instanceof AssignStmt assignStmt) {
                final ValueBox rightOpBox = assignStmt.getRightOpBox();
                final Type leftType = VimpTypeInterpreter.v().typeOf(assignStmt.getLeftOp());
                castIfNeeded(exprConstructor, rightOpBox, leftType);
            }
        }
    }

}
