package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Return;

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
