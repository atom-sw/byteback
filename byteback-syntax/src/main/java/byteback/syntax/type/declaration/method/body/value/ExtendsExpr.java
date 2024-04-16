package byteback.syntax.type.declaration.method.body.value;

import byteback.syntax.type.declaration.method.body.value.box.KindExprBox;
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
        this(new KindExprBox(op1), new KindExprBox(op2));
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
