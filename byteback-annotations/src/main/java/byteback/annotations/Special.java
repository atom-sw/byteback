package byteback.annotations;

public class Special {

	public static boolean old(final boolean value) {
		return value;
	}

	public static byte old(final byte value) {
		return value;
	}

	public static short old(final short value) {
		return value;
	}

	public static int old(final int value) {
		return value;
	}

	public static long old(final long value) {
		return value;
	}

	public static float old(final float value) {
		return value;
	}

	public static double old(final double value) {
		return value;
	}

	public static <T> T old(final T value) {
		return value;
	}

	public static boolean conditional(final boolean condition, final boolean thenValue, final boolean elseValue) {
		if (condition) {
			return thenValue;
		} else {
			return elseValue;
		}
	}

	public static byte conditional(final boolean condition, final byte thenValue, final byte elseValue) {
		if (condition) {
			return thenValue;
		} else {
			return elseValue;
		}
	}

	public static short conditional(final boolean condition, final short thenValue, final short elseValue) {
		if (condition) {
			return thenValue;
		} else {
			return elseValue;
		}
	}

	public static int conditional(final boolean condition, final int thenValue, final int elseValue) {
		if (condition) {
			return thenValue;
		} else {
			return elseValue;
		}
	}

	public static long conditional(final boolean condition, final long thenValue, final long elseValue) {
		if (condition) {
			return thenValue;
		} else {
			return elseValue;
		}
	}

	public static float conditional(final boolean condition, final float thenValue, final float elseValue) {
		if (condition) {
			return thenValue;
		} else {
			return elseValue;
		}
	}

	public static double conditional(final boolean condition, final double thenValue, final double elseValue) {
		if (condition) {
			return thenValue;
		} else {
			return elseValue;
		}
	}

	public static <T> T conditional(final boolean condition, final T thenValue, final T elseValue) {
		if (condition) {
			return thenValue;
		} else {
			return elseValue;
		}
	}

}
