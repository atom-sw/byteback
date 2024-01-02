package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Invariant;
import byteback.annotations.Contract.Return;

@Attach(NullPointerException.class)
public abstract class NullPointerExceptionSpec {

	@Return
	@Invariant
	public NullPointerExceptionSpec() {
	}

	@Return
	@Invariant
	public NullPointerExceptionSpec(String message) {
	}

}
