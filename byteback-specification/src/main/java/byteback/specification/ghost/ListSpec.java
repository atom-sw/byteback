package byteback.specification.ghost;

import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.ghost.Ghost.Export;
import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;

@Attach("java.util.List")
public abstract class ListSpec<T> {

	@Export
	public abstract boolean isMutable();

	@Export
	@Abstract
	@Return
	public ListSpec() {
	}

}
