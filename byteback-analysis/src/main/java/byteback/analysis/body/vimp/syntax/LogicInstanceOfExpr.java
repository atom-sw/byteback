package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.model.syntax.type.Type;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.internal.AbstractInstanceOfExpr;

public class LogicInstanceOfExpr extends AbstractInstanceOfExpr implements LogicExpr {

    public LogicInstanceOfExpr(final Value op, final Type type) {
        super(Vimp.v().newArgBox(op), type);
    }

    public LogicInstanceOfExpr(final ValueBox opBox, final Type type) {
        super(opBox, type);
    }

    @Override
    public LogicInstanceOfExpr clone() {
        return new LogicInstanceOfExpr(Vimp.cloneIfNecessary(getOp()), getCheckType());
    }

}
