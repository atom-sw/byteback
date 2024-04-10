package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;
import java.util.Collection;

@Attach("java.util.Collection")
public abstract class CollectionSpec {

	@Return
	public CollectionSpec() {
	}

	@Return
	abstract Object[] toArray();

}
