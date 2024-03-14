package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.jimple.syntax.stmt.UnopExpr;

public abstract class LogicUnopExpr extends UnopExpr {

    public LogicUnopExpr(final Value op) {
        super(new ImmediateBox(op));
    }

    public LogicUnopExpr(final ValueBox opBox) {
        super(opBox);
    }

    public abstract String getSymbol();

}
