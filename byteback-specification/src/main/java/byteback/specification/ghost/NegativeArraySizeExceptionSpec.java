package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.NegativeArraySizeException")
public abstract class NegativeArraySizeExceptionSpec {

	@Return
	@Abstract
	public NegativeArraySizeExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public NegativeArraySizeExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
