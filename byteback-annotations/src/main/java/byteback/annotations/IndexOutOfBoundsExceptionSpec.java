package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Invariant;
import byteback.annotations.Contract.Return;

@Attach(IndexOutOfBoundsException.class)
public abstract class IndexOutOfBoundsExceptionSpec {

	@Return
	@Invariant
	public IndexOutOfBoundsExceptionSpec() {
	}

	@Return
	@Invariant
	public IndexOutOfBoundsExceptionSpec(String message) {
	}

}
