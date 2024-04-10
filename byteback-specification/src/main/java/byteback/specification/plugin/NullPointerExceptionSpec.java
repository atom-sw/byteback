package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach("java.lang.NullPointerException")
public abstract class NullPointerExceptionSpec {

	@Return
	public NullPointerExceptionSpec() {
	}

	@Return
	public NullPointerExceptionSpec(final String message) {
	}

}
