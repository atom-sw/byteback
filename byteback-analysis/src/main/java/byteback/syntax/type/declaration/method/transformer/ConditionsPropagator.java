package byteback.syntax.type.declaration.method.transformer;

import byteback.syntax.type.declaration.method.context.MethodContext;
import byteback.syntax.type.declaration.method.tag.ConditionsProvider;
import byteback.syntax.type.declaration.method.tag.ConditionsTag;
import soot.*;

import java.util.ArrayDeque;
import java.util.List;

public abstract class ConditionsPropagator<T extends ConditionsTag> extends MethodTransformer {

    private final ConditionsProvider<T> conditionsProvider;

    public ConditionsPropagator(final ConditionsProvider<T> conditionsProvider) {
        this.conditionsProvider = conditionsProvider;
    }

    public abstract void combineConditions(final List<Value> originalConditions,
                                           final List<Value> overridingConditions);

    @Override
    public void walkMethod(final MethodContext context) {
        final SootMethod sootMethod = context.getSootMethod();
        final Scene scene = context.getScene();
        final SootClass declaringClass = sootMethod.getDeclaringClass();
        final ConditionsTag conditionsTag = conditionsProvider.getOrCompute(sootMethod);
        final List<Value> originalConditionBoxes = conditionsTag.getValues();

        if (originalConditionBoxes.isEmpty()) {
            return;
        }

        final Hierarchy hierarchy = scene.getActiveHierarchy();
        final var nextSubClasses = new ArrayDeque<>(hierarchy.getDirectSubclassesOf(declaringClass));

        while (!nextSubClasses.isEmpty()) {
            final SootClass sootClass = nextSubClasses.pop();
            final SootMethod overridingMethod = sootClass.getMethodUnsafe(
                    sootMethod.getName(),
                    sootMethod.getParameterTypes(),
                    sootMethod.getReturnType()
            );

            if (overridingMethod != null) {
                final ConditionsTag overridingConditionsTag = conditionsProvider.getOrCompute(overridingMethod);
                final List<Value> overridingConditions = overridingConditionsTag.getValues();
                combineConditions(originalConditionBoxes, overridingConditions);
            }

            nextSubClasses.addAll(hierarchy.getDirectSubclassesOf(sootClass));
        }
    }

}
