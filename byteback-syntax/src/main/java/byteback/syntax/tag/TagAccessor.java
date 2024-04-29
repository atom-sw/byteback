package byteback.syntax.tag;

import soot.Value;
import soot.tagkit.Host;
import soot.tagkit.Tag;

import java.util.Optional;
import java.util.function.Supplier;

public class TagAccessor<K extends Host, V extends Tag> extends TagReader<K, V> {

    /**
     * Constructs a new TagReader.
     *
     * @param tagName The name of the tag read by this instance.
     */
    public TagAccessor(String tagName) {
        super(tagName);
    }

    public void put(final K host, final V tag) {
        host.addTag(tag);
    }

    public V putIfAbsent(final K host, final Supplier<V> tagSupplier) {
        return get(host).orElseGet(() -> {
            final V tag = tagSupplier.get();
            put(host, tag);

            return tag;
        });
    }

}
