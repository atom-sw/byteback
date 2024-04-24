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
import soot.util.NumberedString;

import java.util.ArrayList;
import java.util.List;

public class PostconditionsResolver extends ConditionsResolver<PostconditionsTag> {

    private static final Lazy<PostconditionsResolver> INSTANCE = Lazy.from(() ->
            new PostconditionsResolver(PostconditionsTagProvider.v()));

    public static PostconditionsResolver v() {
        return INSTANCE.get();
    }

    private PostconditionsResolver(final PostconditionsTagProvider postConditionsProvider) {

        super(postConditionsProvider);
    }

    @Override
    protected NumberedString makeBehaviorSignature(final SootMethod targetMethod, final String behaviorName) {
        final var behaviorParameterTypes = new ArrayList<>(targetMethod.getParameterTypes());

        if (targetMethod.getReturnType() != VoidType.v()) {
            behaviorParameterTypes.add(targetMethod.getReturnType());
        }

        final var behaviorSignature = new MethodSubSignature(
                behaviorName,
                BooleanType.v(),
                behaviorParameterTypes
        );

        return behaviorSignature.numberedSubSig;
    }

    @Override
    protected Value makeConditionValue(SootMethod targetMethod, SootMethod behaviorMethod) {
        final ParameterLocalsTag parameterLocalsTag = ParameterLocalsTagProvider.v().getOrThrow(targetMethod);
        final List<Local> parameterLocals = parameterLocalsTag.getValues();
        final var behaviorParameters = new ArrayList<Value>(parameterLocals);
        final Type returnType = targetMethod.getReturnType();

        if (returnType != VoidType.v()) {
            final ReturnRef returnRef = Vimp.v().newReturnRef(returnType);
            behaviorParameters.add(returnRef);
        }

        return Vimp.v().newCallExpr(behaviorMethod.makeRef(), behaviorParameters);
    }

}
