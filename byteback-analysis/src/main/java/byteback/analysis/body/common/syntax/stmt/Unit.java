package byteback.analysis.body.common.syntax.stmt;

import byteback.analysis.body.common.syntax.expr.ValueBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Unit {

    /**
     * List of UnitBoxes pointing to this Unit.
     */
    protected List<UnitBox> boxesPointingToThis = null;

    /**
     * Returns a list of Boxes containing Values used in this Unit. The list of boxes is dynamically updated as the structure
     * changes. Note that they are returned in usual evaluation order. (this is important for aggregation)
     */
    public List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }

    /**
     * Returns a list of Boxes containing Values defined in this Unit. The list of boxes is dynamically updated as the
     * structure changes.
     */
    public List<ValueBox> getDefBoxes() {
        return Collections.emptyList();
    }

    /**
     * Returns a list of Boxes containing Units defined in this Unit; typically branch targets. The list of boxes is
     * dynamically updated as the structure changes.
     */
    public List<UnitBox> getUnitBoxes() {
        return Collections.emptyList();
    }

    /**
     * Returns a list of Boxes pointing to this Unit.
     */
    public List<UnitBox> getBoxesPointingToThis() {
        return (boxesPointingToThis == null) ?
                Collections.emptyList() : Collections.unmodifiableList(boxesPointingToThis);
    }

    public void addBoxPointingToThis(UnitBox b) {
        List<UnitBox> ref = boxesPointingToThis;

        if (ref == null) {
            boxesPointingToThis = ref = new ArrayList<>();
        }

        ref.add(b);
    }

    public void removeBoxPointingToThis(UnitBox b) {
        List<UnitBox> ref = boxesPointingToThis;

        if (ref != null) {
            ref.remove(b);
        }
    }

    public void clearUnitBoxes() {
        for (UnitBox ub : getUnitBoxes()) {
            ub.setUnit(null);
        }
    }

    /**
     * Returns a list of ValueBoxes, either used or defined in this Unit.
     */
    public List<ValueBox> getUseAndDefBoxes() {
        final List<ValueBox> useBoxes = getUseBoxes();
        final List<ValueBox> defBoxes = getDefBoxes();

        if (useBoxes.isEmpty()) {
            return defBoxes;
        } else if (defBoxes.isEmpty()) {
            return useBoxes;
        } else {
            final var valueBoxes = new ArrayList<ValueBox>(defBoxes.size() + useBoxes.size());
            valueBoxes.addAll(defBoxes);
            valueBoxes.addAll(useBoxes);
            return valueBoxes;
        }
    }

    public void redirectJumpsToThisTo(final Unit newLocation) {
        // important to make a copy to prevent concurrent modification
        for (UnitBox box : new ArrayList<>(getBoxesPointingToThis())) {
            if (box.getUnit() != this) {
                throw new RuntimeException("Something weird's happening");
            }

            if (box.isBranchTarget()) {
                box.setUnit(newLocation);
            }
        }
    }


    /**
     * Returns true if execution after this statement may continue at the following statement. GotoStmt will return false but
     * IfStmt will return true.
     */
    public abstract boolean fallsThrough();

    /**
     * Returns true if execution after this statement does not necessarily continue at the following statement. GotoStmt and
     * IfStmt will both return true.
     */
    public abstract boolean branches();
}