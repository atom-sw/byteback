package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import soot.*;
import soot.util.NumberedString;

public class PreBehaviorResolver extends BehaviorResolver {

    private static final Lazy<PreBehaviorResolver> INSTANCE = Lazy.from(PreBehaviorResolver::new);

    public static PreBehaviorResolver v() {
        return INSTANCE.get();
    }

    @Override
    protected NumberedString makeBehaviorSignature(final SootMethod targetMethod, final String behaviorName) {
        final var behaviorSignature = new MethodSubSignature(
                behaviorName,
                BooleanType.v(),
                targetMethod.getParameterTypes()
        );

        return behaviorSignature.numberedSubSig;
    }

    @Override
    protected Value makeBehaviorExpr(final SootMethod targetMethod, final SootMethod behaviorMethod) {
        return null;
    }

}
