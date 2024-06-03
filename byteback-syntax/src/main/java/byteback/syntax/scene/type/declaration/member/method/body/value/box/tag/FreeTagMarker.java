package byteback.syntax.scene.type.declaration.member.method.body.value.box.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.ValueBox;

public class FreeTagMarker extends TagMarker<ValueBox, FreeTag> {

	private static final Lazy<FreeTagMarker> INSTANCE = Lazy.from(() -> new FreeTagMarker(FreeTag.v()));

	public static FreeTagMarker v() {
		return INSTANCE.get();
	}

	private FreeTagMarker(final FreeTag tag) {
		super(tag);
	}

}
