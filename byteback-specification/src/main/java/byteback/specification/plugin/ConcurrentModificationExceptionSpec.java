package byteback.specification.plugin;

import byteback.specification.Contract.Return;

@Plugin.Attach("java.util.ConcurrentModificationException")
public class ConcurrentModificationExceptionSpec {

	@Return
	public ConcurrentModificationExceptionSpec() {
	}

	@Return
	public ConcurrentModificationExceptionSpec(String message) {
	}

}
