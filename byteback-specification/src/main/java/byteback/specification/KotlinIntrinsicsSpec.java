package byteback.annotations;

import static byteback.annotations.Operator.*;

import byteback.annotations.Contract.AttachLabel;
import byteback.annotations.Contract.Ignore;
import byteback.annotations.Contract.Invariant;
import byteback.annotations.Contract.Lemma;
import byteback.annotations.Contract.Predicate;
import byteback.annotations.Contract.Pure;
import byteback.annotations.Contract.Raise;
import byteback.annotations.Contract.Return;

@AttachLabel("Lkotlin/jvm/internal/Intrinsics;")
public abstract class KotlinIntrinsicsSpec {

	@Pure
	@Predicate
	public static boolean parameter_is_null(Object parameter, String name) {
		return eq(parameter, null);
	}

	@Pure
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
