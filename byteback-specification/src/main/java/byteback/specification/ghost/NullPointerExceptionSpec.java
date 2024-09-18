package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.NullPointerException")
public abstract class NullPointerExceptionSpec {

	@Return
	@Abstract
	public NullPointerExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public NullPointerExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
