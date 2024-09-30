package byteback.syntax.scene.transformer;

import byteback.common.function.Lazy;
import soot.Body;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;

public class MethodResolver extends SceneTransformer {

	private static final Lazy<MethodResolver> INSTANCE = Lazy.from(MethodResolver::new);

	public static MethodResolver v() {
		return INSTANCE.get();
	}

	private MethodResolver() {
	}

	public void ensureMethod(final Scene scene, final SootMethodRef methodRef) {
		final SootMethod sootMethod = methodRef.tryResolve();

		if (sootMethod == null) {
			final SootClass declaringClass = methodRef.getDeclaringClass();
			final var newSootMethod = new SootMethod(
					methodRef.getName(),
					methodRef.getParameterTypes(),
					methodRef.getReturnType());

			if (methodRef.isStatic()) {
				newSootMethod.setModifiers(newSootMethod.getModifiers() | Modifier.STATIC);
			}

			newSootMethod.setModifiers(newSootMethod.getModifiers() | Modifier.ABSTRACT);

			declaringClass.addMethod(newSootMethod);
		}
	}

	@Override
	public void transformScene(final Scene scene) {
		for (final SootClass sootClass : scene.getClasses()) {
			if (sootClass.resolvingLevel() < SootClass.BODIES) {
				continue;
			}

			for (final SootMethod sootMethod : sootClass.getMethods()) {
				if (sootMethod.isPhantom() || sootMethod.isAbstract()) {
					continue;
				}

				final Body body = sootMethod.retrieveActiveBody();

				for (final ValueBox valueBox : body.getUseBoxes()) {
					final Value value = valueBox.getValue();

					if (value instanceof final InvokeExpr invokeExpr) {
						final SootMethodRef methodRef = invokeExpr.getMethodRef();
						ensureMethod(scene, methodRef);
					}
				}
			}
		}
	}

}
