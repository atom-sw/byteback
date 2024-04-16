package byteback.syntax.type.declaration.method.body.value.analyzer;

import soot.Type;
import soot.Value;

/**
 * Base class for a strategy to find a type of given value.
 * @param <T> The base type of the value to which the strategy can be applied.
 *
 * @author paganma
 */
public interface TypeInterpreter<T extends Value> {

    /**
     * Queries a value for its type.
     *
     * @param value The value of which we want to find the type.
     * @return The type of the value according to some interpretation.
     */
    Type typeOf(final T value);

}
