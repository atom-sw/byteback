package byteback.analysis.body.common.syntax;

import byteback.analysis.body.jimple.syntax.Unit;

public abstract class AbstractUnitBox implements UnitBox {

    protected byteback.analysis.body.jimple.syntax.Unit unit;

    @Override
    public boolean isBranchTarget() {
        return true;
    }

    @Override
    public void setUnit(byteback.analysis.body.jimple.syntax.Unit unit) {
        if (!canContainUnit(unit)) {
            throw new RuntimeException("attempting to put invalid unit in UnitBox");
        }

        // Remove this from set of back pointers.
        if (this.unit != null) {
            this.unit.removeBoxPointingToThis(this);
        }

        // Perform link
        this.unit = unit;

        // Add this to back pointers
        if (this.unit != null) {
            this.unit.addBoxPointingToThis(this);
        }
    }

    @Override
    public Unit getUnit() {
        return unit;
    }
}
