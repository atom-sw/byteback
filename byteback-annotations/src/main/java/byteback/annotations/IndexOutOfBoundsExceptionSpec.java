package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Return;
import byteback.annotations.Contract.Invariant;

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
