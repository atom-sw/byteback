package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagFlagger;
import byteback.syntax.scene.type.declaration.member.method.tag.ConditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.ConditionsTagProvider;
import byteback.syntax.transformer.TransformationException;
import soot.*;
import soot.util.NumberedString;

/**
 * Extracts the conditions for a method.
 *
 * @param <T> The type of the {@link ConditionsTag}.
 * @author paganma
 */
public abstract class ConditionsResolver<T extends ConditionsTag> {

    private final ConditionsTagProvider<T> conditionsTagProvider;

    public ConditionsResolver(final ConditionsTagProvider<T> conditionsTagProvider) {
        this.conditionsTagProvider = conditionsTagProvider;
    }

    protected abstract NumberedString makeBehaviorSignature(final SootMethod targetMethod, final String behaviorName);

    protected abstract Value makeConditionValue(final SootMethod targetMethod, final SootMethod behaviorMethod);

    public final void resolveCondition(final SootMethod targetMethod, final String behaviorName) {
        final SootClass declaringClass = targetMethod.getDeclaringClass();
        final T conditionsTag = conditionsTagProvider.getOrCompute(targetMethod);
        final var conditions = conditionsTag.getValues();

        final NumberedString behaviorSignature = makeBehaviorSignature(targetMethod, behaviorName);
        final SootMethod behaviorMethod = declaringClass.getMethodUnsafe(behaviorSignature);

        if (behaviorMethod != null) {
            if (!BehaviorTagFlagger.v().isTagged(behaviorMethod)) {
                throw new TransformationException(
                        "Not a behavior method: " + behaviorName,
                        targetMethod
                );
            }

            final var conditionValue = makeConditionValue(targetMethod, behaviorMethod);
            conditions.add(conditionValue);
        } else {
            throw new TransformationException(
                    "Could not find behavior method: " + behaviorName,
                    targetMethod
            );
        }
    }

}
