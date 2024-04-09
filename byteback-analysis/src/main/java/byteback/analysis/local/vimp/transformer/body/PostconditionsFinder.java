package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.common.name.BBLibNames;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.analysis.local.vimp.tag.body.PostconditionsProvider;
import byteback.analysis.local.vimp.tag.body.PostconditionsTag;
import byteback.common.function.Lazy;
import soot.*;

import java.util.ArrayList;
import java.util.List;

public class PostconditionsFinder extends ConditionsFinder<PostconditionsTag> {

    private static final Lazy<PostconditionsFinder> instance = Lazy.from(() ->
            new PostconditionsFinder(BBLibNames.ENSURE_ANNOTATION, PostconditionsProvider.v()));

    public static PostconditionsFinder v() {
        return instance.get();
    }

    private PostconditionsFinder(final String annotationDescriptor,
                                 final PostconditionsProvider postconditionsProvider) {

        super(annotationDescriptor, postconditionsProvider);
    }

    @Override
    protected List<Type> makeBehaviorParameterTypes(final Body body) {
        final SootMethod targetMethod = body.getMethod();
        final var behaviorParameterTypes = new ArrayList<>(targetMethod.getParameterTypes());

        if (targetMethod.getReturnType() != VoidType.v()) {
            behaviorParameterTypes.add(targetMethod.getReturnType());
        }

        return behaviorParameterTypes;
    }

    @Override
    protected List<Value> makeBehaviorParameters(final Body body) {
        final SootMethod targetMethod = body.getMethod();
        final var behaviorParameters = new ArrayList<Value>(body.getParameterLocals());
        final Type returnType = targetMethod.getReturnType();

        if (returnType != VoidType.v()) {
            behaviorParameters.add(Vimp.v().newReturnRef(returnType));
        }

        return behaviorParameters;
    }

}
