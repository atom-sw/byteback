package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnRef;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import soot.*;

import java.util.ArrayList;
import java.util.List;

public class PostconditionsTagger extends ConditionsFinder<PostconditionsTag> {

    private static final Lazy<PostconditionsTagger> INSTANCE = Lazy.from(() ->
            new PostconditionsTagger(BBLibNames.ENSURE_ANNOTATION, PostconditionsTagProvider.v()));

    public static PostconditionsTagger v() {
        return INSTANCE.get();
    }

    private PostconditionsTagger(final String annotationDescriptor,
                                 final PostconditionsTagProvider postConditionsProvider) {

        super(annotationDescriptor, postConditionsProvider);
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
        final ParameterLocalsTag parameterLocalsTag = ParameterLocalsTagProvider.v().getOrThrow(targetMethod);
        final List<Local> parameterLocals = parameterLocalsTag.getValues();
        final var behaviorParameters = new ArrayList<Value>(parameterLocals);
        final Type returnType = targetMethod.getReturnType();

        if (returnType != VoidType.v()) {
            final ReturnRef returnRef = Vimp.v().newReturnRef(returnType);
            behaviorParameters.add(returnRef);
        }

        return behaviorParameters;
    }

}
