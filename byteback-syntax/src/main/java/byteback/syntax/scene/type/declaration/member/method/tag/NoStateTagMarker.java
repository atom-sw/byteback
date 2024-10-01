package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

public class NoStateTagMarker extends TagMarker<SootMethod, NoStateTag> {

	private static final Lazy<NoStateTagMarker> INSTANCE = Lazy.from(() -> new NoStateTagMarker(NoStateTag.v()));

	public static NoStateTagMarker v() {
		return INSTANCE.get();
	}

	private NoStateTagMarker(final NoStateTag tag) {
		super(tag);
	}

}
