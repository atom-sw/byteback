package byteback.analysis.body.vimp;

import soot.Immediate;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.util.Switch;

public class CallExpr extends AbstractInvokeExpr implements Immediate {

    private final InvokeExpr invokeExpr;

    public CallExpr(final InvokeExpr invokeExpr) {
        super(invokeExpr.getMethodRef(), invokeExpr.getUseBoxes().toArray(new ValueBox[0]));
        this.invokeExpr = invokeExpr;
    }

    public InvokeExpr getInvokeExpr() {
        return invokeExpr;
    }

    @Override
    public Object clone() {
        return new CallExpr((InvokeExpr) invokeExpr.clone());
    }

    @Override
    public void toString(UnitPrinter up) {
        invokeExpr.toString(up);
    }

    @Override
    public boolean equivTo(Object o) {
        return (o instanceof CallExpr callExpr && invokeExpr.equivTo(callExpr.getInvokeExpr()));
    }

    @Override
    public int equivHashCode() {
        return invokeExpr.equivHashCode() * 11;
    }

    @Override
    public void apply(Switch sw) {
        invokeExpr.apply(sw);
    }
}
