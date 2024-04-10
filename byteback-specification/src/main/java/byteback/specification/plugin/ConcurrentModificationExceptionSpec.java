package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach("java.util.ConcurrentModificationException")
public class ConcurrentModificationExceptionSpec {

	@Return
	public ConcurrentModificationExceptionSpec() {
	}

	@Return
	public ConcurrentModificationExceptionSpec(String message) {
	}

}
