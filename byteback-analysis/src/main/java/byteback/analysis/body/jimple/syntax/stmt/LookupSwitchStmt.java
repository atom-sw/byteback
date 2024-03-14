package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.Unit;
import byteback.analysis.body.common.syntax.UnitBox;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;

import java.util.Collections;
import java.util.List;

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
        this(Jimple.v().newImmediateBox(key), lookupValues, targets.toArray(new UnitBox[targets.size()]), defaultTarget);
    }

    protected LookupSwitchStmt(ValueBox keyBox, List<IntConstant> lookupValues, UnitBox[] targetBoxes,
                               UnitBox defaultTargetBox) {
        super(keyBox, defaultTargetBox, targetBoxes);
        setLookupValues(lookupValues);
    }

    @Override
    public Object clone() {
        List<IntConstant> clonedLookupValues = new ArrayList<IntConstant>(lookupValues.size());
        for (IntConstant c : lookupValues) {
            clonedLookupValues.add(IntConstant.v(c.value));
        }
        return new LookupSwitchStmt(getKey(), clonedLookupValues, getTargets(), getDefaultTarget());
    }

    @Override
    public String toString() {
        final char endOfLine = ' ';
        StringBuilder buf = new StringBuilder(Jimple.LOOKUPSWITCH + "(");

        buf.append(keyBox.getValue().toString()).append(')').append(endOfLine);
        buf.append('{').append(endOfLine);

        for (ListIterator<IntConstant> it = lookupValues.listIterator(); it.hasNext(); ) {
            IntConstant c = it.next();
            buf.append("    " + Jimple.CASE + " ").append(c).append(": " + Jimple.GOTO + " ");
            Unit target = getTarget(it.previousIndex());
            buf.append(target == this ? "self" : target).append(';').append(endOfLine);
        }
        {
            buf.append("    " + Jimple.DEFAULT + ": " + Jimple.GOTO + " ");
            Unit target = getDefaultTarget();
            buf.append(target == this ? "self" : target).append(';').append(endOfLine);
        }
        buf.append('}');

        return buf.toString();
    }

    @Override
    public void setLookupValues(List<IntConstant> lookupValues) {
        this.lookupValues = new ArrayList<IntConstant>(lookupValues);
    }

    @Override
    public void setLookupValue(int index, int value) {
        lookupValues.set(index, IntConstant.v(value));
    }

    @Override
    public int getLookupValue(int index) {
        return lookupValues.get(index).value;
    }

    @Override
    public List<IntConstant> getLookupValues() {
        return Collections.unmodifiableList(lookupValues);
    }

    @Override
    public Unit getTargetForValue(int value) {
        for (int i = 0; i < lookupValues.size(); i++) {
            if (lookupValues.get(i).value == value) {
                return getTarget(i);
            }
        }

        return getDefaultTarget();
    }
}
