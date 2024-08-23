package byteback.syntax.scene.type.declaration.transformer;

import byteback.syntax.scene.transformer.SceneTransformer;
import soot.Scene;
import soot.SootClass;

/**
 * Defines how to transform a class.
 *
 * @author paganma
 */
public abstract class SceneClassTransformer extends SceneTransformer {

	public abstract void transformClass(final Scene scene, final SootClass sootClass);

	public void transformScene(final Scene scene) {
		for (final SootClass sootClass : scene.getClasses()) {
			transformClass(scene, sootClass);
		}
	}

}
