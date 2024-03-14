package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.Immediate;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.SpecialExprSwitch;
import byteback.analysis.model.syntax.type.Type;
import soot.*;
import byteback.analysis.body.jimple.syntax.stmt.UnopExpr;
import soot.util.Switch;

public class OldExpr extends UnopExpr implements Immediate {

    public OldExpr(final Value v) {
        super(Vimp.v().newArgBox(v));
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

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof OldExpr oldExpr && oldExpr.getOp().equivTo(getOp());
    }

    @Override
    public int equivHashCode() {
        return getOp().equivHashCode() * 101 + 17 ^ "old".hashCode();
    }

}
