package byteback.analysis.global.vimp.tag;

import byteback.analysis.common.tag.TagProvider;
import soot.SootMethod;

public abstract class ConditionsProvider<T extends ConditionsTag> extends TagProvider<SootMethod, T> {

    public ConditionsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public abstract T compute(final SootMethod sootMethod);

}
