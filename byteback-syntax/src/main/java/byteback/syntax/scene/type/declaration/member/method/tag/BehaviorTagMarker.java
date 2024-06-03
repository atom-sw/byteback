package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class BehaviorTagMarker extends TagMarker<SootMethod, BehaviorTag> {

	private static final Lazy<BehaviorTagMarker> INSTANCE = Lazy.from(() -> new BehaviorTagMarker(BehaviorTag.v()));

	public static BehaviorTagMarker v() {
		return INSTANCE.get();
	}

	private BehaviorTagMarker(final BehaviorTag tag) {
		super(tag);
	}

}
