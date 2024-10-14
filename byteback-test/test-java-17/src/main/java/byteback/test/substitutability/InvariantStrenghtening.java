/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$A -c %{class}$B -o %t.bpl
 */

package byteback.test.substitutability;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.InvariantOnly;
import byteback.specification.Contract.*;
import static byteback.specification.Operators.*;

public class InvariantStrenghtening {

	@InvariantOnly
	@Invariant("p1")
	@Invariant("p2")
	public static class A {

		@Behavior
		public boolean p1() {
			return true;
		}

		int i;

		@Behavior
		public boolean p2() {
			return lte(0, i);
		}

		@Return
		public A() {
			i = 10;
		}

	}

	@InvariantOnly
	@Invariant("p1")
	@Invariant("p2")
	public static class B extends A {

		@Return
		public B() {
			i = 10;
		}

		@Behavior
		public boolean p1() {
			return true;
		}

		@Behavior
		public boolean p2() {
			return lte(1, i);
		}

	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
