package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach("java.lang.Exception")
public abstract class ExceptionSpec {

	@Return
	public ExceptionSpec() {
	}

	@Return
	public ExceptionSpec(String message) {
	}

}
