package byteback.syntax.type.declaration.method.body.transformer;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.type.declaration.method.tag.PreconditionsProvider;
import byteback.syntax.type.declaration.method.tag.PreconditionsTag;
import byteback.common.function.Lazy;
import soot.*;

import java.util.ArrayList;
import java.util.List;

public class PreconditionsFinder extends ConditionsFinder<PreconditionsTag> {

    private static final Lazy<PreconditionsFinder> INSTANCE = Lazy.from(() ->
            new PreconditionsFinder(BBLibNames.REQUIRE_ANNOTATION, PreconditionsProvider.v()));

    private PreconditionsFinder(final String annotationDescriptor,
                                final PreconditionsProvider preconditionsProvider) {

        super(annotationDescriptor, preconditionsProvider);
    }

    public static PreconditionsFinder v() {
        return INSTANCE.get();
    }

    @Override
    protected List<Type> makeBehaviorParameterTypes(final Body body) {
        return new ArrayList<>(body.getMethod().getParameterTypes());
    }

    @Override
    protected List<Value> makeBehaviorParameters(final Body body) {
        return new ArrayList<>(body.getParameterLocals());
    }

}
