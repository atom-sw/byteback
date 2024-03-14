package byteback.analysis.body.common.syntax.stmt;

public abstract class UnitBox {

    protected Unit unit;

    public abstract boolean canContainUnit(final Unit unit);

    public boolean isBranchTarget() {
        return true;
    }

    public void setUnit(final Unit unit) {
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

    public Unit getUnit() {
        return unit;
    }
}
