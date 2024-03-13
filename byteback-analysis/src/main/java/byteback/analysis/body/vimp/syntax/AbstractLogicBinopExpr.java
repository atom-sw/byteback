package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.UnitPrinter;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import soot.grimp.Precedence;
import byteback.analysis.body.jimple.syntax.internal.AbstractBinopExpr;

public abstract class AbstractLogicBinopExpr extends AbstractBinopExpr implements LogicExpr, Precedence {

    public AbstractLogicBinopExpr(final Value op1, final Value op2) {
        super(Vimp.v().newArgBox(op1), Vimp.v().newArgBox(op2));
    }

    public AbstractLogicBinopExpr(final ValueBox op1box, final ValueBox op2box) {
        super(op1box, op2box);
    }

    @Override
    public abstract String getSymbol();

    @Override
    public void toString(final UnitPrinter printer) {
        getOp1().toString(printer);
        printer.literal(getSymbol());
        getOp2().toString(printer);
    }

}
