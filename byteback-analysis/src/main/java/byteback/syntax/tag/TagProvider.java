package byteback.syntax.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

/**
 * Lazily computes and assigns tags to hosts.
 *
 * @param <K> The type of the hosts.
 * @param <V> The type of the tags.
 * @author paganma
 */
public abstract class TagProvider<K extends Host, V extends Tag> extends TagManager<K, V> {

    public TagProvider(final String tagName) {
        super(tagName);
    }

    /**
     * Defines how to compute a tag from a single host.
     *
     * @param host The host for which the tag is computed.
     * @return The new tag associated with the host.
     */
    public abstract V compute(final K host);

    /**
     * Fetches a tag or computes it if needed.
     *
     * @param host The host owning the tag.
     * @return The tag associated with the host.
     */
    public V getOrCompute(final K host) {
        return super.get(host)
                .orElseGet(() -> {
                    final V tag = compute(host);
                    TagProvider.this.put(host, tag);

                    return tag;
                });
    }

}
