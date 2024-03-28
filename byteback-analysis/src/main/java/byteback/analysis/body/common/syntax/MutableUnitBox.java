package byteback.analysis.body.common.syntax;

import soot.Body;
import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.jimple.Stmt;

/**
 * A Unit box whose contents can be changed in a way that is reflected in the outer Body.
 *
 * @author paganma
 */
public class MutableUnitBox implements UnitBox {

	private Unit unit;

	final Body body;

	public MutableUnitBox(final Unit unit, final Body body) {
		this.unit = unit;
		this.body = body;
	}

	/**
	 * Swaps the `unit` in the surrounding body and redirects jumps to the previous unit to the new `unit`.
	 * @param unit The new unit.
	 */
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

	/**
	 * @return `true` if the current `unit` is being targeted by other units in the Body.
	 */
	@Override
	public boolean isBranchTarget() {
		return unit.getBoxesPointingToThis().isEmpty();
	}

	@Override
	public void toString(final UnitPrinter up) {
		up.startUnitBox(this);
		up.unitRef(unit, isBranchTarget());
		up.endUnitBox(this);
	}

}
