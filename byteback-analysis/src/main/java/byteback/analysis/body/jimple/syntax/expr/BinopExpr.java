package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.*;

import java.util.ArrayList;
import java.util.List;

public abstract class BinopExpr implements Expr {

    protected final ValueBox op1Box;
    protected final ValueBox op2Box;

    protected BinopExpr(ValueBox op1Box, ValueBox op2Box) {
        this.op1Box = op1Box;
        this.op2Box = op2Box;
    }

    /**
     * Returns the unique symbol for an operator.
     */
    protected abstract String getSymbol();

    public Value getOp1() {
        return op1Box.getValue();
    }

    public Value getOp2() {
        return op2Box.getValue();
    }

    public ValueBox getOp1Box() {
        return op1Box;
    }

    public ValueBox getOp2Box() {
        return op2Box;
    }

    public void setOp1(Value op1) {
        op1Box.setValue(op1);
    }

    public void setOp2(Value op2) {
        op2Box.setValue(op2);
    }

    public final List<ValueBox> getUseBoxes() {
        final var list = new ArrayList<ValueBox>();

        list.addAll(op1Box.getValue().getUseBoxes());
        list.add(op1Box);

        list.addAll(op2Box.getValue().getUseBoxes());
        list.add(op2Box);

        return list;
    }

    @Override
    public boolean equivTo(Object o) {
        if (o instanceof BinopExpr abe) {
          return this.op1Box.getValue().equivTo(abe.op1Box.getValue()) && this.op2Box.getValue().equivTo(abe.op2Box.getValue())
                    && this.getSymbol().equals(abe.getSymbol());
        }
        return false;
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return op1Box.getValue().equivHashCode() * 101 + op2Box.getValue().equivHashCode() + 17 ^ getSymbol().hashCode();
    }

    @Override
    public String toString() {
        return op1Box.getValue().toString() + getSymbol() + op2Box.getValue().toString();
    }

    protected Type getType(final BinopExprEnum exprTypes) {
        final Type type1 = this.op1Box.getValue().getType();
        final Type type2 = this.op2Box.getValue().getType();

        final IntType tyInt = IntType.v();
        final ByteType tyByte = ByteType.v();
        final ShortType tyShort = ShortType.v();
        final CharType tyChar = CharType.v();
        final BooleanType tyBool = BooleanType.v();

        if ((tyInt.equals(type1) || tyByte.equals(type1) || tyShort.equals(type1) || tyChar.equals(type1) || tyBool.equals(type1))
                && (tyInt.equals(type2) || tyByte.equals(type2) || tyShort.equals(type2) || tyChar.equals(type2) || tyBool.equals(type2))) {
            return tyInt;
        }

        final LongType tyLong = LongType.v();

        if (tyLong.equals(type1) || tyLong.equals(type2)) {
            return tyLong;
        }

        if (exprTypes.equals(BinopExprEnum.ABSTRACT_FLOAT_BINOP_EXPR)) {
            final DoubleType tyDouble = DoubleType.v();
            if (tyDouble.equals(type1) || tyDouble.equals(type2)) {
                return tyDouble;
            }
            final FloatType tyFloat = FloatType.v();
            if (tyFloat.equals(type1) || tyFloat.equals(type2)) {
                return tyFloat;
            }
        }

        return UnknownType.v();
    }

    public enum BinopExprEnum {
        ABASTRACT_INT_LONG_BINOP_EXPR, ABSTRACT_FLOAT_BINOP_EXPR
    }
}
