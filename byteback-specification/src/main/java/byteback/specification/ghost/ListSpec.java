package byteback.specification.ghost;

import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.ghost.Ghost.Export;
import byteback.specification.Contract.Abstract;

@Attach("java.util.List")
public abstract class ListSpec<T> {

	@Export
	public boolean isImmutable;

	@Export
	@Abstract
	public ListSpec() {
	}

}
