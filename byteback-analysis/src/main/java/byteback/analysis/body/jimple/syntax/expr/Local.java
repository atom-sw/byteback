package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Immediate;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.type.Type;

import java.util.Collections;
import java.util.List;

/**
 * A local variable, used within Body classes. Intermediate representations must use an implementation of Local for their
 * local variables.
 */
public class Local implements Value, Immediate {

    protected final String name;

    protected final Type type;

    /** Constructs a Local of the given name and type. */
    public Local(final String name, final Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());

        return result;
    }

    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }
}
