package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach(IllegalArgumentException.class)
public abstract class IllegalArgumentExceptionSpec {

	@Return
	public IllegalArgumentExceptionSpec() {
	}

	@Return
	public IllegalArgumentExceptionSpec(String message) {
	}

}
