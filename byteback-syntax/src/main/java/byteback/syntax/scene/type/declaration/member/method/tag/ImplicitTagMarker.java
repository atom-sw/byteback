package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class ImplicitTagMarker extends TagMarker<SootMethod, ImplicitTag> {

	private static final Lazy<ImplicitTagMarker> INSTANCE = Lazy.from(() -> new ImplicitTagMarker(ImplicitTag.v()));

	public static ImplicitTagMarker v() {
		return INSTANCE.get();
	}

	private ImplicitTagMarker(final ImplicitTag tag) {
		super(tag);
	}

}
