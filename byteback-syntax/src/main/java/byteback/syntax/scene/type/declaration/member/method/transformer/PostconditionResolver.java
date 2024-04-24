package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import soot.*;
import soot.util.NumberedString;

import java.util.ArrayList;

public class PostconditionResolver extends BehaviorResolver<PostconditionsTag> {

    private static final Lazy<PostconditionResolver> INSTANCE = Lazy.from(() ->
            new PostconditionResolver(PostconditionsTagProvider.v()));

    public static PostconditionResolver v() {
        return INSTANCE.get();
    }

    private PostconditionResolver(final PostconditionsTagProvider postConditionsProvider) {

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

}
