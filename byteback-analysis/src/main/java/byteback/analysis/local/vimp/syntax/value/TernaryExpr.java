package byteback.analysis.local.vimp.syntax.value;

import byteback.analysis.local.common.syntax.value.DefaultCaseValue;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Expr;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a ternary expression.
 *
 * @author paganma
 */
public abstract class TernaryExpr implements Expr, DefaultCaseValue {

    private final ValueBox op1Box;

    private final ValueBox op2Box;

    private final ValueBox op3Box;

    public TernaryExpr(final ValueBox op1Box, final ValueBox op2Box, final ValueBox op3Box) {
        this.op1Box = op1Box;
        this.op2Box = op2Box;
        this.op3Box = op3Box;
    }

    public ValueBox getOp1Box() {
        return op1Box;
    }

    public Value getOp1() {
        return op1Box.getValue();
    }

    public ValueBox getOp2Box() {
        return op2Box;
    }

    public Value getOp2() {
        return op2Box.getValue();
    }

    public ValueBox getOp3Box() {
        return op3Box;
    }

    public Value getOp3() {
        return op2Box.getValue();
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        final var useBoxes =  new ArrayList<ValueBox>();
        useBoxes.add(op1Box);
        useBoxes.add(op2Box);
        useBoxes.add(op3Box);

        return useBoxes;
    }

    @Override
    public abstract Object clone();

}
