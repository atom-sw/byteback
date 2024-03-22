package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.SpecialExprSwitch;
import soot.*;
import soot.jimple.internal.AbstractUnopExpr;
import soot.util.Switch;

/**
 * An expression to access the pre-state of a method.
 * @author paganma
 */
public class OldExpr extends AbstractUnopExpr implements Immediate {

	public OldExpr(final Value v) {
		super(Vimp.v().newArgBox(v));
	}

	public OldExpr(final ValueBox vbox) {
		super(vbox);
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("old");
		printer.literal("(");
		getOp().toString(printer);
		printer.literal(")");
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof SpecialExprSwitch<?> specialExprSwitch) {
			specialExprSwitch.caseOldExpr(this);
		}
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
	public boolean equivTo(final Object object) {
		return object instanceof OldExpr oldExpr && oldExpr.getOp().equivTo(getOp());
	}

	@Override
	public int equivHashCode() {
		return getOp().equivHashCode() * 101 + 17 ^ "old".hashCode();
	}

}
