package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.model.syntax.type.IntType;
import byteback.analysis.model.syntax.type.Type;

public class IntConstant extends ArithmeticConstant {

    private static final long serialVersionUID = 8622167089453261784L;

    private final int value;

    private static final int MAX_CACHE = 128;
    private static final int MIN_CACHE = -127;
    private static final int ABS_MIN_CACHE = Math.abs(MIN_CACHE);
    private static final IntConstant[] CACHED = new IntConstant[MAX_CACHE + ABS_MIN_CACHE];

    protected IntConstant(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static IntConstant v(int value) {
        if (value > MIN_CACHE && value < MAX_CACHE) {
            int index = value + ABS_MIN_CACHE;
            IntConstant constant = CACHED[index];

            if (constant != null) {
                return constant;
            }

            constant = new IntConstant(value);
            CACHED[index] = constant;
            return constant;
        }

        return new IntConstant(value);
    }

    @Override
    public boolean equals(Object c) {
        return c instanceof IntConstant && ((IntConstant) c).value == value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    // PTC 1999/06/28
    @Override
    public NumericConstant add(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value + ((IntConstant) c).value);
    }

    @Override
    public NumericConstant subtract(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value - ((IntConstant) c).value);
    }

    @Override
    public NumericConstant multiply(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value * ((IntConstant) c).value);
    }

    @Override
    public NumericConstant divide(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value / ((IntConstant) c).value);
    }

    @Override
    public NumericConstant remainder(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value % ((IntConstant) c).value);
    }

    @Override
    public NumericConstant equalEqual(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v((this.value == ((IntConstant) c).value) ? 1 : 0);
    }

    @Override
    public NumericConstant notEqual(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v((this.value != ((IntConstant) c).value) ? 1 : 0);
    }

    @Override
    public boolean isLessThan(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return this.value < ((IntConstant) c).value;
    }

    @Override
    public NumericConstant lessThan(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v((this.value < ((IntConstant) c).value) ? 1 : 0);
    }

    @Override
    public NumericConstant lessThanOrEqual(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v((this.value <= ((IntConstant) c).value) ? 1 : 0);
    }

    @Override
    public NumericConstant greaterThan(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v((this.value > ((IntConstant) c).value) ? 1 : 0);
    }

    @Override
    public NumericConstant greaterThanOrEqual(NumericConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v((this.value >= ((IntConstant) c).value) ? 1 : 0);
    }

    @Override
    public NumericConstant negate() {
        return IntConstant.v(-(this.value));
    }

    @Override
    public ArithmeticConstant and(ArithmeticConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value & ((IntConstant) c).value);
    }

    @Override
    public ArithmeticConstant or(ArithmeticConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value | ((IntConstant) c).value);
    }

    @Override
    public ArithmeticConstant xor(ArithmeticConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value ^ ((IntConstant) c).value);
    }

    @Override
    public ArithmeticConstant shiftLeft(ArithmeticConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value << ((IntConstant) c).value);
    }

    @Override
    public ArithmeticConstant shiftRight(ArithmeticConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value >> ((IntConstant) c).value);
    }

    @Override
    public ArithmeticConstant unsignedShiftRight(ArithmeticConstant c) {
        if (!(c instanceof IntConstant)) {
            throw new IllegalArgumentException("IntConstant expected");
        }
        return IntConstant.v(this.value >>> ((IntConstant) c).value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Type getType() {
        return IntType.v();
    }
}