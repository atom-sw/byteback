/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$A -c %{class}$B -o %t.bpl
 */

package byteback.test.substitutability;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.RequireOnly;
import static byteback.specification.Operators.*;

public class PreconditionWeakening {

	public static class A {

		@Behavior
		public boolean p1() {
			return false;
		}

		@Require("p1")
		public void t1() {
		}

		@Behavior
		public boolean p2(int i) {
			return lte(1, i);
		}

		@Require("p2")
		public void t2(int i) {
		}

		@Behavior
		public boolean p3(int i) {
			return lte(1, i) & lte(i, 10);
		}

		@Require("p3")
		public void t3(int i) {
		}

	}

	public static class B extends A {

		@Behavior
		public boolean p1 () {
			return true;
		}

		@Override
		@RequireOnly
		@Require("p1")
		public void t1() {
		}

		@Behavior
		public boolean p2(int i) {
			return lte(0, i);
		}

		@Override
		@RequireOnly
		@Require("p2")
		public void t2(int i) {
		}

		@Behavior
		public boolean p3(int i) {
			return lte(0, i) & lte(i, 10);
		}

		@Override
		@RequireOnly
		@Require("p3")
		public void t3(int i) {
		}

	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 12 verified, 0 errors
 */
