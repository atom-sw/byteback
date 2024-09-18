package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.IndexOutOfBoundsException")
public abstract class IndexOutOfBoundsExceptionSpec {

	@Return
	@Abstract
	public IndexOutOfBoundsExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public IndexOutOfBoundsExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
