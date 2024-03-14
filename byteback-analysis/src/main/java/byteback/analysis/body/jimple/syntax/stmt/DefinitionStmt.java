package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.stmt.Stmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DefinitionStmt extends Stmt {

    protected final ValueBox leftBox;

    protected final ValueBox rightBox;

    protected DefinitionStmt(final ValueBox leftBox, final ValueBox rightBox) {
        this.leftBox = leftBox;
        this.rightBox = rightBox;
    }

    public final Value getLeftOp() {
        return leftBox.getValue();
    }

    public final Value getRightOp() {
        return rightBox.getValue();
    }

    public final ValueBox getLeftOpBox() {
        return leftBox;
    }

    public final ValueBox getRightOpBox() {
        return rightBox;
    }

    public final List<ValueBox> getDefBoxes() {
        return Collections.singletonList(leftBox);
    }

    public List<ValueBox> getUseBoxes() {
        final var list = new ArrayList<ValueBox>();

        list.addAll(getLeftOp().getUseBoxes());
        list.add(rightBox);
        list.addAll(getRightOp().getUseBoxes());

        return list;
    }

    public boolean fallsThrough() {
        return true;
    }

    public boolean branches() {
        return false;
    }
}
