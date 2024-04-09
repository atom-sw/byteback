package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.common.name.BBLibNames;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.common.function.Lazy;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.VoidType;

import java.util.ArrayList;
import java.util.List;

public class PreconditionsFinder extends ConditionsFinder {

    private static final Lazy<PreconditionsFinder> instance = Lazy.from(()
            -> new PreconditionsFinder(BBLibNames.ENSURE_ANNOTATION));

    private PreconditionsFinder(final String annotationDescriptor) {
        super(annotationDescriptor);
    }

    @Override
    protected List<Type> makeBehaviorParameterTypes(final SootMethod targetMethod) {
        final var behaviorParameterTypes = new ArrayList<>(targetMethod.getParameterTypes());

        if (targetMethod.getReturnType() != VoidType.v()) {
            behaviorParameterTypes.add(targetMethod.getReturnType());
        }

        return behaviorParameterTypes;
    }

    @Override
    protected List<Value> makeBehaviorParameters(final SootMethod targetMethod) {
        final var behaviorParameters = new ArrayList<Value>(targetMethod.getActiveBody().getParameterLocals());
        final Type returnType = targetMethod.getReturnType();

        if (returnType != VoidType.v()) {
            behaviorParameters.add(Vimp.v().newReturnRef(returnType));
        }

        return behaviorParameters;
    }

}
