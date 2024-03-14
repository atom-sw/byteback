package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.signature.MethodSignature;

import java.util.ArrayList;
import java.util.List;

public class DynamicInvokeExpr extends InvokeExpr {

    protected final MethodSignature bootstrapSignature;

    protected final ValueBox[] bootstrapArgBoxes;

    protected DynamicInvokeExpr(final MethodSignature methodSignature, final ValueBox[] argBoxes,
                                final MethodSignature bootstrapSignature,
                                final ValueBox[] bootstrapArgBoxes) {
        super(methodSignature, argBoxes);
        this.bootstrapSignature = bootstrapSignature;
        this.bootstrapArgBoxes = bootstrapArgBoxes;
    }

    protected DynamicInvokeExpr(final MethodSignature methodSignature, final List<Value> args,
                                final MethodSignature bootstrapSignature,
                                final List<Value> bootstrapArgs) {
        super(methodSignature, args);
        this.bootstrapSignature = bootstrapSignature;
        this.bootstrapArgBoxes = boxArgs(bootstrapArgs);
    }


    public int getBootstrapArgCount() {
        return bootstrapArgBoxes.length;
    }

    public Value getBootstrapArg(int index) {
        return bootstrapArgBoxes[index].getValue();
    }

    public List<Value> getBootstrapArgs() {
        List<Value> l = new ArrayList<>();

        for (ValueBox element : bootstrapArgBoxes) {
            l.add(element.getValue());
        }

        return l;
    }

    public MethodSignature getBootstrapSignature() {
        return bootstrapSignature;
    }
}

