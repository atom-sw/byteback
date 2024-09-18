package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.RuntimeException")
public abstract class RuntimeExceptionSpec {

	@Return
	@Abstract
	public RuntimeExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public RuntimeExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
