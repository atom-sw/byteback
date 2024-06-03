package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

public class OperatorTagMarker extends TagMarker<SootMethod, OperatorTag> {

	private static final Lazy<OperatorTagMarker> INSTANCE = Lazy.from(() -> new OperatorTagMarker(OperatorTag.v()));

	public static OperatorTagMarker v() {
		return INSTANCE.get();
	}

	private OperatorTagMarker(final OperatorTag tag) {
		super(tag);
	}

}
