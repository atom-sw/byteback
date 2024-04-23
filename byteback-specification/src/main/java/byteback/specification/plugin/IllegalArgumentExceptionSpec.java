package byteback.specification.plugin;

import byteback.specification.Contract.Return;

@Plugin.Attach("java.lang.IllegalArgumentException")
public abstract class IllegalArgumentExceptionSpec {

	@Return
	public IllegalArgumentExceptionSpec() {
	}

	@Return
	public IllegalArgumentExceptionSpec(String message) {
	}

}
