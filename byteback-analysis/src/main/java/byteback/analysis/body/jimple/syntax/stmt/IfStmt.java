package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.stmt.Stmt;
import byteback.analysis.body.common.syntax.stmt.StmtBox;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.syntax.stmt.UnitBox;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ConditionExprBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IfStmt extends Stmt {

    protected final ValueBox conditionBox;
    protected final UnitBox targetBox;
    protected final List<UnitBox> targetBoxes;

    public IfStmt(final Value condition, final Unit target) {
        this(condition, new StmtBox(target));
    }

    public IfStmt(final Value condition, final UnitBox target) {
        this(new ConditionExprBox(condition), target);
    }

    protected IfStmt(ValueBox conditionBox, UnitBox targetBox) {
        this.conditionBox = conditionBox;
        this.targetBox = targetBox;
        this.targetBoxes = Collections.singletonList(targetBox);
    }

    @Override
    public String toString() {
        Unit t = getTarget();
        String target = t.branches() ? "(branch)" : t.toString();
        return "if " + getCondition().toString() + " goto " + target;
    }

    public Value getCondition() {
        return conditionBox.getValue();
    }

    public void setCondition(Value condition) {
        conditionBox.setValue(condition);
    }

    public ValueBox getConditionBox() {
        return conditionBox;
    }

    public Stmt getTarget() {
        return (Stmt) targetBox.getUnit();
    }

    public void setTarget(Unit target) {
        targetBox.setUnit(target);
    }

    public UnitBox getTargetBox() {
        return targetBox;
    }

    public List<ValueBox> getUseBoxes() {
        List<ValueBox> useBoxes = new ArrayList<>(conditionBox.getValue().getUseBoxes());
        useBoxes.add(conditionBox);
        return useBoxes;
    }

    @Override
    public final List<UnitBox> getUnitBoxes() {
        return targetBoxes;
    }

    @Override
    public boolean fallsThrough() {
        return true;
    }

    @Override
    public boolean branches() {
        return true;
    }
}
