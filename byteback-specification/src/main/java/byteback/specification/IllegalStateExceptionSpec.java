package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Return;

@Attach(IllegalStateException.class)
public class IllegalStateExceptionSpec {

	@Return
	public IllegalStateExceptionSpec() {
	};

	@Return
	public IllegalStateExceptionSpec(String message) {
	};

}
