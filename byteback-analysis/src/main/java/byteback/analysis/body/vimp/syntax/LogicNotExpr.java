package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.ValueBox;
import soot.jimple.NegExpr;
import soot.util.Switch;

public class LogicNotExpr extends AbstractLogicUnopExpr implements LogicExpr, NegExpr {

    public LogicNotExpr(final Value v) {
        super(v);
    }

    public LogicNotExpr(final ValueBox vbox) {
        super(vbox);
    }

    @Override
    public String getSymbol() {
        return "¬";
    }

    @Override
    public int equivHashCode() {
        return getOp().equivHashCode() * 101 + 17 ^ getSymbol().hashCode();
    }

    @Override
    public boolean equivTo(final Object object) {
        if (object instanceof AbstractLogicUnopExpr logicUnopExpr) {
            return getSymbol().equals(logicUnopExpr.getSymbol()) && getOp().equivTo(logicUnopExpr.getOp());
        }

        return false;
    }

    @Override
    public void apply(final Switch visitor) {
        if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
            vimpValueSwitch.caseLogicNotExpr(this);
        }
    }

    @Override
    public Object clone() {
        return new LogicNotExpr(getOp());
    }

}
