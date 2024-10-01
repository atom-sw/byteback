package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

public class RequireOnlyTagMarker extends TagMarker<SootMethod, RequireOnlyTag> {

	private static final Lazy<RequireOnlyTagMarker> INSTANCE = Lazy.from(() -> new RequireOnlyTagMarker(RequireOnlyTag.v()));

	public static RequireOnlyTagMarker v() {
		return INSTANCE.get();
	}

	private RequireOnlyTagMarker(final RequireOnlyTag tag) {
		super(tag);
	}

}
