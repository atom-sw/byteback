package byteback.specification.plugin;

import byteback.specification.plugin.Plugin.Attach;
import byteback.specification.Contract.Return;
import byteback.specification.Contract.Abstract;

@Attach("java.util.Collection")
public abstract class CollectionSpec {

	@Abstract
	public CollectionSpec() {
	}

	@Return
	abstract Object[] toArray();

}
