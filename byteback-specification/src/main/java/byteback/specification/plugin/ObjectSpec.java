package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;

@Attach("java.lang.Object")
public abstract class ObjectSpec {

	@Return
	public ObjectSpec() {
	}

}
