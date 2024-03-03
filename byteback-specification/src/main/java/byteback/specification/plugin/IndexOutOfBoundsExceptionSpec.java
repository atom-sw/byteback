package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Return;

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
