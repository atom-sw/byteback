/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.quantifier;

import static byteback.specification.Quantifier.exists;
import static byteback.specification.Quantifier.forall;
import static byteback.specification.Operator.*;

import byteback.specification.Binding;
import byteback.specification.Contract.Pure;

public class Basic {

	@Pure
	public static boolean universalQuantifier() {
		int i = Binding.integer();

		return forall(i, eq(i, 0));
	}

	@Pure
	public static boolean existentialQuantifier() {
		int i = Binding.integer();

		return exists(i, eq(i, 0));
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 1 verified, 0 errors
 */
