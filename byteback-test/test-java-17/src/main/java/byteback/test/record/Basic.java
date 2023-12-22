package byteback.test.record;

public class Basic {

	public record Point(int x, int y) {

		@Override
		public String toString() {
			return null;
		}

		@Override
		public int hashCode() {
			return 0;
		}

	};

}
