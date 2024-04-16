package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTag;
import byteback.common.function.Lazy;
import soot.*;

import java.util.ArrayList;
import java.util.List;

public class PreconditionsFinder extends ConditionsFinder<PreconditionsTag> {

    private static final Lazy<PreconditionsFinder> INSTANCE = Lazy.from(() ->
            new PreconditionsFinder(BBLibNames.REQUIRE_ANNOTATION, PreconditionsProvider.v()));

    private PreconditionsFinder(final String annotationDescriptor, final PreconditionsProvider preconditionsProvider) {
        super(annotationDescriptor, preconditionsProvider);
    }

    public static PreconditionsFinder v() {
        return INSTANCE.get();
    }

    @Override
    protected List<Type> makeBehaviorParameterTypes(final SootMethod targetMethod) {
        return new ArrayList<>(targetMethod.getParameterTypes());
    }

    @Override
    protected List<Value> makeBehaviorParameters(final SootMethod targetMethod) {
        final ParameterLocalsTag parameterLocalsTag = ParameterLocalsProvider.v().compute(targetMethod);
        final List<Local> parameterLocals = parameterLocalsTag.getValues();

        return new ArrayList<>(parameterLocals);
    }

}
