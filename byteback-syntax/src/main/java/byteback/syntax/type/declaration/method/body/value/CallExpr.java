package byteback.syntax.type.declaration.method.body.value;

import byteback.syntax.type.declaration.method.body.unit.printer.InlineUnitPrinter;
import byteback.syntax.Vimp;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractInvokeExpr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A call expression representing an invocation of a behavioral function.
 *
 * @author paganma
 */
public class CallExpr extends AbstractInvokeExpr implements Immediate, DefaultCaseValue {

    public CallExpr(final SootMethodRef methodRef, final List<Value> args) {
        super(methodRef, new ValueBox[args.size()]);

        for (final ListIterator<Value> valueIterator = args.listIterator(); valueIterator.hasNext();) {
            final Value value = valueIterator.next();
            this.argBoxes[valueIterator.previousIndex()] = Vimp.v().newImmediateBox(value);
        }
    }

    @Override
    public Object clone() {
        final int count = getArgCount();
        final List<Value> clonedArgs = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            clonedArgs.add(Jimple.cloneIfNecessary(getArg(i)));
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

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("(");
        printer.literal(methodRef.getName());

        if (getArgCount() > 0) {
            printer.literal(" ");
            final Iterator<Value> argIterator = getArgs().iterator();

            while (argIterator.hasNext()) {
                final Value arg = argIterator.next();
                arg.toString(printer);

                if (argIterator.hasNext()) {
                    printer.literal(" ");
                }
            }
        }

        printer.literal(")");
    }

    @Override
    public String toString() {
        final var unitPrinter = new InlineUnitPrinter();
        toString(unitPrinter);

        return unitPrinter.toString();
    }

}
