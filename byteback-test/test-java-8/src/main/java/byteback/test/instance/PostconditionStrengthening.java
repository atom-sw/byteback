/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$A -c %{class}$B -o %t.bpl
 */

package byteback.test.instance;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.EnsureOnly;

public class PostconditionStrengthening {

	public class A {

		@Behavior
		public boolean p1 () {
			return true;
		}

		@Ensure("p1")
		public void test() {
		}

	}

	public class B extends A {

		@Behavior
		public boolean p2 () {
			return true;
		}

		@Override
		@EnsureOnly
		@Ensure("p2")
		public void test() {
		}

	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 6 verified, 0 errors
 */
