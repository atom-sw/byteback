package byteback.analysis.body.vimp;

import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.ValueBox;
import soot.jimple.NegExpr;
import soot.util.Switch;

public class LogicNotExpr extends AbstractLogicUnopExpr implements LogicExpr, NegExpr {

	public LogicNotExpr(final Value v) {
		super(v);
	}

	public LogicNotExpr(final ValueBox vbox) {
		super(vbox);
	}

	@Override
	public String getSymbol() {
		return "¬";
	}

	@Override
	public void apply(final Switch sw) {
		if (sw instanceof VimpValueSwitch<?> vimpValueSwitch) {
			vimpValueSwitch.caseLogicNotExpr(this);
		}
	}

	@Override
	public Object clone() {
		return new LogicNotExpr(getOp());
	}

}
