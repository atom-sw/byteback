package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class InvokeExpr implements Expr {

    protected SootMethodRef methodRef;
    protected final ValueBox[] argBoxes;

    protected InvokeExpr(SootMethodRef methodRef, ValueBox[] argBoxes) {
        this.methodRef = methodRef;
        this.argBoxes = argBoxes.length == 0 ? null : argBoxes;
    }

    public void setMethodRef(SootMethodRef methodRef) {
        this.methodRef = methodRef;
    }

    public SootMethodRef getMethodRef() {
        return methodRef;
    }

    @Override
    public abstract Object clone();

    public Value getArg(int index) {
        if (argBoxes == null) {
            return null;
        }

        ValueBox vb = argBoxes[index];

        return vb == null ? null : vb.getValue();
    }

    public List<Value> getArgs() {
        final ValueBox[] boxes = this.argBoxes;
        final List<Value> r;

        if (boxes == null) {
            r = new ArrayList<>(0);
        } else {
            r = new ArrayList<>(boxes.length);

            for (ValueBox element : boxes) {
                r.add(element == null ? null : element.getValue());
            }
        }

        return r;
    }

    public int getArgCount() {
        return argBoxes == null ? 0 : argBoxes.length;
    }

    public void setArg(int index, Value arg) {
        argBoxes[index].setValue(arg);
    }

    public ValueBox getArgBox(int index) {
        return argBoxes[index];
    }

    @Override
    public Type getType() {
        return methodRef.getReturnType();
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
}
