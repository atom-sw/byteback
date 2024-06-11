/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */

package byteback.test.patternmatching;

import static byteback.specification.Contract.assertion;

import java.util.ArrayList;

public class Basic {

	public static int Convert_ArrayPatternMatch() {
		Object obj = new int[10];

		if (obj instanceof int[] matched) {
			int len = matched.length;

			return len;
		}

		return 0;
	}

	public static int Convert_InstancePatternMatch() {
		Object obj = new ArrayList<>();

		if (obj instanceof ArrayList matched) {
			int size = matched.size();

			return size;
		}

		return 0;
	}

	public static int AssertFalse_InUnreachableMatchIf_Verifies() {
		Object obj = new Object();

		if (obj instanceof ArrayList matched) {
			int size = matched.size();
			assertion(false);

			return size;
		}

		return 0;
	}

	public static int AssertFalse_InReachableMatchIf_Fails() {
		Object obj = new ArrayList<>();

		if (obj instanceof ArrayList matched) {
			int size = matched.size();
			// CHECK: Error: this assertion could not be proved
			assertion(false);

			return size;
		}

		return 0;
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 1 error
 */
