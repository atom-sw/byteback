package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.NoState;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.LinkedList")
@Invariant("is_mutable")
public class LinkedListSpec<T> {

	@NoState
	@Behavior
	public boolean is_mutable() {
		return Ghost.of(ListSpec.class, this).is_mutable();
	}

	@Abstract
	@Return
	public LinkedListSpec() {
	}

}
