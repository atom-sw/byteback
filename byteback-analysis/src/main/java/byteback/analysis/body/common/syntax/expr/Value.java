package byteback.analysis.body.common.syntax.expr;

import byteback.analysis.model.syntax.type.Type;

import java.util.List;

/**
 * Data used as, for instance, arguments to instructions; typical implementations are constants or expressions.
 * <p>
 * Values are typed, clonable and must declare which other Values they use (contain).
 */
public interface Value {

    /**
     * Returns a List of boxes corresponding to Values which are used by (ie contained within) this Value.
     */
    List<ValueBox> getUseBoxes();

    /**
     * Returns the Soot type of this Value.
     */
    Type getType();
}
