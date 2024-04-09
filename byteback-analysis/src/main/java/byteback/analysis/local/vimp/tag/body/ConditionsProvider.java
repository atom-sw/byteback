package byteback.analysis.local.vimp.tag.body;

import byteback.analysis.common.tag.TagProvider;
import soot.Body;

public abstract class ConditionsProvider<T extends ConditionsTag> extends TagProvider<Body, T> {

    public ConditionsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public abstract T compute(final Body host);

}
