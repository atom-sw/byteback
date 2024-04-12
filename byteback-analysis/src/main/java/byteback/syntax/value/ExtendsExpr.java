package byteback.syntax.value;

import byteback.syntax.value.box.TypeExprBox;
import soot.*;
import soot.jimple.internal.AbstractBinopExpr;

public class ExtendsExpr extends AbstractBinopExpr implements DefaultCaseValue {

    public ExtendsExpr(final ValueBox op1Box, final ValueBox op2Box) {
        super(op1Box, op2Box);
    }

    @Override
    protected String getSymbol() {
        return " <: ";
    }

    public ExtendsExpr(final Value op1, final Value op2) {
        this(new TypeExprBox(op1), new TypeExprBox(op2));
    }

    @Override
    public Type getType() {
        return BooleanType.v();
    }

    @Override
    public Object clone() {
        return new ExtendsExpr(op1Box, op2Box);
    }

    @Override
    public boolean equivTo(Object o) {
        return false;
    }

    @Override
    public int equivHashCode() {
        return (getOp1().equivHashCode() + getOp2().equivHashCode()) * 31;
    }

}
