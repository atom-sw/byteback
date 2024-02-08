package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Return;

@Attach(IllegalArgumentException.class)
public abstract class IllegalArgumentExceptionSpec {

	@Return
	public IllegalArgumentExceptionSpec() {
	}

	@Return
	public IllegalArgumentExceptionSpec(String message) {
	}

}
