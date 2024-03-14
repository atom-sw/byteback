package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.stmt.Stmt;
import byteback.analysis.body.common.syntax.stmt.StmtBox;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.syntax.stmt.UnitBox;

import java.util.Collections;
import java.util.List;

public class GotoStmt extends Stmt {

    protected final UnitBox targetBox;
    protected final List<UnitBox> targetBoxes;

    public GotoStmt(Unit target) {
        this(new StmtBox((Stmt) target));
    }

    public GotoStmt(UnitBox box) {
        this.targetBox = box;
        this.targetBoxes = Collections.singletonList(box);
    }

    @Override
    public String toString() {
        Unit t = getTarget();
        String target = t.branches() ? "(branch)" : t.toString();
        return "goto [?= " + target + "]";
    }

    public Unit getTarget() {
        return targetBox.getUnit();
    }

    public UnitBox getTargetBox() {
        return targetBox;
    }

    @Override
    public List<UnitBox> getUnitBoxes() {
        return targetBoxes;
    }

    @Override
    public boolean fallsThrough() {
        return false;
    }

    @Override
    public boolean branches() {
        return true;
    }
}
