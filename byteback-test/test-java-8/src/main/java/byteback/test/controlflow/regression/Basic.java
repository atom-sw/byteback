/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 * RUN: %{verify} %t.bpl | filecheck %s
 */

package byteback.test.controlflow.regression;

import static byteback.specification.Contract.assertion;

public class Basic {

	public static boolean alwaysTrue() {
		return true;
	}

	public static void AssertionFalse_AsSingleStatement_TriggersBoogieError() {
		// CHECK: Error: this assertion could not be proved
		assertion(false);
	}

	public static void AssertionFalse_AfterFiniteLoop_TriggersBoogieError() {
		for (int i = 0; i < 10; ++i); 

		// CHECK: Error: this assertion could not be proved
		assertion(false);
	}

	public static void AssertionFalse_InBasicIfThen_TriggersBoogieError() {
		int i = 10;

		if (i < 11) {
			// CHECK: Error: this assertion could not be proved
			assertion(false);
		}
	}

	public static void AssertionFalse_InUnreachableIfThen_PassesBoogieVerification() {
		int i = 10;

		if (i < 10) {
			// CHECK-NOT: Error: this assertion could not be proved
			assertion(false);
		}
	}

	@SuppressWarnings("all")
	public static void AssertionFalse_AfterInfiniteLoop_PassesBoogieVerification() {
		while(true) {
			if (1 == 0) break;
		}

		// CHECK-NOT: Error: this assertion could not be proved
		assertion(false);
	}

}