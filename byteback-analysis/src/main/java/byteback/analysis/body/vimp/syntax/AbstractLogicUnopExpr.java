package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.UnitPrinter;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.internal.AbstractUnopExpr;

public abstract class AbstractLogicUnopExpr extends AbstractUnopExpr {

    public AbstractLogicUnopExpr(final Value op) {
        super(Vimp.v().newArgBox(op));
    }

    public AbstractLogicUnopExpr(final ValueBox opBox) {
        super(opBox);
    }

    public abstract String getSymbol();

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal(getSymbol());
        getOp().toString(printer);
    }

}
