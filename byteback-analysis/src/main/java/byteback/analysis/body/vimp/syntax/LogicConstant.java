package byteback.analysis.body.vimp.syntax;

import soot.UnitPrinter;
import soot.jimple.Constant;

/**
 * A boolean constant.
 *
 * @author paganma
 */
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
	public void toString(final UnitPrinter printer) {
		if (value) {
			printer.literal("true");
		} else {
			printer.literal("false");
		}
	}

	public boolean getValue() {
		return value;
	}

}
