/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.TwoState;

import static byteback.specification.Operators.*;
import static byteback.specification.Special.*;

public class InstanceFrame {

	static int i = 0;

	@TwoState
	@Behavior
	public static boolean first_element_is_incremented() {
		return eq(i, old(i) + 1);
	}

	@Ensure("first_element_is_incremented")
	public static void test() {
		i++;
	}

}
