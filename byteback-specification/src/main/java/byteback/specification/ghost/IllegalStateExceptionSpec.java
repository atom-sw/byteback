package byteback.specification.plugin;

import byteback.specification.Contract.Return;

@Plugin.Attach("java.lang.IllegalStateException")
public class IllegalStateExceptionSpec {

	@Return
	public IllegalStateExceptionSpec() {
	}

	@Return
	public IllegalStateExceptionSpec(String message) {
	}

}
