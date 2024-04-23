package byteback.specification.plugin;

import byteback.specification.Contract.Return;

@Plugin.Attach("java.lang.IndexOutOfBoundsException")
public abstract class IndexOutOfBoundsExceptionSpec {

	@Return
	public IndexOutOfBoundsExceptionSpec() {
	}

	@Return
	public IndexOutOfBoundsExceptionSpec(String message) {
	}

}
