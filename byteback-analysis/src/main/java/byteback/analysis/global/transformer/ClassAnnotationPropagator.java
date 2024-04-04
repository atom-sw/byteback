package byteback.analysis.global.transformer;

import byteback.analysis.common.name.BBLibNames;
import byteback.analysis.common.name.ClassNames;
import byteback.analysis.common.tag.AnnotationReader;
import byteback.common.function.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;

/**
 * Propagates method implementations through the @Attach annotation.
 *
 * @author paganma
 */
public class ClassAnnotationPropagator extends SceneTransformer {

    private static final Lazy<ClassAnnotationPropagator> instance = Lazy.from(ClassAnnotationPropagator::new);

    public static ClassAnnotationPropagator v() {
        return instance.get();
    }

    @Override
    protected void internalTransform(final String phaseName, final Map<String, String> options) {
        for (final SootClass attachedClass : Scene.v().getClasses()) {
            final AnnotationTag annotation;
            final AnnotationElem element;
            final String value;

            final Optional<AnnotationTag> annotationOptional = AnnotationReader.v()
                    .getAnnotation(attachedClass, BBLibNames.ATTACH_ANNOTATION);

            if (annotationOptional.isPresent()) {
                annotation = annotationOptional.get();
                element = AnnotationReader.v().getElem(annotation, "value").orElseThrow();
                value = ((AnnotationClassElem) element).getDesc();
            } else {
                continue;
            }

            final SootClass hostClass = Scene.v().getSootClass(ClassNames.v().stripDescriptor(value));
            final List<SootMethod> methodsSnapshot = new ArrayList<>(attachedClass.getMethods());

            for (final SootMethod attachedMethod : methodsSnapshot) {
                final SootMethod hostMethod = hostClass.getMethodUnsafe(attachedMethod.getNumberedSubSignature());
                attachedClass.removeMethod(attachedMethod);
                attachedMethod.setDeclared(false);

                if (hostMethod != null) {
                    hostClass.removeMethod(hostMethod);
                }

                hostClass.addMethod(attachedMethod);
            }
        }
    }
}
