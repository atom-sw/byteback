package byteback.annotations;

public class Binding {

	static int state = 0;

	public static int integer() {
		return state++;
	}

	public static double real() {
		return 0.0;
	}

	public static boolean bool() {
		return false;
	}

	public static Object reference() {
		return null;
	}

}
