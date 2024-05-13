package byteback.syntax.scene.type.declaration.member.method.body.unit.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.tagkit.Host;

/**
 * Flags units that are targeted by exceptional branches.
 *
 * @author paganma
 */
public class ThrowTargetTagMarker extends TagMarker<Host, ThrowTargetTag> {

	private static final Lazy<ThrowTargetTagMarker> INSTANCE = Lazy
			.from(() -> new ThrowTargetTagMarker(ThrowTargetTag.v()));

	public static ThrowTargetTagMarker v() {
		return INSTANCE.get();
	}

	private ThrowTargetTagMarker(final ThrowTargetTag tag) {
		super(tag);
	}

}
