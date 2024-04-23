package byteback.specification.plugin;

import byteback.specification.Contract.Return;

@Plugin.Attach("java.util.NoSuchElementException")
public abstract class NoSuchElementExceptionSpec {

	@Return
	public NoSuchElementExceptionSpec() {
	}

	@Return
	public NoSuchElementExceptionSpec(final String message) {
	}

}
