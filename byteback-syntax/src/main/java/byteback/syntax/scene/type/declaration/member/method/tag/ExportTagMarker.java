package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

public class ExportTagMarker extends TagMarker<SootMethod, ExportTag> {

	private static final Lazy<ExportTagMarker> INSTANCE = Lazy.from(() -> new ExportTagMarker(ExportTag.v()));

	public static ExportTagMarker v() {
		return INSTANCE.get();
	}

	private ExportTagMarker(final ExportTag tag) {
		super(tag);
	}

}
