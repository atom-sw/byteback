package byteback.syntax.scene.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.tag.AnnotationReader;
import byteback.syntax.transformer.TransformationException;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.util.Chain;
import soot.util.NumberedString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Propagates method implementations and specification through the @Attach annotation.
 *
 * @author paganma
 */
public class ClassAnnotationPropagator extends SceneTransformer {

    private static final Lazy<ClassAnnotationPropagator> INSTANCE = Lazy.from(ClassAnnotationPropagator::new);

    public static ClassAnnotationPropagator v() {
        return INSTANCE.get();
    }

    private ClassAnnotationPropagator() {
    }

    @Override
    public void transformScene(final SceneContext context) {
        final Scene scene = context.getScene();
        final Chain<SootClass> classes = scene.getClasses();
        final Iterator<SootClass> classIterator = classes.snapshotIterator();

        while (classIterator.hasNext()) {
            final SootClass attachingClass = classIterator.next();
            final AnnotationTag annotation;
            final AnnotationElem element;
            final String attachedName;
            final Optional<AnnotationTag> annotationOptional = AnnotationReader.v()
                    .getAnnotation(attachingClass, BBLibNames.ATTACH_ANNOTATION);

            if (annotationOptional.isPresent()) {
                annotation = annotationOptional.get();
                element = AnnotationReader.v().getElement(annotation, "value")
                        .orElseThrow(() ->
                                new TransformationException("@Attach annotation does not specify value."));

                if (element instanceof AnnotationClassElem annotationClassElement) {
                    attachedName = annotationClassElement.getDesc();
                } else {
                    throw new TransformationException(
                            "Wrong element type for @Attach value "
                                    + element.getName()
                                    + ": "
                                    + element.getKind()
                    );
                }
            } else {
                break;
            }

            final SootClass attachedClass = scene.getSootClassUnsafe(attachedName);

            if (attachedClass == null) {
                throw new TransformationException(
                        "Unable to find attached class: "
                                + attachedName
                                + " for attaching "
                                + attachingClass
                );
            }

            final List<SootMethod> methods = attachingClass.getMethods();
            final List<SootMethod> methodsSnapshot = new ArrayList<>(methods);

            for (final SootMethod attachingMethod : methodsSnapshot) {
                final NumberedString attachedSubSignature = attachingMethod.getNumberedSubSignature();
                final SootMethod attachedMethod = attachedClass.getMethodUnsafe(attachedSubSignature);
                attachingClass.removeMethod(attachingMethod);
                attachingMethod.setDeclared(false);

                if (attachedMethod != null) {
                    attachedClass.removeMethod(attachedMethod);
                }

                attachedClass.addMethod(attachingMethod);
            }
        }
    }

}
