/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.operation;

import static byteback.specification.Operators.implies;
import static byteback.specification.Operators.not;

import byteback.specification.Contract.*;

public class Boolean {

	@Behavior
	public static boolean or(boolean a, boolean b) {
		return a | b;
	}

	@Behavior
	public static boolean and(boolean a, boolean b) {
		return a & b;
	}

	@Behavior
	public static boolean xor(boolean a, boolean b) {
		return a ^ b;
	}

	@Behavior
	public static boolean returnsTrue() {
		return true;
	}

	@Behavior
	public static boolean returnsFalse() {
		return false;
	}

	@Behavior
	public static boolean and_postcondition(boolean a, boolean b, boolean returns) {
		return implies(a & b, returns);
	}

	@Behavior
	public static boolean or_postcondition(boolean a, boolean b, boolean returns) {
		return implies(a | b, returns);
	}

	@Behavior
	public static boolean not_postcondition(boolean a, boolean returns) {
		return implies(a, not(returns));
	}

	@Ensure("and_postcondition")
	public static boolean shortCircuitingAnd(boolean a, boolean b) {
		return a && b;
	}

	@Ensure("or_postcondition")
	public static boolean shortCircuitingOr(boolean a, boolean b) {
		return a || b;
	}

	@Ensure("not_postcondition")
	public static boolean shortCircuitingNot(boolean a) {
		return !a;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
