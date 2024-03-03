package byteback.analysis.scene.transformer;

import byteback.analysis.common.namespace.BBLibNames;
import byteback.analysis.common.namespace.ClassNames;
import byteback.analysis.scene.AnnotationElems.ClassElemExtractor;
import byteback.analysis.scene.AnnotationElems.StringElemExtractor;
import byteback.analysis.scene.Annotations;
import byteback.analysis.scene.Hosts;
import byteback.common.function.Lazy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;

public class ClassAnnotationPropagator extends SceneTransformer {

    private static final Lazy<ClassAnnotationPropagator> instance = Lazy.from(ClassAnnotationPropagator::new);

    public static ClassAnnotationPropagator v() {
        return instance.get();
    }

    @Override
    protected void internalTransform(final String phaseName, final Map<String, String> options) {
        final Iterator<SootClass> classIterator = Scene.v().getClasses().snapshotIterator();

        while (classIterator.hasNext()) {
            final SootClass attachedClass = classIterator.next();
            final AnnotationTag annotation;
            final AnnotationElem element;
            final String value;

            if (Hosts.hasAnnotation(attachedClass, BBLibNames.ATTACH_ANNOTATION)) {
                annotation = Hosts.getAnnotation(attachedClass, BBLibNames.ATTACH_ANNOTATION).orElseThrow();
                element = Annotations.getElem(annotation, "value").orElseThrow();
                value = new ClassElemExtractor().visit(element).orElseThrow();
            } else if (Hosts.hasAnnotation(attachedClass, BBLibNames.ATTACH_LABEL_ANNOTATION)) {
                annotation = Hosts.getAnnotation(attachedClass, BBLibNames.ATTACH_LABEL_ANNOTATION).orElseThrow();
                element = Annotations.getElem(annotation, "value").orElseThrow();
                value = new StringElemExtractor().visit(element).orElseThrow();
            } else {
                continue;
            }

            final SootClass hostClass = Scene.v().loadClassAndSupport(ClassNames.stripDescriptor(value));
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
