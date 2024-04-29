package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.tag.InputsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.InputsTagAccessor;
import soot.*;
import soot.util.NumberedString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        final InputsTag inputsTag = InputsTagAccessor.v().getOrThrow(targetMethod);
        final var inputs = new ArrayList<Value>(inputsTag.getInputRefs());

        if (targetMethod.getReturnType() != VoidType.v()) {
            inputs.add(0, Vimp.v().newReturnRef(targetMethod.getReturnType()));
        }

        return Vimp.v().newCallExpr(behaviorMethod.makeRef(), inputs);
    }

}
