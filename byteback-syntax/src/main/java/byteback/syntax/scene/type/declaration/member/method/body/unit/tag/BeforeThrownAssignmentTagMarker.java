package byteback.syntax.scene.type.declaration.member.method.body.unit.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.Unit;

public class BeforeThrownAssignmentTagMarker extends TagMarker<Unit, BeforeThrownAssignmentTag> {

	private static final Lazy<BeforeThrownAssignmentTagMarker> INSTANCE = Lazy
			.from(() -> new BeforeThrownAssignmentTagMarker(BeforeThrownAssignmentTag.v()));

	public static BeforeThrownAssignmentTagMarker v() {
		return INSTANCE.get();
	}

	private BeforeThrownAssignmentTagMarker(final BeforeThrownAssignmentTag tag) {
		super(tag);
	}

}
