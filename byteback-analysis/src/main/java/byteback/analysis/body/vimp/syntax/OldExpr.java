package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.Immediate;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.model.syntax.type.Type;
import byteback.analysis.body.jimple.syntax.stmt.UnopExpr;

public class OldExpr extends UnopExpr implements Immediate {

    public OldExpr(final Value value) {
        super(new ImmediateBox(value));
    }

    public OldExpr(final ValueBox vbox) {
        super(vbox);
    }

    @Override
    public Object clone() {
        return new OldExpr(getOp());
    }

    @Override
    public Type getType() {
        return getOp().getType();
    }
}
