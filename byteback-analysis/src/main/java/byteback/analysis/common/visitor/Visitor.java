package byteback.analysis.common.visitor;

import soot.util.Switch;
import soot.util.Switchable;

/**
 * Base visitor interface.
 * @param <T> The type of the visitable (or switchable) instance.
 * @param <R> The type of the result obtained.
 */
public interface Visitor<T extends Switchable, R> extends Switch {

	default R getResult() {
		return null;
	}

	default void defaultCase(T o) {
	}

	default R visit(final T o) {
		o.apply(this);

		return getResult();
	}

}
