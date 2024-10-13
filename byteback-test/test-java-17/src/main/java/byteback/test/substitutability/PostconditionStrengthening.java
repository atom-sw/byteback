/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$A -c %{class}$B -o %t.bpl
 */

package byteback.test.substitutability;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.EnsureOnly;
import static byteback.specification.Operators.*;

public class PostconditionStrengthening {

	public static class A {

		@Behavior
		public boolean p1 () {
			return true;
		}

		@Ensure("p1")
		public void t1() {
		}

		@Behavior
		public boolean p2(int i) {
			return lte(0, i);
		}

		@Ensure("p2")
		public int t2() {
			return 5;
		}

		@Behavior
		public boolean p3(int i) {
			return lte(0, i) & lte(i, 10);
		}

		@Ensure("p3")
		public int t3() {
			return 5;
		}

	}

	public static class B extends A {

		@Behavior
		public boolean p1 () {
			return true;
		}

		@Override
		@EnsureOnly
		@Ensure("p1")
		public void t1() {
		}

		@Behavior
		public boolean p2(int i) {
			return lte(1, i);
		}

		@Override
		@EnsureOnly
		@Ensure("p2")
		public int t2() {
			return 5;
		}

		@Behavior
		public boolean p3(int i) {
			return lte(1, i) & lte(i, 10);
		}

		@Override
		@EnsureOnly
		@Ensure("p3")
		public int t3() {
			return 5;
		}

	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 12 verified, 0 errors
 */
