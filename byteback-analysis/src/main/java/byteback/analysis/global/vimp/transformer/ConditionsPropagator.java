package byteback.analysis.global.vimp.transformer;

import byteback.analysis.global.common.transformer.MethodTransformer;
import byteback.analysis.global.vimp.tag.ConditionsProvider;
import byteback.analysis.global.vimp.tag.ConditionsTag;
import byteback.analysis.local.vimp.syntax.value.box.ConditionExprBox;
import soot.*;

import java.util.ArrayDeque;
import java.util.List;

public abstract class ConditionsPropagator<T extends ConditionsTag> extends MethodTransformer {

    private final ConditionsProvider<T> conditionsProvider;

    public ConditionsPropagator(final ConditionsProvider<T> conditionsProvider) {
        this.conditionsProvider = conditionsProvider;
    }

    public abstract void combineConditions(final List<ConditionExprBox> originalConditionBoxes,
                                           final List<ConditionExprBox> overridingConditionBoxes);

    @Override
    public void transformMethod(final Scene scene, final SootMethod sootMethod) {
        final SootClass declaringClass = sootMethod.getDeclaringClass();
        final List<ConditionExprBox> originalConditionBoxes = conditionsProvider.getOrCompute(sootMethod).getValueBoxes();

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
                final List<ConditionExprBox> overridingConditions = conditionsProvider.getOrCompute(overridingMethod).getValueBoxes();
                combineConditions(originalConditionBoxes, overridingConditions);
            }

            nextSubClasses.addAll(hierarchy.getDirectSubclassesOf(sootClass));
        }
    }

}
