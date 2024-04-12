package byteback.syntax.member.method.transformer;

import byteback.syntax.member.method.body.transformer.MethodTransformer;
import byteback.syntax.member.method.tag.ConditionsProvider;
import byteback.syntax.member.method.tag.ConditionsTag;
import byteback.syntax.value.box.ConditionExprBox;
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
