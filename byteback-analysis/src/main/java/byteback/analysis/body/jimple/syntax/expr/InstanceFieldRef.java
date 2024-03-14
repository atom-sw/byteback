package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.Type;

import java.util.ArrayList;
import java.util.List;

public abstract class InstanceFieldRef implements Ref {
    protected SootFieldRef fieldRef;
    final ValueBox baseBox;

    protected InstanceFieldRef(final ValueBox baseBox, SootFieldRef fieldRef) {
        if (fieldRef.isStatic()) {
            throw new RuntimeException("wrong static-ness");
        }
        this.baseBox = baseBox;
        this.fieldRef = fieldRef;
    }

    public abstract Object clone();

    public String toString() {
        return baseBox.getValue().toString() + "." + fieldRef.getSignature();
    }

    public Value getBase() {
        return baseBox.getValue();
    }

    public ValueBox getBaseBox() {
        return baseBox;
    }

    public void setBase(Value base) {
        baseBox.setValue(base);
    }

    public SootFieldRef getFieldRef() {
        return fieldRef;
    }

    public void setFieldRef(SootFieldRef fieldRef) {
        this.fieldRef = fieldRef;
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        final var useBoxes = new ArrayList<ValueBox>();

        useBoxes.addAll(baseBox.getValue().getUseBoxes());
        useBoxes.add(baseBox);

        return useBoxes;
    }

    public Type getType() {
        return fieldRef.type();
    }

    public boolean equivTo(Object o) {
        if (o instanceof InstanceFieldRef fr) {
            fr.baseBox.getValue().equivTo(baseBox.getValue());
        }

        return false;
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    public int equivHashCode() {
        return fieldRef.equivHashCode() * 101 + baseBox.getValue().equivHashCode() + 17;
    }
}
