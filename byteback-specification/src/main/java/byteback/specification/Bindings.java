package byteback.specification;

public class Bindings {

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

	public static <T> T reference(Class<T> tClass) {
		return null;
	}

}
