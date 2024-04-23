package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagFlagger;
import byteback.syntax.scene.type.declaration.member.method.tag.ConditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.ConditionsTagProvider;
import byteback.syntax.tag.AnnotationTagReader;
import byteback.syntax.transformer.TransformationException;
import soot.*;
import soot.tagkit.AnnotationStringElem;

import java.util.List;

/**
 * Extracts the conditions for a method.
 *
 * @param <T> The type of the {@link ConditionsTag}.
 * @author paganma
 */
public abstract class ConditionsFinder<T extends ConditionsTag> extends MethodTransformer {

    private final String annotationDescriptor;

    private final ConditionsTagProvider<T> conditionsTagProvider;

    public ConditionsFinder(final String annotationDescriptor, final ConditionsTagProvider<T> conditionsTagProvider) {
        this.annotationDescriptor = annotationDescriptor;
        this.conditionsTagProvider = conditionsTagProvider;
    }

    protected abstract List<Value> makeBehaviorParameters(final SootMethod sootMethod);

    protected abstract List<Type> makeBehaviorParameterTypes(final SootMethod sootMethod);

    @Override
    public void transformMethod(final MethodContext methodContext) {
        final SootMethod targetMethod = methodContext.getSootMethod();
        final SootClass declaringClass = targetMethod.getDeclaringClass();
        final T conditionsTag = conditionsTagProvider.gerOrCompute(targetMethod);
        final var conditions = conditionsTag.getValues();

        AnnotationTagReader.v().getAnnotations(targetMethod)
                .filter((tag) -> tag.getType().equals(annotationDescriptor))
                .forEach((tag) -> {
                    if (AnnotationTagReader.v().getValue(tag).orElse(null) instanceof
                            final AnnotationStringElem annotationStringElement) {
                        final String behaviorName = annotationStringElement.getValue();
                        final List<Type> behaviorParameterTypes = makeBehaviorParameterTypes(targetMethod);
                        final Type behaviorReturnType = BooleanType.v();
                        final SootMethod behaviorMethod = declaringClass.getMethodUnsafe(
                                behaviorName,
                                behaviorParameterTypes,
                                behaviorReturnType
                        );

                        if (behaviorMethod != null) {
                            if (!BehaviorTagFlagger.v().isTagged(behaviorMethod)) {
                                throw new TransformationException(
                                        "Not a behavior method: " + behaviorName,
                                        targetMethod
                                );
                            }

                            final var behaviorParameters = makeBehaviorParameters(targetMethod);
                            final SootMethodRef methodRef = behaviorMethod.makeRef();
                            final Value behaviorValue = Vimp.v().newCallExpr(methodRef, behaviorParameters);
                            conditions.add(behaviorValue);
                        } else {
                            throw new TransformationException(
                                    "Could not find behavior method: " + behaviorName,
                                    targetMethod
                            );
                        }
                    } else {
                        throw new TransformationException(
                                "Invalid format for annotation",
                                targetMethod
                        );
                    }
                });
    }

}
