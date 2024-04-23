package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.tag.*;
import soot.Local;
import soot.SootMethod;
import soot.Type;
import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class PreconditionsTagger extends ConditionsFinder<PreconditionsTag> {

    private static final Lazy<PreconditionsTagger> INSTANCE = Lazy.from(() ->
            new PreconditionsTagger(BBLibNames.REQUIRE_ANNOTATION, PreconditionsTagProvider.v()));

    private PreconditionsTagger(final String annotationDescriptor, final PreconditionsTagProvider preconditionsProvider) {
        super(annotationDescriptor, preconditionsProvider);
    }

    public static PreconditionsTagger v() {
        return INSTANCE.get();
    }

    @Override
    protected List<Type> makeBehaviorParameterTypes(final SootMethod targetMethod) {
        return new ArrayList<>(targetMethod.getParameterTypes());
    }

    @Override
    protected List<Value> makeBehaviorParameters(final SootMethod targetMethod) {
        final ParameterLocalsTag parameterLocalsTag = ParameterLocalsTagProvider.v().getOrThrow(targetMethod);
        final List<Local> parameterLocals = parameterLocalsTag.getValues();

        return new ArrayList<>(parameterLocals);
    }

}
