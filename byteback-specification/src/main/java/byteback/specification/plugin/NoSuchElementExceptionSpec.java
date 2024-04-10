package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach("java.util.NoSuchElementException")
public abstract class NoSuchElementExceptionSpec {

	@Return
	public NoSuchElementExceptionSpec() {
	}

	@Return
	public NoSuchElementExceptionSpec(final String message) {
	}

}
