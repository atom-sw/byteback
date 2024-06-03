package byteback.syntax.scene.type.declaration.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.SootClass;

public class InvariantsTagAccessor extends TagAccessor<SootClass, InvariantsTag> {

	private static final Lazy<InvariantsTagAccessor> INSTANCE = Lazy
			.from(() -> new InvariantsTagAccessor(InvariantsTag.NAME));

	public static InvariantsTagAccessor v() {
		return INSTANCE.get();
	}

	private InvariantsTagAccessor(final String tagName) {
		super(tagName);
	}

}
