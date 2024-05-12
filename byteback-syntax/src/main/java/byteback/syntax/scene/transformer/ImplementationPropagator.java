package byteback.syntax.scene.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.tag.AnnotationTagReader;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;
import soot.util.Chain;
import soot.util.NumberedString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Propagates method implementations and specification through the @Attach
 * annotation.
 *
 * @author paganma
 */
public class ImplementationPropagator extends SceneTransformer {

	private static final Logger logger = LoggerFactory.getLogger(ImplementationPropagator.class);

	private static final Lazy<ImplementationPropagator> INSTANCE = Lazy.from(ImplementationPropagator::new);

	public static ImplementationPropagator v() {
		return INSTANCE.get();
	}

	private ImplementationPropagator() {
	}

	@Override
	public void transformScene(final SceneContext sceneContext) {
		final Scene scene = sceneContext.getScene();
		final Chain<SootClass> classes = scene.getClasses();
		final Iterator<SootClass> classIterator = classes.snapshotIterator();

		while (classIterator.hasNext()) {
			final SootClass attachingClass = classIterator.next();
			final AnnotationTag annotationTag;
			final String attachedName;
			final Optional<AnnotationTag> annotationOptional = AnnotationTagReader.v().getAnnotation(
					attachingClass, BBLibNames.ATTACH_ANNOTATION);

			if (annotationOptional.isPresent()) {
				annotationTag = annotationOptional.get();
				final AnnotationStringElem annotationStringElement = AnnotationTagReader.v()
						.getValue(annotationTag, AnnotationStringElem.class)
						.orElseThrow();
				attachedName = annotationStringElement.getValue();
			} else {
				continue;
			}

			final SootClass attachedClass = scene.getSootClassUnsafe(attachedName);

			if (attachedClass == null) {
				logger.warn("Unable to find class: " + attachedName + " for attaching " + attachingClass);
				continue;
			}

			final List<SootMethod> methods = attachingClass.getMethods();
			final var methodsSnapshot = new ArrayList<>(methods);

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
