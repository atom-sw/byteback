package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Return;

@Attach(Object.class)
public abstract class ObjectSpec {

	@Return
	public ObjectSpec() {
	}

}
