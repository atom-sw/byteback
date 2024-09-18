package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.ClassCastException")
public abstract class ClassCastExceptionSpec {

	@Return
	@Abstract
	public ClassCastExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public ClassCastExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
