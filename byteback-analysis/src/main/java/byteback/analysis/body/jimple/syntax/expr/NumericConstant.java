package byteback.analysis.body.jimple.syntax.expr;


public abstract class NumericConstant extends Constant {

    public abstract NumericConstant add(NumericConstant c);

    public abstract NumericConstant subtract(NumericConstant c);

    public abstract NumericConstant multiply(NumericConstant c);

    public abstract NumericConstant divide(NumericConstant c);

    public abstract NumericConstant remainder(NumericConstant c);

    public abstract NumericConstant equalEqual(NumericConstant c);

    public abstract NumericConstant notEqual(NumericConstant c);

    public abstract boolean isLessThan(NumericConstant c);

    public abstract NumericConstant lessThan(NumericConstant c);

    public abstract NumericConstant lessThanOrEqual(NumericConstant c);

    public abstract NumericConstant greaterThan(NumericConstant c);

    public abstract NumericConstant greaterThanOrEqual(NumericConstant c);

    public abstract NumericConstant negate();
}