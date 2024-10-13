package byteback.syntax.scene.type.declaration.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootClass;

public class InvariantOnlyTagMarker extends TagMarker<SootClass, InvariantOnlyTag> {

	private static final Lazy<InvariantOnlyTagMarker> INSTANCE = Lazy.from(() -> new InvariantOnlyTagMarker(InvariantOnlyTag.v()));

	public static InvariantOnlyTagMarker v() {
		return INSTANCE.get();
	}

	private InvariantOnlyTagMarker(final InvariantOnlyTag tag) {
		super(tag);
	}

}
