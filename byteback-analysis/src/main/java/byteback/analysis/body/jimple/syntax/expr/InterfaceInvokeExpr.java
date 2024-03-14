package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;

public abstract class InterfaceInvokeExpr extends InstanceInvokeExpr {

    protected InterfaceInvokeExpr(ValueBox baseBox, SootMethodRef methodRef, ValueBox[] argBoxes) {
        super(methodRef, baseBox, argBoxes);
        if (methodRef.isStatic()) {
            throw new RuntimeException("wrong static-ness");
        }
    }

    @Override
    public boolean equivTo(Object o) {
        if (o instanceof InterfaceInvokeExpr ie) {
          if ((this.argBoxes == null ? 0 : this.argBoxes.length) != (ie.argBoxes == null ? 0 : ie.argBoxes.length)
                    || !this.getMethod().equals(ie.getMethod()) || !this.baseBox.getValue().equivTo(ie.baseBox.getValue())) {
                return false;
            }
            if (this.argBoxes != null) {
                for (int i = 0, e = this.argBoxes.length; i < e; i++) {
                    if (!this.argBoxes[i].getValue().equivTo(ie.argBoxes[i].getValue())) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
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
        final StringBuilder buf = new StringBuilder("interfaceinvoke ");

        buf.append(baseBox.getValue().toString()).append('.').append(methodRef.getSignature()).append('(');

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
