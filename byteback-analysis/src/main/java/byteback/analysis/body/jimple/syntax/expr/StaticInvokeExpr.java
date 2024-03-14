package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.signature.MethodSignature;

import java.util.List;
import java.util.ListIterator;

public class StaticInvokeExpr extends InvokeExpr {

    public StaticInvokeExpr(final MethodSignature signature, List<Value> args) {
        this(signature, new ValueBox[args.size()]);

        for (ListIterator<Value> it = args.listIterator(); it.hasNext(); ) {
            Value v = it.next();
            this.argBoxes[it.previousIndex()] = new ImmediateBox(v);
        }
    }

    protected StaticInvokeExpr(final MethodSignature signature, ValueBox[] argBoxes) {
        super(signature, argBoxes);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("staticinvoke ");

        buf.append(getSignature()).append('(');

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
