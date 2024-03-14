package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.jimple.syntax.expr.Constant;

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

    public boolean getValue() {
        return value;
    }

}
