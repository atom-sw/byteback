package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.IllegalArgumentException")
public abstract class IllegalArgumentExceptionSpec {

	@Return
	@Abstract
	public IllegalArgumentExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public IllegalArgumentExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
