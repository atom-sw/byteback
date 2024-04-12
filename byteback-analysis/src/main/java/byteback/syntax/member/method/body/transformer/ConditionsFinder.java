package byteback.syntax.member.method.body.transformer;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.tag.AnnotationReader;
import byteback.syntax.member.method.tag.ConditionsProvider;
import byteback.syntax.Vimp;
import byteback.syntax.member.method.tag.ConditionsTag;
import byteback.syntax.value.box.ConditionExprBox;
import soot.*;
import soot.tagkit.AnnotationStringElem;

import java.util.*;

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
                            if (!AnnotationReader.v().hasAnnotation(behaviorMethod, BBLibNames.BEHAVIOR_ANNOTATION)) {
                                throw new SpecificationFormatException(
                                        "Not a behavior method: " + behaviorName,
                                        body
                                );
                            }

                            final var behaviorParameters = makeBehaviorParameters(body);
                            final Value behaviorExpression =
                                    Vimp.v().newCallExpr(behaviorMethod.makeRef(), behaviorParameters);
                            final ConditionExprBox conditionBox = Vimp.v().newConditionExprBox(behaviorExpression);
                            conditionsTagProvider.getOrCompute(targetMethod).getValueBoxes().add(conditionBox);
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
