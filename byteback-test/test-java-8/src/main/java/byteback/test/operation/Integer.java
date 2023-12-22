/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.operation;

import static byteback.annotations.Operator.eq;
import static byteback.annotations.Operator.not;

import byteback.annotations.Contract.Predicate;
import byteback.annotations.Contract.Pure;

public class Integer {

	@Pure
	@Predicate
	public static int addition(int a, int b) {
		return a + b;
	}

	public static int proceduralAddition(int a, int b) {
		return a + b;
	}

	@Pure
	@Predicate
	public static int subtraction(int a, int b) {
		return a - b;
	}

	public static int proceduralSubtraction(int a, int b) {
		return a - b;
	}

	@Pure
	@Predicate
	public static int minus(int a) {
		return -a;
	}

	public static int proceduralMinus(int a) {
		return -a;
	}

	@Pure
	@Predicate
	public static int multiplication(int a, int b) {
		return a * b;
	}

	public static int proceduralMultiplication(int a, int b) {
		return a * b;
	}

	@Pure
	@Predicate
	public static int division(int a, int b) {
		return a / b;
	}

	public static int proceduralDivision(int a, int b) {
		return a / b;
	}

	@Pure
	@Predicate
	public static int modulo(int a, int b) {
		return a % b;
	}

	public static int proceduralModulo(int a, int b) {
		return a % b;
	}

	@Pure
	@Predicate
	public static int square(int a) {
		return a * a;
	}

	public static int proceduralSquare(int a) {
		return a * a;
	}

	@Pure
	@Predicate
	public static int squareArea(int a) {
		return square(a);
	}

	public static int proceduralSquareArea(int a) {
		return square(a);
	}

	@Pure
	@Predicate
	public static int rectangleArea(int a, int b) {
		return multiplication(a, b);
	}

	public static int proceduralRectangleArea(int a, int b) {
		return multiplication(a, b);
	}

	@Pure
	@Predicate
	public static boolean even(int a) {
		return eq(a % 2, 0);
	}

	public static boolean proceduralEven(int a) {
		return eq(a % 2, 0);
	}

	@Pure
	@Predicate
	public static boolean odd(int a) {
		return not(even(a));
	}

	public static boolean proceduralOdd(int a) {
		return not(even(a));
	}

	@Pure
	@Predicate
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

	@Pure
	@Predicate
	public static int assignPlus(int a) {
		a = a + 1;

		return a;
	}

	public static int proceduralAssignPlus(int a) {
		a = a + 1;

		return a;
	}

	@Pure
	@Predicate
	public static int nestedPlus(int a) {
		return a + 1 + 2 + 3 + 4 + 5;
	}

	public static int proceduralNestedPlus(int a) {
		return a + 1 + 2 + 3 + 4 + 5;
	}

	@Pure
	@Predicate
	public static int assignPlusIndirectVariables(int a) {
		int b = a + 1;
		int c = b + 2;
		int d = c + 3;
		int e = d + 4;
		int f = e + 5;

		return f;
	}

	public static int proceduralAssignPlusIndirectVariables(int a) {
		int b = a + 1;
		int c = b + 2;
		int d = c + 3;
		int e = d + 4;
		int f = e + 5;

		return f;
	}

	@Pure
	@Predicate
	public static int commonSubExpressionPlus(int a) {
		a = a + 1;
		int b = a + a;

		return b;
	}

	public static int proceduralCommonSubExpressionPlus(int a) {
		a = a + 1;
		int b = a + a;

		return b;
	}

	@Pure
	@Predicate
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

	@Pure
	@Predicate
	public static int returnsOne() {
		return 1;
	}

	public static int proceduralReturnsOne() {
		return 1;
	}

	@Pure
	@Predicate
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
