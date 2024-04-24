package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.tag.*;
import soot.*;
import soot.util.NumberedString;

public class PreconditionResolver extends BehaviorResolver<PreconditionsTag> {

    private static final Lazy<PreconditionResolver> INSTANCE = Lazy.from(() ->
            new PreconditionResolver(PreconditionsTagProvider.v()));

    private PreconditionResolver(final PreconditionsTagProvider preconditionsProvider) {
        super(preconditionsProvider);
    }

    public static PreconditionResolver v() {
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

}
