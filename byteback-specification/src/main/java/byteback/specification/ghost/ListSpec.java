package byteback.specification.ghost;

import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.ghost.Ghost.Export;

import static byteback.specification.Operators.not;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;

@Attach("java.util.List")
public interface ListSpec<T> {

	@Export
	@Behavior
	boolean is_modifiable();

	@Raise(exception = UnsupportedOperationException.class,
				 when = "is_unmodifiable")
	@Export
	void add(T element);

	@Raise(exception = UnsupportedOperationException.class,
				 when = "is_unmodifiable")
	@Export
	void remove(T element);

}
