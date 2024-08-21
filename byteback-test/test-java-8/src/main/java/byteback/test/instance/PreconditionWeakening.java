/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$A -c %{class}$B -o %t.bpl
 */

package byteback.test.instance;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.RequireOnly;

public class PreconditionWeakening {

	public class A {

		@Behavior
		public boolean p1 () {
			return false;
		}

		@Require("p1")
		public void test() {
		}

	}

	public class B extends A {

		@Behavior
		public boolean p2 () {
			return true;
		}

		@Override
		@RequireOnly("p2")
		public void test() {
		}

	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 6 verified, 0 errors
 */
