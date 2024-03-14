package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;

import java.util.List;
import java.util.ListIterator;

public abstract class StaticInvokeExpr extends InvokeExpr {

    StaticInvokeExpr(SootMethodRef methodRef, List<Value> args) {
        this(methodRef, new ValueBox[args.size()]);

        for (ListIterator<Value> it = args.listIterator(); it.hasNext(); ) {
            Value v = it.next();
            this.argBoxes[it.previousIndex()] = new ImmediateBox(v);
        }
    }

    protected StaticInvokeExpr(SootMethodRef methodRef, ValueBox[] argBoxes) {
        super(methodRef, argBoxes);
        if (!methodRef.isStatic()) {
            throw new RuntimeException("wrong static-ness");
        }
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return getMethod().equivHashCode();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("staticinvoke ");

        buf.append(methodRef.getSignature()).append('(');

        if (argBoxes != null) {
            for (int i = 0, e = argBoxes.length; i < e; i++) {
                if (i != 0) {
                    buf.append(", ");
                }
                buf.append(argBoxes[i].getValue().toString());
            }
        }

        buf.append(')');

        return buf.toString();
    }
}
