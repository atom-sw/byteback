package byteback.syntax.type.declaration.method.transformer;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.tag.AnnotationReader;
import byteback.syntax.transformer.TransformationException;
import byteback.syntax.type.declaration.method.body.context.BodyContext;
import byteback.syntax.type.declaration.method.body.transformer.BodyTransformer;
import byteback.syntax.type.declaration.method.tag.ConditionsProvider;
import byteback.syntax.Vimp;
import byteback.syntax.type.declaration.method.tag.ConditionsTag;
import soot.*;
import soot.tagkit.AnnotationStringElem;

import java.util.*;

/**
 * Extracts the conditions for a method.
 *
 * @param <T> The type of the {@link ConditionsTag}.
 * @author paganma
 */
public abstract class ConditionsFinder<T extends ConditionsTag> extends BodyTransformer {

    private final String annotationDescriptor;

    private final ConditionsProvider<T> conditionsTagProvider;

    public ConditionsFinder(final String annotationDescriptor, final ConditionsProvider<T> conditionsTagProvider) {
        this.annotationDescriptor = annotationDescriptor;
        this.conditionsTagProvider = conditionsTagProvider;
    }

    protected abstract List<Value> makeBehaviorParameters(final Body body);

    protected abstract List<Type> makeBehaviorParameterTypes(final Body body);

    @Override
    public void walkBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final SootMethod targetMethod = bodyContext.getSootMethod();
        final SootClass declaringClass = targetMethod.getDeclaringClass();

        AnnotationReader.v().getAnnotations(targetMethod)
                .filter((tag) -> tag.getType().equals(annotationDescriptor))
                .forEach((tag) -> {
                    if (AnnotationReader.v().getValue(tag).orElse(null) instanceof
                            final AnnotationStringElem annotationStringElement) {
                        final String behaviorName = annotationStringElement.getValue();
                        final List<Type> behaviorParameterTypes = makeBehaviorParameterTypes(body);
                        final Type behaviorReturnType = BooleanType.v();
                        final SootMethod behaviorMethod = declaringClass.getMethodUnsafe(
                                behaviorName,
                                behaviorParameterTypes,
                                behaviorReturnType
                        );

                        if (behaviorMethod != null) {
                            if (!AnnotationReader.v().hasAnnotation(behaviorMethod, BBLibNames.BEHAVIOR_ANNOTATION)) {
                                throw new TransformationException(
                                        "Not a behavior method: " + behaviorName,
                                        body
                                );
                            }

                            final var behaviorParameters = makeBehaviorParameters(body);
                            final SootMethodRef methodRef = behaviorMethod.makeRef();
                            final Value behaviorValue = Vimp.v().newCallExpr(methodRef, behaviorParameters);
                            conditionsTagProvider.getOrCompute(targetMethod).getValues().add(behaviorValue);
                        } else {
                            throw new TransformationException(
                                    "Could not find behavior method: " + behaviorName,
                                    body
                            );
                        }
                    } else {
                        throw new TransformationException(
                                "Invalid format for annotation",
                                body
                        );
                    }
        });
    }

}
