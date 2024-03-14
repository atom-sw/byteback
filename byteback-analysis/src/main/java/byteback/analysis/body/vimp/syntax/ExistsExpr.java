package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.common.syntax.Chain;
import soot.util.Chain;
import soot.util.Switch;

public class ExistsExpr extends QuantifierExpr {

    public ExistsExpr(final Chain<Local> freeLocals, final Value value) {
        super(freeLocals, value);
    }

    @Override
    protected String getSymbol() {
        return "∃";
    }
}
