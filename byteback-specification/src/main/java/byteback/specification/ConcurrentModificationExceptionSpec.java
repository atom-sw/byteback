package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Return;
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
