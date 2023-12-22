package byteback.analysis.vimp;

import byteback.analysis.LogicExprSwitch;
import soot.UnitPrinter;
import soot.jimple.Constant;
import soot.util.Switch;

public class LogicConstant extends Constant implements LogicExpr {

	public final boolean value;

	private static final LogicConstant falseConstant = new LogicConstant(false);

	private static final LogicConstant trueConstant = new LogicConstant(true);

	private LogicConstant(final boolean value) {
		this.value = value;
	}

	public static LogicConstant v(boolean value) {
		return value ? trueConstant : falseConstant;
	}

	@Override
	public void toString(final UnitPrinter up) {
		if (value) {
			up.literal("true");
		} else {
			up.literal("false");
		}
	}

	@Override
	public void apply(final Switch sw) {
		if (sw instanceof LogicExprSwitch) {
			((LogicExprSwitch<?>) sw).caseLogicConstant(this);
		}
	}

	public boolean getValue() {
		return value;
	}

}
