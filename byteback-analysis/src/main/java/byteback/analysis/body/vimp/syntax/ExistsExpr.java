package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.jimple.syntax.expr.Local;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.common.syntax.Chain;

public class ExistsExpr extends QuantifierExpr {

    public ExistsExpr(final Chain<Local> freeLocals, final Value value) {
        super(freeLocals, value);
    }

    @Override
    protected String getSymbol() {
        return "∃";
    }
}
