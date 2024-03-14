package byteback.analysis.body.jimple.syntax.expr;

public abstract class ArithmeticConstant extends NumericConstant {
    // PTC 1999/06/28
    public abstract ArithmeticConstant and(ArithmeticConstant constant);

    public abstract ArithmeticConstant or(ArithmeticConstant constant);

    public abstract ArithmeticConstant xor(ArithmeticConstant constant);

    public abstract ArithmeticConstant shiftLeft(ArithmeticConstant constant);

    public abstract ArithmeticConstant shiftRight(ArithmeticConstant constant);

    public abstract ArithmeticConstant unsignedShiftRight(ArithmeticConstant constant);

}