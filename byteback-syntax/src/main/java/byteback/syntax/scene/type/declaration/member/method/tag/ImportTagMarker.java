package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

public class ImportTagMarker extends TagMarker<SootMethod, ImportTag> {

	private static final Lazy<ImportTagMarker> INSTANCE = Lazy.from(() -> new ImportTagMarker(ImportTag.v()));

	public static ImportTagMarker v() {
		return INSTANCE.get();
	}

	private ImportTagMarker(final ImportTag tag) {
		super(tag);
	}

}
