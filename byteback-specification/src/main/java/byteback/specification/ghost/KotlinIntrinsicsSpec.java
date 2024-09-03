package byteback.specification.ghost;

import static byteback.specification.Operators.*;

import byteback.specification.Contract.Ignore;
import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;

@Ghost.Attach("kotlin.jvm.internal.Intrinsics")
public abstract class KotlinIntrinsicsSpec {

	@Behavior
	public static boolean parameter_is_null(Object parameter, String name) {
		return eq(parameter, null);
	}

	@Behavior
	public static boolean parameter_is_not_null(Object parameter, String name) {
		return neq(parameter, null);
	}

	@Abstract
	public KotlinIntrinsicsSpec() {
	}

	@Ignore
	@Abstract
	public static void checkNotNull(Object o) {
		throw new UnsupportedOperationException();
	}

	@Ignore
	@Abstract
	public static void checkNotNullParameter(Object parameter, String name) {
		throw new UnsupportedOperationException();
	}

}
