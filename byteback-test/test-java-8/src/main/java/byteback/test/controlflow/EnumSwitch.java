package byteback.test.controlflow;

public class EnumSwitch {

	public enum Fruit {
		BANANA, APPLE, ORANGE, GRAPES
	}

	public static int enumSwitch(final Fruit a) {
		int b;

		switch (a) {
			case BANANA:
				b = 1;
				break;
			case APPLE:
				b = 2;
			case ORANGE:
				b = 3;
				break;
			default :
				b = 0;
		}

		return b;
	}

}
