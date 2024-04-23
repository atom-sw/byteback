package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.ConditionsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.ConditionsTag;
import soot.*;

import java.util.ArrayDeque;
import java.util.List;

public abstract class ConditionsPropagator<T extends ConditionsTag> extends MethodTransformer {

    private final ConditionsTagProvider<T> conditionsTagManager;

    public ConditionsPropagator(final ConditionsTagProvider<T> conditionsTagManager) {
        this.conditionsTagManager = conditionsTagManager;
    }

    public abstract List<Value> combineConditions(List<Value> originalConditions, List<Value> overridingConditions);

    protected final List<SootClass> subTypesOf(final Hierarchy hierarchy, final SootClass sootClass) {
        if (sootClass.isInterface()) {
            return hierarchy.getDirectImplementersOf(sootClass);
        } else {
            return hierarchy.getDirectSubclassesOf(sootClass);
        }
    }

    @Override
    public void transformMethod(final MethodContext methodContext) {
        final SootMethod sootMethod = methodContext.getSootMethod();
        final ClassContext classContext = methodContext.getClassContext();
        final SceneContext sceneContext = classContext.getSceneContext();
        final Scene scene = sceneContext.getScene();
        final SootClass declaringClass = classContext.getSootClass();
        final ConditionsTag conditionsTag = conditionsTagManager.getOrThrow(sootMethod);
        final List<Value> originalConditions = conditionsTag.getValues();

        if (originalConditions.isEmpty()) {
            return;
        }

        final Hierarchy hierarchy = scene.getActiveHierarchy();
        final var nextSubClasses = new ArrayDeque<>(subTypesOf(hierarchy, declaringClass));

        while (!nextSubClasses.isEmpty()) {
            final SootClass sootClass = nextSubClasses.pop();
            final SootMethod overridingMethod = sootClass.getMethodUnsafe(
                    sootMethod.getName(),
                    sootMethod.getParameterTypes(),
                    sootMethod.getReturnType()
            );

            if (overridingMethod != null) {
                final ConditionsTag overridingConditionsTag = conditionsTagManager.getOrThrow(overridingMethod);
                final List<Value> overridingConditions = overridingConditionsTag.getValues();
                final List<Value> combinedConditions = combineConditions(originalConditions, overridingConditions);
                overridingConditionsTag.setValues(combinedConditions);
            }

            nextSubClasses.addAll(subTypesOf(hierarchy, sootClass));
        }
    }

}
