package byteback.analysis.body.vimp.syntax;

import soot.BooleanType;
import soot.Type;
import soot.UnitPrinter;
import soot.jimple.Constant;

/**
 * A boolean constant.
 *
 * @author paganma
 */
public class LogicConstant extends Constant implements UnswitchableExpr {

	public final boolean value;

	private static final LogicConstant falseConstant = new LogicConstant(false);

	private static final LogicConstant trueConstant = new LogicConstant(true);

	private LogicConstant(final boolean value) {
		this.value = value;
	}

	public static LogicConstant v(boolean value) {
		return value ? trueConstant : falseConstant;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void toString(final UnitPrinter printer) {
		if (value) {
			printer.literal("true");
		} else {
			printer.literal("false");
		}
	}

	@Override
	public Type getType() {
		return BooleanType.v();
	}

}
