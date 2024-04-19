package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.tag.AnnotationReader;
import byteback.syntax.tag.TagReader;
import soot.SootMethod;

public class OperatorFlagger extends TagReader<SootMethod, OperatorTag> {

    private static final Lazy<OperatorFlagger> INSTANCE =
            Lazy.from(() -> new OperatorFlagger(PreludeDefinitionTag.NAME));

    public static OperatorFlagger v() {
        return INSTANCE.get();
    }

    private OperatorFlagger(final String tagName) {
        super(tagName);
    }

    @Override
    public boolean isTagged(final SootMethod sootMethod) {
        return AnnotationReader.v().hasAnnotation(sootMethod, BBLibNames.OPERATOR_ANNOTATION);
    }

}