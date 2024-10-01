package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

public class EnsureOnlyTagMarker extends TagMarker<SootMethod, EnsureOnlyTag> {

	private static final Lazy<EnsureOnlyTagMarker> INSTANCE = Lazy.from(() -> new EnsureOnlyTagMarker(EnsureOnlyTag.v()));

	public static EnsureOnlyTagMarker v() {
		return INSTANCE.get();
	}

	private EnsureOnlyTagMarker(final EnsureOnlyTag tag) {
		super(tag);
	}

}
