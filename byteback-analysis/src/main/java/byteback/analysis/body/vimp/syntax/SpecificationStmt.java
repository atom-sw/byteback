package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.stmt.Stmt;

import java.util.ArrayList;
import java.util.List;

public abstract class SpecificationStmt extends Stmt {

    private final ValueBox conditionBox;

    public SpecificationStmt(final Value condition) {
        this.conditionBox = new ImmediateBox(condition);
    }

    public ValueBox getConditionBox() {
        return conditionBox;
    }

    public Value getCondition() {
        return conditionBox.getValue();
    }

    @Override
    public boolean branches() {
        return false;
    }

    @Override
    public boolean fallsThrough() {
        return true;
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        final ArrayList<ValueBox> useBoxes = new ArrayList<>();
        useBoxes.add(conditionBox);
        useBoxes.addAll(conditionBox.getValue().getUseBoxes());

        return useBoxes;
    }

}
