package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
import soot.util.NumberedString;

import java.util.ArrayList;

public class PostBehaviorResolver extends BehaviorResolver {

    private static final Lazy<PostBehaviorResolver> INSTANCE = Lazy.from(PostBehaviorResolver::new);

    public static PostBehaviorResolver v() {
        return INSTANCE.get();
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
    protected Value makeBehaviorExpr(final SootMethod targetMethod, final SootMethod behaviorMethod) {
        for (int i = 0; i < targetMethod.getParameterCount(); ++i) {
            final ParameterRef parameterRef = Jimple.v().newParameterRef();
        }
    }

}
