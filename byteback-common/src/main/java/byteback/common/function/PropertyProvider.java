package byteback.common.function;

import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * Base class for a lazy weak property provider.
 *
 * @param <A> Instance owning the property.
 * @param <B> Type of the property.
 * @author paganma
 */
public abstract class PropertyProvider<A, B> implements Function<A, B> {

	final WeakHashMap<A, B> instanceToProperty;

	public PropertyProvider(final WeakHashMap<A, B> instanceToProperty) {
		this.instanceToProperty = instanceToProperty;
	}

	public PropertyProvider() {
		this(new WeakHashMap<>());
	}

	protected abstract B compute(final A instance);

	public B apply(final A instance) {
		B property = instanceToProperty.get(instance);

		if (property == null) {
			property = compute(instance);
			instanceToProperty.put(instance, property);
		}

		return property;
	}

}
