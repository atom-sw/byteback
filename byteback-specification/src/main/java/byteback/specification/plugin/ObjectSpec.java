package byteback.specification.plugin;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;

@Plugin.Attach("java.lang.Object")
public abstract class ObjectSpec {

	@Abstract
	@Return
	public ObjectSpec() {
	}

}
