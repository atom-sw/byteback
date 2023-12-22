package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Return;

@Attach(Exception.class)
public abstract class ExceptionSpec {

	@Return
	public ExceptionSpec() {
	}

	@Return
	public ExceptionSpec(String message) {
	}

}
