package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.signature.MethodSignature;

public abstract class VirtualInvokeExpr extends InstanceInvokeExpr {

    protected VirtualInvokeExpr(final MethodSignature signature, final ValueBox baseBox, ValueBox[] argBoxes) {
        super(signature, baseBox, argBoxes);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("virtualinvoke ");

        buf.append(baseBox.getValue().toString()).append('.').append(signature).append('(');
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
