package byteback.specification;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Return;
import java.util.NoSuchElementException;

@Attach(NoSuchElementException.class)
public abstract class NoSuchElementExceptionSpec {

	@Return
	@Invariant
	public NoSuchElementExceptionSpec() {
	}

	@Return
	@Invariant
	public NoSuchElementExceptionSpec(String message) {
	}

}
