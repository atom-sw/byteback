package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

/**
 * @author paganma
 */
public class ExceptionalTagMarker extends TagMarker<SootMethod, ExceptionalTag> {

	private static final Lazy<ExceptionalTagMarker> INSTANCE = Lazy
			.from(() -> new ExceptionalTagMarker(ExceptionalTag.v()));

	public static ExceptionalTagMarker v() {
		return INSTANCE.get();
	}

	private ExceptionalTagMarker(final ExceptionalTag tag) {
		super(tag);
	}

}
