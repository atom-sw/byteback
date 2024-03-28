package byteback.analysis.common.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

public abstract class TagProvider<K extends Host, V extends Tag> extends TagManager<K, V> {

    public TagProvider(final String tagName) {
        super(tagName);
    }

    public abstract V compute(final K host);

    public V getOrCompute(final K host) {
        return super.get(host)
                .orElseGet(() -> {
                    final V tag = compute(host);
                    TagProvider.this.put(host, tag);

                    return tag;
                });
    }

}
