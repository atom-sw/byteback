package byteback.specification.plugin;

import static byteback.specification.Operators.*;

import byteback.specification.Contract.Ignore;
import byteback.specification.plugin.Plugin.Export;
import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;

@Plugin.Attach("kotlin.jvm.internal.Intrinsics")
public abstract class KotlinIntrinsicsSpec {

	@Behavior
	public static boolean parameter_is_null(Object parameter, String name) {
		return eq(parameter, null);
	}

	@Behavior
	public static boolean parameter_is_not_null(Object parameter, String name) {
		return neq(parameter, null);
	}

	@Export
	@Abstract
	public KotlinIntrinsicsSpec() {
	}

	@Ignore
	@Export
	@Abstract
	public static void checkNotNull(Object o) {
		throw new UnsupportedOperationException();
	}

	@Ignore
	@Export
	@Abstract
	public static void checkNotNullParameter(Object parameter, String name) {
		throw new UnsupportedOperationException();
	}

}
