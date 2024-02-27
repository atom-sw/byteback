package byteback.specification;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Return;
import java.util.Collection;

@Attach(Collection.class)
public abstract class CollectionSpec {

	@Return
	public CollectionSpec() {
	}

	@Return
	abstract Object[] toArray();

}
