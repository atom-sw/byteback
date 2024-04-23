/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.operation;

import static byteback.specification.Operators.eq;
import static byteback.specification.Operators.not;

import byteback.specification.Contract.Behavior;

public class Integer {

	@Behavior
	public static int addition(int a, int b) {
		return a + b;
	}

	public static int proceduralAddition(int a, int b) {
		return a + b;
	}

	@Behavior
	public static int subtraction(int a, int b) {
		return a - b;
	}

	public static int proceduralSubtraction(int a, int b) {
		return a - b;
	}

	@Behavior
	public static int minus(int a) {
		return -a;
	}

	public static int proceduralMinus(int a) {
		return -a;
	}

	@Behavior
	public static int multiplication(int a, int b) {
		return a * b;
	}

	public static int proceduralMultiplication(int a, int b) {
		return a * b;
	}

	@Behavior
	public static int division(int a, int b) {
		return a / b;
	}

	public static int proceduralDivision(int a, int b) {
		return a / b;
	}

	@Behavior
	public static int modulo(int a, int b) {
		return a % b;
	}

	public static int proceduralModulo(int a, int b) {
		return a % b;
	}

	@Behavior
	public static int square(int a) {
		return a * a;
	}

	public static int proceduralSquare(int a) {
		return a * a;
	}

	@Behavior
	public static int squareArea(int a) {
		return square(a);
	}

	public static int proceduralSquareArea(int a) {
		return square(a);
	}

	@Behavior
	public static int rectangleArea(int a, int b) {
		return multiplication(a, b);
	}

	public static int proceduralRectangleArea(int a, int b) {
		return multiplication(a, b);
	}

	@Behavior
	public static boolean even(int a) {
		return eq(a % 2, 0);
	}

	public static boolean proceduralEven(int a) {
		return eq(a % 2, 0);
	}

	@Behavior
	public static boolean odd(int a) {
		return not(even(a));
	}

	public static boolean proceduralOdd(int a) {
		return not(even(a));
	}

	@Behavior
	public static int assignIndirect(int a) {
		int b = a;
		int c = b;
		int d = c;
		int e = d;
		int f = e;

		return f;
	}

	public static int proceduralAssignIndirect(int a) {
		int b = a;
		int c = b;
		int d = c;
		int e = d;
		int f = e;

		return f;
	}

	@Behavior
	public static int assignPlus(int a) {
		a = a + 1;

		return a;
	}

	public static int proceduralAssignPlus(int a) {
		a = a + 1;

		return a;
	}

	@Behavior
	public static int nestedPlus(int a) {
		return a + 1 + 2 + 3 + 4 + 5;
	}

	public static int proceduralNestedPlus(int a) {
		return a + 1 + 2 + 3 + 4 + 5;
	}

	@Behavior
	public static int assignPlusIndirectVariables(int a) {
		final int b = a + 1;
		final int c = b + 2;
		final int d = c + 3;
		final int e = d + 4;

        return e + 5;
	}

	public static int proceduralAssignPlusIndirectVariables(int a) {
		int b = a + 1;
		int c = b + 2;
		int d = c + 3;
		int e = d + 4;
		int f = e + 5;

		return f;
	}

	public static int commonSubExpressionPlus(int a) {
		a = a + 1;
		int b = a + a;

		return b;
	}

	@Behavior
	public static int swappingExpressionPlus(int x) {
		int a = x;
		int b = x + 1;
		int t = a;
		a = b;
		b = t;

		return b;
	}

	public static int proceduralSwappingExpressionPlus(int x) {
		int a = x;
		int b = x + 1;
		int t = a;
		a = b;
		b = t;

		return b;
	}

	@Behavior
	public static int returnsOne() {
		return 1;
	}

	public static int proceduralReturnsOne() {
		return 1;
	}

	@Behavior
	public static int returnsZero() {
		return 0;
	}

	public static int proceduralReturnsZero() {
		return 0;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 20 verified, 0 errors
 */
