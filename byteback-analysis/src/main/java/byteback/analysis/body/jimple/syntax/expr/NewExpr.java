package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.Type;

import java.util.Collections;
import java.util.List;

public abstract class NewExpr implements Expr {

    protected ClassType type;

    @Override
    public abstract Object clone();

    @Override
    public boolean equivTo(final Object o) {
        if (o instanceof NewExpr ae) {
            return type.equals(ae.type);
        }

        return false;
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "new " + type.toString();
    }

    public ClassType getBaseType() {
        return type;
    }

    public void setBaseType(final ClassType type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }
}
