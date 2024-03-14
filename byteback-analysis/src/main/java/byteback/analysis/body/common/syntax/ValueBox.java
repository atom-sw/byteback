package byteback.analysis.body.common.syntax;

/**
 * Reference implementation for ValueBox; just add a canContainValue method.
 */
public abstract class ValueBox {

    protected Value value;

    public abstract boolean canContainValue(final Value value);

    public void setValue(Value value) {
        if (value == null) {
            throw new IllegalArgumentException("value may not be null");
        }
        if (canContainValue(value)) {
            this.value = value;
        } else {
            throw new RuntimeException("Box " + this + " cannot contain value: " + value + "(" + value.getClass() + ")");
        }
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + value + ")";
    }
}
