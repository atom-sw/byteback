package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.LinkedHashMap")
@Invariant("is_mutable")
public class LinkedHashMapSpec<K, V> {

	@Behavior
	public boolean is_mutable() {
		return Ghost.of(MapSpec.class, this).is_mutable();
	}

	@Abstract
	@Return
	public LinkedHashMapSpec() {
	}

}
