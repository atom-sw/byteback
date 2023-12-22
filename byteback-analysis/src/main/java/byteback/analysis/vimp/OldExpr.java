package byteback.analysis.vimp;

import byteback.analysis.JimpleValueSwitch;
import byteback.analysis.Vimp;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.AbstractUnopExpr;
import soot.util.Switch;

public class OldExpr extends AbstractUnopExpr {

	public OldExpr(final Value v) {
		super(Vimp.v().newArgBox(v));
	}

	public OldExpr(final ValueBox vbox) {
		super(vbox);
	}

	@Override
	public void toString(UnitPrinter up) {
		up.literal("old");
		up.literal("(");
		getOp().toString(up);
		up.literal(")");
	}

	@Override
	public void apply(final Switch sw) {
		((JimpleValueSwitch<?>) sw).caseOldExpr(this);
	}

	@Override
	public Object clone() {
		return new OldExpr(getOp());
	}

	@Override
	public Type getType() {
		return getOp().getType();
	}

	@Override
	public boolean equivTo(final Object o) {
		return o instanceof OldExpr oldExpr && oldExpr.getOp().equivTo(getOp());
	}

	@Override
	public int equivHashCode() {
		return getOp().equivHashCode() * 101 + 17 ^ "old".hashCode();
	}

}
