package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.stmt.StmtBox;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.syntax.stmt.UnitBox;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.body.jimple.syntax.expr.IntConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class LookupSwitchStmt extends SwitchStmt {
    /**
     * List of lookup values from the corresponding bytecode instruction, represented as IntConstants.
     */
    protected List<IntConstant> lookupValues;

    /**
     * Constructs a new JLookupSwitchStmt. lookupValues should be a list of IntConst s.
     */
    public LookupSwitchStmt(final Value key, List<IntConstant> lookupValues, List<? extends Unit> targets, Unit defaultTarget) {
        this(new ImmediateBox(key), lookupValues, getTargetBoxesArray(targets, StmtBox::new), new StmtBox(defaultTarget));
    }

    /**
     * Constructs a new JLookupSwitchStmt. lookupValues should be a list of IntConst s.
     */
    public LookupSwitchStmt(Value key, List<IntConstant> lookupValues, List<? extends UnitBox> targets,
                            UnitBox defaultTarget) {
        this(new ImmediateBox(key), lookupValues, targets.toArray(new UnitBox[0]), defaultTarget);
    }

    protected LookupSwitchStmt(ValueBox keyBox, List<IntConstant> lookupValues, UnitBox[] targetBoxes,
                               UnitBox defaultTargetBox) {
        super(keyBox, defaultTargetBox, targetBoxes);
        setLookupValues(lookupValues);
    }

    @Override
    public String toString() {
        final char endOfLine = ' ';
        StringBuilder buf = new StringBuilder("lookupswitch (");

        buf.append(keyBox.getValue().toString()).append(')').append(endOfLine);
        buf.append('{').append(endOfLine);

        for (ListIterator<IntConstant> it = lookupValues.listIterator(); it.hasNext(); ) {
            IntConstant c = it.next();
            buf.append("    case ").append(c).append(": goto ");
            Unit target = getTarget(it.previousIndex());
            buf.append(target == this ? "self" : target).append(';').append(endOfLine);
        }
        {
            buf.append("    default: goto ");
            Unit target = getDefaultTarget();
            buf.append(target == this ? "self" : target).append(';').append(endOfLine);
        }
        buf.append('}');

        return buf.toString();
    }

    public void setLookupValues(List<IntConstant> lookupValues) {
        this.lookupValues = new ArrayList<>(lookupValues);
    }

    public void setLookupValue(int index, int value) {
        lookupValues.set(index, IntConstant.v(value));
    }

    public int getLookupValue(int index) {
        return lookupValues.get(index).getValue();
    }

    public List<IntConstant> getLookupValues() {
        return Collections.unmodifiableList(lookupValues);
    }

    public Unit getTargetForValue(final int value) {
        for (int i = 0; i < lookupValues.size(); i++) {
            if (lookupValues.get(i).getValue() == value) {
                return getTarget(i);
            }
        }

        return getDefaultTarget();
    }
}
