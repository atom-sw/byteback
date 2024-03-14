package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.signature.MethodSignature;
import byteback.analysis.model.syntax.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class InvokeExpr extends MethodRef implements Expr {

    protected static ValueBox[] boxArgs(final List<Value> args) {
        final int size = args.size();
        final var argBoxes = new ValueBox[args.size()];

        for (int i = 0; i < size; ++i) {
            argBoxes[i] = new ImmediateBox(args.get(i));
        }

        return argBoxes;
    }

    protected final ValueBox[] argBoxes;

    protected InvokeExpr(final MethodSignature methodSignature, final ValueBox[] argBoxes) {
        super(methodSignature);
        this.argBoxes = argBoxes;
    }

    protected InvokeExpr(final MethodSignature methodSignature, List<Value> args) {
        super(methodSignature);
        this.argBoxes = boxArgs(args);
    }

    public Value getArg(int index) {
        if (argBoxes == null) {
            return null;
        }

        ValueBox vb = argBoxes[index];

        return vb == null ? null : vb.getValue();
    }

    public List<Value> getArgs() {
        final ValueBox[] boxes = this.argBoxes;
        final List<Value> arguments;

        if (boxes == null) {
            arguments = new ArrayList<>(0);
        } else {
            arguments = new ArrayList<>(boxes.length);

            for (final ValueBox valueBox : boxes) {
                arguments.add(valueBox == null ? null : valueBox.getValue());
            }
        }

        return arguments;
    }

    public int getArgCount() {
        return argBoxes == null ? 0 : argBoxes.length;
    }

    public ValueBox getArgBox(int index) {
        return argBoxes[index];
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        final ValueBox[] boxes = argBoxes;

        if (boxes == null) {
            return Collections.emptyList();
        }

        final var list = new ArrayList<ValueBox>();
        Collections.addAll(list, boxes);

        for (ValueBox element : boxes) {
            list.addAll(element.getValue().getUseBoxes());
        }

        return list;
    }

    @Override
    public Type getType() {
        return getReturnType();
    }
}
