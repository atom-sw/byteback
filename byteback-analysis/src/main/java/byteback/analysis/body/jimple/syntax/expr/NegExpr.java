package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.stmt.UnopExpr;
import byteback.analysis.model.syntax.type.*;

public abstract class NegExpr extends UnopExpr {

    protected NegExpr(final ValueBox opBox) {
        super(opBox);
    }

    /**
     * Compares the specified object with this one for structural equality.
     */
    @Override
    public boolean equivTo(Object o) {
        if (o instanceof NegExpr) {
            return this.opBox.getValue().equivTo(((NegExpr) o).opBox.getValue());
        }

        return false;
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return opBox.getValue().equivHashCode();
    }

    @Override
    public String toString() {
        return "neg " + opBox.getValue().toString();
    }

    @Override
    public Type getType() {
        final Type type = opBox.getValue().getType();

        final IntType tyInt = IntType.v();
        final ByteType tyByte = ByteType.v();
        final ShortType tyShort = ShortType.v();
        final CharType tyChar = CharType.v();
        final BooleanType tyBool = BooleanType.v();

        if (tyInt.equals(type) || tyByte.equals(type) || tyShort.equals(type) || tyChar.equals(type) || tyBool.equals(type)) {
            return tyInt;
        }

        final LongType tyLong = LongType.v();

        if (tyLong.equals(type)) {
            return tyLong;
        }

        final DoubleType tyDouble = DoubleType.v();

        if (tyDouble.equals(type)) {
            return tyDouble;
        }

        final FloatType tyFloat = FloatType.v();

        if (tyFloat.equals(type)) {
            return tyFloat;
        }

        return UnknownType.v();
    }
}
