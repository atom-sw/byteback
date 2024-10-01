package byteback.specification.ghost;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.NoState;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.Collection")
public interface CollectionSpec {

	@NoState
	@Implicit
	@Behavior
	boolean is_modifiable();

	@NoState
	@Implicit
	@Behavior
	boolean is_resizeable();

	@NoState
	@Implicit
	@Behavior
	boolean is_nullable();

	@NoState
	@Implicit
	@Behavior
	default boolean is_mutable() {
		return is_modifiable() & is_resizeable() & is_nullable();
	}

}
