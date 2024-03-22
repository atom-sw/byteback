package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.ValueBox;
import soot.jimple.NegExpr;
import soot.util.Switch;

/**
 * Logic equivalent of NegExpr.
 * @see soot.jimple.internal.JNegExpr
 * @author paganma
 */
public class NotExpr extends AbstractLogicUnopExpr implements LogicExpr, NegExpr {

	public NotExpr(final Value v) {
		super(v);
	}

	public NotExpr(final ValueBox vbox) {
		super(vbox);
	}

	@Override
	public String getSymbol() {
		return "¬";
	}

	@Override
	public int equivHashCode() {
		return getOp().equivHashCode() * 101 + 17 ^ getSymbol().hashCode();
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
			vimpValueSwitch.caseNotExpr(this);
		}
	}

	@Override
	public Object clone() {
		return new NotExpr(getOp());
	}

}
