package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.common.name.BBLibNames;
import byteback.analysis.common.tag.AnnotationReader;
import byteback.analysis.common.tag.TagProvider;
import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.analysis.local.vimp.tag.body.ConditionsTag;
import soot.*;
import soot.tagkit.AnnotationStringElem;

import java.util.*;

public abstract class ConditionsFinder<T extends ConditionsTag> extends BodyTransformer {

    private final String annotationDescriptor;

    private final TagProvider<Body, T> conditionsTagProvider;

    public ConditionsFinder(final String annotationDescriptor, final TagProvider<Body, T> conditionsTagProvider) {
        this.annotationDescriptor = annotationDescriptor;
        this.conditionsTagProvider = conditionsTagProvider;
    }

    protected abstract List<Value> makeBehaviorParameters(final Body body);

    protected abstract List<Type> makeBehaviorParameterTypes(final Body body);

    @Override
    public void transformBody(final Body body) {
        final SootMethod targetMethod = body.getMethod();
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
                            if (!AnnotationReader.v().hasAnnotation(behaviorMethod, BBLibNames.PREDICATE_ANNOTATION)) {
                                throw new SpecificationFormatException(
                                        "Not a behavior method: " + behaviorName,
                                        body
                                );
                            }

                            final var behaviorParameters = makeBehaviorParameters(body);
                            final Value behaviorExpression =
                                    Vimp.v().newCallExpr(behaviorMethod.makeRef(), behaviorParameters);

                            conditionsTagProvider.getOrCompute(body).getValues().add(behaviorExpression);
                        } else {
                            throw new SpecificationFormatException(
                                    "Could not find behavior method: " + behaviorName,
                                    body
                            );
                        }
                    } else {
                        throw new SpecificationFormatException(
                                "Invalid format for annotation",
                                body
                        );
                    }
        });
    }

}
