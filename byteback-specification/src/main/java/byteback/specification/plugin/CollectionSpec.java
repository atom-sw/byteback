package byteback.specification.plugin;

import byteback.specification.plugin.Plugin.Attach;
import byteback.specification.Contract.Return;

@Attach("java.util.Collection")
public abstract class CollectionSpec {

	@Return
	public CollectionSpec() {
	}

	@Return
	abstract Object[] toArray();

}
