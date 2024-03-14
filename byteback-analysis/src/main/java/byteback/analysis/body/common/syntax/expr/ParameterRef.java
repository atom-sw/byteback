package byteback.analysis.body.common.syntax.expr;

import byteback.analysis.model.syntax.type.Type;

import java.util.Collections;
import java.util.List;

public class ParameterRef implements IdentityRef {

    final Type type;

    final int index;

    /**
     * Constructs a ParameterRef object of the specified type, representing the specified parameter number.
     */
    public ParameterRef(final Type type, final int index) {
        this.type = type;
        this.index = index;
    }

    @Override
    public Type getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "@parameter" + index + ": " + type;
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }
}