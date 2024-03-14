package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;

public abstract class SpecialInvokeExpr extends InstanceInvokeExpr {

    protected SpecialInvokeExpr(ValueBox baseBox, SootMethodRef methodRef, ValueBox[] argBoxes) {
        super(methodRef, baseBox, argBoxes);

        if (methodRef.isStatic()) {
            throw new RuntimeException("wrong static-ness");
        }
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return baseBox.getValue().equivHashCode() * 101 + getMethod().equivHashCode() * 17;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("specialinvoke ");

        builder.append(baseBox.getValue().toString()).append('.').append(methodRef.getSignature()).append('(');

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
