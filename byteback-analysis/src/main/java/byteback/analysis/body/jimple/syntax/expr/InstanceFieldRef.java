package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.signature.FieldSignature;
import byteback.analysis.model.syntax.type.Type;

import java.util.ArrayList;
import java.util.List;

public abstract class InstanceFieldRef extends FieldRef {
    final ValueBox baseBox;

    protected InstanceFieldRef(final ValueBox baseBox, final FieldSignature signature) {
        super(signature);
        this.baseBox = baseBox;
    }

    public abstract Object clone();

    public String toString() {
        return baseBox.getValue().toString() + "." + getSignature();
    }

    public Value getBase() {
        return baseBox.getValue();
    }

    public ValueBox getBaseBox() {
        return baseBox;
    }

    public void setBase(final Value base) {
        baseBox.setValue(base);
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        final var useBoxes = new ArrayList<ValueBox>();

        useBoxes.addAll(baseBox.getValue().getUseBoxes());
        useBoxes.add(baseBox);

        return useBoxes;
    }
}
