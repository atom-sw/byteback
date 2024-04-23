package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.syntax.tag.LazyTagProvider;
import soot.SootMethod;

public abstract class ConditionsTagProvider<T extends ConditionsTag> extends LazyTagProvider<SootMethod, T> {

    public ConditionsTagProvider(final String tagName) {
        super(tagName);
    }

}
