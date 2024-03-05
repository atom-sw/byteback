package byteback.analysis.common.property;

import java.util.WeakHashMap;

public abstract class Properties<A, B> {

    final WeakHashMap<A, B> instanceToProperty;

    public Properties(final WeakHashMap<A, B> instanceToProperty) {
        this.instanceToProperty = instanceToProperty;
    }

    public Properties() {
        this(new WeakHashMap<>());
    }

    protected abstract B compute(final A instance);

    public B of(final A instance) {
        B property = instanceToProperty.get(instance);

        if (property == null) {
            property = compute(instance);
            instanceToProperty.put(instance, property);
        }

        return property;
    }

}
