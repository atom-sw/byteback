package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.Immediate;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.body.jimple.syntax.expr.InvokeExpr;
import byteback.analysis.body.jimple.syntax.expr.MethodRef;
import byteback.analysis.model.syntax.signature.MethodSignature;

import java.util.List;
import java.util.ListIterator;

public class CallExpr extends InvokeExpr implements Immediate {

    public CallExpr(final MethodSignature signature, final List<Value> args) {
        super(signature, args);

        for (final ListIterator<Value> valueIterator = args.listIterator(); valueIterator.hasNext(); ) {
            final Value value = valueIterator.next();
            this.argBoxes[valueIterator.previousIndex()] = new ImmediateBox(value);
        }
    }
}
