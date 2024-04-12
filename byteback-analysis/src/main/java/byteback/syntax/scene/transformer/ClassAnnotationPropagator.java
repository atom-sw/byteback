package byteback.syntax.scene.transformer;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.name.ClassNames;
import byteback.syntax.tag.AnnotationReader;
import byteback.syntax.type.declaration.transformer.ClassTransformer;
import byteback.common.function.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import soot.Scene;
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
public class ClassAnnotationPropagator extends ClassTransformer {

    private static final Lazy<ClassAnnotationPropagator> instance = Lazy.from(ClassAnnotationPropagator::new);

    public static ClassAnnotationPropagator v() {
        return instance.get();
    }

    @Override
    public void transformClass(final Scene scene, final SootClass sootClass) {
        final AnnotationTag annotation;
        final AnnotationElem element;
        final String value;
        final Optional<AnnotationTag> annotationOptional = AnnotationReader.v()
                .getAnnotation(sootClass, BBLibNames.ATTACH_ANNOTATION);

        if (annotationOptional.isPresent()) {
            annotation = annotationOptional.get();
            element = AnnotationReader.v().getElem(annotation, "value").orElseThrow();
            value = ((AnnotationClassElem) element).getDesc();
        } else {
            return;
        }

        final SootClass hostClass = Scene.v().getSootClass(ClassNames.v().stripDescriptor(value));
        final List<SootMethod> methodsSnapshot = new ArrayList<>(sootClass.getMethods());

        for (final SootMethod attachedMethod : methodsSnapshot) {
            final SootMethod hostMethod = hostClass.getMethodUnsafe(attachedMethod.getNumberedSubSignature());
            sootClass.removeMethod(attachedMethod);
            attachedMethod.setDeclared(false);

            if (hostMethod != null) {
                hostClass.removeMethod(hostMethod);
            }

            hostClass.addMethod(attachedMethod);
        }
    }
}
