package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.Exception")
public abstract class ExceptionSpec {

	@Abstract
	public ExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public ExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
