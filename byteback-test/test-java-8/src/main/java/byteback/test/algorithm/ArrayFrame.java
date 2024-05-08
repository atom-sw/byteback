/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.TwoState;

import static byteback.specification.Operators.*;
import static byteback.specification.Special.*;

public class ArrayFrame {

	@Behavior
	public static boolean array_is_not_empty(int[] a) {
		return gt(a.length, 0);
	}

	@TwoState
	@Behavior
	public static boolean first_element_is_incremented(int[] a) {
		return eq(a[0], old(a[0]) + 2);
	}

	@Require("array_is_not_empty")
	@Ensure("first_element_is_incremented")
	public static void test(int[] a) {
		a[0]++;
	}

}
