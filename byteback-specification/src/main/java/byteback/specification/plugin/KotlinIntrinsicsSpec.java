package byteback.specification.plugin;

import static byteback.specification.Operators.*;

import byteback.specification.Contract.Ignore;
import byteback.specification.Contract.Lemma;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

@Plugin.Attach("kotlin/jvm/internal/Intrinsics;")
public abstract class KotlinIntrinsicsSpec {

	@Behavior
	public static boolean parameter_is_null(Object parameter, String name) {
		return eq(parameter, null);
	}

	@Behavior
	public static boolean parameter_is_not_null(Object parameter, String name) {
		return neq(parameter, null);
	}

	@Ignore
	public static void checkNotNull(Object o) {
	}

	@Ignore
	@Lemma
	@Raise(exception = IllegalArgumentException.class, when = "parameter_is_null")
	@Return(when = "parameter_is_not_null")
	public static void checkNotNullParameter(Object parameter, String name) {
	}

}
