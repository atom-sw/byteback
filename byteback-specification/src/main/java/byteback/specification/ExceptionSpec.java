package byteback.specification;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach(Exception.class)
public abstract class ExceptionSpec {

	@Return
	public ExceptionSpec() {
	}

	@Return
	public ExceptionSpec(String message) {
	}

}
