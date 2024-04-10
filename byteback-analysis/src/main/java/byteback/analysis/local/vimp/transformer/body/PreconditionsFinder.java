package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.common.name.BBLibNames;
import byteback.analysis.global.vimp.tag.PreconditionsProvider;
import byteback.analysis.global.vimp.tag.PreconditionsTag;
import byteback.common.function.Lazy;
import soot.*;

import java.util.ArrayList;
import java.util.List;

public class PreconditionsFinder extends ConditionsFinder<PreconditionsTag> {

    private static final Lazy<PreconditionsFinder> instance = Lazy.from(() ->
            new PreconditionsFinder(BBLibNames.REQUIRE_ANNOTATION, PreconditionsProvider.v()));

    private PreconditionsFinder(final String annotationDescriptor,
                                final PreconditionsProvider preconditionsProvider) {

        super(annotationDescriptor, preconditionsProvider);
    }

    public static PreconditionsFinder v() {
        return instance.get();
    }

    @Override
    protected List<Type> makeBehaviorParameterTypes(final Body body) {
        return new ArrayList<>(body.getMethod().getParameterTypes());
    }

    @Override
    protected List<Value> makeBehaviorParameters(final Body body) {
        return new ArrayList<Value>(body.getParameterLocals());
    }

}
