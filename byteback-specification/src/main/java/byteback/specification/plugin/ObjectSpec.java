package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach(Object.class)
public abstract class ObjectSpec {

	@Return
	public ObjectSpec() {
	}

}
