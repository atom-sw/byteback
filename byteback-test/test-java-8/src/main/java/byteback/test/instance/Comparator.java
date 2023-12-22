/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.instance;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;

public interface Comparator {

	public static class DefaultComparator {

		@Predicate
		public boolean compare_default(int a, int b, boolean returns) {
			return returns;
		}

		public boolean compare(int a, int b) {
			return true;
		}

	}

	public static class LessThanComparator extends DefaultComparator {

		@Predicate
		public boolean compare_less_than(int a, int b, boolean returns) {
			return implies(returns, lt(a, b));
		}

		@Ensure("compare_less_than")
		@Override
		public boolean compare(int a, int b) {
			return a < b;
		}

	}

	public static class GreaterThanComparator extends DefaultComparator {

		@Predicate
		public boolean compare_greater_than(int a, int b, boolean returns) {
			return implies(returns, gt(a, b));
		}

		@Ensure("compare_greater_than")
		@Override
		public boolean compare(int a, int b) {
			return a > b;
		}

	}

	public static void main() {
		LessThanComparator ltComparator = new LessThanComparator();
		GreaterThanComparator gtComparator = new GreaterThanComparator();

		boolean a = ltComparator.compare(2, 1);
		assertion(not(a));

		boolean b = gtComparator.compare(1, 2);
		assertion(not(b));
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
