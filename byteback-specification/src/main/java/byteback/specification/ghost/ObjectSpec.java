package byteback.specification.plugin;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.plugin.Plugin.Export;

@Plugin.Attach("java.lang.Object")
public abstract class ObjectSpec {

	@Return
	@Export
	@Abstract
	public ObjectSpec() {
		throw new UnsupportedOperationException();
	}

}
