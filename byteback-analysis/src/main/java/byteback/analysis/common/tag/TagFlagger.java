package byteback.analysis.common.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

/**
 *
 * @param <K>
 * @param <V>
 */
public abstract class TagFlagger<K extends Host, V extends Tag> extends TagManager<K, V> {

    private final V tag;

    public TagFlagger(final V tag) {
        super(tag.getName());
        this.tag = tag;
    }

    public void flag(final K host) {
        if (!host.hasTag(tagName)) {
            put(host, tag);
        }
    }

}
