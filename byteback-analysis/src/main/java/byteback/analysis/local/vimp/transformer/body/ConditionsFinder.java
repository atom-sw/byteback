package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.common.name.BBLibNames;
import byteback.analysis.common.tag.AnnotationReader;
import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.common.function.Lazy;
import soot.*;
import soot.tagkit.AnnotationStringElem;

import java.util.*;

public class PostconditionsFinder extends BodyTransformer {

    private final static Lazy<PostconditionsFinder> instance = Lazy.from(PostconditionsFinder::new);

    public static PostconditionsFinder v() {
        return instance.get();
    }

    private PostconditionsFinder() {
    }

    @Override
    public void transformBody(final Body body) {
        final SootMethod sootMethod = body.getMethod();
        final SootClass sootClass = sootMethod.getDeclaringClass();

        System.out.println("*******************");
        AnnotationReader.v().getAnnotations(sootMethod)
                .filter((tag) -> tag.getType().equals(BBLibNames.ENSURE_ANNOTATION))
                .forEach((tag) -> {
                    System.out.println("================");
                    System.out.println(tag);
                    System.out.println("================");
                    if (AnnotationReader.v().getValue(tag).orElse(null) instanceof
                            final AnnotationStringElem annotationStringElement) {
                        final String behaviorName = annotationStringElement.getValue();
                        final var behaviorParameterTypes = new ArrayList<>(sootMethod.getParameterTypes());
                        behaviorParameterTypes.add(sootMethod.getReturnType());
                        final Type behaviorReturnType = BooleanType.v();
                        final SootMethod behaviorMethod = sootClass.getMethodUnsafe(
                                behaviorName,
                                behaviorParameterTypes,
                                behaviorReturnType
                        );
                        if (behaviorMethod != null) {
                            System.out.println(behaviorMethod);
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
        System.out.println("*******************");
    }

}
