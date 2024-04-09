package byteback.analysis.global.vimp.transformer;

import byteback.analysis.global.common.transformer.MethodTransformer;
import byteback.analysis.local.vimp.tag.body.ConditionsProvider;
import byteback.analysis.local.vimp.tag.body.ConditionsTag;
import soot.*;

import java.util.ArrayDeque;
import java.util.List;

public abstract class ConditionsPropagator<T extends ConditionsTag> extends MethodTransformer {

    private final ConditionsProvider<T> conditionsProvider;

    public ConditionsPropagator(final ConditionsProvider<T> conditionsProvider) {
        this.conditionsProvider = conditionsProvider;
    }

    public abstract void combineConditions(List<Value> originalConditions, List<Value> overridingConditions);

    @Override
    public void transformMethod(final Scene scene, final SootMethod sootMethod) {
        final SootClass declaringClass = sootMethod.getDeclaringClass();

        if (!sootMethod.hasActiveBody()) {
            return;
        }
        
        final Body body = sootMethod.getActiveBody();
        final List<Value> originalConditions = conditionsProvider.getOrCompute(body).getValues();
        final Hierarchy hierarchy = scene.getActiveHierarchy();
        final var nextSubClasses = new ArrayDeque<>(hierarchy.getDirectSubclassesOf(declaringClass));

        while (!nextSubClasses.isEmpty()) {
            final SootClass sootClass = nextSubClasses.pop();
            final SootMethod overridingMethod = sootClass.getMethodUnsafe(
                    sootMethod.getName(),
                    sootMethod.getParameterTypes(),
                    sootMethod.getReturnType()
            );

            if (overridingMethod != null && overridingMethod.hasActiveBody()) {
                final Body overridingBody = overridingMethod.getActiveBody();
                final List<Value> overridingConditions = conditionsProvider.getOrCompute(overridingBody).getValues();
                combineConditions(originalConditions, overridingConditions);
            }

            nextSubClasses.addAll(hierarchy.getDirectSubclassesOf(sootClass));
        }
    }

}
