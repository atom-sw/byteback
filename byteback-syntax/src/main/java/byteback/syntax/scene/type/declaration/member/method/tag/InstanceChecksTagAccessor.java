package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.SootMethod;

public class InstanceChecksTagAccessor extends TagAccessor<SootMethod, InstanceChecksTag> {

	private static final Lazy<InstanceChecksTagAccessor> INSTANCE = Lazy
		.from(() -> new InstanceChecksTagAccessor(InstanceChecksTag.NAME));

	public static InstanceChecksTagAccessor v() {
		return INSTANCE.get();
	}

	public InstanceChecksTagAccessor(final String tagName) {
		super(tagName);
	}

}
