package byteback.analysis.body.common.syntax;

import byteback.analysis.model.syntax.type.Type;

/**
 * A local variable, used within Body classes. Intermediate representations must use an implementation of Local for their
 * local variables.
 */
public interface Local extends Value, Immediate {
    /**
     * Returns the name of the current Local variable.
     */
    String getName();

    /**
     * Sets the name of the current variable.
     */
    void setName(String name);

    /**
     * Sets the type of the current variable.
     */
    void setType(Type t);

    boolean isStackLocal();
}
