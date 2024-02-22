package byteback.analysis.common.visitor;

import soot.util.Switch;
import soot.util.Switchable;

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
