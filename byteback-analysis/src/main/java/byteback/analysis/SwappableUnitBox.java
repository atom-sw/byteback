package byteback.analysis;

import soot.Body;
import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.jimple.Stmt;

public class SwappableUnitBox implements UnitBox {

	Unit unit;

	final Body body;

	public SwappableUnitBox(final Unit unit, final Body body) {
		this.unit = unit;
		this.body = body;
	}

	@Override
	public void setUnit(final Unit unit) {
		if (canContainUnit(unit)) {
			body.getUnits().swapWith(this.unit, unit);
			this.unit.redirectJumpsToThisTo(unit);
			this.unit = unit;
		}
	}

	@Override
	public Unit getUnit() {
		return unit;
	}

	@Override
	public boolean canContainUnit(final Unit unit) {
		return unit instanceof Stmt;
	}

	@Override
	public boolean isBranchTarget() {
		return false;
	}

	@Override
	public void toString(final UnitPrinter up) {
		up.startUnitBox(this);
		up.unitRef(unit, isBranchTarget());
		up.endUnitBox(this);
	}

}
