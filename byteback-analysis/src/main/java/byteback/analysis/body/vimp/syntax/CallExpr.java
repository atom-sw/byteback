package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.Immediate;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.jimple.syntax.expr.InvokeExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CallExpr extends InvokeExpr implements Immediate {

    public CallExpr(final SootMethodRef methodRef, final List<Value> args) {
        super(methodRef, new ValueBox[args.size()]);

        for (final ListIterator<Value> valueIterator = args.listIterator(); valueIterator.hasNext(); ) {
            final Value value = valueIterator.next();
            this.argBoxes[valueIterator.previousIndex()] = Vimp.v().newImmediateBox(value);
        }
    }

    @Override
    public Object clone() {
        final int count = getArgCount();
        List<Value> clonedArgs = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            clonedArgs.add(Vimp.cloneIfNecessary(getArg(i)));
        }

        return new CallExpr(methodRef, clonedArgs);
    }

    @Override
    public boolean equivTo(final Object object) {
        if (object instanceof CallExpr callExpr) {
            if ((this.argBoxes == null ? 0 : this.argBoxes.length)
                    != (callExpr.argBoxes == null ? 0 : callExpr.argBoxes.length)
                    || !this.getMethod().equals(callExpr.getMethod())) {
                return false;
            }

            if (this.argBoxes != null) {
                for (int i = 0, e = this.argBoxes.length; i < e; i++) {
                    if (!this.argBoxes[i].getValue().equivTo(callExpr.argBoxes[i].getValue())) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public int equivHashCode() {
        return getMethod().equivHashCode() * 17;
    }
}
