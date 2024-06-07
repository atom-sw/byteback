package byteback.common.function;

import java.util.function.Supplier;

/**
 * A lazy supplier.
 *
 * @param <T> The type of the value supplied.
 * @author paganma
 */
public class Lazy<T> implements Supplier<T> {

	private final Supplier<T> supplier;

	private T value;

	private Lazy(final Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public static <T> Lazy<T> from(final Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}

	public synchronized void invalidate() {
		value = null;
	}

	public synchronized T get() {
		if (value != null) {
			return value;
		} else {
			return initialize();
		}
	}

	private T initialize() {
		value = supplier.get();

		return value;
	}

}
