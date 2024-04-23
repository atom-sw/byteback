package byteback.specification;

import byteback.specification.Contract.Prelude;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Operator;

/**
 * Utilities to aid the formulation of complex boolean expressions.
 *
 * Note that being defined as static functions, none of these operations are
 * short-circuiting. For this reason, using them outside ByteBack might not
 * be ideal.
 */
public class Operators {

	/**
	 * Boolean implication.
	 */
	@Operator
	@Behavior
	@Prelude("implies")
	public static boolean implies(final boolean a, final boolean b) {
		return !a || b;
	}

	/**
	 * Boolean equivalence.
	 */
	@Operator
	@Behavior
	@Prelude("iff")
	public static boolean iff(final boolean a, final boolean b) {
		return a == b;
	}

	/**
	 * Boolean AND.
	 */
	@Operator
	@Behavior
	@Prelude("and")
	public static boolean and(final boolean a, final boolean b) {
		return a && b;
	}

	/**
	 * Boolean OR.
	 */
	@Operator
	@Behavior
	@Prelude("or")
	public static boolean or(final boolean a, final boolean b) {
		return a || b;
	}

	/**
	 * Boolean NOT.
	 */
	@Operator
	@Behavior
	@Prelude("not")
	public static boolean not(final boolean a) {
		return !a;
	}

	/**
	 * Equality.
	 */
	@Operator
	@Behavior
	@Prelude("eq")
	public static boolean eq(final Object a, final Object b) {
		return a.equals(b);
	}

	@Operator
	@Behavior
	@Prelude("eq")
	public static boolean eq(final boolean a, final boolean b) {
		return a == b;
	}

	@Operator
	@Behavior
	@Prelude("eq")
	public static boolean eq(final byte a, final byte b) {
		return a == b;
	}

	@Operator
	@Behavior
	@Prelude("eq")
	public static boolean eq(final short a, final short b) {
		return a == b;
	}

	@Operator
	@Behavior
	@Prelude("eq")
	public static boolean eq(final int a, final int b) {
		return a == b;
	}

	@Operator
	@Behavior
	@Prelude("eq")
	public static boolean eq(final long a, final long b) {
		return a == b;
	}

	@Operator
	@Behavior
	@Prelude("eq")
	public static boolean eq(final char a, final char b) {
		return a == b;
	}

	@Operator
	@Behavior
	@Prelude("eq")
	public static boolean eq(final float a, final float b) {
		return a == b;
	}

	@Operator
	@Behavior
	@Prelude("eq")
	public static boolean eq(final double a, final double b) {
		return a == b;
	}

	/**
	 * Inequality
	 */
	@Operator
	@Behavior
	@Prelude("neq")
	public static boolean neq(final Object a, final Object b) {
		return !a.equals(b);
	}

	@Operator
	@Behavior
	@Prelude("neq")
	public static boolean neq(final boolean a, final boolean b) {
		return a != b;
	}

	@Operator
	@Behavior
	@Prelude("neq")
	public static boolean neq(final byte a, final byte b) {
		return a != b;
	}

	@Operator
	@Behavior
	@Prelude("neq")
	public static boolean neq(final short a, final short b) {
		return a != b;
	}

	@Operator
	@Behavior
	@Prelude("neq")
	public static boolean neq(final int a, final int b) {
		return a != b;
	}

	@Operator
	@Behavior
	@Prelude("neq")
	public static boolean neq(final long a, final long b) {
		return a != b;
	}

	@Operator
	@Behavior
	@Prelude("neq")
	public static boolean neq(final char a, final char b) {
		return a != b;
	}

	@Operator
	@Behavior
	@Prelude("neq")
	public static boolean neq(final float a, final float b) {
		return a != b;
	}

	@Operator
	@Behavior
	@Prelude("neq")
	public static boolean neq(final double a, final double b) {
		return a != b;
	}

	/**
	 * Less-than
	 */

	@Operator
	@Behavior
	@Prelude("int.lt")
	public static boolean lt(final byte a, final byte b) {
		return a < b;
	}

	@Operator
	@Behavior
	@Prelude("int.lt")
	public static boolean lt(final short a, final short b) {
		return a < b;
	}

	@Operator
	@Behavior
	@Prelude("int.lt")
	public static boolean lt(final int a, final int b) {
		return a < b;
	}

	@Operator
	@Behavior
	@Prelude("int.lt")
	public static boolean lt(final long a, final long b) {
		return a < b;
	}

	@Operator
	@Behavior
	@Prelude("real.lt")
	public static boolean lt(final float a, final float b) {
		return a < b;
	}

	@Operator
	@Behavior
	@Prelude("real.lt")
	public static boolean lt(final double a, final double b) {
		return a < b;
	}

	/**
	 * Less-than or equal
	 */
	@Operator
	@Behavior
	@Prelude("int.lte")
	public static boolean lte(final byte a, final byte b) {
		return a <= b;
	}

	@Operator
	@Behavior
	@Prelude("int.lte")
	public static boolean lte(final short a, final short b) {
		return a <= b;
	}

	@Operator
	@Behavior
	@Prelude("int.lte")
	public static boolean lte(final int a, final int b) {
		return a <= b;
	}

	@Operator
	@Behavior
	@Prelude("int.lte")
	public static boolean lte(final long a, final long b) {
		return a <= b;
	}

	@Operator
	@Behavior
	@Prelude("real.lte")
	public static boolean lte(final float a, final float b) {
		return a <= b;
	}

	@Operator
	@Behavior
	@Prelude("real.lte")
	public static boolean lte(final double a, final double b) {
		return a <= b;
	}

	/**
	 * Greater-than
	 */
	@Operator
	@Behavior
	@Prelude("int.gt")
	public static boolean gt(final byte a, final byte b) {
		return a > b;
	}

	@Operator
	@Behavior
	@Prelude("int.gt")
	public static boolean gt(final short a, final short b) {
		return a > b;
	}

	@Operator
	@Behavior
	@Prelude("int.gt")
	public static boolean gt(final int a, final int b) {
		return a > b;
	}

	@Operator
	@Behavior
	@Prelude("int.gt")
	public static boolean gt(final long a, final long b) {
		return a > b;
	}

	@Operator
	@Behavior
	@Prelude("real.gt")
	public static boolean gt(final float a, final float b) {
		return a > b;
	}

	@Operator
	@Behavior
	@Prelude("real.gt")
	public static boolean gt(final double a, final double b) {
		return a > b;
	}

	/**
	 * Greater-than or equal
	 */
	@Operator
	@Behavior
	@Prelude("int.gte")
	public static boolean gte(final byte a, final byte b) {
		return a >= b;
	}

	@Operator
	@Behavior
	@Prelude("int.gte")
	public static boolean gte(final short a, final short b) {
		return a >= b;
	}

	@Operator
	@Behavior
	@Prelude("int.gte")
	public static boolean gte(final int a, final int b) {
		return a >= b;
	}

	@Operator
	@Behavior
	@Prelude("int.gte")
	public static boolean gte(final long a, final long b) {
		return a >= b;
	}

	@Operator
	@Behavior
	@Prelude("real.gte")
	public static boolean gte(final float a, final float b) {
		return a >= b;
	}

	@Operator
	@Behavior
	@Prelude("real.gte")
	public static boolean gte(final double a, final double b) {
		return a >= b;
	}

}
