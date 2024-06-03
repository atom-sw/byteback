package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class PureTagMarker extends TagMarker<SootMethod, PureTag> {

	private static final Lazy<PureTagMarker> INSTANCE = Lazy.from(() -> new PureTagMarker(PureTag.v()));

	public static PureTagMarker v() {
		return INSTANCE.get();
	}

	private PureTagMarker(final PureTag tag) {
		super(tag);
	}

}
