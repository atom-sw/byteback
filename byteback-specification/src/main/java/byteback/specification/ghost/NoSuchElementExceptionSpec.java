package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.NoSuchElementException")
public abstract class NoSuchElementExceptionSpec {

	@Return
	@Abstract
	public NoSuchElementExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public NoSuchElementExceptionSpec(final String message) {
		throw new UnsupportedOperationException();
	}

}
