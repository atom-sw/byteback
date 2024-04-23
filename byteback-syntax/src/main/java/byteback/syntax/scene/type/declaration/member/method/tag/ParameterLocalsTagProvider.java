package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagProvider;
import soot.SootMethod;

public class ParameterLocalsTagProvider extends TagProvider<SootMethod, ParameterLocalsTag> {

    private static final Lazy<ParameterLocalsTagProvider> INSTANCE =
            Lazy.from(() -> new ParameterLocalsTagProvider(ParameterLocalsTag.NAME));

    public static ParameterLocalsTagProvider v() {
        return INSTANCE.get();
    }

    private ParameterLocalsTagProvider(final String tagName) {
        super(tagName);
    }

}