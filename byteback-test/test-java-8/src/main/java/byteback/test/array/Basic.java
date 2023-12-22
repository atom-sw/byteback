/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.array;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;

public class Basic {

	@Pure
	@Predicate
	public static boolean last_element_is_1(int[] as) {
		return eq(as[as.length - 1], 1);
	}

	@Ensure("last_element_is_1")
	public static void assignElement(int[] as) {
		as[as.length - 1] = 1;
	}

	@SuppressWarnings("unused")
	public static void literals() {
		int[] a = { 1, 2, 3, 4 };
		int[] b = { 5, 4, 3, 2 };
	}

	public static void constructors() {
		int[] intArray = new int[10];
		assertion(eq(intArray.length, 10));
		double[] doubleArray = new double[10];
		assertion(eq(doubleArray.length, 10));
		Object[] objectArray = new Object[10];
		assertion(eq(objectArray.length, 10));
	}

	@SuppressWarnings("unused")
	public static void lengthReference(int[] a) {
		int length = a.length;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
