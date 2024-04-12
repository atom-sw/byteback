package byteback.syntax.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

import java.util.Optional;

/**
 * Reads a tag from a host.
 *
 * @param <K> The type of the hosts.
 * @param <V> The type of the tags.
 * @author paganma
 */
public abstract class TagReader<K extends Host, V extends Tag> {


    protected final String tagName;

    public TagReader(final String tagName) {
        this.tagName = tagName;
    }

    @SuppressWarnings("unchecked")
    public Optional<V> get(final K host) {
        // This unchecked cast is necessary. If the type of the tag is not `V` it means that the tag corresponding to
        // the name specified in `getTagName()` has a type that is not a subclass of `V`.
        return (Optional<V>) Optional.ofNullable(host.getTag(tagName));
    }

    public boolean isTagged(final Host host) {
        return host.hasTag(tagName);
    }

}