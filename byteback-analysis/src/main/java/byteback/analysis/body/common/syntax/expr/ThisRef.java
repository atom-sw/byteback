package byteback.analysis.body.common.syntax.expr;

import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.Type;

import java.util.Collections;
import java.util.List;

public class ThisRef implements IdentityRef {


    private ClassType type;

    public ThisRef(final ClassType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "@this: " + type;
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }

    @Override
    public Type getType() {
        return type;
    }
}
