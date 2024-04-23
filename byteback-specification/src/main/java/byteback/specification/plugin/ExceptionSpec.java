package byteback.specification.plugin;

import byteback.specification.Contract.Return;

@Plugin.Attach("java.lang.Exception")
public abstract class ExceptionSpec {

	@Return
	public ExceptionSpec() {
	}

	@Return
	public ExceptionSpec(String message) {
	}

}
