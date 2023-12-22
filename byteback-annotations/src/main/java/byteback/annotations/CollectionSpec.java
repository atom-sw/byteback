package byteback.annotations;

import byteback.annotations.Contract.Attach;
import byteback.annotations.Contract.Return;
import java.util.Collection;

@Attach(Collection.class)
public abstract class CollectionSpec {

	@Return
	public CollectionSpec() {
	}

	@Return
	abstract Object[] toArray();

}
