package byteback.test.substitutability;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.NoState;
import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.Ghost.Attach;

@Attach("com.google.common.collect.ImmutableMap")
@Invariant("this_is_unmodifiable")
public class ImmutableGuavaMapSpec {

	@Abstract
	public ImmutableGuavaMapSpec() {
	}

	@NoState
	@Behavior
	public boolean this_is_unmodifiable() {
		return Ghost.of(CollectionSpec.class, this).is_unmodifiable();
	}

}
