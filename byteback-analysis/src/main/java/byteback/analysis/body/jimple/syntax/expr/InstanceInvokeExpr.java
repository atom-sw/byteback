package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.signature.MethodSignature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class InstanceInvokeExpr extends InvokeExpr {

    protected final ValueBox baseBox;

    protected InstanceInvokeExpr(final MethodSignature signature, final ValueBox baseBox, final ValueBox[] argBoxes) {
        super(signature, argBoxes);
        this.baseBox = baseBox;
    }

    protected InstanceInvokeExpr(final MethodSignature signature, final Value base, final List<Value> args) {
        super(signature, args);
        this.baseBox = new LocalBox(base);
    }

    public Value getBase() {
        return baseBox.getValue();
    }

    public ValueBox getBaseBox() {
        return baseBox;
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        final var useBoxes = new ArrayList<ValueBox>(baseBox.getValue().getUseBoxes());
        useBoxes.add(baseBox);

        if (argBoxes != null) {
            Collections.addAll(useBoxes, argBoxes);

            for (ValueBox element : argBoxes) {
                useBoxes.addAll(element.getValue().getUseBoxes());
            }
        }

        return useBoxes;
    }
}
