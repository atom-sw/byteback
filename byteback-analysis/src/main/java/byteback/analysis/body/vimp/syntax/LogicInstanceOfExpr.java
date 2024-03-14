package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.model.syntax.type.Type;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.jimple.syntax.expr.InstanceOfExpr;

public class LogicInstanceOfExpr extends InstanceOfExpr implements LogicExpr {

    public LogicInstanceOfExpr(final Value op, final Type type) {
        super(new ImmediateBox(op), type);
    }

    public LogicInstanceOfExpr(final ValueBox opBox, final Type type) {
        super(opBox, type);
    }
}
