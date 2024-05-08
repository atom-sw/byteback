package byteback.specification.plugin;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;

@Plugin.Attach("java.lang.Exception")
public abstract class ExceptionSpec {

	@Abstract
	public ExceptionSpec() {
	}

	@Abstract
	@Return
	public ExceptionSpec(String message) {
	}

}
