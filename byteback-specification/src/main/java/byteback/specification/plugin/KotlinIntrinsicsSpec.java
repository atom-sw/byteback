package byteback.specification.plugin;

import static byteback.specification.Operator.*;

import byteback.specification.Contract.AttachLabel;
import byteback.specification.Contract.Ignore;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Lemma;
import byteback.specification.Contract.Predicate;
import byteback.specification.Contract.Function;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

@AttachLabel("Lkotlin/jvm/internal/Intrinsics;")
public abstract class KotlinIntrinsicsSpec {

	@Function
	@Predicate
	public static boolean parameter_is_null(Object parameter, String name) {
		return eq(parameter, null);
	}

	@Function
	@Predicate
	public static boolean parameter_is_not_null(Object parameter, String name) {
		return neq(parameter, null);
	}

	@Ignore
	public static void checkNotNull(Object o) {
	}

	@Ignore
	@Lemma
	@Invariant
	@Raise(exception = IllegalArgumentException.class, when = "parameter_is_null")
	@Return(when = "parameter_is_not_null")
	public static void checkNotNullParameter(Object parameter, String name) {
	}

}
