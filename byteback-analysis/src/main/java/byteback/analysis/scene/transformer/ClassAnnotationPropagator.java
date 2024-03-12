package byteback.analysis.scene.transformer;

import byteback.analysis.common.Hosts;
import byteback.analysis.common.namespace.BBLibNames;
import byteback.analysis.common.namespace.ClassNames;
import byteback.analysis.scene.AnnotationElems;
import byteback.analysis.scene.Annotations;
import byteback.common.function.Lazy;
import byteback.analysis.model.ClassModel;
import soot.Scene;
import soot.SceneTransformer;
import byteback.analysis.model.MethodModel;
import soot.tag.AnnotationElement;
import soot.tag.AnnotationTag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClassAnnotationPropagator extends SceneTransformer {

    private static final Lazy<ClassAnnotationPropagator> instance = Lazy.from(ClassAnnotationPropagator::new);

    public static ClassAnnotationPropagator v() {
        return instance.get();
    }

    @Override
    protected void internalTransform(final String phaseName, final Map<String, String> options) {
        final Iterator<ClassModel> classIterator = Scene.v().getClasses().snapshotIterator();

        while (classIterator.hasNext()) {
            final ClassModel attachedClass = classIterator.next();
            final AnnotationTag annotation;
            final AnnotationElement element;
            final String value;

            if (Hosts.v().hasAnnotation(attachedClass, BBLibNames.ATTACH_ANNOTATION)) {
                annotation = Hosts.v().getAnnotation(attachedClass, BBLibNames.ATTACH_ANNOTATION).orElseThrow();
                element = Annotations.v().getElem(annotation, "value").orElseThrow();
                value = AnnotationElems.v().new ClassElementExtractor().visit(element).orElseThrow();
            } else if (Hosts.v().hasAnnotation(attachedClass, BBLibNames.ATTACH_LABEL_ANNOTATION)) {
                annotation = Hosts.v().getAnnotation(attachedClass, BBLibNames.ATTACH_LABEL_ANNOTATION).orElseThrow();
                element = Annotations.v().getElem(annotation, "value").orElseThrow();
                value = AnnotationElems.v().new StringElementExtractor().visit(element).orElseThrow();
            } else {
                continue;
            }

            final ClassModel hostClass = Scene.v().loadClassAndSupport(ClassNames.stripDescriptor(value));
            final List<MethodModel> methodsSnapshot = new ArrayList<>(attachedClass.getMethodModels());

            for (final MethodModel attachedMethod : methodsSnapshot) {
                final MethodModel hostMethod = hostClass.getMethodUnsafe(attachedMethod.getNumberedSubSignature());
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
