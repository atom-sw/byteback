package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Invariant;
import byteback.annotations.Contract.Return;
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
