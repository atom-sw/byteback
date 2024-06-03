package byteback.syntax.scene.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.type.declaration.member.method.tag.ExportTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.ImportTagMarker;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ImplementationPropagator.class);

	private static final Lazy<ImplementationPropagator> INSTANCE = Lazy.from(ImplementationPropagator::new);

	public static ImplementationPropagator v() {
		return INSTANCE.get();
	}

	private ImplementationPropagator() {
	}

	private SootMethod cloneMethodImplementation(final SootMethod sootMethod) {
		final var clonedSootMethod = new SootMethod(sootMethod.getName(), sootMethod.getParameterTypes(),
				sootMethod.getReturnType(), sootMethod.getModifiers());
		clonedSootMethod.addAllTagsOf(sootMethod);

		return clonedSootMethod;
	}

	@Override
	public void transformScene(final SceneContext sceneContext) {
		final Scene scene = sceneContext.getScene();
		final Chain<SootClass> classes = scene.getClasses();
		final Iterator<SootClass> classIterator = classes.snapshotIterator();

		while (classIterator.hasNext()) {
			final SootClass pluginClass = classIterator.next();
			final AnnotationTag annotationTag;
			final String attachedName;
			final Optional<AnnotationTag> annotationOptional = AnnotationTagReader.v().getAnnotation(pluginClass,
					BBLibNames.ATTACH_ANNOTATION);

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
				LOGGER.warn("Unable to find class: " + attachedName + " for attaching " + pluginClass + ".");
				continue;
			}

			final List<SootMethod> methods = pluginClass.getMethods();
			final var methodsSnapshot = new ArrayList<>(methods);

			for (final SootMethod pluginMethod : methodsSnapshot) {
				if (ExportTagMarker.v().hasTag(pluginMethod)) {
					final SootMethod attachingMethod = cloneMethodImplementation(pluginMethod);
					final NumberedString attachedSubSignature = attachingMethod.getNumberedSubSignature();
					final SootMethod attachedMethod = attachedClass.getMethodUnsafe(attachedSubSignature);
					attachingMethod.setDeclared(false);

					if (attachedMethod != null) {
						attachedClass.removeMethod(attachedMethod);
					}

					attachedClass.addMethod(attachingMethod);
					attachingMethod.setDeclared(true);
				} else if (ImportTagMarker.v().hasTag(pluginMethod)) {
					final NumberedString pluginSubSignature = pluginMethod.getNumberedSubSignature();
					final SootMethod attachedMethod = cloneMethodImplementation(
							attachedClass.getMethodUnsafe(pluginSubSignature));
					attachedMethod.setDeclared(false);

					if (attachedMethod != null) {
						pluginClass.removeMethod(pluginMethod);
					}

					pluginClass.addMethod(attachedMethod);
					attachedMethod.setDeclared(true);
				}
			}
		}
	}

}
