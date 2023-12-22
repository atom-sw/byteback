package byteback.analysis;

import byteback.analysis.util.AnnotationElems.ClassElemExtractor;
import byteback.analysis.util.AnnotationElems.StringElemExtractor;
import byteback.analysis.util.SootAnnotations;
import byteback.analysis.util.SootHosts;
import byteback.util.Lazy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.util.Chain;

public class ClassInjector {

	private static final Lazy<ClassInjector> instance = Lazy.from(ClassInjector::new);

	public static ClassInjector v() {
		return instance.get();
	}

	public void inject(final Chain<SootClass> classes) {
		final Iterator<SootClass> classIterator = classes.snapshotIterator();

		while (classIterator.hasNext()) {
			final SootClass attachedClass = classIterator.next();
			final AnnotationTag annotation;
			final AnnotationElem element;
			final String value;

			if (SootHosts.hasAnnotation(attachedClass, Namespace.ATTACH_ANNOTATION)) {
				annotation = SootHosts.getAnnotation(attachedClass, Namespace.ATTACH_ANNOTATION).orElseThrow();
				element = SootAnnotations.getElem(annotation, "value").orElseThrow();
				value = new ClassElemExtractor().visit(element);
			} else if (SootHosts.hasAnnotation(attachedClass, Namespace.ATTACH_LABEL_ANNOTATION)) {
				annotation = SootHosts.getAnnotation(attachedClass, Namespace.ATTACH_LABEL_ANNOTATION).orElseThrow();
				element = SootAnnotations.getElem(annotation, "value").orElseThrow();
				value = new StringElemExtractor().visit(element);
			} else {
				continue;
			}

			final SootClass hostClass = Scene.v().loadClassAndSupport(Namespace.stripDescriptor(value));
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
