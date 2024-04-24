package byteback.syntax.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

public abstract class LazyTagProvider<K extends Host, V extends Tag> extends TagProvider<K, V> {

    /**
     * Constructs a new TagProvider.
     *
     * @param tagName The name of the tag managed by this instance.
     */
    public LazyTagProvider(final String tagName) {
        super(tagName);
    }

    public abstract V compute();

    public V getOrCompute(final K host) {
        return get(host)
                .orElseGet(() -> {
                    final V tag = compute();
                    put(host, tag);

                    return tag;
                });
    }

}
