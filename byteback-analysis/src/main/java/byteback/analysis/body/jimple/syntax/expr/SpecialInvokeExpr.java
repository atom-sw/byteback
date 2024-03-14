package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.signature.MethodSignature;
import byteback.analysis.model.syntax.type.Type;

import java.util.List;

public class SpecialInvokeExpr extends InstanceInvokeExpr {

    public SpecialInvokeExpr(final MethodSignature signature, final ValueBox baseBox, ValueBox[] argBoxes) {
        super(signature, baseBox, argBoxes);
    }

    public SpecialInvokeExpr(final MethodSignature signature, final Value value, final List<Value> args) {
        super(signature, value, args);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("specialinvoke ");
        builder.append(baseBox.getValue().toString()).append('.').append(getSignature()).append('(');

        if (argBoxes != null) {
            for (int i = 0, e = argBoxes.length; i < e; i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                builder.append(argBoxes[i].getValue().toString());
            }
        }

        builder.append(')');

        return builder.toString();
    }
}
