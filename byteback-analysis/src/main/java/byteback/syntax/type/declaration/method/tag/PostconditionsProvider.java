package byteback.syntax.type.declaration.method.tag;

import byteback.common.function.Lazy;
import soot.SootMethod;

import java.util.ArrayList;

public class PostconditionsProvider extends ConditionsProvider<PostconditionsTag> {

    private static final Lazy<PostconditionsProvider> INSTANCE =
            Lazy.from(()  -> new PostconditionsProvider(PostconditionsTag.NAME));

    public static PostconditionsProvider v() {
        return INSTANCE.get();
    }

    private PostconditionsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public PostconditionsTag compute(final SootMethod sootMethod) {
        return new PostconditionsTag(new ArrayList<>());
    }

}