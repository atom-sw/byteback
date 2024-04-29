package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.tag.InputsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.InputsTagAccessor;
import soot.*;
import soot.util.NumberedString;

import java.util.Collections;
import java.util.List;

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
        final InputsTag inputsTag = InputsTagAccessor.v().getOrThrow(targetMethod);
        final List<Value> inputs = Collections.unmodifiableList(inputsTag.getInputRefs());

        return Vimp.v().newCallExpr(targetMethod.makeRef(), inputs);
    }

}
