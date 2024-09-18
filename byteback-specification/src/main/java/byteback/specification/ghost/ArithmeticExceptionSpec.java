package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.ArithmeticException")
public abstract class ArithmeticExceptionSpec {

	@Return
	@Abstract
	public ArithmeticExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public ArithmeticExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
