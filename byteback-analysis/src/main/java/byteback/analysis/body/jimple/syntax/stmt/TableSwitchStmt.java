package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.stmt.StmtBox;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.syntax.stmt.UnitBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;

import java.util.List;

public class TableSwitchStmt extends SwitchStmt {

    protected int lowIndex;
    protected int highIndex;

    public TableSwitchStmt(final Value key, int lowIndex, int highIndex, final List<? extends Unit> targets,
                           final Unit defaultTarget) {

        this(new ImmediateBox(key), lowIndex, highIndex,
                getTargetBoxesArray(targets, StmtBox::new),
                new StmtBox(defaultTarget));
    }

    public TableSwitchStmt(final Value key, final int lowIndex, int highIndex, final List<? extends UnitBox> targets,
                           final UnitBox defaultTarget) {
        this(new ImmediateBox(key), lowIndex, highIndex, targets.toArray(new UnitBox[0]), defaultTarget);
    }

    protected TableSwitchStmt(final ValueBox keyBox, final int lowIndex, final int highIndex,
                              final UnitBox[] targetBoxes, final UnitBox defaultTargetBox) {

        super(keyBox, defaultTargetBox, targetBoxes);

        if (lowIndex > highIndex) {
            throw new IllegalArgumentException(
                    "Error creating tableswitch: lowIndex(" + lowIndex + ") can't be greater than highIndex(" + highIndex + ").");
        }

        this.lowIndex = lowIndex;
        this.highIndex = highIndex;
    }

    @Override
    public String toString() {
        final char endOfLine = ' ';
        StringBuilder builder = new StringBuilder("tableswitch (");

        builder.append(keyBox.getValue().toString()).append(')').append(endOfLine);
        builder.append('{').append(endOfLine);

        // In this for-loop, we cannot use "<=" since 'i' would wrap around.
        // The case for "i == highIndex" is handled separately after the loop.
        final int low = lowIndex, high = highIndex;
        for (int i = low; i < high; i++) {
            builder.append("    case ").append(i).append(": goto ");
            Unit target = getTarget(i - low);
            builder.append(target == this ? "self" : target).append(';').append(endOfLine);
        }
        {
            builder.append("    goto ").append(high).append(": goto ");
            Unit target = getTarget(high - low);
            builder.append(target == this ? "self" : target).append(';').append(endOfLine);
        }
        {
            Unit target = getDefaultTarget();
            builder.append("    default: goto ");
            builder.append(target == this ? "self" : target).append(';').append(endOfLine);
        }
        builder.append('}');

        return builder.toString();
    }

    public void setLowIndex(int lowIndex) {
        this.lowIndex = lowIndex;
    }

    public void setHighIndex(int highIndex) {
        this.highIndex = highIndex;
    }

    public int getLowIndex() {
        return lowIndex;
    }

    public int getHighIndex() {
        return highIndex;
    }
}
