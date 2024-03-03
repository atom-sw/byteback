package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;
import java.util.ConcurrentModificationException;

@Attach(ConcurrentModificationException.class)
public class ConcurrentModificationExceptionSpec {

	@Return
	public ConcurrentModificationExceptionSpec() {
	}

	@Return
	public ConcurrentModificationExceptionSpec(String message) {
	}

}
