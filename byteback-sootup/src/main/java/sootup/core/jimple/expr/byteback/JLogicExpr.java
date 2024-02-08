package sootup.core.jimple.expr.byteback;

import sootup.core.jimple.basic.Value;
import sootup.core.jimple.visitor.CompoundExprVisitor;
import sootup.core.types.PrimitiveType;
import sootup.core.types.Type;

public class JLogicExpr<T extends Value> extends JCompoundExpr<T> {

    public JLogicExpr(final T expr) {
        super(expr);
    }

    @Override
    public void accept(final CompoundExprVisitor visitor) {
        visitor.caseLogicExpr(this);
    }

    @Override
    public Type getType() {
        return PrimitiveType.BooleanType.getInstance();
    }

}
