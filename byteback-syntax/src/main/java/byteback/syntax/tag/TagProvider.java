package byteback.syntax.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

/**
 * Manages tags in a given host.
 *
 * @param <K> The type of the hosts.
 * @param <V> The type of the tags.
 * @author paganma
 */
public abstract class TagProvider<K extends Host, V extends Tag> extends TagReader<K, V> {

    /**
     * Constructs a new TagManager.
     *
     * @param tagName The name of the tag managed by this instance.
     */
    public TagProvider(final String tagName) {
        super(tagName);
    }

    /**
     * Adds or replaces a tag of a host.
     *
     * @param host The host owning the tags.
     * @param tag  The tag to be updated within the tags owned by the host.
     */
    public void put(final K host, final V tag) {
        final String actualTagName = tag.getName();

        if (actualTagName.equals(tagName)) {
            host.removeTag(tagName);
            host.addTag(tag);
        } else {
            throw new IllegalArgumentException(
                    "Wrong name for tag: "
                            + tag.getName() + ". "
                            + "Check that this tag manager is appropriately configured to deal with tags of type "
                            + tag.getClass()
            );
        }
    }

}
