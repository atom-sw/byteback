package byteback.analysis;

import soot.util.Switch;
import soot.util.Switchable;

public interface Visitor<T extends Switchable, R> extends Switch {

	default R result() {
		return null;
	}

	default void caseDefault(final T o) {
	}

	default R visit(final T o) {
		o.apply(this);

		return result();
	}

}
