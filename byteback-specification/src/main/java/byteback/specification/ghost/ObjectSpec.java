package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Export;
import byteback.specification.ghost.Ghost.Attach;;

@Attach("java.lang.Object")
public abstract class ObjectSpec {

	@Return
	@Export
	@Abstract
	public ObjectSpec() {
		throw new UnsupportedOperationException();
	}

}
