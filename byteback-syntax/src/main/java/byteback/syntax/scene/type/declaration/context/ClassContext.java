package byteback.syntax.scene.type.declaration.context;

import byteback.syntax.context.Context;
import byteback.syntax.scene.context.SceneContext;
import soot.SootClass;

/**
 * A context surrounding a single class.
 *
 * @author paganma
 */
public class ClassContext implements Context {

	private final SceneContext sceneContext;

	private final SootClass sootClass;

	/**
	 * Constructs a new ClassContext.
	 *
	 * @param sceneContext The outer scene context.
	 * @param sootClass The class of this context.
	 */
	public ClassContext(final SceneContext sceneContext, final SootClass sootClass) {
		this.sceneContext = sceneContext;
		this.sootClass = sootClass;
	}

	/**
	 * Getter for the Soot Class in this context.
	 *
	 * @return The Soot Class in this context.
	 */
	public SootClass getSootClass() {
		return sootClass;
	}

	/**
	 * Getter for the surrounding Scene context.
	 *
	 * @return The Scene context associated with this class context.
	 */
	public SceneContext getSceneContext() {
		return sceneContext;
	}

}
