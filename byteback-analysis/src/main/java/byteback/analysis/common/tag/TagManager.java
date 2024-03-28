package byteback.analysis.common.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

public abstract class TagManager<K extends Host, V extends Tag> extends TagReader<K, V> {

    public TagManager(final String tagName) {
        super(tagName);
    }

    public void put(final K host, final V tag) {
        if (tag.getName().equals(tagName)) {
            host.addTag(tag);
        } else {
            throw new IllegalArgumentException("Wrong name for tag: " + tag.getName());
        }
    }

}
