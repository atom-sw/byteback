package byteback.annotations;

public interface Quantifier {

	public static boolean forall(int $, boolean p) {
		return p;
	}

	public static boolean exists(int $, boolean p) {
		return p;
	}

	public static boolean forall(double $, boolean p) {
		return p;
	}

	public static boolean exists(double $, boolean p) {
		return p;
	}

	public static boolean forall(boolean $, boolean p) {
		return p;
	}

	public static boolean exists(boolean $, boolean p) {
		return p;
	}

	public static boolean forall(Object $, boolean p) {
		return p;
	}

	public static boolean exists(Object $, boolean p) {
		return p;
	}

}
