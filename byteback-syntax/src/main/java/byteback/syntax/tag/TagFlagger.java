package byteback.syntax.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

/**
 * Flags a host with a single given tag.
 *
 * @param <K> The type of the hosts.
 * @param <V> The type of the tags.
 * @author paganma
 */
public abstract class TagFlagger<K extends Host, V extends Tag> extends TagProvider<K, V> {

    private final V tag;

    /**
     * Constructs a new TagFlagger.
     *
     * @param tag The tag used to flag the hosts.
     */
    public TagFlagger(final V tag) {
        super(tag.getName());
        this.tag = tag;
    }

    /**
     * Flags the host with the given `tag`, unless it was already flagged.
     *
     * @param host The host to be flagged.
     */
    public void flag(final K host) {
        if (!host.hasTag(tagName)) {
            put(host, tag);
        }
    }

}
