package byteback.analysis.body.vimp.syntax;

import byteback.analysis.scene.Types;
import soot.Type;
import soot.ValueBox;
import soot.jimple.Expr;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTernaryExpr implements Expr {

    private final ValueBox op1Box;

    private final ValueBox op2Box;

    private final ValueBox op3Box;

    public AbstractTernaryExpr(final ValueBox op1Box, final ValueBox op2Box, final ValueBox op3Box) {
        this.op1Box = op1Box;
        this.op2Box = op2Box;
        this.op3Box = op3Box;
    }

    public ValueBox getOp1Box() {
        return op1Box;
    }

    public ValueBox getOp2Box() {
        return op2Box;
    }

    public ValueBox getOp3Box() {
        return op3Box;
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
    public Type getType() {
        return Types.v().join(op2Box.getValue().getType(), op3Box.getValue().getType());
    }

    @Override
    public abstract Object clone();

}
