package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach("java.lang.IllegalStateException")
public class IllegalStateExceptionSpec {

	@Return
	public IllegalStateExceptionSpec() {
	}

	@Return
	public IllegalStateExceptionSpec(String message) {
	}

}
