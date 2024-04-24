package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.CallExpr;
import byteback.syntax.scene.type.declaration.member.method.tag.*;
import soot.*;
import soot.util.NumberedString;

import java.util.ArrayList;
import java.util.List;

public class PreconditionsResolver extends ConditionsResolver<PreconditionsTag> {

    private static final Lazy<PreconditionsResolver> INSTANCE = Lazy.from(() ->
            new PreconditionsResolver(PreconditionsTagProvider.v()));

    private PreconditionsResolver(final PreconditionsTagProvider preconditionsProvider) {
        super(preconditionsProvider);
    }

    public static PreconditionsResolver v() {
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
    protected Value makeConditionValue(final SootMethod targetMethod, final SootMethod behaviorMethod) {
        final ParameterLocalsTag parameterLocalsTag = ParameterLocalsTagProvider.v().getOrThrow(targetMethod);
        final var parameterLocals = new ArrayList<Value>(parameterLocalsTag.getValues());

        return Vimp.v().newCallExpr(behaviorMethod.makeRef(), parameterLocals);
    }
}
