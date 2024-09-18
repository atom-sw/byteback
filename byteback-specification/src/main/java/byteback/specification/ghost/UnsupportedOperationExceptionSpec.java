package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.UnsupportedOperationException")
public abstract class UnsupportedOperationExceptionSpec {

	@Return
	@Abstract
	public UnsupportedOperationExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public UnsupportedOperationExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
