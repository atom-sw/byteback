package byteback.common;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private T value;

    private Lazy(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Lazy<T> from(final Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    public static <T> Lazy<T> empty() {
        return new Lazy<T>(() -> {
            throw new IllegalStateException("Unable to initialize value");
        });
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
