package byteback.analysis.common.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

public abstract class LazyTagger<K extends Host, V extends Tag> {

    public abstract String getTagName();

    protected abstract V compute(Host host);

    @SuppressWarnings("unchecked")
    public V get(final Host host) {
        Tag tag = host.getTag(getTagName());

        if (tag == null) {
            tag = compute(host);
        }

        host.addTag(tag);

        // This unchecked cast is necessary. If the type of the tag is not `V` it means that the tag corresponding to
        // the name specified in `getTagName()` has a type that is not a subclass of `V`.
        return (V) tag;
    }

    public V

}
