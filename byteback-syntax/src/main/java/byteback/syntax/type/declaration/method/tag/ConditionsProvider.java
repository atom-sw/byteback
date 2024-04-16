package byteback.syntax.type.declaration.method.tag;

import byteback.syntax.tag.TagProvider;
import soot.SootMethod;

public abstract class ConditionsProvider<T extends ConditionsTag> extends TagProvider<SootMethod, T> {

    public ConditionsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public abstract T compute(final SootMethod sootMethod);

}
