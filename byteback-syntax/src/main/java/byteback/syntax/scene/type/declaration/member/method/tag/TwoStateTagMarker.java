package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class TwoStateTagMarker extends TagMarker<SootMethod, TwoStateTag> {

	private static final Lazy<TwoStateTagMarker> INSTANCE = Lazy.from(() -> new TwoStateTagMarker(TwoStateTag.v()));

	public static TwoStateTagMarker v() {
		return INSTANCE.get();
	}

	private TwoStateTagMarker(final TwoStateTag tag) {
		super(tag);
	}

}
