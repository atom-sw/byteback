package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach("java.lang.IndexOutOfBoundsException")
public abstract class IndexOutOfBoundsExceptionSpec {

	@Return
	public IndexOutOfBoundsExceptionSpec() {
	}

	@Return
	public IndexOutOfBoundsExceptionSpec(String message) {
	}

}
