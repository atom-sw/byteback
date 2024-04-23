/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.quantifier;

import static byteback.specification.Quantifiers.exists;
import static byteback.specification.Quantifiers.forall;
import static byteback.specification.Operators.*;

import byteback.specification.Bindings;
import byteback.specification.Contract.Behavior;

public class Basic {

	@Behavior
	public static boolean universalQuantifier() {
		int i = Bindings.integer();

		return forall(i, eq(i, 0));
	}

	@Behavior
	public static boolean existentialQuantifier() {
		int i = Bindings.integer();

		return exists(i, eq(i, 0));
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 1 verified, 0 errors
 */
