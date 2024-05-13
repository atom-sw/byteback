package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Export;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.lang.Exception")
public abstract class ExceptionSpec {

	@Export
	@Abstract
	public ExceptionSpec() {
		throw new UnsupportedOperationException();
	}

	@Return
	@Export
	@Abstract
	public ExceptionSpec(String message) {
		throw new UnsupportedOperationException();
	}

}
