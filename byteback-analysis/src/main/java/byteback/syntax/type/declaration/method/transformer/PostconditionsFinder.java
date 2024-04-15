package byteback.syntax.type.declaration.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.Vimp;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.type.declaration.method.tag.ParameterLocalsProvider;
import byteback.syntax.type.declaration.method.tag.ParameterLocalsTag;
import byteback.syntax.type.declaration.method.tag.PostconditionsProvider;
import byteback.syntax.type.declaration.method.tag.PostconditionsTag;
import soot.*;

import java.util.ArrayList;
import java.util.List;

public class PostconditionsFinder extends ConditionsFinder<PostconditionsTag> {

    private static final Lazy<PostconditionsFinder> INSTANCE = Lazy.from(() ->
            new PostconditionsFinder(BBLibNames.ENSURE_ANNOTATION, PostconditionsProvider.v()));

    public static PostconditionsFinder v() {
        return INSTANCE.get();
    }

    private PostconditionsFinder(final String annotationDescriptor,
                                 final PostconditionsProvider postconditionsProvider) {

        super(annotationDescriptor, postconditionsProvider);
    }

    @Override
    protected List<Type> makeBehaviorParameterTypes(final SootMethod targetMethod) {
        final var behaviorParameterTypes = new ArrayList<>(targetMethod.getParameterTypes());

        if (targetMethod.getReturnType() != VoidType.v()) {
            behaviorParameterTypes.add(targetMethod.getReturnType());
        }

        return behaviorParameterTypes;
    }

    @Override
    protected List<Value> makeBehaviorParameters(final SootMethod targetMethod) {
        final ParameterLocalsTag parameterLocalsTag = ParameterLocalsProvider.v().getOrCompute(targetMethod);
        final List<Local> parameterLocals = parameterLocalsTag.getValues();
        final var behaviorParameters = new ArrayList<Value>(parameterLocals);
        final Type returnType = targetMethod.getReturnType();

        if (returnType != VoidType.v()) {
            behaviorParameters.add(Vimp.v().newReturnRef(returnType));
        }

        return behaviorParameters;
    }

}
