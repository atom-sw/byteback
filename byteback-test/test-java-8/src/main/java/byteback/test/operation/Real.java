/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.operation;

import byteback.specification.Contract.*;

public class Real {

	@Behavior
	public static double division(double a, double b) {
		return a / b;
	}

	@Behavior
	public static float division(float a, float b) {
		return a / b;
	}

	@Behavior
	public static double multiplication(double a, double b) {
		return a * b;
	}

	@Behavior
	public static float multiplication(float a, float b) {
		return a * b;
	}

	@Behavior
	public static double circleArea(double r) {
		return 3.14 * r * r;
	}

	public static double sumIfSmaller(double a, double b) {

		if (b < a) {
			return a + b;
		}

		return 0;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
