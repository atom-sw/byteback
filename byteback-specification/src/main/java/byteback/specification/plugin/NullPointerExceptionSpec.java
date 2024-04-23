package byteback.specification.plugin;

import byteback.specification.Contract.Return;

@Plugin.Attach("java.lang.NullPointerException")
public abstract class NullPointerExceptionSpec {

	@Return
	public NullPointerExceptionSpec() {
	}

	@Return
	public NullPointerExceptionSpec(final String message) {
	}

}
